package org.ashot.microservice_starter.node.tab;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.terminal.PtyProcessTtyConnector;
import org.ashot.microservice_starter.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OutputTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(OutputTab.class);
    private String commandDisplayName;
    private JediTermFxWidget terminal;
    private VBox terminalWrapper;
    private boolean inProgress = false;
    private boolean finished = false;
    private boolean failed = false;
    private boolean canceled = false;

    private OutputTab(OutputTabBuilder outputTabBuilder){
        this.commandDisplayName = outputTabBuilder.commandDisplayName;
        this.terminal = outputTabBuilder.terminal;
        this.setTooltip(outputTabBuilder.tooltip);
        this.setText(outputTabBuilder.tabName);
        Platform.runLater(this::setupOutputTab);
    }

    public void setupOutputTab() {
        if(terminal != null) {
            this.terminalWrapper = new VBox(terminal.getPane());
            this.terminalWrapper.setFillWidth(true);
            this.terminalWrapper.setPadding(new Insets(5));
            VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
            this.setContent(this.terminalWrapper);
        }
        this.setClosable(true);
        this.setOnClosed(_ -> this.terminal.close());
    }

    public static OutputTab constructOutputTabWithTerminalProcess(PtyProcess process, Command command) {
        return new OutputTab.OutputTabBuilder(TerminalFactory.createTerminalWidget(process))
                .setTabName(command.isNameSet() ? command.getName() : "Process - " + process.pid())
                .setCommandDisplayName(command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .build();
    }

    public static OutputTab constructSequencePartOutputTab(Command command) {
        OutputTab tab = new OutputTab.OutputTabBuilder().build();
        Platform.runLater(() -> {
            tab.setClosable(false);
            tab.setCommandDisplayName(command.getArgumentsString());
            tab.getTooltip().setText(command.getArgumentsString());
            tab.setText(command.getName());
            tab.setDisable(true);
        });
        return tab;
    }

    public void shutDownTerminal(){
        if(getTerminal() != null){
            this.getTerminal().close();
        }
    }
    public void startTerminal(){
        if(getTerminal() != null){
            this.getTerminal().start();
        }
    }

    public static List<OutputTab> getOutputTabsFromTabPane(TabPane tabPane){
        return tabPane.getTabs().stream().filter(e -> e instanceof OutputTab).map(o -> (OutputTab) o).toList();
    }

    public void setCommandDisplayName(String commandDisplayName) {
        this.commandDisplayName = commandDisplayName;
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public void closeTerminal(){
        this.terminal.close();
    }

    public JediTermFxWidget getTerminal() {
        return terminal;
    }

    public void setTerminal(JediTermFxWidget terminal) {
        this.terminal = terminal;
        Platform.runLater(() -> {
            this.terminalWrapper = new VBox(terminal.getPane());
            this.terminalWrapper.setFillWidth(true);
            this.terminalWrapper.setPadding(new Insets(5));
            VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
            this.setContent(this.terminalWrapper);
            if(this.getText().isBlank()){
                this.setText("Process - " + ((PtyProcessTtyConnector) terminal.getTtyConnector()).getProcess().pid());
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

    public static class OutputTabBuilder{

        private JediTermFxWidget terminal;

        private String tabName;
        private final Tooltip tooltip = new Tooltip();
        private String commandDisplayName;

        public OutputTabBuilder(){}

        public OutputTabBuilder(JediTermFxWidget terminal){
            this.terminal = terminal;
        }

        public OutputTabBuilder setTabName(String tabName){
            this.tabName = tabName;
            return this;
        }

        public OutputTabBuilder setCommandDisplayName(String commandDisplayName){
            this.commandDisplayName = commandDisplayName;
            return this;
        }

        public OutputTabBuilder setTooltip(String tooltipText){
            this.tooltip.setText(tooltipText);
            return this;
        }

        public OutputTab build(){
            return new OutputTab(this);
        }
    }
}
