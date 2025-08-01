package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Runnable1;

import java.util.Objects;

/**
 * A functional task that accepts an input and may throw an exception during execution.
 *
 * @param <T> the type of the input
 */
@FunctionalInterface
public interface Task<T> extends Runnable1<T> {

    /**
     * A no-op task instance.
     */
    @SuppressWarnings("rawtypes")
    Task EMPTY = v -> {};

    /**
     * Returns a no-op task.
     *
     * @param <T> the input type
     * @return an empty task
     */
    @SuppressWarnings("unchecked")
    static <T> Task<T> empty() {
        return EMPTY;
    }

    /**
     * Executes this task with the specified input.
     *
     * @param t the input
     * @throws Throwable if any error occurs
     */
    @Override
    void run(T t) throws Throwable;

    /**
     * Executes the task and rethrows any checked exception as unchecked.
     *
     * @param t the input
     */
    default void accept(T t) {
        try {
            run(t);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }

    /**
     * Chains this task with another, executing them in sequence.
     *
     * @param func the task to execute after this one
     * @return a composed task
     */
    default Task<T> andThen(Task<T> func) {
        Objects.requireNonNull(func);
        return t -> {
            run(t);
            func.run(t);
        };
    }
}
