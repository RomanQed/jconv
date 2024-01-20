package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * {@link PipelineBuilder} implementation, which creates a linked chain of tasks.
 *
 * @param <T> pipeline context type
 */
public final class LinkedPipelineBuilder<T> implements PipelineBuilder<T> {
    private final Deque<LinkedTask<T>> deque;

    public LinkedPipelineBuilder() {
        this.deque = new LinkedBlockingDeque<>();
    }

    @Override
    public PipelineBuilder<T> add(Runnable2<T, Task<T>> runnable) {
        var task = new LinkedTask<>(runnable);
        if (!deque.isEmpty()) {
            deque.peek().setNext(task);
        }
        deque.push(task);
        return this;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public PipelineBuilder<T> remove() {
        if (deque.isEmpty()) {
            return this;
        }
        deque.pop();
        deque.peek().resetNext();
        return this;
    }

    @Override
    public PipelineBuilder<T> clear() {
        deque.clear();
        return this;
    }

    @Override
    public Task<T> build() {
        if (deque.isEmpty()) {
            return Task.empty();
        }
        var ret = deque.peekLast();
        deque.clear();
        return ret;
    }
}
