package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Function0;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Represents a function that does not accept parameters and returns a value.
 * Implements the {@link Supplier} interface for direct compatibility
 * with the {@link java.util.concurrent.CompletableFuture}.
 *
 * <p>This is a
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html">functional interface</a>
 * whose functional method is {@link #invoke()}.
 *
 * @param <T> the type of the return value
 */
@FunctionalInterface
public interface AsyncFunction0<T> extends Function0<T>, Supplier<T> {

    /**
     * Creates {@link AsyncFunction0} instance that wraps {@link Function0} instance
     * and using the passed {@link ExecutorService} instance.
     *
     * @param function {@link Function0} instance
     * @param service  {@link ExecutorService} instance, must be non-null
     * @param <T>      return type of the function
     * @return {@link AsyncFunction0} instance
     * @throws NullPointerException if service will be null
     */
    static <T> AsyncFunction0<T> of(Function0<T> function, ExecutorService service) {
        Objects.requireNonNull(service);
        return new AsyncFunction0<>() {
            @Override
            public T invoke() throws Throwable {
                return function.invoke();
            }

            @Override
            public T get() {
                try {
                    return function.invoke();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Future<T> submit() {
                return service.submit(() -> {
                    try {
                        return function.invoke();
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
    default Future<T> submit() {
        throw new UnsupportedOperationException();
    }

    @Override
    default T get() {
        try {
            return this.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
