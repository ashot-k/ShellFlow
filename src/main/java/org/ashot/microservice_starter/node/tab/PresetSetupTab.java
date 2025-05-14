package org.ashot.microservice_starter.node.tab;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.PresetType;
import org.ashot.microservice_starter.data.constant.SettingsFileNames;
import org.ashot.microservice_starter.utils.FileUtils;
import org.ashot.microservice_starter.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class PresetSetupTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(PresetSetupTab.class);

    public PresetSetupTab() {
        setupPresetTab();
    }

    private static final int SPACING = 10;
    private static final String TAB_NAME = "Preset Setup";
    private static final String COMMANDS = "commands";
    private static final String PATHS = "paths";
    public static final TableView<String> commandsTable = new TableView<>();
    public static final TableView<String> pathsTable = new TableView<>();

    public void setupPresetTab() {
        this.setId("presetSetupTab");
        this.setText(TAB_NAME);
        this.setClosable(false);

        ScrollPane scrollPane = setupScrollPane();
        scrollPane.setFitToWidth(true);

        HBox hbox = new HBox();
        hbox.setSpacing(SPACING);
        createTable(commandsTable, PresetType.COMMAND);
        createTable(pathsTable, PresetType.PATH);

        setupFromFile();

        Button addCommandRow = addRowButton(commandsTable);
        Button removeCommandRow = removeEntry(commandsTable);
        HBox commandButtons = new HBox(SPACING, addCommandRow, removeCommandRow);
        Button addPathRow = addRowButton(pathsTable);
        Button removePathRow = removeEntry(pathsTable);
        HBox pathButtons = new HBox(SPACING, addPathRow, removePathRow);

        Button saveAll = saveButton();
        saveAll.setPrefWidth(150);

        VBox commandsTableContainer = new VBox(SPACING, setupCategoryTitle("Commands"), commandsTable, commandButtons);
        commandsTableContainer.setFillWidth(true);
        commandsTableContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(commandsTableContainer, Priority.ALWAYS);

        VBox pathsTableContainer = new VBox(SPACING, setupCategoryTitle("Paths"), pathsTable, pathButtons);
        pathsTableContainer.setFillWidth(true);
        pathsTableContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(pathsTableContainer, Priority.ALWAYS);
        hbox.getChildren().addAll(commandsTableContainer, pathsTableContainer);

        VBox outerContainer = new VBox(SPACING, hbox, saveAll);
        outerContainer.setPadding(new Insets(10));
        outerContainer.setAlignment(Pos.CENTER);
        scrollPane.setContent(outerContainer);
        this.setContent(scrollPane);

        commandsTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) ->
            removeCommandRow.setDisable(newSelection == null)
        );
        pathsTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) ->
            removePathRow.setDisable(newSelection == null)
        );
    }

    private static void setupFromFile() {
        File file = new File(SettingsFileNames.PRESETS.getValue());
        if (file.exists()) {
            loadExisting(file);
        } else {
            createNewPresetsFile(file);
        }
    }

    private static void saveToFile() {
        File file = new File(SettingsFileNames.PRESETS.getValue());
        JSONObject jsonObject = new JSONObject();
        JSONArray commands = new JSONArray();
        for (String p : commandsTable.getItems()) {
            if(p.isBlank()) continue;
            JSONObject row = createJSONRow(p);
            commands.put(row);
        }
        JSONArray paths = new JSONArray();
        for (String p : pathsTable.getItems()) {
            if(p.isBlank()) continue;
            JSONObject row = createJSONRow(p);
            paths.put(row);
        }
        jsonObject.put(COMMANDS, commands);
        jsonObject.put(PATHS, paths);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    private static JSONObject createJSONRow(String p){
        JSONObject row = new JSONObject();
        row.put("value", p);
        return row;
    }

    private static void loadExisting(File file) {
        JSONObject jsonObject = Utils.createJSONObject(file);
        JSONArray commands = jsonObject.getJSONArray(COMMANDS);
        JSONArray paths = jsonObject.getJSONArray(PATHS);
        for (int i = 0; i < commands.toList().size(); i++) {
            JSONObject o = commands.getJSONObject(i);
            String value = o.optString("value");
            loadRow(commandsTable, value);
        }
        for (int i = 0; i < paths.toList().size(); i++) {
            JSONObject o = paths.getJSONObject(i);
            String value = o.optString("value");
            loadRow(pathsTable, value);
        }
    }

    private static void loadRow(TableView<String> table, String preset) {
        table.getItems().add(preset);
    }

    private static void createNewPresetsFile(File file) {
        try {
            if (!file.createNewFile()) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray commands = new JSONArray();
            JSONArray paths = new JSONArray();
            jsonObject.put(COMMANDS, commands);
            jsonObject.put(PATHS, paths);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void createTable(TableView<String> tableView, PresetType presetType) {
        TableColumn<String, String> valueCol = new TableColumn<>(presetType.getValue());
        valueCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            event.getTableView().getItems().set(row, event.getNewValue());
        });
        tableView.getColumns().add(valueCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        tableView.setPrefHeight(300);
    }

    private static ScrollPane setupScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(Main.SIZE_X);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private static Label setupCategoryTitle(String name) {
        Label title = new Label(name);
        title.setFont(Font.font(18));
        return title;
    }

    private static Button addRowButton(TableView<String> tableView) {
        Button addRowButton = new Button("Add");
        addRowButton.setOnAction(_ -> tableView.getItems().add(""));
        return addRowButton;
    }

    private static Button removeEntry(TableView<String> tableView){
        Button removeRowButton = new Button("Remove");
        removeRowButton.setDisable(true);
        removeRowButton.setOnAction(_ -> {
            String preset = tableView.getSelectionModel().getSelectedItem();
            tableView.getItems().remove(preset);
        });
        return removeRowButton;
    }

    private static Button saveButton() {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(_ -> saveToFile());
        return saveButton;
    }

}
