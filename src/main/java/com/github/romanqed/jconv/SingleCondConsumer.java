package com.github.romanqed.jconv;

import java.util.function.Predicate;

final class SingleCondConsumer<T> implements TaskConsumer<T> {
    final Predicate<T> predicate;

    SingleCondConsumer(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void run(T t, Task<T> task) throws Throwable {
        if (!predicate.test(t)) {
            task.run(t);
        }
    }
}
