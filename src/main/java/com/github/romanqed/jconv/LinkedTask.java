package com.github.romanqed.jconv;

import java.util.concurrent.CompletableFuture;

/**
 * A composable {@link Task} implementation that wraps a {@link TaskConsumer} and delegates execution
 * to a downstream {@link Task}, forming a linked processing chain.
 *
 * @param <T> the type of input processed by this task
 */
public final class LinkedTask<T> implements Task<T> {
    final TaskConsumer<T> body;
    Task<T> next;

    /**
     * Constructs a new {@link LinkedTask} with the given consumer and an initially empty continuation.
     *
     * @param body the consumer representing this task's logic
     */
    @SuppressWarnings("unchecked")
    public LinkedTask(TaskConsumer<T> body) {
        this.body = body;
        this.next = Task.EMPTY;
    }

    /**
     * Sets the next {@link Task} to execute after this one.
     *
     * @param next the next task
     */
    public void setNext(Task<T> next) {
        this.next = next;
    }

    /**
     * Resets the next task to an empty no-op.
     */
    @SuppressWarnings("unchecked")
    public void resetNext() {
        this.next = Task.EMPTY;
    }

    /**
     * Executes this task by invoking the underlying {@link TaskConsumer}
     * with the given input and the continuation task.
     *
     * @param t the input
     * @throws Throwable if any error occurs during execution
     */
    @Override
    public void run(T t) throws Throwable {
        body.run(t, next);
    }

    @Override
    public CompletableFuture<Void> runAsync(T t) {
        return body.runAsync(t, next);
    }

    @Override
    public boolean isAsync() {
        return body.isAsync() && next.isAsync();
    }

    @Override
    public boolean isSync() {
        return body.isSync() && next.isSync();
    }

    @Override
    public boolean isUni() {
        return body.isUni() && next.isUni();
    }
}
