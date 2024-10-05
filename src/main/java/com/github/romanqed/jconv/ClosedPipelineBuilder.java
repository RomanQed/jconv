package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;

import java.util.LinkedList;

/**
 * {@link PipelineBuilder} implementation, which creates a linked chain of tasks.
 * Clears task chain on {@link PipelineBuilder#build()}.
 *
 * @param <T> pipeline context type
 */
public final class ClosedPipelineBuilder<T> extends AbstractLinkedBuilder<T> {

    /**
     * Constructs a {@link ClosedPipelineBuilder} empty instance.
     */
    public ClosedPipelineBuilder() {
        super(new LinkedList<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Runnable1<T> build() {
        if (deque.isEmpty()) {
            return Util.EMPTY;
        }
        var ret = deque.peekLast();
        deque.clear();
        return ret;
    }
}
