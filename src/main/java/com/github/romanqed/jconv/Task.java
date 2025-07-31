package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Runnable1;

import java.util.Objects;

@FunctionalInterface
public interface Task<T> extends Runnable1<T> {
    @SuppressWarnings("rawtypes")
    Task EMPTY = v -> {};

    @SuppressWarnings("unchecked")
    static <T> Task<T> empty() {
        return EMPTY;
    }

    @Override
    void run(T t) throws Throwable;

    default void accept(T t) {
        try {
            run(t);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
    }

    default Task<T> andThen(Task<T> func) {
        Objects.requireNonNull(func);
        return t -> {
            run(t);
            func.run(t);
        };
    }
}
