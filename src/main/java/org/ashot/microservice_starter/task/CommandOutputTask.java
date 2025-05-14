package org.ashot.microservice_starter.task;

import javafx.application.Platform;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.message.OutputMessages;
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

public class CommandOutputTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CommandOutputTask.class);
    private static final int MAX_LINES = 9000;
    private boolean darkTheme = Main.getDarkModeSetting();
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
                    outputTab.appendColoredLine(stringBuilder.toString());
                    int lines = codeArea.getParagraphs().size();
                    if (lines > MAX_LINES) {
                        int end = codeArea.getAbsolutePosition(lines - MAX_LINES, 0);
                        codeArea.deleteText(0, end);
                    }
                    if (!outputTab.getOutputSearchOptions().UsedScrolling()) {
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
                codeArea.getStyleClass().removeAll("light-mode", "dark-mode");
                codeArea.getStyleClass().add(darkTheme ? "dark-mode" : "light-mode");
                int start = 0;
                StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
                String defaultFg = darkTheme ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
                spans.add(List.of(defaultFg), codeArea.getLength());
                codeArea.setStyleSpans(start, spans.create());
            });
        }
    }

    private void setupOutputReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(outputTab.getProcess().getInputStream()));
        pendingLines.addFirst(OutputMessages.currentlyRunningCommand(outputTab.getCommand()));
        readLineFromStream(reader);
        pendingLines.add(OutputMessages.commandFinishedMessage(startTime));
    }

    private void setupOutputErrReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(outputTab.getProcess().getErrorStream()));
        readLineFromStream(reader);
    }

    private void readLineFromStream(BufferedReader reader) {
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
                        Thread.sleep(10);
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
