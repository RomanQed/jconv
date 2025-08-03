package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SyncTaskConsumer<T> extends TaskConsumer<T> {

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

    @Override
    default boolean isSync() {
        return true;
    }

    @Override
    default boolean isUni() {
        return false;
    }
}
