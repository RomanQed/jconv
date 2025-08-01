package com.github.romanqed.jconv;

import java.util.LinkedList;

public final class LinkedPipelineBuilder<T>
        extends AbstractLinkedConfigurer<T, PipelineBuilder<T>>
        implements PipelineBuilder<T> {

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
