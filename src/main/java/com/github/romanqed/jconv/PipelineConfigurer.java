package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Defines a fluent interface for building and configuring a task pipeline.
 * Implementations typically assemble conditional or sequential processing chains.
 *
 * @param <T> the input type processed by the pipeline
 */
public interface PipelineConfigurer<T> {

    /**
     * Appends the specified task consumer to the end of the pipeline.
     *
     * @param consumer the consumer to add
     * @return this configurer
     */
    PipelineConfigurer<T> add(TaskConsumer<T> consumer);

    /**
     * Prepends the specified task consumer to the start of the pipeline.
     *
     * @param consumer the consumer to prepend
     * @return this configurer
     */
    PipelineConfigurer<T> prepend(TaskConsumer<T> consumer);

    /**
     * Conditionally adds a consumer that runs if the given predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param task the consumer to run if the condition is true
     * @return this configurer
     */
    PipelineConfigurer<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    /**
     * Conditionally adds a nested sub-pipeline, assembled via the provided consumer.
     *
     * @param predicate the condition to evaluate
     * @param consumer the builder of the conditional sub-pipeline
     * @return this configurer
     */
    PipelineConfigurer<T> addWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    /**
     * Conditionally maps execution to a task if the predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param task the task to run if the condition is true
     * @return this configurer
     */
    PipelineConfigurer<T> mapWhen(Predicate<T> predicate, Task<T> task);

    /**
     * Conditionally maps execution to a sub-pipeline if the predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param consumer the builder of the conditional sub-pipeline
     * @return this configurer
     */
    PipelineConfigurer<T> mapWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    /**
     * Removes the last added task from the pipeline.
     *
     * @return this configurer
     */
    PipelineConfigurer<T> remove();

    /**
     * Clears all tasks from the pipeline.
     *
     * @return this configurer
     */
    PipelineConfigurer<T> clear();
}
