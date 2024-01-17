package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

/**
 * A pipeline builder that does not support {@link java.util.concurrent.ExecutorService}.
 * Inside the pipeline, an attempt to call {@link AsyncRunnable1#submit} will result in an exception.
 *
 * @param <T> pipeline context type
 */
public final class SinglePipelineBuilder<T> extends AbstractPipelineBuilder<T> {

    @Override
    protected Task<T> create(Runnable2<T, AsyncRunnable1<T>> body) {
        return new Task<>(body);
    }
}
