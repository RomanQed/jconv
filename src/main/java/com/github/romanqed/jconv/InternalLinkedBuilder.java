package com.github.romanqed.jconv;

import java.util.LinkedList;

final class InternalLinkedBuilder<T> extends AbstractLinkedBuilder<T> {
    InternalLinkedBuilder() {
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
    public Task<T> build() {
        throw new UnsupportedOperationException("Cannot use build from internal builder");
    }
}
