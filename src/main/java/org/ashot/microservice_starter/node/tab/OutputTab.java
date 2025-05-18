package org.ashot.microservice_starter.node.tab;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.task.CommandOutputTask;
import org.ashot.microservice_starter.utils.Utils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class OutputTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(OutputTab.class);
    private final VirtualizedScrollPane<CodeArea> scrollPane;
    private final CodeArea codeArea;
    private final OutputTabOptions outputTabOptions;
    private CommandOutputTask commandOutputTask;
    private Process process;
    private String commandDisplayName;
    private boolean searchVisible = false;

    private OutputTab(OutputTabBuilder outputTabBuilder){
        this.process = outputTabBuilder.process;
        this.commandDisplayName = outputTabBuilder.commandDisplayName;
        this.setTooltip(outputTabBuilder.tooltip);
        this.setText(outputTabBuilder.tabName);
        this.codeArea = new CodeArea();
        this.codeArea.setStyle("-fx-font-family: 'Noto-Sans'; -fx-font-size: 14");
        this.scrollPane = new VirtualizedScrollPane<>(codeArea);
        this.outputTabOptions = new OutputTabOptions(this);
        Platform.runLater(this::setupOutputTab);
    }

    public void setupOutputTab() {
        this.codeArea.getStyleClass().addAll("command-output-container", Main.getDarkModeSetting() ? "dark-mode" : "light-mode", Main.getDarkModeSetting() ? "dark-mode-text" : "light-mode-text");
        this.codeArea.setEditable(false);
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() < 0) {
                if (scrollPane.getTotalHeightEstimate() - scrollPane.getEstimatedScrollY() <= 1000) {
                    this.outputTabOptions.setAutoScroll(true);
                }
            } else {
                this.outputTabOptions.setAutoScroll(false);
            }
        });
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.setContent(new StackPane(scrollPane, OutputTabButton.clearOutputButton(this)));
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.getOutputSearchOptions().getSearchField().focusedProperty().addListener((_, _, focused) -> {
            if(focused){
                this.commandOutputTask.pause();
            }
            else if(this.getTabPane() != null && this.getTabPane().getScene().getWindow().isFocused()){
                this.commandOutputTask.unpause();
            }
        });
        this.setClosable(true);
        this.setOnClosed(_ -> Utils.killProcess(process));
        this.setupUserInput();
    }

    private void setupUserInput() {
        this.setOnSelectionChanged(_ -> {
            if (isSelected() && searchVisible) {
                showOptions();
            } else {
                closeOptions();
            }
        });
        this.getSearchOuterContainer().setOnKeyPressed(this::handleSearchTogglingInput);
        this.codeArea.setOnKeyPressed(this::handleCodeAreaUserInput);
        this.codeArea.setOnMouseClicked((event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)){
                addSelectionToClipBoard();
            }
        }));
    }

    private void handleSearchTogglingInput(KeyEvent event){
        if (isSelected() && event.isControlDown() && event.getCode() == KeyCode.F) {
            toggleOptions();
        }
    }

    public void toggleOptions(){
        if (!this.searchVisible) {
            showOptions();
        } else {
            closeOptions();
        }
        this.searchVisible = !this.searchVisible;
    }
    private void addSelectionToClipBoard(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(codeArea.getSelectedText());
        clipboard.setContent(clipboardContent);
    }

    private void handleCodeAreaUserInput(KeyEvent event) {
        try {
            if(event.isControlDown()) {
                if (event.getCode() == KeyCode.C && event.isShiftDown()) {
                    addSelectionToClipBoard();
                } else if (event.getCode() == KeyCode.C) {
                    Utils.killProcess(process);
                    Platform.runLater(() -> appendLine("^C"));
                }
            }
            else if (event.getCode() == KeyCode.ENTER) {
                process.getOutputStream().write('\n');
            } else {
                process.getOutputStream().write((event.getText()).getBytes());
                Platform.runLater(() -> appendLine(event.getText()));
            }
            process.getOutputStream().flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private VBox getSearchOuterContainer() {
        return ControllerRegistry.get("main", Controller.class).getSceneContainer();
    }

    private void showOptions() {
        Platform.runLater(() -> {
            getSearchOuterContainer().getChildren().add(2, this.outputTabOptions);
            this.outputTabOptions.getSearchField().requestFocus();
        });
    }

    private void closeOptions() {
        Platform.runLater(() -> getSearchOuterContainer().getChildren().remove(this.outputTabOptions));
    }

    public void appendLine(String line) {
        int start = codeArea.getLength();
        line = line.replaceAll("\u001B\\[[;\\d]*m", ""); // strip ansi
        this.codeArea.appendText(line);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        String defaultFg = Utils.getTextColorClass();
        spans.add(List.of(defaultFg), this.codeArea.getLength());
        this.codeArea.setStyleSpans(start, spans.create());
    }

    public void appendErrorLine(String line) {
        int start = codeArea.getLength();
        line = line.replaceAll("\u001B\\[[;\\d]*m", ""); // strip ansi
        this.codeArea.appendText(line);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        String defaultFg = Utils.getErrorTextColorClass();
        spans.add(List.of(defaultFg), this.codeArea.getLength());
        this.codeArea.setStyleSpans(start, spans.create());
    }

    public void appendTooltipLineText(String tooltipText){
        this.getTooltip().setText(this.getTooltip().getText() + "\n" + tooltipText);
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public Process getProcess() {
        return process;
    }

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return scrollPane;
    }

    public CommandOutputTask getCommandOutputThread() {
        return commandOutputTask;
    }

    public OutputTabOptions getOutputSearchOptions() {
        return outputTabOptions;
    }

    public void setCommandOutputThread(CommandOutputTask commandOutputTask) {
        this.commandOutputTask = commandOutputTask;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public static List<OutputTab> getOutputTabsFromTabPane(TabPane tabPane){
        return tabPane.getTabs().stream().filter(e -> e instanceof OutputTab).map(o -> (OutputTab) o).toList();
    }

    public void setCommandDisplayName(String commandDisplayName) {
        this.commandDisplayName = commandDisplayName;
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public static class OutputTabBuilder{

        private final Process process;

        private String tabName;
        private final Tooltip tooltip = new Tooltip();
        private String commandDisplayName;

        public OutputTabBuilder(Process process){
            this.process = process;
        }

        public OutputTabBuilder setTabName(String tabName){
            this.tabName = tabName;
            return this;
        }

        public OutputTabBuilder setCommandDisplayName(String commandDisplayName){
            this.commandDisplayName = commandDisplayName;
            return this;
        }

        public OutputTabBuilder setTooltip(String tooltipText){
            this.tooltip.setText(tooltipText);
            return this;
        }

        public OutputTab build(){
            return new OutputTab(this);
        }
    }
}
