package com.github.romanqed.jconv;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface AsyncTaskConsumer<T> extends TaskConsumer<T> {

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

    @Override
    default boolean isSync() {
        return false;
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
