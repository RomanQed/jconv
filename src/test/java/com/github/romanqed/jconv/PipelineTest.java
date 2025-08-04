package com.github.romanqed.jconv;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public final class PipelineTest {

    @Test
    public void testEmptyPipeline() throws Throwable {
        var pipeline = TaskBuilders.<String>linked().build();
        assertNotNull(pipeline);
        // Should be the EMPTY task
        assertEquals(Task.EMPTY, pipeline);

        // Running empty task does nothing
        pipeline.run("test");
        assertTrue(pipeline.runAsync("test").isDone());
    }

    @Test
    public void testAddAndExecutionOrder() throws Throwable {
        var log = new ArrayList<String>();
        var builder = TaskBuilders.<String>linked();

        builder.add((s, next) -> {
            log.add("A-" + s);
            next.run(s);
        });
        builder.add((s, next) -> {
            log.add("B-" + s);
            next.run(s);
        });
        builder.add((s, next) -> {
            log.add("C-" + s); // last in chain
        });

        var pipeline = builder.build();

        pipeline.run("X");

        assertEquals(List.of("A-X", "B-X", "C-X"), log);
    }

    @Test
    public void testPrependExecutionOrder() throws Throwable {
        var log = new ArrayList<String>();
        var builder = TaskBuilders.<String>linked();

        builder.add((s, next) -> {
            log.add("A-" + s);
        });
        builder.prepend((s, next) -> {
            log.add("P-" + s);
            next.run(s);
        });

        var pipeline = builder.build();

        pipeline.run("Y");

        // Prepend runs first
        assertEquals(List.of("P-Y", "A-Y"), log);
    }

    @Test
    public void testAddWhenPredicateTrue() throws Throwable {
        var log = new ArrayList<String>();
        Predicate<String> pred = s -> s.equals("run");

        var builder = TaskBuilders.<String>linked();

        builder.addWhen(pred, (s, next) -> {
            log.add("Cond-" + s);
            next.run(s);
        });
        builder.add((s, next) -> {
            log.add("Always-" + s);
        });

        var pipeline = builder.build();

        pipeline.run("run");
        pipeline.run("skip");

        assertEquals(List.of("Cond-run", "Always-run", "Always-skip"), log);
    }

    @Test
    public void testMapWhenWithTask() throws Throwable {
        var log = new ArrayList<String>();
        var builder = TaskBuilders.<String>linked();

        builder.mapWhen(s -> s.startsWith("go"), (SyncTask<String>) s -> log.add("Mapped-" + s));

        builder.add((s, next) -> {
            log.add("After-" + s);
        });

        var pipeline = builder.build();

        pipeline.run("go123");  // выполнится только Mapped-go123
        pipeline.run("nope");   // выполнится только After-nope

        assertEquals(List.of("Mapped-go123", "After-nope"), log);
    }

    @Test
    public void testAsyncTaskExecution() {
        var log = new ArrayList<String>();

        var builder = TaskBuilders.<String>linked();
        builder.addWhen(s -> s.equals("yes"), (s, n) -> {
            return CompletableFuture.runAsync(() -> log.add("Async-" + s));
        });

        var pipeline = builder.build();

        var fut = pipeline.runAsync("yes");
        fut.join();

        assertEquals(List.of("Async-yes"), log);
    }

    @Test
    public void testSyncTaskExecution() throws Throwable {
        var log = new ArrayList<String>();

        SyncTask<String> syncTask = s -> log.add("Sync-" + s);

        var builder = TaskBuilders.<String>linked();
        builder.mapWhen(s -> s.equals("run"), syncTask);

        var pipeline = builder.build();

        pipeline.run("run");
        pipeline.run("skip");

        assertEquals(List.of("Sync-run"), log);
    }

    @Test
    public void testExceptionPropagation() {
        var builder = TaskBuilders.<String>linked();

        builder.add((SyncTaskConsumer<String>) (s, next) -> {
            throw new IllegalStateException("fail");
        });

        var pipeline = builder.build();

        var ex = assertThrows(IllegalStateException.class, () -> pipeline.run("test"));
        assertEquals("fail", ex.getMessage());
    }

    @Test
    public void testRemoveAndClear() throws Throwable {
        TaskBuilder<String> builder = TaskBuilders.linked();

        var runA = new AtomicBoolean(false);
        var runB = new AtomicBoolean(false);

        builder.add((s, next) -> {
            runA.set(true);
        });
        builder.add((s, next) -> {
            runB.set(true);
        });

        builder.remove();

        Task<String> pipeline = builder.build();

        // Only first task remains
        assertFalse(runA.get());
        pipeline.run("X");
        assertTrue(runA.get());
        assertFalse(runB.get());

        builder.clear();

        pipeline = builder.build();

        // Pipeline empty, nothing happens, no exceptions
        pipeline.run("Y");
    }

    @Test
    public void testNestedAddWhenBranchesVariants() throws Throwable {
        var log = new ArrayList<String>();

        var builder = TaskBuilders.<String>linked();

        builder.addWhen(s -> s.startsWith("hi"), b -> {
            b.add((s, n) -> {
                log.add("Hi branch");
                n.run(s);
            });
            b.addWhen(s -> s.startsWith("hi, a"), b1 -> {
                b1.add((s, n) -> {
                    log.add("A branch");
                    n.run(s);
                });
                b1.addWhen(s -> s.endsWith("end"), b2 -> {
                    b2.add((s, n) -> {
                        log.add("End branch");
                        n.run(s);
                    });
                });
            });
        });

        builder.add((s, t) -> {
            log.add("Common branch");
            t.run(s);
        });

        builder.add((s, t) -> {
            log.add("Very last branch");
        });

        var task = builder.build();

        // 1) Полный проход — все ветки
        log.clear();
        task.run("hi, a end");
        assertEquals(List.of("Hi branch", "A branch", "End branch", "Common branch", "Very last branch"), log);

        // 2) Нет захода в вложенную ветку "hi, a"
        log.clear();
        task.run("hi, b end");
        assertEquals(List.of("Hi branch", "Common branch", "Very last branch"), log);

        // 3) Нет захода в ветку "end"
        log.clear();
        task.run("hi, a notnd");
        assertEquals(List.of("Hi branch", "A branch", "Common branch", "Very last branch"), log);

        // 4) Нет захода в ветку "hi" вообще
        log.clear();
        task.run("hello");
        assertEquals(List.of("Common branch", "Very last branch"), log);
    }
}
