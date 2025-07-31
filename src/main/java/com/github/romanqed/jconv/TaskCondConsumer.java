package com.github.romanqed.jconv;

import java.util.function.Predicate;

final class TaskCondConsumer<T> implements TaskConsumer<T> {
    final Predicate<T> predicate;
    final Task<T> when;

    TaskCondConsumer(Predicate<T> predicate, Task<T> when) {
        this.predicate = predicate;
        this.when = when;
    }

    @Override
    public void run(T t, Task<T> task) throws Throwable {
        if (predicate.test(t)) {
            when.run(t);
        } else {
            task.run(t);
        }
    }
}
