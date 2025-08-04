package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface SyncTaskConsumer<T> extends TaskConsumer<T> {

    /**
     *
     * @param t the first input argument
     * @param task the second input argument
     * @return
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
     *
     * @return
     */
    @Override
    default boolean isSync() {
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
