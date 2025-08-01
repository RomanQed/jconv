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
public abstract class AbstractLinkedBuilder<T> implements PipelineBuilder<T> {
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
    protected AbstractLinkedBuilder(Deque<LinkedTask<T>> deque) {
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
     * @return a new {@link AbstractLinkedBuilder} instance
     */
    protected abstract <V> AbstractLinkedBuilder<V> newInternalBuilder();

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
    public PipelineBuilder<T> add(TaskConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        addLast(new LinkedTask<>(consumer));
        return this;
    }

    @Override
    public PipelineBuilder<T> prepend(TaskConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        addFirst(new LinkedTask<>(consumer));
        return this;
    }

    @Override
    public PipelineBuilder<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(task);
        addLast(new LinkedTask<>(new CondConsumer<>(predicate, task)));
        return this;
    }

    @Override
    public PipelineBuilder<T> addWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);
        var builder = this.<T>newInternalBuilder();
        consumer.accept(builder);
        var deque = builder.deque;
        if (deque.isEmpty()) {
            addLast(new LinkedTask<>(new SingleCondConsumer<>(predicate)));
            return this;
        }
        var task = new TaskCondConsumer<>(predicate, deque.peekLast());
        addLast(new LinkedTask<>(task));
        last = builder.last == null ? deque.peek() : builder.last;
        return this;
    }

    @Override
    public PipelineBuilder<T> mapWhen(Predicate<T> predicate, Task<T> task) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(task);
        addLast(new LinkedTask<>(new TaskCondConsumer<>(predicate, task)));
        return this;
    }

    @Override
    public PipelineBuilder<T> mapWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);
        var builder = this.<T>newBuilder();
        consumer.accept(builder);
        var task = builder.build();
        addLast(new LinkedTask<>(new TaskCondConsumer<>(predicate, task)));
        return this;
    }

    @Override
    public PipelineBuilder<T> remove() {
        if (deque.isEmpty()) {
            return this;
        }
        deque.pop();
        if (deque.isEmpty()) {
            return this;
        }
        deque.peek().resetNext();
        return this;
    }

    @Override
    public PipelineBuilder<T> clear() {
        deque.clear();
        return this;
    }
}
