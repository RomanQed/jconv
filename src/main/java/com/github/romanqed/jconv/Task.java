package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.juni.UniRunnable1;

import java.util.concurrent.CompletableFuture;

public interface Task<T> extends UniRunnable1<T> {

    @SuppressWarnings("rawtypes")
    Task EMPTY = new Task() {

        @Override
        public void run(Object o) {
            // Do nothing
        }

        @Override
        public CompletableFuture<Void> runAsync(Object o) {
            return CompletableFuture.completedFuture(null);
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
    };

    @SuppressWarnings("unchecked")
    static <T> Task<T> empty() {
        return EMPTY;
    }

    @Override
    void run(T t) throws Throwable;

    @Override
    CompletableFuture<Void> runAsync(T t);

    default void accept(T t) {
        try {
            run(t);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }
}
