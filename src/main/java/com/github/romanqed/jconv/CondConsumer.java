package com.github.romanqed.jconv;

import java.util.function.Predicate;

final class CondConsumer<T> implements TaskConsumer<T> {
    final Predicate<T> predicate;
    final TaskConsumer<T> when;

    CondConsumer(Predicate<T> predicate, TaskConsumer<T> when) {
        this.predicate = predicate;
        this.when = when;
    }

    @Override
    public void run(T t, Task<T> task) throws Throwable {
        if (predicate.test(t)) {
            when.run(t, task);
        } else {
            task.run(t);
        }
    }
}
