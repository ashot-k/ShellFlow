package org.ashot.microservice_starter.node;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ScrollEvent;
import org.ashot.microservice_starter.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabOutput {
    private static final Logger logger = LoggerFactory.getLogger(TabOutput.class);
    private final CodeArea codeArea;
    private final Tab tab;
    private final Process process;
    private boolean usedScrolling = false;

    public TabOutput(Tab tab, CodeArea codeArea, Process process, String name){
        this.tab = tab;
        this.codeArea = codeArea;
        this.process = process;
        setupTab(name, process);
        setupTabOutput();
    }
    public void setupTabOutput(){
        codeArea.setPrefWidth(Main.SIZE_X);
        codeArea.getStyleClass().add("command-output-container");
        codeArea.getStyleClass().add(Main.getDarkModeSetting() ? "dark-mode-text" : "light-mode-text");
        codeArea.setEditable(false);
        VirtualizedScrollPane<CodeArea> v = ((VirtualizedScrollPane<CodeArea>) tab.getContent());
        codeArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if(e.getDeltaY() < 0) {
                if (v.getTotalHeightEstimate() - v.getEstimatedScrollY() <= 1000) {
                    logger.info("Scrolled to the end");
                    usedScrolling = false;
                }
            }
            else{
                usedScrolling = true;
            }
        });
    }

    private void setupTab(String name, Process process){
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        scrollPane.setPrefWidth(Main.SIZE_X);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tab.setText(name.replace("\"", ""));
        tab.setContent(scrollPane);
        tab.setClosable(true);
        tab.setOnClosed(_-> process.destroy());
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public Tab getTab() {
        return tab;
    }

    public boolean usedScrolling() {
        return usedScrolling;
    }

    public Process getProcess() {
        return process;
    }
}
