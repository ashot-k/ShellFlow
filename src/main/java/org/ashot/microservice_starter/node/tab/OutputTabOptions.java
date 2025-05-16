package org.ashot.microservice_starter.node.tab;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.CodeAreaSearch;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OutputTabOptions extends HBox {

    private static final Logger log = LoggerFactory.getLogger(OutputTabOptions.class);
    private final OutputTab outputTab;
    private CodeAreaSearch search;
    private final TextField searchField = new TextField();
    private final TextField inputField = new TextField();
    private final CodeArea codeArea;
    private boolean autoScroll = true;

    public OutputTabOptions(OutputTab outputTab, CodeArea codeArea) {
        this.outputTab = outputTab;
        this.codeArea = codeArea;
        setupOptions();
    }

    private void setupOptions() {
        HBox optionRow = new HBox(15, createFind(), createInput());
        optionRow.setFillHeight(true);
        optionRow.setAlignment(Pos.CENTER);
        optionRow.setPadding(new Insets(0, 15, 0, 15));
        VBox box = new VBox(createHeader(), optionRow);
        box.setStyle("-fx-border-style: solid solid none none; -fx-border-width: 1; -fx-border-color: #1f242d;");
        this.getChildren().addAll(box);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    private VBox createHeader() {
        Button close = new Button("", Icons.getCloseButtonIcon(20));
        close.setOnAction(_ -> outputTab.toggleOptions());
        close.getStyleClass().add("no-outline-btn");
        VBox header = new VBox(0, close);
        header.setAlignment(Pos.TOP_RIGHT);
        header.setPadding(new Insets(2, 4, 0 ,0));
        header.setFillWidth(true);
        return header;
    }

    private VBox createFind() {
        Label results = new Label();
        search = new CodeAreaSearch(results, codeArea);
        searchField.setOnKeyPressed(this::handleSearchFieldUserInput);
        searchField.textProperty().addListener((_, _, input) -> {
            autoScroll = false;
            search.resetFindIndexToStart();
            search.performForwardSearch(input);
        });
        HBox resultsContainer = new HBox(results);
        results.setLabelFor(searchField);
        resultsContainer.setAlignment(Pos.CENTER_RIGHT);
        return new VBox(new Label("Find"), searchField, resultsContainer);
    }

    private VBox createInput() {
        inputField.setOnKeyPressed(this::handleInputFieldUserInput);
        return new VBox(new Label("Input"), inputField);
    }

    private void handleSearchFieldUserInput(KeyEvent event) {
        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            autoScroll = false;
            search.performBackwardSearch(search.getCurrentInput());
        } else if (event.getCode() == KeyCode.ENTER) {
            autoScroll = false;
            search.performForwardSearch(search.getCurrentInput());
        }
    }

    private void handleInputFieldUserInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                outputTab.getProcess().getOutputStream().write(inputField.getText().getBytes());
                outputTab.getProcess().getOutputStream().write("\n".getBytes());
                outputTab.getProcess().getOutputStream().flush();
                Platform.runLater(() -> outputTab.appendColoredLine("\n------\nYour input: " + inputField.getText() + "\n------\n"));
            } catch (IOException e) {
                this.inputField.setDisable(true);
                ErrorPopup.errorPopup("Process output stream is closed");
                log.error(e.getMessage());
            }
        }
    }

    public CodeAreaSearch getSearch() {
        return search;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public boolean autoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }
}
