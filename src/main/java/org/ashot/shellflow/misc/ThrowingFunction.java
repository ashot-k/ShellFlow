package org.ashot.shellflow.misc;

import org.ashot.shellflow.exception.app.ActionFailureException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws ActionFailureException;
}
