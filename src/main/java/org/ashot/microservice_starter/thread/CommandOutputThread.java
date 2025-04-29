package org.ashot.microservice_starter.thread;

import javafx.application.Platform;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.node.tabs.OutputTab;
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

public class CommandOutputThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CommandOutputThread.class);
    private static final int MAX_LINES = 9000;
    private final OutputTab outputTab;
    private final CodeArea codeArea;
    private final Process process;
    private final long startTime;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private boolean darkTheme = Main.getDarkModeSetting();
    private final List<String> pendingLines = Collections.synchronizedList(new ArrayList<>());

    public CommandOutputThread(OutputTab outputTab) {
        this.outputTab = outputTab;
        this.process = outputTab.getProcess();
        this.codeArea = outputTab.getCodeArea();
        this.startTime = System.currentTimeMillis();
    }

    public void run() {
        setupScheduledOutputPolling();
        new Thread(this::setupOutputReading).start();
        new Thread(this::setupOutputErrReading).start();
    }

    private void setupScheduledOutputPolling() {
        executorService.scheduleAtFixedRate(() -> {
            checkThemeChange();
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
                    if (!outputTab.usedScrolling()) {
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
                this.codeArea.getStyleClass().removeAll("light-mode", "dark-mode");
                this.codeArea.getStyleClass().add(darkTheme ? "dark-mode" : "light-mode");
                int start = 0;
                StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
                String defaultFg = darkTheme ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
                spans.add(List.of(defaultFg), this.codeArea.getLength());
                this.codeArea.setStyleSpans(start, spans.create());
            });
        }
    }

    private void setupOutputReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        readLineFromStream(reader);
        pendingLines.add("\nCommand(s) finished in: " + ((System.currentTimeMillis() - startTime) / 1000L) + "s");
    }

    private void setupOutputErrReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        readLineFromStream(reader);
    }

    private void readLineFromStream(BufferedReader reader) {
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    if(pendingLines.size() > 50){
                        Thread.sleep(20);
                    }
                    pendingLines.add(line);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

}
