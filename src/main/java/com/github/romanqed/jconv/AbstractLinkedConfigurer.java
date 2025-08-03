package com.github.romanqed.jconv;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Abstract base implementation of {@link TaskConfigurer} based on a linked task structure.
 * <p>
 * This class provides a common foundation for building configurable processing pipelines using a
 * {@link Deque}-based structure of {@link LinkedTask} elements, supporting both unconditional
 * and conditional task addition.
 *
 * @param <T> the type of data consumed by the pipeline
 * @param <R> the self-referential type for fluent configuration
 */
public abstract class AbstractLinkedConfigurer<T, R extends TaskConfigurer<T>> implements TaskConfigurer<T> {

    /**
     * Task chain container, representing a linked structure (logical FIFO, physical LIFO).
     * The deque head holds the most recently added task.
     */
    protected final Deque<LinkedTask<T>> deque;

    protected LinkedTask<T> last;

    protected List<LinkedTask<T>> lastList;

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
     * @return a new {@link TaskBuilder} instance
     */
    protected abstract <V> TaskBuilder<V> newBuilder();

    /**
     * Creates a new configurer for internal conditional branches.
     * Unlike {@code newBuilder()}, this method preserves access to the internal {@code Deque}.
     *
     * @param <V> the input type of the nested configurer
     * @return a new {@link AbstractLinkedConfigurer} instance
     */
    protected abstract <V> AbstractLinkedConfigurer<V, ? extends TaskConfigurer<V>> newConfigurer();

    /**
     * Inserts a task at the beginning (tail) of the chain.
     * Used primarily for {@link #prepend(TaskConsumer)} operations.
     *
     * @param task the task to insert
     */
    protected void addFirst(LinkedTask<T> task) {
        if (!deque.isEmpty()) {
            task.setNext(deque.peekLast());
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
            deque.peek().setNext(task);
            if (last != null) {
                last.setNext(task);
                last = null;
            } else if (lastList != null) {
                for (var item : lastList) {
                    item.setNext(task);
                }
                lastList = null;
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
    public R addWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer) {
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
        if (configurer.lastList != null) {
            lastList = configurer.lastList;
            lastList.add(deque.peek());
        } else if (configurer.last != null) {
            lastList = new LinkedList<>();
            lastList.add(configurer.last);
            lastList.add(deque.peek());
        } else {
            last = deque.peek();
        }
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
    public R mapWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer) {
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
