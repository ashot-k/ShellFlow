package org.ashot.microservice_starter.node.tab;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.constant.ProcessStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProfilerTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(ProfilerTab.class);
    private final ScheduledExecutorService checkProcessesTask = Executors.newSingleThreadScheduledExecutor();
    private final VBox content = new VBox();
    private final List<Node> profilerProcessNodeList = new ArrayList<>();

    public ProfilerTab(){
        setupProfilerTab();
    }

    public void start(){
        checkProcessesStart();
    }

    private void checkProcessesStart(){
        checkProcessesTask.scheduleAtFixedRate(()-> {
            try {
                profilerProcessNodeList.stream().filter(e -> e instanceof ProfilerProcessNode).map(o -> (ProfilerProcessNode) o).forEach(this::refreshProcess);
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void refreshProcesses(TabPane tabPane){
        profilerProcessNodeList.clear();
        content.getChildren().clear();
        List<OutputTab> outputTabs = OutputTab.getOutputTabsFromTabPane(tabPane);
        if(outputTabs.isEmpty()){
            content.getChildren().add(new Text("No active processes"));
        }
        else {
            for (OutputTab outputTab : outputTabs) {

                Process p = ((ProcessTtyConnector) outputTab.getTerminal().getTtyConnector()).getProcess();
                ProfilerProcessNode profilerProcessNode = new ProfilerProcessNode(p, String.valueOf(p.pid()), ProcessStatus.ACTIVE, outputTab);
                profilerProcessNodeList.addAll(List.of(profilerProcessNode, new Separator(Orientation.HORIZONTAL)));
            }
            content.getChildren().addAll(profilerProcessNodeList);
        }
    }

    private void refreshProcess(ProfilerProcessNode profilerProcessNode){
        Process refreshedProcess = ((ProcessTtyConnector) profilerProcessNode.getTab().getTerminal().getTtyConnector()).getProcess();
        ProcessStatus status;
        String exitCode = null;
        if(refreshedProcess.isAlive()){
            status = ProcessStatus.ACTIVE;
        }else{
            if(refreshedProcess.exitValue() != 0){
                status = ProcessStatus.FAILED;
                exitCode = String.valueOf(refreshedProcess.exitValue());
            }
            else{
                status = ProcessStatus.EXITED;
            }
        }
        StringBuilder ids = new StringBuilder();
        ids.append("Shell pid: ").append(refreshedProcess.pid()).append("\n");
        for (ProcessHandle p : refreshedProcess.descendants().toList()){
            ids.append("Process pid: ").append(p.pid()).append("\n");
        }
        String finalExitCode = exitCode;
        Platform.runLater(()->{
            profilerProcessNode.refreshStatus(status, finalExitCode);
            profilerProcessNode.refreshID(ids.isEmpty() ? String.valueOf(refreshedProcess.pid()) : ids.toString());
            profilerProcessNode.refreshCommand(profilerProcessNode.getTab().getCommandDisplayName());
            profilerProcessNode.refreshName(profilerProcessNode.getTab().getText());
        });
    }

    private void setupProfilerTab(){
        this.setText("Status");
        this.setClosable(false);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        this.setContent(scrollPane);
        content.setFillWidth(true);
        content.setPadding(new Insets(15));
    }
}
