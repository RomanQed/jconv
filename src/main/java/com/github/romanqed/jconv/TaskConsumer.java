package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.juni.UniRunnable2;

import java.util.concurrent.CompletableFuture;

public interface TaskConsumer<T> extends UniRunnable2<T, Task<T>> {

    @Override
    void run(T t, Task<T> task) throws Throwable;

    @Override
    CompletableFuture<Void> runAsync(T t, Task<T> tTask);

    default void accept(T t, Task<T> task) {
        try {
            run(t, task);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }
}
