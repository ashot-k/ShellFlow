package org.ashot.shellflow.node.execution.tab;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.execution.container.TerminalContainer;
import org.ashot.shellflow.node.toolbar.TerminalToolBar;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.ashot.shellflow.utils.TabUtils;
import org.ashot.shellflow.utils.ThemeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.data.constant.ExecutionState.*;

public class SingleExecutionTab extends Tab implements TerminalContainer {
    private static final Logger log = LoggerFactory.getLogger(SingleExecutionTab.class);
    private ShellFlowTerminalWidget terminal;
    private final VBox terminalWrapper = new VBox();
    private final SimpleObjectProperty<ExecutionState> state = new SimpleObjectProperty<>();
    private final StackPane stackPane = new StackPane();
    private Process process;

    private SingleExecutionTab(OutputTabBuilder outputTabBuilder) {
        setTooltip(outputTabBuilder.tooltip);
        setText(outputTabBuilder.tabName);
        setDisable(outputTabBuilder.disabled);
        setClosable(outputTabBuilder.closable);
        setTerminal(outputTabBuilder.terminal);
        runLater(this::setupOutputTab);
    }

    public void setupOutputTab() {
        terminalWrapper.setFillWidth(true);
        terminalWrapper.setPadding(new Insets(5));
        terminalWrapper.getStyleClass().addAll(ThemeHandler.getSelectedTheme().isDark() ? "dark" : "light", "terminal-wrapper");
        stackPane.getChildren().add(terminalWrapper);
        setContent(stackPane);
    }

    public static SingleExecutionTab constructTabFromCommand(Command command) {
        return new OutputTabBuilder(TerminalFactory.createTerminalWidget())
                .setTabName(command.isNameSet() ? command.getName() : command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .build();
    }

    public static SingleExecutionTab constructSequencePartOutputTab(Command command) {
        return new OutputTabBuilder(TerminalFactory.createTerminalWidget())
                .setTabName(command.isNameSet() ? command.getName() : command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .setDisabled(true)
                .setClosable(false)
                .build();
    }

    @Override
    public void shutDownTerminal() {
        if (getTerminal() != null) {
            this.getTerminal().close();
        }
    }

    @Override
    public void startTerminal() {
        if (getTerminal() != null && getTerminal().getTtyConnector() != null && getTerminal().canOpenSession()) {
            runLater(() -> {
                getTerminal().start();
                TerminalToolBar terminalToolBar = terminal.getTerminalToolBar();
                stackPane.getChildren().add(terminalToolBar);
                StackPane.setAlignment(terminalToolBar, Pos.BOTTOM_RIGHT);
                StackPane.setMargin(terminalToolBar, new Insets(0, 25, 15, 0));
            });
        }
    }

    @Override
    public ShellFlowTerminalWidget getTerminal() {
        return terminal;
    }

    public void setTerminal(ShellFlowTerminalWidget terminal) {
        this.terminal = terminal;
        if (terminal != null) {
            setOnClose(null);
            runLater(() -> {
                terminalWrapper.getChildren().add(terminal.getPane());
                VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
            });
        }
    }

    public void updateState(ExecutionState state, boolean sequence) {
        log.debug("Execution: {} ({}), updated state: {}", getText(), getTooltip().getText(), state);
        switch (state) {
            case IN_PROGRESS -> TabUtils.setInProgress(this);
            case INTERNAL_FAILURE, FAILURE -> TabUtils.setFailed(this, sequence);
            case FINISHED -> TabUtils.setFinished(this, sequence);
            case CANCELLED -> TabUtils.setCancelled(this, sequence);
        }
    }

    public void setOnClose(EventHandler<Event> event) {
        this.setOnCloseRequest(closeEvent -> {
            if (event != null) {
                event.handle(closeEvent);
                return;
            }
            if (!closeEvent.isConsumed()) {
                this.terminal.close();
            }
        });
    }

    @Override
    public SimpleObjectProperty<ExecutionState> stateProperty() {
        return state;
    }

    public boolean isInProgress() {
        return state.get().equals(IN_PROGRESS);
    }

    public boolean isFinished() {
        return state.get().equals(FINISHED);
    }

    public boolean isCanceled() {
        return state.get().equals(CANCELLED);
    }

    public boolean isFailed() {
        return state.get().equals(FAILURE);
    }

    public void setCancelled() {
        state.setValue(CANCELLED);
    }

    public void setInProgress() {
        state.setValue(IN_PROGRESS);
    }

    public void setFinished() {
        state.setValue(FINISHED);
    }

    public void setFailed() {
        state.setValue(FAILURE);
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public static class OutputTabBuilder {

        private final ShellFlowTerminalWidget terminal;

        private String tabName;
        private final Tooltip tooltip = new Tooltip();
        private boolean disabled = false;
        private boolean closable = true;

        public OutputTabBuilder(ShellFlowTerminalWidget terminal) {
            this.terminal = terminal;
        }

        public OutputTabBuilder setTabName(String tabName) {
            this.tabName = tabName;
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

        public SingleExecutionTab build() {
            return new SingleExecutionTab(this);
        }
    }
}
