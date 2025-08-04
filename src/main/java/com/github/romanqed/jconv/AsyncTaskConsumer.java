package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

/**
 * A {@link TaskConsumer} that natively supports asynchronous execution.
 *
 * <p>The {@link #run(Object, Task)} method blocks on the result of {@link #runAsync(Object, Task)}.</p>
 *
 * @param <T> the input type
 */
@FunctionalInterface
public interface AsyncTaskConsumer<T> extends TaskConsumer<T> {

    /**
     * Executes this consumer synchronously by blocking on the result of its asynchronous {@link #runAsync(Object, Task)} method.
     * <p>
     * If the asynchronous execution throws an exception, it is unwrapped and rethrown.
     * If interrupted, the thread is re-interrupted and the exception is propagated.
     *
     * @param t     the first input argument
     * @param task  the continuation task to invoke
     * @throws Throwable if the underlying asynchronous execution fails
     */
    @Override
    default void run(T t, Task<T> task) throws Throwable {
        try {
            runAsync(t, task).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * Indicates that this consumer is asynchronous by nature.
     *
     * @return {@code true}
     */
    @Override
    default boolean isAsync() {
        return true;
    }

    /**
     * Indicates that this consumer is not a unified consumer (as it only natively supports async execution).
     *
     * @return {@code false}
     */
    @Override
    default boolean isUni() {
        return false;
    }
}
