package com.github.romanqed.jconv;

import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Abstract base implementation of {@link PipelineBuilder} based on a linked task structure.
 * Maintains an internal {@link Deque} of {@link LinkedTask} elements to form the execution chain.
 *
 * <p>This class provides common logic for building and modifying task pipelines in a composable and conditional manner.</p>
 *
 * @param <T> the type of input processed by the pipeline
 */
public abstract class AbstractLinkedConfigurer<T, R extends PipelineConfigurer<T>> implements PipelineConfigurer<T> {

    /**
     * The deque holding the pipeline's linked tasks in execution order (LIFO).
     */
    protected final Deque<LinkedTask<T>> deque;

    /**
     * The most recent task inserted as part of a multi-step conditional configuration.
     */
    protected LinkedTask<T> last;

    /**
     * Constructs the builder with a user-supplied {@link Deque} storage.
     *
     * @param deque the underlying deque used to build the pipeline
     */
    protected AbstractLinkedConfigurer(Deque<LinkedTask<T>> deque) {
        this.deque = deque;
        this.last = null;
    }

    /**
     * Creates a new builder for assembling a nested pipeline with the same abstraction level.
     *
     * @param <V> the type of input for the nested builder
     * @return a new {@link PipelineBuilder} instance
     */
    protected abstract <V> PipelineBuilder<V> newBuilder();

    /**
     * Creates a new internal builder for conditional configuration.
     * This variant allows access to internal task structures.
     *
     * @param <V> the type of input for the nested builder
     * @return a new {@link AbstractLinkedConfigurer} instance
     */
    protected abstract <V> AbstractLinkedConfigurer<V, ? extends PipelineConfigurer<V>> newConfigurer();

    /**
     * Adds a task to the front of the pipeline.
     * Typically used for {@link #prepend(TaskConsumer)} operations.
     *
     * @param task the task to add
     */
    protected void addFirst(LinkedTask<T> task) {
        if (!deque.isEmpty()) {
            task.next = deque.peekLast();
        }
        deque.addLast(task);
    }

    /**
     * Adds a task to the end of the pipeline.
     * Typically used for {@link #add(TaskConsumer)} or conditional insertions.
     *
     * @param task the task to add
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
