package com.github.romanqed.jconv;

import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Abstract base implementation of {@link PipelineConfigurer} based on a linked task structure.
 * <p>
 * This class provides a common foundation for building configurable processing pipelines using a
 * {@link Deque}-based structure of {@link LinkedTask} elements, supporting both unconditional
 * and conditional task addition.
 *
 * @param <T> the type of data consumed by the pipeline
 * @param <R> the self-referential type for fluent configuration
 */
public abstract class AbstractLinkedConfigurer<T, R extends PipelineConfigurer<T>> implements PipelineConfigurer<T> {

    /**
     * Task chain container, representing a linked structure (logical FIFO, physical LIFO).
     * The deque head holds the most recently added task.
     */
    protected final Deque<LinkedTask<T>> deque;

    /**
     * Internal reference to the last task of a nested conditional block.
     * Used to re-link the conditional segment back into the main chain.
     */
    protected LinkedTask<T> last;

    /**
     * Constructs a configurer with the specified deque to be used as internal task storage.
     *
     * @param deque the task container to use for pipeline construction
     */
    protected AbstractLinkedConfigurer(Deque<LinkedTask<T>> deque) {
        this.deque = deque;
        this.last = null;
    }

    /**
     * Creates a new builder intended for full pipeline construction.
     * Used by {@code mapWhen(...)} to create executable subpipelines.
     *
     * @param <V> the input type of the subpipeline
     * @return a new {@link PipelineBuilder} instance
     */
    protected abstract <V> PipelineBuilder<V> newBuilder();

    /**
     * Creates a new configurer for internal conditional branches.
     * Unlike {@code newBuilder()}, this method preserves access to the internal {@code Deque}.
     *
     * @param <V> the input type of the nested configurer
     * @return a new {@link AbstractLinkedConfigurer} instance
     */
    protected abstract <V> AbstractLinkedConfigurer<V, ? extends PipelineConfigurer<V>> newConfigurer();

    /**
     * Inserts a task at the beginning (tail) of the chain.
     * Used primarily for {@link #prepend(TaskConsumer)} operations.
     *
     * @param task the task to insert
     */
    protected void addFirst(LinkedTask<T> task) {
        if (!deque.isEmpty()) {
            task.next = deque.peekLast();
        }
        deque.addLast(task);
    }

    /**
     * Inserts a task at the end (head) of the chain.
     * Used for {@link #add(TaskConsumer)} or conditional segment insertion.
     *
     * @param task the task to insert
     */
    protected void addLast(LinkedTask<T> task) {
        if (!deque.isEmpty()) {
            deque.peek().next = task;
            if (last != null) {
                last.next = task;
                last = null;
            }
        }
        deque.push(task);
    }

    @Override
    @SuppressWarnings("unchecked")
    public R add(TaskConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        addLast(new LinkedTask<>(consumer));
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R prepend(TaskConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        addFirst(new LinkedTask<>(consumer));
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R addWhen(Predicate<T> predicate, TaskConsumer<T> task) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(task);
        addLast(new LinkedTask<>(new CondConsumer<>(predicate, task)));
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R addWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);
        var configurer = this.<T>newConfigurer();
        consumer.accept(configurer);
        var deque = configurer.deque;
        if (deque.isEmpty()) {
            addLast(new LinkedTask<>(new SingleCondConsumer<>(predicate)));
            return (R) this;
        }
        var task = new TaskCondConsumer<>(predicate, deque.peekLast());
        addLast(new LinkedTask<>(task));
        last = configurer.last == null ? deque.peek() : configurer.last;
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R mapWhen(Predicate<T> predicate, Task<T> task) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(task);
        addLast(new LinkedTask<>(new TaskCondConsumer<>(predicate, task)));
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R mapWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);
        var builder = this.<T>newBuilder();
        consumer.accept(builder);
        var task = builder.build();
        addLast(new LinkedTask<>(new TaskCondConsumer<>(predicate, task)));
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R remove() {
        if (deque.isEmpty()) {
            return (R) this;
        }
        deque.pop();
        if (deque.isEmpty()) {
            return (R) this;
        }
        deque.peek().resetNext();
        return (R) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R clear() {
        deque.clear();
        return (R) this;
    }
}
