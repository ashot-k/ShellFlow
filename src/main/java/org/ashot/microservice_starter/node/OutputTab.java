package org.ashot.microservice_starter.node;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTab extends Tab{
    private static final Logger logger = LoggerFactory.getLogger(OutputTab.class);
    private final VirtualizedScrollPane<CodeArea> scrollPane;
    private final CodeArea codeArea;
    private final Process process;
    private boolean usedScrolling = false;

    public OutputTab(CodeArea codeArea, Process process, String name){
        this.codeArea = codeArea;
        this.process = process;
        this.scrollPane = new VirtualizedScrollPane<>(codeArea);
        setupOutputTab(name);
    }

    public void setupOutputTab(String name){
        this.codeArea.setPrefWidth(Main.SIZE_X);
        this.codeArea.getStyleClass().add("command-output-container");
        this.codeArea.getStyleClass().add(Main.getDarkModeSetting() ? "dark-mode-text" : "light-mode-text");
        this.codeArea.setEditable(false);
        VirtualizedScrollPane<CodeArea> v = scrollPane;
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if(e.getDeltaY() < 0) {
                if (v.getTotalHeightEstimate() - v.getEstimatedScrollY() <= 1000) {
                    this.usedScrolling = false;
                }
            }
            else{
                this.usedScrolling = true;
            }
        });
        this.scrollPane.setPrefWidth(Main.SIZE_X);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setText(name.replace("\"", ""));
        this.setContent(scrollPane);
        this.setClosable(true);
        this.setOnClosed(_-> this.process.destroy());
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
}
