package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link TaskConsumer} that is designed to be executed synchronously.
 *
 * <p>Its {@link #runAsync(Object, Task)} method wraps synchronous execution inside a {@link CompletableFuture}.</p>
 *
 * @param <T> the input type
 */
@FunctionalInterface
public interface SyncTaskConsumer<T> extends TaskConsumer<T> {

    /**
     * Executes this consumer asynchronously by wrapping its synchronous {@link #run(Object, Task)} method
     * in a {@link CompletableFuture}. Any thrown exception is rethrown as unchecked.
     *
     * @param t the first input argument
     * @param task the continuation task to invoke
     * @return a {@link CompletableFuture} representing the asynchronous execution
     */
    @Override
    default CompletableFuture<Void> runAsync(T t, Task<T> task) {
        return CompletableFuture.runAsync(() -> {
            try {
                run(t, task);
            } catch (Throwable e) {
                Exceptions.throwAny(e);
            }
        });
    }

    /**
     * Indicates that this consumer is synchronous by nature.
     *
     * @return {@code true}
     */
    @Override
    default boolean isSync() {
        return true;
    }

    /**
     * Indicates that this consumer is not a unified consumer (as it only natively supports sync execution).
     *
     * @return {@code false}
     */
    @Override
    default boolean isUni() {
        return false;
    }
}
