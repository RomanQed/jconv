package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface AsyncTaskConsumer<T> extends TaskConsumer<T> {

    /**
     *
     * @param t first function parameter
     * @param task second function parameter
     * @throws Throwable
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
     *
     * @return
     */
    @Override
    default boolean isAsync() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    default boolean isUni() {
        return false;
    }
}
