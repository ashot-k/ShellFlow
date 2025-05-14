package org.ashot.microservice_starter.node.tab;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.constant.ProcessStatus;

public class ProfilerProcessNode extends HBox{
    private final Process process;
    private final Text processName;
    private final Text processID;
    private final Text processStatus;
    private final Text command;
    private final OutputTab tab;

    public ProfilerProcessNode(Process process, String processID, ProcessStatus status, OutputTab tab){
        this.process = process;
        this.processName = new Text(tab.getText());
        this.processID = new Text(processID);
        this.tab = tab;
        this.processStatus = new Text();
        this.command = new Text(tab.getCommandDisplayName());

        this.getChildren().addAll(
                this.processName, new Separator(Orientation.VERTICAL),
                this.processID, new Separator(Orientation.VERTICAL),
                this.processStatus, new Separator(Orientation.VERTICAL),
                this.command
                );
        this.setFillHeight(true);
        command.setWrappingWidth(600);
    }

    public Text getProcessStatus() {
        return processStatus;
    }

    public Text getProcessName() {
        return processName;
    }

    public Text getProcessID() {
        return processID;
    }

    public void refreshStatus(ProcessStatus processStatus, String exitCode) {
        this.processStatus.setText(processStatus.name() + (exitCode != null ? " (" + exitCode + ")": ""));
        switch (processStatus){
            case ACTIVE -> this.processStatus.getStyleClass().setAll("ansi-fg-bright-green");
            case EXITED -> this.processStatus.getStyleClass().setAll("ansi-fg-bright-gray");
            case FAILED -> this.processStatus.getStyleClass().setAll("ansi-fg-bright-red");
        }
    }

    public void refreshName(String name){
        this.processName.setText(name);
    }

    public void refreshID(String id){
        this.processID.setText(id);
    }

    public void refreshCommand(String command){
        this.command.setText("Current command: (" + command + ")");
    }

    public Process getProcess() {
        return process;
    }

    public OutputTab getTab() {
        return tab;
    }

    public Text getCommand() {
        return command;
    }

}
