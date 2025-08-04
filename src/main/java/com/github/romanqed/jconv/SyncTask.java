package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface SyncTask<T> extends Task<T> {

    /**
     *
     * @param t the input argument
     * @return
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
