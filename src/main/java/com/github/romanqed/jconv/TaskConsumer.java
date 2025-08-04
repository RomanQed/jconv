package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.juni.UniRunnable2;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @param <T>
 */
public interface TaskConsumer<T> extends UniRunnable2<T, Task<T>> {

    /**
     *
     * @param t first function parameter
     * @param task second function parameter
     * @throws Throwable
     */
    @Override
    void run(T t, Task<T> task) throws Throwable;

    /**
     *
     * @param t the first input argument
     * @param tTask the second input argument
     * @return
     */
    @Override
    CompletableFuture<Void> runAsync(T t, Task<T> tTask);

    /**
     *
     * @param t
     * @param task
     */
    default void accept(T t, Task<T> task) {
        try {
            run(t, task);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }
}
