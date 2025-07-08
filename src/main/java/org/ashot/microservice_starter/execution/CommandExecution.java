package org.ashot.microservice_starter.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.techsenger.jeditermfx.ui.TerminalWidget;
import com.techsenger.jeditermfx.ui.TerminalWidgetListener;
import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.data.message.OutputMessages;
import org.ashot.microservice_starter.mapper.EntryToCommandMapper;
import org.ashot.microservice_starter.node.entry.Entry;
import org.ashot.microservice_starter.node.notification.Notification;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.task.CommandExecutionTask;
import org.ashot.microservice_starter.terminal.TerminalFactory;
import org.ashot.microservice_starter.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.ashot.microservice_starter.utils.Utils.calculateDelay;


public class CommandExecution {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecution.class);

    public static void execute(Command command){
        try {
            PtyProcess process = buildProcess(command).start();
            ProcessRegistry.register(String.valueOf(process.pid()), process);
            runInNewTab(process, command);
        } catch (Exception e) {
            logger.error(e.getMessage());
            new ErrorPopup(e.getMessage());
        }
    }

    private static PtyProcessBuilder buildProcess(Command command){
        HashMap<String, String> environment = new HashMap<>(System.getenv());
        return new PtyProcessBuilder().setWindowsAnsiColorEnabled(true).setEnvironment(environment).setCommand(command.getArgumentList()).setDirectory(command.isWsl() ? "/" : command.getPath());
    }

    private static OutputTab runInNewTab(PtyProcess process, Command command) {
        Controller controller = ControllerRegistry.get("main", Controller.class);

        TabPane tabs = controller.getTabPane();
        OutputTab outputTab = new OutputTab.OutputTabBuilder(TerminalFactory.createTerminalWidget(process))
                .setTabName(command.getName())
                .setCommandDisplayName(command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .build();
        Platform.runLater(() -> {
            tabs.getTabs().add(outputTab);
            tabs.getSelectionModel().select(outputTab);
        });
        runCommand(outputTab, command.getArgumentsString());
        return outputTab;
    }

    private static void updateSequentialTabName(OutputTab tab, CommandSequence commandSequence, Command singleCommandInSequence ){
        if (commandSequence.getSequenceName() != null && !commandSequence.getSequenceName().isBlank()) {
            tab.setText(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
        } else{
            tab.setText(singleCommandInSequence.getName().replace("\"", ""));
        }
    }

    public static void executeSequential(CommandSequence commandSequence){
      new Thread(() -> {
            PtyProcessBuilder processBuilder = null;
            PtyProcess process = null;
            OutputTab tab = null;
          List<Command> commandList = commandSequence.getCommandList();
          for (int i = 0; i < commandList.size(); i++) {
              Command currentCommand = commandList.get(i);
              try {
                  if (processBuilder == null) {
                      processBuilder = buildProcess(currentCommand);
                      process = processBuilder.start();
                  }
                  if (tab != null) {
                      OutputTab finalTab = tab;
                      Platform.runLater(() -> finalTab.appendTooltipLineText(currentCommand.getArgumentsString()));
                          if (i < commandList.size()) {
                              process.getOutputStream().write(currentCommand.getRawArgumentsString().getBytes());
                              if(Utils.checkIfWindows() && !currentCommand.isWsl()){
                                  process.getOutputStream().write("\r".getBytes());
                              }
                              process.getOutputStream().write("\n".getBytes());
                              process.getOutputStream().flush();
                          }
                      //delay per command happens here
                      Thread.sleep(commandSequence.getDelayPerCommand());
                  } else {
                      tab = runInNewTab(process, currentCommand);
                  }
                  OutputTab finalTab = tab;
                  Platform.runLater(() -> updateSequentialTabName(finalTab, commandSequence, currentCommand));
              } catch (IOException | InterruptedException e) {
                  logger.error(e.getMessage());
              }
          }
          Platform.runLater(()->{
              Notification.display(commandSequence.getSequenceName() + " has finished", null, null, NotificationType.INFO);
          });
        }).start();
    }

    public static void executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        List<Entry> entries = Entry.getEntriesFromPane(container);
        List<Command> seqCommands = new ArrayList<>();
        List<CommandExecutionTask> tasks = new ArrayList<>();
        int i = 0;
        for (Entry entry : entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry);
            if (seqOption) {
                seqCommands.add(cmd);
            } else {
                long delay = calculateDelay(i++, delayPerCmd);
                tasks.add(new CommandExecutionTask(cmd, delay));
            }
        }
        if (seqOption) {
            CommandSequence commandSequence = new CommandSequence(seqCommands, delayPerCmd, seqName);
            CommandExecution.executeSequential(commandSequence);
        } else {
            for(CommandExecutionTask t : tasks){
                new Thread(t).start();
            }
        }
    }

    private static void runCommand(OutputTab outputTab, String command){
        outputTab.setCommandDisplayName(command);
        outputTab.getTerminal().start();
    }

}
