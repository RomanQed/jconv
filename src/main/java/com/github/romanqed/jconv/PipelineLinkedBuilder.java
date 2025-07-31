package com.github.romanqed.jconv;

import java.util.LinkedList;

public final class PipelineLinkedBuilder<T> extends AbstractLinkedBuilder<T> {

    public PipelineLinkedBuilder() {
        super(new LinkedList<>());
    }

    @Override
    protected <V> PipelineBuilder<V> newBuilder() {
        return new PipelineLinkedBuilder<>();
    }

    @Override
    protected <V> AbstractLinkedBuilder<V> newInternalBuilder() {
        return new InternalLinkedBuilder<>();
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
