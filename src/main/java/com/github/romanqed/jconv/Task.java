package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.Objects;

class Task<T> implements AsyncRunnable1<T> {
    private static final AsyncRunnable1<?> EMPTY = t -> {
    };

    protected final Runnable2<T, AsyncRunnable1<T>> body;
    protected AsyncRunnable1<T> next;

    Task(Runnable2<T, AsyncRunnable1<T>> body) {
        this.body = body;
        this.resetNext();
    }

    void setNext(AsyncRunnable1<T> next) {
        this.next = Objects.requireNonNull(next);
    }

    @SuppressWarnings("unchecked")
    void resetNext() {
        this.next = (AsyncRunnable1<T>) EMPTY;
    }

    @Override
    public void run(T t) throws Throwable {
        this.body.run(t, this.next);
    }

    @Override
    public void accept(T t) {
        try {
            this.body.run(t, this.next);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
