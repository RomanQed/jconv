package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Represents a function that accepts a single parameter and does not return a value.
 * Implements the {@link Consumer} interface for direct compatibility
 * with the {@link java.util.concurrent.CompletableFuture}.
 *
 * <p>This is a
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html">functional interface</a>
 * whose functional method is {@link #run(Object)}.
 *
 * @param <T> the type of the function parameter
 */
@FunctionalInterface
public interface AsyncRunnable1<T> extends Runnable1<T>, Consumer<T> {

    /**
     * Creates {@link AsyncRunnable1} instance that wraps {@link Runnable1} instance
     * and using the passed {@link ExecutorService} instance.
     *
     * @param runnable {@link Runnable1} instance
     * @param service  {@link ExecutorService} instance, must be non-null
     * @param <T>      argument type of the function
     * @return {@link AsyncRunnable1} instance
     * @throws NullPointerException if service will be null
     */
    static <T> AsyncRunnable1<T> of(Runnable1<T> runnable, ExecutorService service) {
        Objects.requireNonNull(service);
        return new AsyncRunnable1<>() {
            @Override
            public void run(T t) throws Throwable {
                runnable.run(t);
            }

            @Override
            public void accept(T t) {
                try {
                    runnable.run(t);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Future<?> submit(T t) {
                return service.submit(() -> {
                    try {
                        runnable.run(t);
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
    default Future<?> submit(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void accept(T t) {
        try {
            this.run(t);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
