package org.ashot.shellflow.misc;

import org.ashot.shellflow.exception.app.ActionFailureException;

@FunctionalInterface
public interface ButtonActionCallback<T> {
    void accept(T t) throws ActionFailureException;
}
