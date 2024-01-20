package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.Objects;

final class LinkedTask<T> implements Task<T> {
    private final Runnable2<T, Task<T>> body;
    private Task<T> next;

    LinkedTask(Runnable2<T, Task<T>> body) {
        this.body = body;
        this.resetNext();
    }

    void setNext(Task<T> next) {
        this.next = Objects.requireNonNull(next);
    }

    void resetNext() {
        this.next = Task.empty();
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
