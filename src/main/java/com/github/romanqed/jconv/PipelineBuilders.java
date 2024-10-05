package com.github.romanqed.jconv;

/**
 * A utility class containing methods for creating basic implementations of {@link PipelineBuilder}.
 */
public final class PipelineBuilders {
    private PipelineBuilders() {
    }

    /**
     * Creates the {@link OpenedPipelineBuilder} instance.
     *
     * @param <T> the pipeline context type
     * @return the {@link OpenedPipelineBuilder} instance
     */
    public static <T> PipelineBuilder<T> createOpened() {
        return new OpenedPipelineBuilder<>();
    }

    /**
     * Creates the {@link ClosedPipelineBuilder} instance.
     *
     * @param <T> the pipeline context type
     * @return the {@link ClosedPipelineBuilder} instance
     */
    public static <T> PipelineBuilder<T> createClosed() {
        return new ClosedPipelineBuilder<>();
    }
}
