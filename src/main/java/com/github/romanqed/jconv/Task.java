package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.juni.UniRunnable1;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a unit of work that consumes a single input argument and may be executed
 * either synchronously or asynchronously.
 *
 * <p>Implementations define how the task is executed and may indicate whether they are
 * natively synchronous, asynchronous, or both via {@link #isSync()}, {@link #isAsync()},
 * and {@link #isUni()}.</p>
 *
 * @param <T> the input type accepted by this task
 */
public interface Task<T> extends UniRunnable1<T> {

    /**
     * A shared no-op {@link Task} instance that performs no action and completes immediately.
     * <p>
     * This task reports itself as both synchronous and asynchronous, making it compatible
     * with all execution modes.
     */
    @SuppressWarnings("rawtypes")
    Task EMPTY = new Task() {

        @Override
        public void run(Object o) {
            // Do nothing
        }

        @Override
        public CompletableFuture<Void> runAsync(Object o) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public boolean isSync() {
            return true;
        }

        @Override
        public boolean isAsync() {
            return true;
        }

        @Override
        public boolean isUni() {
            return true;
        }
    };

    /**
     * Returns a shared no-op task instance.
     *
     * @param <T> the input type
     * @return an empty {@link Task} that performs no operation
     */
    @SuppressWarnings("unchecked")
    static <T> Task<T> empty() {
        return EMPTY;
    }

    /**
     * Executes this task synchronously with the given input.
     *
     * @param t the input argument to process
     * @throws Throwable if an error occurs during execution
     */
    @Override
    void run(T t) throws Throwable;

    /**
     * Executes this task asynchronously with the given input.
     *
     * @param t the input argument
     * @return a {@link CompletableFuture} representing the asynchronous execution
     */
    @Override
    CompletableFuture<Void> runAsync(T t);

    /**
     * Accepts the input and executes this task synchronously, rethrowing any checked exception
     * as an unchecked one using {@link Exceptions#throwAny(Throwable)}.
     *
     * @param t the input argument
     */
    default void accept(T t) {
        try {
            run(t);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }
}
