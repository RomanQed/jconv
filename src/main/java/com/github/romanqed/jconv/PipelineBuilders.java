package com.github.romanqed.jconv;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Factory class for creating {@link PipelineBuilder} instances.
 * <p>
 * This utility provides entry points for building pipelines using the default
 * linked task implementation. It serves as an abstraction over concrete builder
 * types and allows clients to create pipelines in a concise and expressive manner.
 * </p>
 * <p>
 * Although {@link LinkedPipelineBuilder} is the default implementation provided by this library,
 * users are encouraged to rely on this class for instantiation to decouple from implementation details.
 * </p>
 */
public final class PipelineBuilders {
    private PipelineBuilders() {
    }

    /**
     * Creates a new, empty linked pipeline builder.
     *
     * @param <T> the input type of the pipeline
     * @return a new instance of {@link PipelineBuilder}
     */
    public static <T> PipelineBuilder<T> linked() {
        return new LinkedPipelineBuilder<>();
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
     * @param <T> the input type of the pipeline
     * @return a configured {@link PipelineBuilder} instance
     * @throws NullPointerException if {@code consumer} is {@code null}
     */
    public static <T> PipelineBuilder<T> linked(Consumer<PipelineConfigurer<T>> consumer) {
        Objects.requireNonNull(consumer);
        var ret = new LinkedPipelineBuilder<T>();
        consumer.accept(ret);
        return ret;
    }
}
