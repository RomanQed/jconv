package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * @param <T>
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

    Task<T> build();
}
