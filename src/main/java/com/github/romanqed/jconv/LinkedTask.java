package com.github.romanqed.jconv;

public final class LinkedTask<T> implements Task<T> {
    final TaskConsumer<T> body;
    Task<T> next;

    @SuppressWarnings("unchecked")
    public LinkedTask(TaskConsumer<T> body) {
        this.body = body;
        this.next = Task.EMPTY;
    }

    public void setNext(Task<T> next) {
        this.next = next;
    }

    @SuppressWarnings("unchecked")
    public void resetNext() {
        this.next = Task.EMPTY;
    }

    @Override
    public void run(T t) throws Throwable {
        body.run(t, next);
    }
}
