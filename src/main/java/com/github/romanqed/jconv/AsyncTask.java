package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface AsyncTask<T> extends Task<T> {

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

    @Override
    default boolean isAsync() {
        return true;
    }

    @Override
    default boolean isUni() {
        return false;
    }
}
