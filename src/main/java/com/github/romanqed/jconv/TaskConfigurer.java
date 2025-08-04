package com.github.romanqed.jconv;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Defines a fluent interface for building and configuring a task pipeline.
 * Implementations typically assemble conditional or sequential processing chains.
 *
 * @param <T> the input type processed by the pipeline
 */
public interface TaskConfigurer<T> {

    /**
     * Appends the specified task consumer to the end of the pipeline.
     *
     * @param consumer the consumer to add
     * @return this configurer
     */
    TaskConfigurer<T> add(TaskConsumer<T> consumer);

    /**
     *
     * @param consumer
     * @return
     */
    default TaskConfigurer<T> add(SyncTaskConsumer<T> consumer) {
        return add((TaskConsumer<T>) consumer);
    }

    /**
     *
     * @param consumer
     * @return
     */
    default TaskConfigurer<T> add(AsyncTaskConsumer<T> consumer) {
        return add((TaskConsumer<T>) consumer);
    }

    /**
     * Prepends the specified task consumer to the start of the pipeline.
     *
     * @param consumer the consumer to prepend
     * @return this configurer
     */
    TaskConfigurer<T> prepend(TaskConsumer<T> consumer);

    /**
     *
     * @param consumer
     * @return
     */
    default TaskConfigurer<T> prepend(SyncTaskConsumer<T> consumer) {
        return prepend((TaskConsumer<T>) consumer);
    }

    /**
     *
     * @param consumer
     * @return
     */
    default TaskConfigurer<T> prepend(AsyncTaskConsumer<T> consumer) {
        return prepend((TaskConsumer<T>) consumer);
    }

    /**
     * Conditionally adds a consumer that runs if the given predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param task      the consumer to run if the condition is true
     * @return this configurer
     */
    TaskConfigurer<T> addWhen(Predicate<T> predicate, TaskConsumer<T> task);

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    default TaskConfigurer<T> addWhen(Predicate<T> predicate, SyncTaskConsumer<T> task) {
        return addWhen(predicate, (TaskConsumer<T>) task);
    }

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    default TaskConfigurer<T> addWhen(Predicate<T> predicate, AsyncTaskConsumer<T> task) {
        return addWhen(predicate, (TaskConsumer<T>) task);
    }

    /**
     * Conditionally adds a nested sub-pipeline, assembled via the provided consumer.
     *
     * @param predicate the condition to evaluate
     * @param consumer  the builder of the conditional sub-pipeline
     * @return this configurer
     */
    TaskConfigurer<T> addWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer);

    /**
     * Conditionally maps execution to a task if the predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param task      the task to run if the condition is true
     * @return this configurer
     */
    TaskConfigurer<T> mapWhen(Predicate<T> predicate, Task<T> task);

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    default TaskConfigurer<T> mapWhen(Predicate<T> predicate, SyncTask<T> task) {
        return mapWhen(predicate, (Task<T>) task);
    }

    /**
     *
     * @param predicate
     * @param task
     * @return
     */
    default TaskConfigurer<T> mapWhen(Predicate<T> predicate, AsyncTask<T> task) {
        return mapWhen(predicate, (Task<T>) task);
    }

    /**
     * Conditionally maps execution to a sub-pipeline if the predicate passes.
     *
     * @param predicate the condition to evaluate
     * @param consumer  the builder of the conditional sub-pipeline
     * @return this configurer
     */
    TaskConfigurer<T> mapWhen(Predicate<T> predicate, Consumer<TaskConfigurer<T>> consumer);

    /**
     * Removes the last added task from the pipeline.
     *
     * @return this configurer
     */
    TaskConfigurer<T> remove();

    /**
     * Clears all tasks from the pipeline.
     *
     * @return this configurer
     */
    TaskConfigurer<T> clear();
}
