package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.microservice_starter.AnsiColorParser;
import org.ashot.microservice_starter.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class CommandOutputThread implements Runnable{
    private final Tab tab;
    private final Process process;
    public CommandOutputThread(Tab tab, Process process){
        this.tab = tab;
        this.process = process;
    }
    public void run() {
        VirtualizedScrollPane v =  ((VirtualizedScrollPane) tab.getContent());
        CodeArea codeArea = (CodeArea) v.getContent();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String finalLine = line;
            Platform.runLater(() -> {
                System.out.println(finalLine); // Still logs to console
                if(!finalLine.isBlank()){
                    appendColoredLine(finalLine, codeArea);
                }
            });
        }
    }


    private void appendColoredLine(String line, CodeArea codeArea) {
        String plainText = line.replaceAll("\u001B\\[[;\\d]*m", ""); // Strip for display
        int start = codeArea.getLength();
        if(Main.getDarkModeSetting()) {
            codeArea.getStyleClass().add("dark-mode-text");
        }
        else {
            codeArea.getStyleClass().add("light-mode-text");
        }
        codeArea.appendText(plainText + "\n");
        try {
            StyleSpans<Collection<String>> styles = AnsiColorParser.parse(line);
            codeArea.setStyleSpans(start, styles);
        }catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }
    }
}
