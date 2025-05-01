package org.ashot.microservice_starter.node;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.CheckBoxField;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.node.tabs.PresetSetupTab;
import org.ashot.microservice_starter.utils.ToolTips;

import java.util.List;
import java.util.Map;

public class Entry extends HBox{
    private static final double PREF_NAME_FIELD_WIDTH = 300;
    private static final double PREF_PATH_FIELD_WIDTH = 300;
    private static final double PREF_COMMAND_FIELD_WIDTH = 300;

    private TextArea nameField;
    private TextArea pathField;
    private TextArea commandField;
    private CheckBoxField wslToggle;
    private Button execute;
    private Button deleteEntry;

    public Entry buildEmptyEntry(Pane v) {
        return buildEntry(v, "", "", "", false);
    }

    public Entry buildEntry(Pane container, String name, String path, String command, boolean wsl) {

        nameField = Fields.createField(FieldType.NAME, name);
        nameField.getStyleClass().add("name-field");
        nameField.setPromptText("Name");
        nameField.setPrefWidth(PREF_NAME_FIELD_WIDTH);
        nameField.setTooltip(new Tooltip(ToolTips.nameField()));

        commandField = Fields.createField(FieldType.COMMAND, command);
        commandField.getStyleClass().add("command-field");
        commandField.setPromptText("Command");
        commandField.setPrefWidth(PREF_COMMAND_FIELD_WIDTH);
        commandField.setTooltip(new Tooltip(ToolTips.commandField()));
        ContextMenu commandFieldContextMenu = new ContextMenu();
        commandField.setContextMenu(commandFieldContextMenu);
        commandField.textProperty().addListener((_, _, input) ->
                setupAutoComplete(input, commandFieldContextMenu, commandField, PresetSetupTab.commandsMap)
        );

        HBox pathFieldContainer = new HBox();
        pathField = Fields.createField(FieldType.PATH, path);
        pathField.getStyleClass().add("path-field");
        pathField.setPromptText("Path");
        pathField.setPrefWidth(PREF_PATH_FIELD_WIDTH);
        pathField.setTooltip(new Tooltip(ToolTips.pathField()));
        ContextMenu pathFieldContextMenu = new ContextMenu();
        pathField.setContextMenu(pathFieldContextMenu);
        pathField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, pathFieldContextMenu, pathField, PresetSetupTab.pathsMap)
        );

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL");
        wslToggle.getCheckBox().setSelected(wsl);

        Button pathBrowser = Buttons.browsePathBtn(pathField, wslToggle.getCheckBox());
        pathFieldContainer.getChildren().addAll(pathField, pathBrowser);

        deleteEntry = Buttons.deleteEntryButton(container, this);
        execute = Buttons.executeBtn(nameField, commandField, pathField, wslToggle);
        VBox orderingContainer = Buttons.createOrderingContainer();

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(deleteEntry, nameField, pathFieldContainer, commandField, wslToggle, execute, orderingContainer);
        this.getStyleClass().add("entry");
        return this;
    }

    private void setupAutoComplete(String input, ContextMenu menu, TextArea field, Map<String, String> searchMap) {
        menu.getItems().clear();
        for (String preset : searchMap.keySet()) {
            if (preset.toLowerCase().trim().contains(input.toLowerCase().trim())) {
                List<MenuItem> existing = menu.getItems().filtered(item -> {
                    String existingKey = searchMap.keySet().stream().filter(key -> key.equals(item.getText())).findFirst().orElseGet(()-> null);
                    return preset.equals(existingKey);
                });
                if (existing.isEmpty()) {
                    MenuItem menuItem = new MenuItem(preset + " (" + searchMap.get(preset) + ")");
                    menuItem.setOnAction(_ -> field.setText(searchMap.get(preset)));
                    menu.getItems().add(menuItem);
                }
            }
        }
        Bounds boundsInScreen = field.localToScreen(field.getBoundsInLocal());
        menu.show(field, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
    }

    public TextArea getNameField() {
        return nameField;
    }

    public TextArea getPathField() {
        return pathField;
    }

    public TextArea getCommandField() {
        return commandField;
    }

    public CheckBoxField getWslToggle() {
        return wslToggle;
    }

    public Button getDeleteEntry() {
        return deleteEntry;
    }

    public Button getExecute() {
        return execute;
    }
}
