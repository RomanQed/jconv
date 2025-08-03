package com.github.romanqed.jconv;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

final class TaskCondConsumer<T> implements TaskConsumer<T> {
    final Predicate<T> predicate;
    final Task<T> when;

    TaskCondConsumer(Predicate<T> predicate, Task<T> when) {
        this.predicate = predicate;
        this.when = when;
    }

    @Override
    public void run(T t, Task<T> task) throws Throwable {
        if (predicate.test(t)) {
            when.run(t);
        } else {
            task.run(t);
        }
    }

    @Override
    public CompletableFuture<Void> runAsync(T t, Task<T> task) {
        try {
            if (predicate.test(t)) {
                return when.runAsync(t);
            }
            return task.runAsync(t);
        } catch (Throwable e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public boolean isSync() {
        return when.isSync();
    }

    @Override
    public boolean isAsync() {
        return when.isAsync();
    }

    @Override
    public boolean isUni() {
        return when.isUni();
    }
}
