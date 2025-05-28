package org.ashot.microservice_starter.task;

import javafx.application.Platform;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.data.message.OutputMessages;
import org.ashot.microservice_starter.node.notification.Notification;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.ashot.microservice_starter.registry.ControllerRegistry.getMainController;

public class CommandOutputTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CommandOutputTask.class);
    private static final int MAX_LINES = 8000;
    private boolean darkTheme = false;
    private final long startTime;
    private final OutputTab outputTab;
    private final List<String> pendingLines = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService writeToOutputExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService pauseCheckExecutor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean paused = false;

    private final Object pauseLock = new Object();

    public CommandOutputTask(OutputTab outputTab) {
        this.outputTab = outputTab;
        this.startTime = System.currentTimeMillis();
        outputTab.setCommandOutputThread(this);
    }

    public void run() {
        Platform.runLater(()->{
            Notification.display(outputTab.getText() + " has started", null, ()-> getMainController().getTabPane().getSelectionModel().select(outputTab), NotificationType.INFO);
        });
        setupScheduledOutputPolling();
        Thread readOutputThread = new Thread(this::setupOutputReading);
        readOutputThread.start();
        Thread readErrorThread = new Thread(this::setupOutputErrReading);
        readErrorThread.start();
        pauseChecking();
    }

    private void pauseChecking(){
        pauseCheckExecutor.scheduleAtFixedRate(()-> {
            synchronized (pauseLock) {
                if (paused) {

                } else {
                    pauseLock.notifyAll();
                }
            }
        },0 ,25, TimeUnit.MILLISECONDS);
    }

    private void setupScheduledOutputPolling() {
        writeToOutputExecutor.scheduleAtFixedRate(() -> {
            checkThemeChange();
            CodeArea codeArea = outputTab.getCodeArea();
            if (!pendingLines.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                List<String> batch;
                synchronized (pendingLines) {
                    batch = new ArrayList<>(pendingLines);
                    pendingLines.clear();
                }
                Platform.runLater(() -> {
                    for (String newLine : batch) {
                        stringBuilder.append(newLine).append("\n");
                    }
                    outputTab.appendLine(stringBuilder.toString());
                    int lines = codeArea.getParagraphs().size();
                    if (lines > MAX_LINES) {
                        int end = codeArea.getAbsolutePosition(lines - MAX_LINES, 0);
                        codeArea.deleteText(0, end);
                    }
                    if (outputTab.getOutputSearchOptions().autoScroll()) {
                        codeArea.moveTo(codeArea.getLength());
                        codeArea.requestFollowCaret();
                    }
                });
            }
        }, 0, 5, TimeUnit.MILLISECONDS);
    }

    private void checkThemeChange() {
        if (darkTheme != Main.getDarkModeSetting()) {
            Platform.runLater(() -> {
                darkTheme = Main.getDarkModeSetting();
                CodeArea codeArea = outputTab.getCodeArea();
                codeArea.getStyleClass().addAll("output");
                StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
                String defaultFg = darkTheme ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
                spans.add(List.of(defaultFg), codeArea.getLength());
                codeArea.setStyleSpans(0, spans.create());
            });
        }
    }

    private void setupOutputReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(outputTab.getProcess().getInputStream()));
        pendingLines.addFirst(OutputMessages.currentlyRunningCommand(outputTab.getCommandDisplayName()));
        readLineFromStream(reader, false);
        pendingLines.add(OutputMessages.commandFinishedMessage(startTime));
        try {
            Process process = outputTab.getProcess();
            process.waitFor();
            if(process.exitValue() != 0) {
                Platform.runLater(() -> {
                    outputTab.appendErrorLine(OutputMessages.failureMessage(outputTab.getCommandDisplayName(), outputTab.getText(), String.valueOf(process.exitValue())));
                    Notification.display("Failure for: " + outputTab.getText(), "Exited with code: " + process.exitValue(), null, NotificationType.ERROR);
                });
            } else{
                Platform.runLater(()->{
                    Notification.display(outputTab.getText() + " has exited", null, ()-> getMainController().getTabPane().getSelectionModel().select(outputTab), NotificationType.INFO);
                });
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void setupOutputErrReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(outputTab.getProcess().getErrorStream()));
        readLineFromStream(reader, true);
    }

    private void readLineFromStream(BufferedReader reader, boolean errorMode) {
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                synchronized (pauseLock){
                    while (paused){
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                if (!line.isBlank()) {
                    if(pendingLines.size() > 100){
                        Thread.sleep(100);
                    }
                    if(errorMode){
/*
                        String finalLine = line;
                        Platform.runLater(()->{
                            Notification.display(
                                    "Error in: " + outputTab.getText(),
                                    finalLine,
                                    ()->{
                                        Main.getPrimaryStage().toFront();
                                        getMainController().getTabPane().getSelectionModel().select(outputTab);
                                        },
                                    NotificationType.EXECUTION_FAILURE);
                        });
*/
                    }
                    pendingLines.add(line);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void unpause(){
        paused = false;
    }
}
