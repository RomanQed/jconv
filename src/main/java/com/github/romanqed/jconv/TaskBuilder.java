package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Extends {@link TaskConfigurer} by providing the ability to compile
 * the configured pipeline into an executable {@link Task}.
 *
 * @param <T> the input type processed by the pipeline
 */
public interface TaskBuilder<T> extends TaskConfigurer<T> {

    @Override
    TaskBuilder<T> add(TaskConsumer<T> consumer);

    @Override
    default TaskBuilder<T> add(SyncTaskConsumer<T> consumer) {
        return add((TaskConsumer<T>) consumer);
    }

    @Override
    default TaskBuilder<T> add(AsyncTaskConsumer<T> consumer) {
        return add((TaskConsumer<T>) consumer);
    }

    @Override
    TaskBuilder<T> prepend(TaskConsumer<T> consumer);

    @Override
    default TaskBuilder<T> prepend(SyncTaskConsumer<T> consumer) {
        return prepend((TaskConsumer<T>) consumer);
    }

    @Override
    default TaskBuilder<T> prepend(AsyncTaskConsumer<T> consumer) {
        return prepend((TaskConsumer<T>) consumer);
    }

    @Override
    TaskBuilder<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    @Override
    default TaskBuilder<T> addWhen(Predicate<T> predicate, SyncTaskConsumer<T> task) {
        return addWhen(predicate, (TaskConsumer<T>) task);
    }

    @Override
    default TaskBuilder<T> addWhen(Predicate<T> predicate, AsyncTaskConsumer<T> task) {
        return addWhen(predicate, (TaskConsumer<T>) task);
    }

    @Override
    TaskBuilder<T> addWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer);

    @Override
    TaskBuilder<T> mapWhen(Predicate<T> predicate, Task<T> task);

    @Override
    default TaskBuilder<T> mapWhen(Predicate<T> predicate, SyncTask<T> task) {
        return mapWhen(predicate, (Task<T>) task);
    }

    @Override
    default TaskBuilder<T> mapWhen(Predicate<T> predicate, AsyncTask<T> task) {
        return mapWhen(predicate, (Task<T>) task);
    }

    @Override
    TaskBuilder<T> mapWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer);

    @Override
    TaskBuilder<T> remove();

    @Override
    TaskBuilder<T> clear();

    /**
     * Finalizes and compiles the current pipeline configuration into a reusable task.
     *
     * @return the composed task representing the pipeline
     */
    Task<T> build();
}
