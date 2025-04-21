package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.ControllerRegistry;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.Fields;
import org.ashot.microservice_starter.node.TabOutput;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.microservice_starter.Utils.calculateDelay;
import static org.ashot.microservice_starter.Utils.getSystemOS;
import static org.ashot.microservice_starter.execution.CommandFormatUtils.*;


public class CommandExecution {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecution.class);

    public static void execute(String command, String path, String name, boolean seqOption) throws IOException {
        if (!seqOption) {
            List<String> unformattedCommands = new ArrayList<>(List.of(command.split(";")));
            command = formatCommands(unformattedCommands);
            command = "cd " + path + " && " + command;
        }
        name = formatName(name);
        logger.info("Name: {} Path: {} Command: {}", name, path, command);
        ProcessBuilder pb = null;
        if (getSystemOS().contains("linux")) {
            pb = new ProcessBuilder().command("bash", "-c", command).directory(new File(seqOption ? "/" : path));
        }else if (getSystemOS().contains("windows")){
            pb = new ProcessBuilder().command("wsl.exe", "-e", "bash", "-c", command).directory(new File(seqOption ? "/" : path));
        }
        Process process = null;
        try {
            process = pb.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        runInNewTab(process, name);
    }
    /*  try {
              PtyProcessBuilder pb  = new PtyProcessBuilder()
                      .setCommand(new String[]{"wsl.exe", "-e", "bash", "-c", command})
                      .setDirectory(new File(seqOption ? "/" : path).getAbsolutePath());
              PtyProcess process = pb.start();
              runInNewTab(process, name);
          } catch (IOException i) {
              logger.error(i.getMessage());
          }*/
    public static String executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        String currentCmdText = "";
        ObservableList<Node> entryChildren = container.getChildren();
        StringBuilder seqCommands = new StringBuilder();
        for (int idx = 0; idx < entryChildren.size(); idx++) {
            Node node = entryChildren.get(idx);
            if (!(node instanceof HBox)) {
                continue;
            }
            String name = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.NAME);
            String command = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.COMMAND);
            String path = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.PATH);
            if (!seqOption) {
                currentCmdText = currentCmdText.isEmpty() ? command : currentCmdText + "\n" + command + " at " + path;
                long timeInMS = calculateDelay(idx, delayPerCmd);
                CommandExecutionThread t = new CommandExecutionThread(command, path, name, timeInMS);
                new Thread(t).start();
            } else {
                handleSequentialCommandChain(seqCommands, command, path, idx, entryChildren.size(), delayPerCmd);
            }
        }
        if(seqOption) {
            try {
                currentCmdText = seqCommands.toString();
                CommandExecution.execute(seqCommands.toString(), null, seqName, true);
            } catch (IOException e) {
                ErrorPopup.errorPopup(e.getMessage());
            }
        }
        return currentCmdText;
    }
    private static void runInNewTab(Process process, String name){
        Controller controller = ControllerRegistry.get("main", Controller.class);
        TabPane tabs = controller.getTabs();
        TabOutput tabOutput = new TabOutput(new Tab(), new CodeArea(), process, name);

        Platform.runLater(() -> {
            tabs.getTabs().add(tabOutput.getTab());
            tabs.getSelectionModel().select(tabOutput.getTab());
        });
        CommandOutputThread thread = new CommandOutputThread(tabOutput);
        Thread t = new Thread(thread);
        t.start();
    }

}
