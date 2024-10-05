package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;

import java.util.LinkedList;

/**
 * {@link PipelineBuilder} implementation, which creates a linked chain of tasks.
 * Does not clear task chain on {@link PipelineBuilder#build()}.
 *
 * @param <T> pipeline context type
 */
public final class OpenedPipelineBuilder<T> extends AbstractLinkedBuilder<T> {

    /**
     * Constructs a {@link OpenedPipelineBuilder} empty instance.
     */
    public OpenedPipelineBuilder() {
        super(new LinkedList<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Runnable1<T> build() {
        if (deque.isEmpty()) {
            return Util.EMPTY;
        }
        return deque.peekLast();
    }
}
