package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Function1;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Represents a function that takes one parameter and returns a value.
 * Implements the {@link Function} interface for direct compatibility
 * with the {@link java.util.concurrent.CompletableFuture}.
 *
 * <p>This is a
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html">functional interface</a>
 * whose functional method is {@link #invoke(Object)}.
 *
 * @param <T> the type of the function parameter
 * @param <R> the type of the return value
 */
@FunctionalInterface
public interface AsyncFunction1<T, R> extends Function1<T, R>, Function<T, R> {

    /**
     * Creates {@link AsyncFunction1} instance that wraps {@link Function1} instance
     * and using the passed {@link ExecutorService} instance.
     *
     * @param function {@link Function1} instance
     * @param service  {@link ExecutorService} instance, must be non-null
     * @param <T>      argument type of the function
     * @param <R>      return type of the function
     * @return {@link AsyncFunction1} instance
     * @throws NullPointerException if service will be null
     */
    static <T, R> AsyncFunction1<T, R> of(Function1<T, R> function, ExecutorService service) {
        Objects.requireNonNull(service);
        return new AsyncFunction1<>() {
            @Override
            public R invoke(T t) throws Throwable {
                return function.invoke(t);
            }

            @Override
            public R apply(T t) {
                try {
                    return function.invoke(t);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Future<R> submit(T t) {
                return service.submit(() -> {
                    try {
                        return function.invoke(t);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        };
    }

    /**
     * Executes the function body using the associated {@link ExecutorService} instance.
     *
     * @return {@link Future} instance
     * @throws UnsupportedOperationException if executor is not set
     */
    default Future<R> submit(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default R apply(T t) {
        try {
            return this.invoke(t);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
