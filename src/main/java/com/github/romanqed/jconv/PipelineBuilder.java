package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface PipelineBuilder<T> {
    PipelineBuilder<T> add(TaskConsumer<T> consumer);

    PipelineBuilder<T> prepend(TaskConsumer<T> consumer);

    PipelineBuilder<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    PipelineBuilder<T> addWhen(Predicate<T> predicate, Consumer<PipelineBuilder<T>> consumer);

    PipelineBuilder<T> mapWhen(Predicate<T> predicate, Task<T> task);

    PipelineBuilder<T> mapWhen(Predicate<T> predicate, Consumer<PipelineBuilder<T>> consumer);

    PipelineBuilder<T> remove();

    PipelineBuilder<T> clear();

    Task<T> build();
}
