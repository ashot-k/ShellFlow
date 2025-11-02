package org.ashot.shellflow.data.command;

import java.util.List;

public record CommandSequence(List<Command> commandList, String sequenceName) {
}
