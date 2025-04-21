package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.node.TabOutput;
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
    private static final int MAX_LINES = 2500;
    private final TabOutput tabOutput;
    private final Tab tab;
    private final CodeArea codeArea;
    private final Process process;
    private final long startTime;

    private final List<String> pendingLines = Collections.synchronizedList(new ArrayList<>());

    public CommandOutputThread(TabOutput tabOutput) {
        this.tabOutput = tabOutput;
        this.tab = tabOutput.getTab();
        this.process = tabOutput.getProcess();
        this.codeArea = tabOutput.getCodeArea();
        this.startTime = System.currentTimeMillis();
    }

    public void run() {
        setupScheduledOutputPolling();
        setupOutputReading();
    }

    private void setupScheduledOutputPolling() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
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
                    appendColoredLine(stringBuilder.toString(), codeArea);
                    int lines = ((List<?>) codeArea.getParagraphs()).size();
                    if (lines > MAX_LINES) {
                        int end = codeArea.getAbsolutePosition(lines - MAX_LINES, 0);
                        codeArea.deleteText(0, end);
                    }
                    if (!tabOutput.usedScrolling()) {
                        codeArea.moveTo(codeArea.getLength());
                        codeArea.requestFollowCaret();
                    }
                });
            }
        }, 0, 5, TimeUnit.MILLISECONDS);
    }

    private void setupOutputReading() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
                if (!line.isBlank()) {
                    pendingLines.add(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        pendingLines.add("\nCommand(s) finished in: " + ((System.currentTimeMillis() - startTime)/ 1000l)  + "s");
    }

    private void appendColoredLine(String line, CodeArea codeArea) {
        int start = codeArea.getLength();
        line = line.replaceAll("\u001B\\[[;\\d]*m", ""); // Strip for display
        codeArea.appendText(line);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        String defaultFg = Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
        spans.add(List.of(defaultFg), codeArea.getLength());
        codeArea.setStyleSpans(start, spans.create());
    }
        /*try {
        int start = codeArea.getLength();
            StyleSpans<Collection<String>> styles = AnsiColorParser.parse(line);
            codeArea.setStyleSpans(start, styles);
        }catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }*/
}
