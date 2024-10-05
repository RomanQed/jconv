package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jfunc.Runnable2;

import java.util.Deque;
import java.util.Objects;

abstract class AbstractLinkedBuilder<T> implements PipelineBuilder<T> {
    protected final Deque<LinkedRunnable<T>> deque;

    protected AbstractLinkedBuilder(Deque<LinkedRunnable<T>> deque) {
        this.deque = deque;
    }

    @Override
    public PipelineBuilder<T> add(Runnable2<T, Runnable1<T>> runnable) {
        Objects.requireNonNull(runnable);
        var task = new LinkedRunnable<>(runnable);
        if (!deque.isEmpty()) {
            deque.peek().setNext(task);
        }
        deque.push(task);
        return this;
    }

    @Override
    public PipelineBuilder<T> remove() {
        if (deque.isEmpty()) {
            return this;
        }
        deque.pop();
        if (deque.isEmpty()) {
            return this;
        }
        deque.peek().resetNext();
        return this;
    }

    @Override
    public PipelineBuilder<T> clear() {
        deque.clear();
        return this;
    }
}
