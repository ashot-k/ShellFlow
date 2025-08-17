package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.notification.Notification;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ParallelExecutionsTab;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.ashot.shellflow.utils.TabUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.*;
import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class CommandExecutor {
    private final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    public CommandExecutor(){}

    public void execute(Command command) {
        execute(command, 0);
    }

    public void execute(Command command, long delay) {
        if(command == null){
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
            }
            PtyProcessBuilder processBuilder = buildProcess(command);
            ExecutionTab tab = ExecutionTab.constructTabFromCommand(command);
            addToExecutions(tab);
            PtyProcess process = startProcess(tab, processBuilder);
            runLater(()-> tab.checkTabName(command, process));
            waitForProcess(process);
            handleProcessExit(tab, process);
        }).start();
    }

    public void execute(ExecutionTab tab, Command command, long delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
            }
            PtyProcessBuilder processBuilder = buildProcess(command);
            PtyProcess process = startProcess(tab, processBuilder);
            runLater(()-> tab.checkTabName(command, process));
            waitForProcess(process);
            handleProcessExit(tab, process);
        }).start();
    }

    protected PtyProcess startProcess(ExecutionTab tab, PtyProcessBuilder processBuilder){
        try {
            PtyProcess process = processBuilder.start();
            startTtySession(tab, process);
            setInProgress(tab);
            return process;
        } catch (IOException e) {
            log.error(e.getMessage());
            new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
        }
        return null;
    }

    private void startTtySession(ExecutionTab tab, PtyProcess process){
        tab.getTerminal().setTtyConnector(TerminalFactory.createTtyConnector(process));
        TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
        tab.startTerminal();
    }

    protected int waitForProcess(Process process){
        try {
            process.waitFor();
            return process.exitValue();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            new AlertPopup("Execution Interrupted", null, e.getMessage(), false).show();
        }
        return -1;
    }

    private void handleProcessExit(ExecutionTab tab, Process process){
        if (process.exitValue() == 0) {
            handleProcessFinished(tab);
        } else if (process.exitValue() > 0){
            handleProcessFailed(tab, process.exitValue());
        }
    }

    public void executeAll(List<Entry> entries, String executionName, int delayPerCmd) {
        List<Command> commandList = new ArrayList<>();
        for (Entry entry: entries) {
            Command cmd = EntryMapper.entryToCommand(entry, false);
            if(cmd == null){
                continue;
            }
            commandList.add(cmd);
        }
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab();
        parallelExecutionsTab.setName(executionName);
        addToExecutions(parallelExecutionsTab);

        List<ExecutionTab> executionTabs = constructPlaceHolderTabs(commandList, parallelExecutionsTab);
        runLater(()-> parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().select(0));
        for (int i = 0; i < executionTabs.size(); i++) {
            ExecutionTab executionTab = executionTabs.get(i);
            long delay = calculateDelay(i, delayPerCmd);
            execute(executionTab, commandList.get(i), delay);
        }
    }

    private List<ExecutionTab> constructPlaceHolderTabs(List<Command> commandList, ParallelExecutionsTab parallelExecutionsTab) {
        List<ExecutionTab> executionTabs = new ArrayList<>();
        for (Command command : commandList) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            executionTabs.add(tab);
            runLater(() -> parallelExecutionsTab.getParallelExecutionTabPane().getTabs().add(tab));
        }
        return executionTabs;
    }

    public void handleProcessFinished(ExecutionTab tab){
        Notification.display(
                TabUtils.ExecutionState.FINISHED.getValue(),
                finishedNotificationMessage(tab.getText()),
                null,
                NotificationType.SUCCESS);
        setFinished(tab);
    }

    public void handleProcessFailed(ExecutionTab tab, int exitValue){
        Notification.display(
                TabUtils.ExecutionState.FAILURE.getValue(),
                failNotificationMessage(tab.getText(), exitValue),
                null,
                NotificationType.EXECUTION_FAILURE);
        setFailed(tab);
    }

}
