package com.github.romanqed.jconv;

import java.util.LinkedList;

final class LinkedTaskConfigurer<T> extends AbstractLinkedConfigurer<T, TaskConfigurer<T>> {

    LinkedTaskConfigurer() {
        super(new LinkedList<>());
    }

    @Override
    protected <V> TaskBuilder<V> newBuilder() {
        return new LinkedTaskBuilder<>();
    }

    @Override
    protected <V> AbstractLinkedConfigurer<V, ? extends TaskConfigurer<V>> newConfigurer() {
        return new LinkedTaskConfigurer<>();
    }
}
