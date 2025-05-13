package org.ashot.microservice_starter.node.tab;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.OutputSearch;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OutputTabOptions extends VBox {

    private static final Logger log = LoggerFactory.getLogger(OutputTabOptions.class);
    private final OutputTab outputTab;
    private OutputSearch search;
    private final TextField searchField = new TextField();
    private final TextField inputField = new TextField();
    private boolean usedScrolling;
    private final CodeArea codeArea;

    public OutputTabOptions(OutputTab outputTab, CodeArea codeArea){
        this.outputTab = outputTab;
        this.codeArea =codeArea;
        setupOptions();
    }

    private void setupOptions() {
        HBox optionRow = new HBox(
                new Separator(Orientation.VERTICAL), createFind(),
                new Separator(Orientation.VERTICAL), createInput(),
                new Separator(Orientation.VERTICAL)
                );
        optionRow.setFillHeight(true);
        this.getChildren().addAll(createHeader(), optionRow);
    }

    private VBox createHeader(){
        Separator top = new Separator(Orientation.HORIZONTAL);
        top.setPrefHeight(2); top.setMaxHeight(2); top.setMinHeight(2);
        Button close = new Button("", Icons.getCloseButtonIcon(24));
        close.setOnAction(_ -> outputTab.toggleOptions());
        close.getStyleClass().add("no-outline-btn");
        VBox header = new VBox(0, top, close);
        header.setAlignment(Pos.TOP_RIGHT);
        header.setPadding(new Insets(5, 5, 0, 5));
        return header;
    }

    private VBox createFind(){
        Label results = new Label();
        search = new OutputSearch(results, codeArea);
        searchField.setOnKeyPressed(this::handleSearchFieldUserInput);
        searchField.textProperty().addListener((_, _, input) -> {
            usedScrolling = true;
            search.resetFindIndexToStart();
            search.performForwardSearch(input);
        });
        HBox resultsContainer = new HBox(results);
        results.setLabelFor(searchField);
        resultsContainer.setAlignment(Pos.CENTER_RIGHT);
        return new VBox(new Label("Find"), searchField, resultsContainer);
    }

    private VBox createInput(){
        inputField.setOnKeyPressed(this::handleInputFieldUserInput);
        return new VBox(new Label("Input"), inputField);
    }

    private void handleSearchFieldUserInput(KeyEvent event){
        if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
            usedScrolling = true;
            search.performBackwardSearch(search.getCurrentInput());
        } else if (event.getCode() == KeyCode.ENTER) {
            usedScrolling = true;
            search.performForwardSearch(search.getCurrentInput());
        }
    }

    private void handleInputFieldUserInput(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
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

    public OutputSearch getSearch() {
        return search;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public boolean UsedScrolling() {
        return usedScrolling;
    }

    public void setUsedScrolling(boolean usedScrolling) {
        this.usedScrolling = usedScrolling;
    }
}
