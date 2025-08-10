package org.ashot.shellflow.node.tab.preset;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.Preset;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.constant.PresetType;
import org.ashot.shellflow.data.constant.SettingsFilePaths;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PresetSetupTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(PresetSetupTab.class);

    public PresetSetupTab() {
        setupPresetTab();
    }

    private static final int SPACING = 10;
    private static final String TAB_NAME = "Preset Setup";
    private static final String COMMANDS = "commands";
    private static final String PATHS = "paths";
    public static final Map<String, String> commandsMap = new ConcurrentHashMap<>();
    private static final TableView<Preset> commandsTable = new TableView<>();
    public static final Map<String, String> pathsMap = new ConcurrentHashMap<>();
    private static final TableView<Preset> pathsTable = new TableView<>();


    public void setupPresetTab() {
        this.setId("presetSetupTab");
        this.setText(TAB_NAME);
        this.setClosable(false);

        ScrollPane scrollPane = setupScrollPane();
        scrollPane.setFitToWidth(true);

        HBox tablesContainer = new HBox();
        tablesContainer.setSpacing(SPACING);
        createTableContent(commandsTable, PresetType.COMMAND);
        createTableContent(pathsTable, PresetType.PATH);

        setupFromFile();

        Button addCommandRow = addRowButton(commandsTable);
        Button removeCommandRow = removeEntry(commandsTable, PresetType.COMMAND);
        HBox commandButtons = new HBox(SPACING, addCommandRow, removeCommandRow);
        Button addPathRow = addRowButton(pathsTable);
        Button removePathRow = removeEntry(pathsTable, PresetType.PATH);
        HBox pathButtons = new HBox(SPACING, addPathRow, removePathRow);

        VBox commandsTableContainer = new VBox(SPACING, setupCategoryTitle("Commands"), commandsTable, commandButtons);
        commandsTableContainer.setFillWidth(true);
        commandsTableContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(commandsTableContainer, Priority.ALWAYS);

        VBox pathsTableContainer = new VBox(SPACING, setupCategoryTitle("Paths"), pathsTable, pathButtons);
        pathsTableContainer.setFillWidth(true);
        pathsTableContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(pathsTableContainer, Priority.ALWAYS);
        tablesContainer.getChildren().addAll(commandsTableContainer, pathsTableContainer);

        Button saveAll = saveButton();

        VBox outerContainer = new VBox(SPACING, tablesContainer, saveAll);
        outerContainer.setPadding(new Insets(10, 20, 10, 20));
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
        File file = new File(Main.getConfig().getPresetConfigLocation());
        if (file.exists()) {
            loadExisting(file);
        } else {
            createNewPresetsFile(file);
        }
    }

    private static void saveToFile() {
        File file = new File(SettingsFilePaths.PRESETS.getValue());
        JSONObject jsonObject = new JSONObject();
        JSONArray commands = new JSONArray();
        for (Preset p : commandsTable.getItems()) {
            JSONObject row = createJSONRow(p);
            commands.put(row);
        }
        JSONArray paths = new JSONArray();
        for (Preset p : pathsTable.getItems()) {
            JSONObject row = createJSONRow(p);
            paths.put(row);
        }

        jsonObject.put(COMMANDS, commands);
        jsonObject.put(PATHS, paths);

        if (FileUtils.writeJSONDataToFile(file, jsonObject)) {
            refreshMaps();
        }
    }

    private static JSONObject createJSONRow(Preset p) {
        JSONObject row = new JSONObject();
        row.put("name", p.getName());
        row.put("value", p.getValue());
        return row;
    }

    private static void loadExisting(File file) {
        JSONObject jsonObject = Utils.createJSONObject(file);
        JSONArray commands = jsonObject.getJSONArray(COMMANDS);
        JSONArray paths = jsonObject.getJSONArray(PATHS);
        for (int i = 0; i < commands.toList().size(); i++) {
            JSONObject o = commands.getJSONObject(i);
            String name = o.optString("name");
            String value = o.optString("value");
            commandsMap.put(name, value);
            loadRow(commandsTable, new Preset(name, value));
        }
        for (int i = 0; i < paths.toList().size(); i++) {
            JSONObject o = paths.getJSONObject(i);
            String name = o.optString("name");
            String value = o.optString("value");
            pathsMap.put(name, value);
            loadRow(pathsTable, new Preset(name, value));
        }
    }

    private static void refreshMaps() {
        commandsTable.getItems().forEach(e -> {
            if (e.getName() != null && e.getValue() != null) {
                commandsMap.put(e.getName(), e.getValue());
            }
        });
        pathsTable.getItems().forEach(e -> {
            if (e.getName() != null && e.getValue() != null) {
                pathsMap.put(e.getName(), e.getValue());
            }
        });
    }

    private static void loadRow(TableView<Preset> table, Preset preset) {
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

    private static void createTableContent(TableView<Preset> tableView, PresetType presetType) {
        TableColumn<Preset, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(_ -> createPresetCellWithTooltip());
        nameCol.setOnEditCommit(event -> {
            Preset preset = event.getRowValue();
            preset.setName(event.getNewValue());
        });
        TableColumn<Preset, String> valueCol = new TableColumn<>(presetType.getValue());
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setCellFactory(_ -> createPresetCellWithTooltip());
        valueCol.setOnEditCommit(event -> {
            Preset preset = event.getRowValue();
            preset.setValue(event.getNewValue());
            if (PresetType.COMMAND.equals(presetType)) {
                commandsMap.put(preset.getName(), preset.getValue());
            } else {
                pathsMap.put(preset.getName(), preset.getValue());
            }
        });
        tableView.getColumns().addAll(nameCol, valueCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        tableView.setPrefHeight(360);
    }

    private static TextFieldTableCell<Preset, String> createPresetCellWithTooltip() {
        TextFieldTableCell<Preset, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
        Tooltip tooltip = new Tooltip();
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(700);
        cell.setOnMouseEntered(_ -> {
            if (cell.getItem() != null && !cell.getItem().isBlank()) {
                tooltip.setText(cell.getItem());
                tooltip.setShowDelay(Duration.millis(1));
                Point2D p = cell.localToScreen(0, cell.getHeight());
                tooltip.show(cell, p.getX(), p.getY());
            }
        });
        cell.setOnMouseExited(_ -> tooltip.hide());
        return cell;
    }

    private static ScrollPane setupScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(Main.SIZE_X);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private static Label setupCategoryTitle(String name) {
        Label title = new Label(name);
        title.setFont(Fonts.title);
        return title;
    }

    private static Button addRowButton(TableView<Preset> tableView) {
        Button addRowButton = new Button("Add");
        addRowButton.setOnAction(_ -> tableView.getItems().add(new Preset()));
        return addRowButton;
    }

    private static Button removeEntry(TableView<Preset> tableView, PresetType presetType) {
        Button removeRowButton = new Button("Remove");
        removeRowButton.setDisable(true);
        removeRowButton.setOnAction(_ -> {
            Preset preset = tableView.getSelectionModel().getSelectedItem();
            if(preset.getName() == null) return;
            if (PresetType.COMMAND.equals(presetType) && commandsMap.containsKey(preset.getName())) {
                commandsMap.remove(preset.getName());
            } else if (pathsMap.containsKey(preset.getName())) {
                pathsMap.remove(preset.getName());
            }
            tableView.getItems().remove(preset);
        });
        return removeRowButton;
    }

    private static Button saveButton() {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(_ -> saveToFile());
        saveButton.setPrefWidth(150);
        return saveButton;
    }

}
