package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Runnable2;

import java.util.Objects;

/**
 * A two-argument consumer operating on an input and a continuation {@link Task},
 * typically used in pipeline execution.
 *
 * @param <T> the input type
 */
@FunctionalInterface
public interface TaskConsumer<T> extends Runnable2<T, Task<T>> {

    /**
     * Executes this consumer with the specified input and continuation task.
     *
     * @param t the input
     * @param task the next task to execute
     * @throws Throwable if any error occurs
     */
    @Override
    void run(T t, Task<T> task) throws Throwable;

    /**
     * Executes the consumer and rethrows any checked exception as unchecked.
     *
     * @param t the input
     * @param task the continuation task
     */
    default void accept(T t, Task<T> task) {
        try {
            run(t, task);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }

    /**
     * Chains this consumer with another, executing them in sequence.
     *
     * @param func the consumer to execute after this one
     * @return a composed consumer
     */
    default TaskConsumer<T> andThen(TaskConsumer<T> func) {
        Objects.requireNonNull(func);
        return (t1, t2) -> {
            run(t1, t2);
            func.run(t1, t2);
        };
    }
}
