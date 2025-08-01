package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Extends {@link PipelineConfigurer} by providing the ability to compile
 * the configured pipeline into an executable {@link Task}.
 *
 * @param <T> the input type processed by the pipeline
 */
public interface PipelineBuilder<T> extends PipelineConfigurer<T> {

    @Override
    PipelineBuilder<T> add(TaskConsumer<T> consumer);

    @Override
    PipelineBuilder<T> prepend(TaskConsumer<T> consumer);

    @Override
    PipelineBuilder<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    @Override
    PipelineBuilder<T> addWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    @Override
    PipelineBuilder<T> mapWhen(Predicate<T> predicate, Task<T> task);

    @Override
    PipelineBuilder<T> mapWhen(Predicate<T> predicate, Consumer<PipelineConfigurer<T>> consumer);

    @Override
    PipelineBuilder<T> remove();

    @Override
    PipelineBuilder<T> clear();

    /**
     * Finalizes and compiles the current pipeline configuration into a reusable task.
     *
     * @return the composed task representing the pipeline
     */
    Task<T> build();
}
