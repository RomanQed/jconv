package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * A pipeline builder that support {@link java.util.concurrent.ExecutorService}.
 * Pipeline builder that supports ExecutorService. Inside the pipeline,
 * {@link AsyncRunnable1#submit} call will be forwarded to the used
 * {@link java.util.concurrent.ExecutorService} instance.
 */
public final class ThreadPipelineBuilder<T> extends AbstractPipelineBuilder<T> {
    private final ExecutorService executor;

    /**
     * Initializes the collector with the {@link ExecutorService} instance.
     *
     * @param executor {@link ExecutorService} instance, must be non-null
     */
    public ThreadPipelineBuilder(ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    protected Task<T> create(Runnable2<T, AsyncRunnable1<T>> body) {
        return new ExecutorTask<>(body, executor);
    }
}
