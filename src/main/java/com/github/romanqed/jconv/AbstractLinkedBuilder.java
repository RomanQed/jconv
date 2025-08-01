package com.github.romanqed.jconv;

import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractLinkedBuilder<T> implements PipelineBuilder<T> {
    protected final Deque<LinkedTask<T>> deque;
    protected LinkedTask<T> last;

    protected AbstractLinkedBuilder(Deque<LinkedTask<T>> deque) {
        this.deque = deque;
        this.last = null;
    }

    protected abstract <V> PipelineBuilder<V> newBuilder();

    protected abstract <V> AbstractLinkedBuilder<V> newInternalBuilder();

    protected void addFirst(LinkedTask<T> task) {
        if (!deque.isEmpty()) {
            task.next = deque.peekLast();
        }
        deque.addLast(task);
    }

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
