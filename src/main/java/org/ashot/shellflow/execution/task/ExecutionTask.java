package org.ashot.shellflow.execution.task;

import org.ashot.shellflow.execution.container.ExecutionContainer;

public interface ExecutionTask extends Runnable {
    ExecutionContainer getContainer();
}
