import com.github.romanqed.jconv.LinkedPipelineBuilder;
import com.github.romanqed.jconv.PipelineBuilder;
import com.github.romanqed.jconv.Task;
import com.github.romanqed.jfunc.Runnable2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class PipelineTest extends Assertions {
    public void testBuilder(PipelineBuilder<Context> builder) throws Throwable {
        var task = builder
                .add((context, next) -> {
                    context.value += 1;
                    next.run(context);
                })
                .add((context, next) -> {
                    context.value += 5;
                    next.run(context);
                })
                .clear()
                .add((context, next) -> {
                    context.value = 10;
                    next.run(context);
                })
                .add((context, next) -> context.value += 10)
                .remove()
                .build();
        var context = new Context();
        task.run(context);
        assertEquals(10, context.value);
    }

    @Test
    public void testLinkedBuilder() throws Throwable {
        testBuilder(new LinkedPipelineBuilder<>());
    }

    @Test
    public void testLinkedExceptionHandling() {
        testExceptionHandling(new LinkedPipelineBuilder<>());
    }

    public void testExceptionHandling(PipelineBuilder<Context> builder) {
        final var exception = new Exception();
        var task = builder
                .add((context, next) -> {
                    try {
                        next.run(context);
                    } catch (Exception e) {
                        context.exception = e;
                        throw e;
                    }
                })
                .add((context, next) -> {
                    throw exception;
                })
                .build();
        assertAll(
                () -> {
                    var context = new Context();
                    Exception e = null;
                    try {
                        task.run(context);
                    } catch (Exception e1) {
                        e = e1;
                    }
                    assertEquals(exception, e);
                    assertEquals(exception, context.exception);
                },
                () -> {
                    var context = new Context();
                    RuntimeException e = null;
                    try {
                        task.accept(context);
                    } catch (RuntimeException e1) {
                        e = e1;
                    }
                    assertNotNull(e);
                    assertEquals(exception, e.getCause());
                    assertEquals(exception, context.exception);
                }
        );
    }

    public void testShortCircuiting(PipelineBuilder<Context> builder) {
        var lambda = (Runnable2<Context, Task<Context>>) (context, next) -> {
            if (context.value > 0) {
                context.value -= 1;
                next.run(context);
            }
        };
        for (var i = 0; i < 10; ++i) {
            builder.add(lambda);
        }
        var task = builder.build();
        var context = new Context();
        assertAll(
                () -> {
                    context.value = 0;
                    task.run(context);
                    assertEquals(0, context.value);
                },
                () -> {
                    context.value = -10;
                    task.run(context);
                    assertEquals(-10, context.value);
                },
                () -> {
                    context.value = 2;
                    task.run(context);
                    assertEquals(0, context.value);
                },
                () -> {
                    context.value = 15;
                    task.run(context);
                    assertEquals(5, context.value);
                }
        );
    }

    @Test
    public void testLinkedShortCircuiting() {
        testShortCircuiting(new LinkedPipelineBuilder<>());
    }

    static final class Context {
        Integer value = 0;
        Exception exception;
    }
}
