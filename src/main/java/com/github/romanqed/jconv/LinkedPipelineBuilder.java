package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.Stack;

public final class LinkedPipelineBuilder<T> implements PipelineBuilder<T> {
    private final Stack<LinkedTask<T>> stack;

    public LinkedPipelineBuilder() {
        this.stack = new Stack<>();
    }

    @Override
    public PipelineBuilder<T> add(Runnable2<T, Task<T>> runnable) {
        var task = new LinkedTask<>(runnable);
        if (!stack.isEmpty()) {
            stack.peek().setNext(task);
        }
        stack.push(task);
        return this;
    }

    @Override
    public PipelineBuilder<T> remove() {
        if (stack.isEmpty()) {
            return this;
        }
        stack.pop();
        stack.peek().resetNext();
        return this;
    }

    @Override
    public PipelineBuilder<T> clear() {
        stack.clear();
        return this;
    }

    @Override
    public Task<T> build() {
        if (stack.isEmpty()) {
            return Task.empty();
        }
        var ret = stack.firstElement();
        stack.clear();
        return ret;
    }
}
