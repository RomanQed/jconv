package com.github.romanqed.jconv;

import java.util.LinkedList;

/**
 * A concrete {@link PipelineBuilder} implementation that uses a linked structure
 * to compose and manage task pipelines.
 *
 * <p>This class extends {@link AbstractLinkedConfigurer} and provides full support for pipeline
 * configuration and finalization. Once configured, the pipeline can be built into a {@link Task}
 * using {@link #build()}.</p>
 *
 * <p>Tasks are stored internally in a {@link LinkedList}-backed deque.</p>
 *
 * @param <T> the type of input processed by the pipeline
 */
public final class LinkedPipelineBuilder<T>
        extends AbstractLinkedConfigurer<T, PipelineBuilder<T>>
        implements PipelineBuilder<T> {

    /**
     * Constructs a new, empty pipeline builder.
     */
    public LinkedPipelineBuilder() {
        super(new LinkedList<>());
    }

    @Override
    protected <V> PipelineBuilder<V> newBuilder() {
        return new LinkedPipelineBuilder<>();
    }

    @Override
    protected <V> AbstractLinkedConfigurer<V, ? extends PipelineConfigurer<V>> newConfigurer() {
        return new LinkedPipelineConfigurer<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Task<T> build() {
        if (deque.isEmpty()) {
            return Task.EMPTY;
        }
        var ret = deque.peekLast();
        deque.clear();
        return ret;
    }
}
