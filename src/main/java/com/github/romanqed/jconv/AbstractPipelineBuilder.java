package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jfunc.Runnable2;

import java.util.Stack;

abstract class AbstractPipelineBuilder<T> implements PipelineBuilder<T> {
    protected final Stack<Task<T>> stack;

    protected AbstractPipelineBuilder() {
        this.stack = new Stack<>();
    }

    protected abstract Task<T> create(Runnable2<T, AsyncRunnable1<T>> body);

    @Override
    public PipelineBuilder<T> add(Runnable2<T, AsyncRunnable1<T>> runnable) {
        var task = create(runnable);
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
    public Runnable1<T> build() {
        if (stack.isEmpty()) {
            return t -> {
            };
        }
        var ret = stack.firstElement();
        stack.clear();
        return ret;
    }
}
