package org.ashot.microservice_starter.node;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import org.ashot.microservice_starter.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class OutputTab extends Tab{
    private static final Logger logger = LoggerFactory.getLogger(OutputTab.class);
    private final VirtualizedScrollPane<CodeArea> scrollPane;
    private final CodeArea codeArea;
    private final Process process;
    private final OutputTabOptions outputTabOptions;
    private boolean usedScrolling = false;

    public OutputTab(CodeArea codeArea, Process process, String name){
        this.outputTabOptions = new OutputTabOptions(this);
        this.codeArea = codeArea;
        this.process = process;
        this.scrollPane = new VirtualizedScrollPane<>(codeArea);
        setupOutputTab(name);
    }

    public void setupOutputTab(String name){
        this.codeArea.getStyleClass().add("command-output-container");
        this.codeArea.getStyleClass().add(Main.getDarkModeSetting() ? "dark-mode-text" : "light-mode-text");
        this.codeArea.setEditable(false);
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if(e.getDeltaY() < 0) {
                if (scrollPane.getTotalHeightEstimate() - scrollPane.getEstimatedScrollY() <= 1000) {
                    this.usedScrolling = false;
                }
            }
            else{
                this.usedScrolling = true;
            }
        });
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setText(name.replace("\"", ""));
        this.setContent(scrollPane);
        this.setClosable(true);
        this.setOnClosed(_-> this.process.destroy());
        this.setupUserInput();
    }

    private void setupUserInput(){
        this.setOnSelectionChanged((_)->{
            if(this.isSelected()){
                this.codeArea.setOnKeyPressed((event)->{
                    try {
                        if(event.isControlDown() && event.getCode() == KeyCode.C){
                            this.process.destroy();
                        }
                        else if(event.getCode() == KeyCode.ENTER){
                            process.getOutputStream().write('\n');
                        }
                        else {
                            process.getOutputStream().write(event.getCode().getChar().getBytes());
                        }
                        process.getOutputStream().flush();
                        Platform.runLater(()-> appendColoredLine(event.getCode().getChar()));
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
            }

        });

    }

    public void appendColoredLine(String line) {
        int start = codeArea.getLength();
        line = line.replaceAll("\u001B\\[[;\\d]*m", ""); // Strip for display
        this.codeArea.appendText(line);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        String defaultFg = Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
        spans.add(List.of(defaultFg), this.codeArea.getLength());
        this.codeArea.setStyleSpans(start, spans.create());
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }
    public boolean usedScrolling() {
        return usedScrolling;
    }
    public Process getProcess() {
        return process;
    }
    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return scrollPane;
    }
    public OutputTabOptions getOutputTabOptions() {
        return outputTabOptions;
    }
}
