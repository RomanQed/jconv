package com.github.romanqed.jconv;

import java.util.LinkedList;

final class LinkedPipelineConfigurer<T> extends AbstractLinkedConfigurer<T, PipelineConfigurer<T>> {

    LinkedPipelineConfigurer() {
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
}
