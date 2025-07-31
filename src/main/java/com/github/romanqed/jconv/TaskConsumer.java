package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Runnable2;

import java.util.Objects;

@FunctionalInterface
public interface TaskConsumer<T> extends Runnable2<T, Task<T>> {

    @Override
    void run(T t, Task<T> task) throws Throwable;

    default void accept(T t, Task<T> task) {
        try {
            run(t, task);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }

    default TaskConsumer<T> andThen(TaskConsumer<T> func) {
        Objects.requireNonNull(func);
        return (t1, t2) -> {
            run(t1, t2);
            func.run(t1, t2);
        };
    }
}
