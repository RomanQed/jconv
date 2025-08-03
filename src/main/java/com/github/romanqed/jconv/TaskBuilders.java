package com.github.romanqed.jconv;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Factory class for creating {@link TaskBuilder} instances.
 * <p>
 * This utility provides entry points for building pipelines using the default
 * linked task implementation. It serves as an abstraction over concrete builder
 * types and allows clients to create pipelines in a concise and expressive manner.
 * </p>
 * <p>
 * Although {@link LinkedTaskBuilder} is the default implementation provided by this library,
 * users are encouraged to rely on this class for instantiation to decouple from implementation details.
 * </p>
 */
public final class TaskBuilders {
    private TaskBuilders() {
    }

    /**
     * Creates a new, empty linked pipeline builder.
     *
     * @param <T> the input type of the pipeline
     * @return a new instance of {@link TaskBuilder}
     */
    public static <T> TaskBuilder<T> linked() {
        return new LinkedTaskBuilder<>();
    }

    /**
     * Creates a new linked pipeline builder and applies the given configuration.
     * <p>
     * This is a convenience method for building pipelines in a single statement:
     * </p>
     * <pre>{@code
     * Task<String> pipeline = PipelineBuilders.linked(builder -> {
     *     builder.add(task1)
     *            .add(task2)
     *            .addWhen(condition, task3);
     * }).build();
     * }</pre>
     *
     * @param consumer the configuration logic to apply
     * @param <T>      the input type of the pipeline
     * @return a configured {@link TaskBuilder} instance
     * @throws NullPointerException if {@code consumer} is {@code null}
     */
    public static <T> TaskBuilder<T> linked(Consumer<TaskConfigurer<T>> consumer) {
        Objects.requireNonNull(consumer);
        var ret = new LinkedTaskBuilder<T>();
        consumer.accept(ret);
        return ret;
    }
}
