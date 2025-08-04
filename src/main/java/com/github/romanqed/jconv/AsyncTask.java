package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface AsyncTask<T> extends Task<T> {

    /**
     *
     * @param t function parameter
     * @throws Throwable
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
