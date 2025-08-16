package org.ashot.shellflow.node.tab.executions;

import com.pty4j.PtyProcess;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;

public class ExecutionTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTab.class);
    private String commandDisplayName;
    private ShellFlowTerminalWidget terminal;
    private final VBox terminalWrapper = new VBox();
    private boolean inProgress = false;
    private boolean finished = false;
    private boolean failed = false;
    private boolean canceled = false;
    private final StackPane stackPane = new StackPane();

    private ExecutionTab(OutputTabBuilder outputTabBuilder) {
        this.commandDisplayName = outputTabBuilder.commandDisplayName;
        this.setTooltip(outputTabBuilder.tooltip);
        this.setText(outputTabBuilder.tabName);
        this.setDisable(outputTabBuilder.disabled);
        this.setClosable(outputTabBuilder.closable);
        setTerminal(outputTabBuilder.terminal);
        runLater(this::setupOutputTab);
    }

    public void setupOutputTab() {
        this.terminalWrapper.setFillWidth(true);
        this.terminalWrapper.setPadding(new Insets(5));
        this.terminalWrapper.getStyleClass().addAll(ShellFlow.getSelectedThemeOption().isDark() ? "dark" : "light", "terminal-wrapper");
        this.stackPane.getChildren().add(terminalWrapper);
        this.setContent(stackPane);
    }

    public static ExecutionTab constructOutputTabWithTerminalProcess(PtyProcess process, Command command) {
        return new OutputTabBuilder(TerminalFactory.createTerminalWidget(process))
                .setTabName(command.isNameSet() ? command.getName() : "Process - " + process.pid())
                .setCommandDisplayName(command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .build();
    }

    public static ExecutionTab constructSequencePartOutputTab(Command command) {
        return new OutputTabBuilder()
                .setTabName(command.getName())
                .setTooltip(command.getArgumentsString())
                .setCommandDisplayName(command.getArgumentsString())
                .setDisabled(true)
                .setClosable(false)
                .build();
    }

    public void shutDownTerminal() {
        if (getTerminal() != null) {
            this.getTerminal().close();
        }
    }

    public void startTerminal() {
        if (getTerminal() != null) {
            this.getTerminal().start();
            runLater(()->{
                this.terminal.createToolBar();
                TerminalToolBar terminalToolBar = this.terminal.getTerminalToolBar();
                this.stackPane.getChildren().add(terminalToolBar);
                StackPane.setAlignment(terminalToolBar, Pos.BOTTOM_RIGHT);
                StackPane.setMargin(terminalToolBar, new Insets(0, 25, 20, 0));
            });
        }
    }

    public static List<ExecutionTab> getOutputTabsFromTabPane(TabPane tabPane) {
        List<ExecutionTab> tabs = new ArrayList<>();
        for (Tab tab : tabPane.getTabs()) {
            if (tab instanceof ExecutionTab executionTab) {
                tabs.add(executionTab);
            } else if (tab instanceof SequenceExecutionsTab sequenceTab) {
                tabs.addAll(sequenceTab.getSequentialExecutionTabPaneTabs());
            }
        }
        return tabs;
    }

    public void setCommandDisplayName(String commandDisplayName) {
        this.commandDisplayName = commandDisplayName;
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public void closeTerminal() {
        this.terminal.close();
    }

    public ShellFlowTerminalWidget getTerminal() {
        return terminal;
    }

    public void setTerminal(ShellFlowTerminalWidget terminal) {
        this.terminal = terminal;
        if(terminal != null) {
            setOnClose(null);
            runLater(() -> {
                terminalWrapper.getChildren().add(terminal.getPane());
                VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
                if (this.getText().isBlank()) {
                    this.setText("Process - " + ((PtyProcessTtyConnector) terminal.getTtyConnector()).getProcess().pid());
                }
            });
        }
    }


    public void setOnClose(EventHandler<Event> event){
        this.setOnCloseRequest(closeEvent->{
            if(event != null) {
                event.handle(closeEvent);
                return;
            }
            if(!closeEvent.isConsumed()) {
                this.terminal.close();
            }
        });
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public static class OutputTabBuilder {

        private ShellFlowTerminalWidget terminal;

        private String tabName;
        private final Tooltip tooltip = new Tooltip();
        private String commandDisplayName;
        private boolean disabled = false;
        private boolean closable = true;

        public OutputTabBuilder() {
        }

        public OutputTabBuilder(ShellFlowTerminalWidget terminal) {
            this.terminal = terminal;
        }

        public OutputTabBuilder setTabName(String tabName) {
            this.tabName = tabName;
            return this;
        }

        public OutputTabBuilder setCommandDisplayName(String commandDisplayName) {
            this.commandDisplayName = commandDisplayName;
            return this;
        }

        public OutputTabBuilder setTooltip(String tooltipText) {
            this.tooltip.setText(tooltipText);
            return this;
        }

        public OutputTabBuilder setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public OutputTabBuilder setClosable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public ExecutionTab build() {
            return new ExecutionTab(this);
        }
    }
}
