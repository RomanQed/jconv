package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.juni.UniRunnable2;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a middleware-style consumer that operates on an input and delegates
 * to the next {@link Task} in the pipeline.
 *
 * @param <T> the type of input data
 */
public interface TaskConsumer<T> extends UniRunnable2<T, Task<T>> {

    /**
     * Executes this consumer synchronously with the given input and continuation task.
     *
     * @param t the input data to process
     * @param task the next task to invoke after this consumer
     * @throws Throwable if an error occurs during execution
     */
    @Override
    void run(T t, Task<T> task) throws Throwable;

    /**
     * Executes this consumer asynchronously with the given input and continuation task.
     *
     * @param t the first input argument
     * @param task the second input argument (continuation)
     * @return a {@link CompletableFuture} representing the asynchronous execution
     */
    @Override
    CompletableFuture<Void> runAsync(T t, Task<T> task);

    /**
     * Executes this consumer synchronously with the given input and continuation task,
     * rethrowing any checked exception as unchecked using {@link Exceptions#throwAny(Throwable)}.
     *
     * @param t the input data
     * @param task the next task in the pipeline
     */
    default void accept(T t, Task<T> task) {
        try {
            run(t, task);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }
}
