package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jfunc.Runnable2;

/**
 * An interface describing the universal pipeline builder.
 * Pipeline is a connected pipeline consisting of sequentially calling each other functions.
 * Each function is added to the end of the pipeline, and the order of execution will
 * directly depend on the order of addition.
 *
 * @param <T> pipeline context type
 */
public interface PipelineBuilder<T> {

    /**
     * Adds a function to the end of the pipeline.
     *
     * @param runnable pipeline function
     * @return {@link PipelineBuilder} instance
     */
    PipelineBuilder<T> add(Runnable2<T, AsyncRunnable1<T>> runnable);

    /**
     * Removes the last function added to the pipeline.
     *
     * @return {@link PipelineBuilder} instance
     */
    PipelineBuilder<T> remove();

    /**
     * Removes all functions added to the pipeline.
     *
     * @return {@link PipelineBuilder} instance
     */
    PipelineBuilder<T> clear();

    /**
     * Builds a pipeline.
     *
     * @return {@link Runnable1} instance, containing built pipeline
     */
    Runnable1<T> build();
}
