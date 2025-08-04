package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

/**
 * A {@link Task} that natively supports asynchronous execution.
 *
 * <p>Its synchronous method {@link #run(Object)} is implemented by blocking on the result of {@link #runAsync(Object)}.</p>
 *
 * @param <T> the input type
 */
@FunctionalInterface
public interface AsyncTask<T> extends Task<T> {

    /**
     * Executes this task synchronously by blocking on the result of its asynchronous {@link #runAsync(Object)} method.
     * <p>
     * If the asynchronous execution throws an exception, it is unwrapped and rethrown.
     * If interrupted, the thread is re-interrupted and the exception is propagated.
     *
     * @param t the input argument
     * @throws Throwable if the asynchronous execution fails
     */
    @Override
    default void run(T t) throws Throwable {
        try {
            runAsync(t).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * Indicates that this task is asynchronous by nature.
     *
     * @return {@code true}
     */
    @Override
    default boolean isAsync() {
        return true;
    }

    /**
     * Indicates that this task is not a unified task (as it only natively supports async execution).
     *
     * @return {@code false}
     */
    @Override
    default boolean isUni() {
        return false;
    }
}
