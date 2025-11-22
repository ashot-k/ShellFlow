package org.ashot.shellflow.data.execution;

import org.ashot.shellflow.data.entry.Entry;

import java.util.List;

public record Execution(List<Entry> entries, String executionName, int delay, boolean sequence) {
}
