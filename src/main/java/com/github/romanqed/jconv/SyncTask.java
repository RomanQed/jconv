package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link Task} that is designed to be executed synchronously.
 *
 * <p>Its {@link #runAsync(Object)} method wraps synchronous execution inside a {@link CompletableFuture}.</p>
 *
 * @param <T> the input type
 */
@FunctionalInterface
public interface SyncTask<T> extends Task<T> {

    /**
     * Executes this task asynchronously by wrapping its synchronous {@link #run(Object)} method
     * in a {@link CompletableFuture}. Any thrown exception is rethrown as unchecked.
     *
     * @param t the input argument
     * @return a {@link CompletableFuture} representing the completion of the task
     */
    @Override
    default CompletableFuture<Void> runAsync(T t) {
        return CompletableFuture.runAsync(() -> {
            try {
                run(t);
            } catch (Throwable e) {
                Exceptions.throwAny(e);
            }
        });
    }

    /**
     * Indicates that this task is synchronous by nature.
     *
     * @return {@code true}
     */
    @Override
    default boolean isSync() {
        return true;
    }

    /**
     * Indicates that this task is not a unified task (as it only natively supports sync execution).
     *
     * @return {@code false}
     */
    @Override
    default boolean isUni() {
        return false;
    }
}
