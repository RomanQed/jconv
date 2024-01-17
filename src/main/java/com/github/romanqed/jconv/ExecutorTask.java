package com.github.romanqed.jconv;

import com.github.romanqed.jfunc.Runnable2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

final class ExecutorTask<T> extends Task<T> {
    private final ExecutorService executor;

    ExecutorTask(Runnable2<T, AsyncRunnable1<T>> body, ExecutorService executor) {
        super(body);
        this.executor = executor;
    }

    @Override
    public Future<?> submit(T t) {
        return this.executor.submit(() -> {
            try {
                this.body.run(t, this.next);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
