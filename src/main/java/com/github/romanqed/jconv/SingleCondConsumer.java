package com.github.romanqed.jconv;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

final class SingleCondConsumer<T> implements TaskConsumer<T> {
    final Predicate<T> predicate;

    SingleCondConsumer(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void run(T t, Task<T> task) throws Throwable {
        if (!predicate.test(t)) {
            task.run(t);
        }
    }

    @Override
    public CompletableFuture<Void> runAsync(T t, Task<T> task) {
        try {
            if (!predicate.test(t)) {
                return task.runAsync(t);
            }
            return CompletableFuture.completedFuture(null);
        } catch (Throwable e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public boolean isUni() {
        return true;
    }
}
