package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * @param <T>
 */
public interface PipelineConfigurer<T> {

    /**
     *
     * @param consumer
     * @return
     */
    PipelineConfigurer<T> add(TaskConsumer<T> consumer);

    /**
     *
     * @param consumer
     * @return
     */
    PipelineConfigurer<T> prepend(TaskConsumer<T> consumer);

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    PipelineConfigurer<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    /**
     *
     * @param predicate
     * @param consumer
     * @return
     */
    PipelineConfigurer<T> addWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    PipelineConfigurer<T> mapWhen(Predicate<T> predicate, Task<T> task);

    /**
     *
     * @param predicate
     * @param consumer
     * @return
     */
    PipelineConfigurer<T> mapWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    /**
     *
     * @return
     */
    PipelineConfigurer<T> remove();

    /**
     *
     * @return
     */
    PipelineConfigurer<T> clear();
}
