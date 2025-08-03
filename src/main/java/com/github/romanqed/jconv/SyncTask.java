package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SyncTask<T> extends Task<T> {

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

    @Override
    default boolean isSync() {
        return true;
    }

    @Override
    default boolean isUni() {
        return false;
    }
}
