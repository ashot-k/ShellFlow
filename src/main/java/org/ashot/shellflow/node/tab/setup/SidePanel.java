package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.Spacer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.entry.variable.VariableEntry;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;
import static org.ashot.shellflow.data.constant.SettingsFilePaths.VARIABLES;


public class SidePanel extends VBox {
    private static final Logger log = LoggerFactory.getLogger(SidePanel.class);
    private final double COLLAPSED_WIDTH = 40;
    private final double EXPANDED_WIDTH = 550;

    private final StackPane stackPane;
    private boolean expanded = false;
    private final Timeline animation = new Timeline();
    private final Button toggleButton;
    private Button addVariableButton;
    private VBox variableSetup;
    private VBox variableSetupContainer;


    public SidePanel() {
        toggleButton = new Button("", Icons.getSidePanelToggle(DEFAULT_BUTTON_ICON_SIZE, expanded));
        toggleButton.setPadding(Insets.EMPTY);
        toggleButton.setBackground(Background.EMPTY);

        toggleButton.setOnAction(_->{
            expanded = !expanded;
            toggleSidePanel(expanded);
        });

/*
        focusWithinProperty().addListener((_, _, focus) -> {
            expanded = focus;
            toggleSidePanel(expanded);
        });
*/

/*
        hoverProperty().addListener((_, _, hovering) -> {
            expanded = hovering;
            toggleSidePanel(expanded);
        });
*/

        Label variableSetupTitle = new Label("Variables");
        variableSetupTitle.setFont(Fonts.title());
        variableSetup = new VBox(5, variableSetupTitle);
        variableSetup.setPadding(new Insets(5));
        variableSetup.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = new ScrollPane(variableSetup);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(EXPANDED_WIDTH - 30);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        addVariableButton = new Button("", Icons.getAddButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        addVariableButton.setOnAction(_-> addVariableEntry());
        Button saveAll = saveButton();
        HBox variableOptions = new HBox(5, saveAll, addVariableButton);
        variableOptions.setAlignment(Pos.TOP_RIGHT);
        variableOptions.setPadding(new Insets(10));

        variableSetupContainer = new VBox(5, scrollPane, new Spacer(), variableOptions);

        stackPane = new StackPane(variableSetupContainer, toggleButton);
        stackPane.setPadding(new Insets(10));

        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);

        VBox.setVgrow(stackPane, Priority.ALWAYS);
        getChildren().addAll(stackPane);
        setSpacing(20);
        setMaxWidth(COLLAPSED_WIDTH);
        getStyleClass().addAll("solid-bg-container");
        toggleSidePanel(false);

        File file = new File(ShellFlow.getConfig().getVariablesConfigLocation());
        setupFromFile();
    }

    private void removeVariableEntry(VariableEntry variableEntry){
        variableSetup.getChildren().remove(variableEntry);
    }

    private void addVariableEntry(){
        addVariableEntry("","");
    }

    private void addVariableEntry(String name, String value){
        VariableEntry variableEntry = new VariableEntry(name, value);
        variableEntry.setOnRemove(_-> removeVariableEntry(variableEntry));
        variableSetup.getChildren().add(variableEntry);
    }

    public List<VariableEntry> getVariableEntries(){
        return variableSetup.getChildren().stream().filter(e -> e instanceof VariableEntry).map(e ->(VariableEntry) e).toList();
    }

    private void toggleSidePanel(boolean expanded){
        if(expanded){
            animation.stop();
            animation.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(maxWidthProperty(), COLLAPSED_WIDTH)),
                    new KeyFrame(Duration.millis(200), new KeyValue(maxWidthProperty(), EXPANDED_WIDTH))
            );
            animation.play();
            if (!stackPane.getChildren().contains(variableSetupContainer)) {
                stackPane.getChildren().addFirst(variableSetupContainer);
            }
        }
        else{
            animation.stop();
            setMaxWidth(COLLAPSED_WIDTH);
            stackPane.getChildren().remove(variableSetupContainer);
        }
        toggleButton.setGraphic(Icons.getSidePanelToggle(DEFAULT_BUTTON_ICON_SIZE, expanded));
    }


    private void createNewVariablesFile(File file){
        try {
            if (!file.createNewFile()) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray variables= new JSONArray();
            jsonObject.put("variables", variables);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadExisting(File file) {
        JSONObject jsonObject = Utils.createJSONObject(file);
        JSONArray variables = jsonObject.getJSONArray("variables");
        for (int i = 0; i < variables.toList().size(); i++) {
            JSONObject o = variables.getJSONObject(i);
            String name = o.optString("name");
            String value = o.optString("value");
            addVariableEntry(name, value);
        }
    }

    private void setupFromFile() {
        File file = new File(ShellFlow.getConfig().getVariablesConfigLocation());
        if (file.exists()) {
            loadExisting(file);
        } else {
            createNewVariablesFile(file);
        }
    }

    private void saveToFile() {
        File file = new File(VARIABLES.getValue());
        JSONObject jsonObject = new JSONObject();
        JSONArray variables = new JSONArray();
        for (VariableEntry variable : getVariableEntries()) {
            JSONObject row = createVariableJSONEntry(variable);
            variables.put(row);
        }
        jsonObject.put("variables", variables);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    private static JSONObject createVariableJSONEntry(VariableEntry entry) {
        JSONObject row = new JSONObject();
        row.put("name", entry.getNameFieldValue());
        row.put("value", entry.getValueFieldValue());
        return row;
    }

    private Button saveButton() {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(_ -> saveToFile());
        saveButton.setPrefWidth(80);
        return saveButton;
    }
}

