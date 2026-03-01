package org.ashot.shellflow.execution;

import java.util.List;

public record SequenceExecution(List<SingularExecution> executions) {
}
