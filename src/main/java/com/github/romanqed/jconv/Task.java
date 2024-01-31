package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * An interface represents a universal task that supports execution in both
 * unchecked and checked exceptions styles.
 *
 * <p>This is a
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html">functional interface</a>
 * whose functional method is {@link #run(Object)}.
 *
 * @param <T> the type of the function parameter
 */
@FunctionalInterface
public interface Task<T> extends Runnable1<T>, Consumer<T> {
    Task<?> EMPTY = new Task<>() {
        @Override
        public void run(Object o) {
        }

        @Override
        public void accept(Object o) {
        }
    };

    /**
     * Returns empty {@link Task} instance.
     *
     * @param <T> argument type
     * @return {@link Task} instance
     */
    @SuppressWarnings("unchecked")
    static <T> Task<T> empty() {
        return (Task<T>) EMPTY;
    }

    /**
     * Creates a combined {@link Task} containing the calls of
     * the passed tasks inside in the specified order.
     *
     * @param first  the task that will be executed first, must be non-null
     * @param second the task that will be executed second, must be non-null
     * @param <T>    type of tasks parameter
     * @return a composed {@link Task}
     * @throws NullPointerException if first or second task is null
     */
    static <T> Task<T> combine(Task<T> first, Task<T> second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return new Task<>() {
            @Override
            public void run(T t) throws Throwable {
                first.run(t);
                second.run(t);
            }

            @Override
            public void accept(T t) {
                try {
                    first.run(t);
                    second.run(t);
                } catch (Error | RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Creates a combined {@link Task} containing first a call to this task,
     * and then a call to the specified task.
     *
     * @param func the task that will be executed after this task
     * @return a composed {@link Runnable1}
     * @throws NullPointerException if passed task is null
     */
    default Task<T> andThen(Task<T> func) {
        return Task.combine(this, func);
    }

    default void accept(T t) {
        try {
            this.run(t);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
