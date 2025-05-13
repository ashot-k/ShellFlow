package org.ashot.microservice_starter.node.tab;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.icon.Icons;
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
    private String command;
    private boolean searchVisible = false;

    public OutputTab(CodeArea codeArea, Process process, String name) {
        this.codeArea = codeArea;
        this.process = process;
        this.scrollPane = new VirtualizedScrollPane<>(codeArea);
        this.outputTabOptions = new OutputTabOptions(this, codeArea);
        setupOutputTab(name);
    }

    public void setupOutputTab(String name) {
        this.codeArea.getStyleClass().addAll("command-output-container", Main.getDarkModeSetting() ? "dark-mode" : "light-mode", Main.getDarkModeSetting() ? "dark-mode-text" : "light-mode-text");
        this.codeArea.setEditable(false);
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() < 0) {
                if (scrollPane.getTotalHeightEstimate() - scrollPane.getEstimatedScrollY() <= 1000) {
                    this.outputTabOptions.setUsedScrolling(false);
                }
            } else {
                this.outputTabOptions.setUsedScrolling(true);
            }
        });
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setText(name.replace("\"", ""));
        Button clearButton = new Button("Clear", Icons.getClearIcon(18));
        clearButton.setGraphicTextGap(20);
        clearButton.setContentDisplay(ContentDisplay.RIGHT);
        clearButton.setOnAction(_->{
            this.getCodeArea().clear();
            this.getOutputSearchOptions().setUsedScrolling(false);
        });
        HBox outputOptionsContainer = new HBox(clearButton);
        outputOptionsContainer.setPadding(new Insets(0, 10, 0, 10));

        this.setContent(new VBox(scrollPane, new Separator(Orientation.HORIZONTAL), outputOptionsContainer));
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.getOutputSearchOptions().getSearchField().focusedProperty().addListener((_, _, focused) -> {
            if(focused){
                this.commandOutputTask.pause();
            }
            else if(this.getTabPane().getScene().getWindow().isFocused()){
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
                if (searchVisible) {
                    closeOptions();
                    searchVisible = true;
                } else {
                    closeOptions();
                }
            }
            this.getSearchOuterContainer().setOnKeyPressed(this::handleSearchTogglingInput);
            this.codeArea.setOnKeyPressed(this::handleCodeAreaUserInput);
            this.codeArea.setOnMouseClicked((event -> {
                if(event.getButton().equals(MouseButton.SECONDARY)){
                    addSelectionToClipBoard();
                }
            }));
        });
    }

    private void handleSearchTogglingInput(KeyEvent event){
        if (event.isControlDown() && event.getCode() == KeyCode.F && isSelected()) {
            toggleOptions();
        }
    }

    public void toggleOptions(){
        if (!this.searchVisible) {
            showOptions();
        } else {
            closeOptions();
        }
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
                    Platform.runLater(() -> appendColoredLine("^C"));
                }
            }
            else if (event.getCode() == KeyCode.ENTER) {
                process.getOutputStream().write('\n');
            } else {
                process.getOutputStream().write((event.getText()).getBytes());
                Platform.runLater(() -> appendColoredLine(event.getText()));
            }
            process.getOutputStream().flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private VBox getSearchOuterContainer() {
        return (VBox) ControllerRegistry.get("main", Controller.class).getTabPane().getParent().getParent();
    }

    private void showOptions() {
        this.searchVisible = true;
        this.outputTabOptions.getSearch().setActive(true);
        Platform.runLater(() -> {
            getSearchOuterContainer().getChildren().add(Controller.SETUP_TABS, this.outputTabOptions);
            this.outputTabOptions.getSearchField().requestFocus();
        });
    }

    private void closeOptions() {
        this.searchVisible = false;
        this.outputTabOptions.getSearch().setActive(false);
        Platform.runLater(() -> getSearchOuterContainer().getChildren().remove(this.outputTabOptions));
    }

    public void appendColoredLine(String line) {
        int start = codeArea.getLength();
        line = line.replaceAll("\u001B\\[[;\\d]*m", ""); // strip ansi
        this.codeArea.appendText(line);
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        String defaultFg = Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
        spans.add(List.of(defaultFg), this.codeArea.getLength());
        this.codeArea.setStyleSpans(start, spans.create());
    }

    public void toggleWrapText(boolean option) {
        this.getCodeArea().setWrapText(option);
        this.getScrollPane().setHbarPolicy(option ? ScrollPane.ScrollBarPolicy.NEVER : ScrollPane.ScrollBarPolicy.AS_NEEDED);
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

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
