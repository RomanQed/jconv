package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jfunc.Runnable2;

final class LinkedRunnable<T> implements Runnable1<T> {
    private final Runnable2<T, Runnable1<T>> body;
    private Runnable1<T> next;

    LinkedRunnable(Runnable2<T, Runnable1<T>> body) {
        this.body = body;
        this.resetNext();
    }

    void setNext(Runnable1<T> next) {
        this.next = next;
    }

    @SuppressWarnings("unchecked")
    void resetNext() {
        this.next = Util.EMPTY;
    }

    @Override
    public void run(T t) throws Throwable {
        this.body.run(t, this.next);
    }
}
