package org.ashot.microservice_starter.node.tabs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.Preset;
import org.ashot.microservice_starter.data.constant.PresetType;
import org.ashot.microservice_starter.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PresetSetupTab extends Tab{
    private static final Logger logger = LoggerFactory.getLogger(PresetSetupTab.class);
    public PresetSetupTab(){
        setupPresetTab();
    }

    private static final String TAB_NAME = "Preset Setup";
    private static final String COMMANDS = "commands";
    private static final String PATHS = "paths";
    private static final String SAVE_LOCATION = "presets.json";
    public static final Map<String, String> commandsMap= new HashMap<>();
    public static final Map<String, String> pathsMap= new HashMap<>();

    public void setupPresetTab(){
        this.setId("presetSetupTab");
        this.setText(TAB_NAME);
        this.setClosable(false);

        ScrollPane scrollPane = setupScrollPane();
        scrollPane.setFitToWidth(true);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(5, 10, 5, 10));
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        TableView<Preset> commandsTable= createTable(PresetType.COMMAND);
        TableView<Preset> pathsTable = createTable(PresetType.PATH);

        setupFromFile(commandsTable, pathsTable);

        Button addCommandRow = addRowButton(commandsTable);
        Button addPathRow = addRowButton(pathsTable);
        Button saveAll = saveButton(commandsTable, pathsTable);
        vBox.getChildren().addAll(
                setupCategoryTitle("Commands"), commandsTable, addCommandRow,
                setupCategoryTitle("Paths"), pathsTable, addPathRow,
                saveAll
        );
        scrollPane.setContent(vBox);
        this.setContent(scrollPane);
    }

    private static boolean setupFromFile(TableView<Preset> commandsTable, TableView<Preset> pathsTable){
        File file = new File(SAVE_LOCATION);
        return file.exists() ? loadExisting(file, commandsTable, pathsTable) : createNewPresetsFile(file);
    }

    private static boolean saveToFile(TableView<Preset> commandsTable, TableView<Preset> pathsTable){
        File file = new File(SAVE_LOCATION);
        JSONObject jsonObject = new JSONObject();
        JSONArray commands = new JSONArray();
        for (Preset p : commandsTable.getItems()){
            JSONObject row = new JSONObject();
            row.put("name", p.getName());
            row.put("value", p.getValue());
            commands.put(row);
        }
        JSONArray paths = new JSONArray();
        for (Preset p : pathsTable.getItems()){
            JSONObject row = new JSONObject();
            row.put("name", p.getName());
            row.put("value", p.getValue());
            paths.put(row);
        }

        jsonObject.put(COMMANDS, commands);
        jsonObject.put(PATHS, paths);

        return Utils.writeDataToFile(file, jsonObject);
    }

    private static boolean loadExisting(File file, TableView<Preset> commandsTable, TableView<Preset> pathsTable){
        JSONObject jsonObject = Utils.createJSONObject(file);
        JSONArray commands = jsonObject.getJSONArray(COMMANDS);
        JSONArray paths = jsonObject.getJSONArray(PATHS);
        for (int i = 0; i < commands.toList().size(); i++) {
            JSONObject o = commands.getJSONObject(i);
            String name = o.getString("name");
            String value = o.getString("value");
            commandsMap.put(name, value);
            loadRow(commandsTable, new Preset(name, value));
        }
        for (int i = 0; i < paths.toList().size(); i++) {
            JSONObject o = paths.getJSONObject(i);
            String name = o.getString("name");
            String value = o.getString("value");
            pathsMap.put(name, value);
            loadRow(pathsTable, new Preset(name, value));
        }
        return true;
    }

    private static void loadRow(TableView<Preset> table, Preset preset){
        table.getItems().add(preset);
    }

    private static boolean createNewPresetsFile(File file){
        try {
            if(!file.createNewFile()){
                return false;
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray commands = new JSONArray();
            JSONArray paths = new JSONArray();
            jsonObject.put(COMMANDS, commands);
            jsonObject.put(PATHS, paths);
            return Utils.writeDataToFile(file, jsonObject);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    private static TableView<Preset> createTable(PresetType presetType){
        TableView<Preset> tableView = new TableView<>();
        
        TableColumn<Preset, String> nameCol = new TableColumn<>("Name");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> {
            Preset preset = event.getRowValue();
            preset.setName(event.getNewValue());
        });

        TableColumn<Preset, String> valueCol = new TableColumn<>(presetType.getValue());
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setOnEditCommit(event -> {
            Preset preset = event.getRowValue();
            preset.setValue(event.getNewValue());
            if(PresetType.COMMAND.equals(presetType)){
                commandsMap.put(preset.getName(), preset.getValue());
            }else{
                pathsMap.put(preset.getName(), preset.getValue());
            }
        });

        tableView.getColumns().addAll(nameCol, valueCol);
        tableView.autosize();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        return tableView;
    }

    private static ScrollPane setupScrollPane(){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(Main.SIZE_X);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private static Label setupCategoryTitle(String name){
        Label title = new Label(name);
        title.setFont(Font.font(24));
        return title;
    }

    private static Button addRowButton(TableView<Preset> tableView){
        Button addRowButton = new Button("Add");
        addRowButton.setOnAction(_-> tableView.getItems().add(new Preset()));
        return addRowButton;
    }

    private static Button saveButton(TableView<Preset> commandsTable, TableView<Preset> pathsTable) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(_-> saveToFile(commandsTable, pathsTable));
        return saveButton;
    }

}
