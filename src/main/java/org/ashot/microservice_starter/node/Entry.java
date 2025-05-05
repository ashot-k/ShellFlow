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
    private static final double PREF_NAME_FIELD_WIDTH = 200;
    private static final double PREF_PATH_FIELD_WIDTH = 400;
    private static final double PREF_COMMAND_FIELD_WIDTH = 400;

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

        nameField = Fields.createField(
                FieldType.NAME, name, "Name",
                ToolTips.nameField(), PREF_NAME_FIELD_WIDTH, "name-field"
        );
        commandField = Fields.createField(
                FieldType.COMMAND, command,  "Command",
                ToolTips.commandField(), PREF_COMMAND_FIELD_WIDTH, "command-field"
        );
        ContextMenu commandFieldContextMenu = new ContextMenu();
        commandField.setContextMenu(commandFieldContextMenu);
        commandField.textProperty().addListener((_, _, input) ->
                setupAutoComplete(input, commandFieldContextMenu, commandField, PresetSetupTab.commandsMap)
        );

        pathField = Fields.createField(
                FieldType.PATH, path, "Path",
                ToolTips.pathField(), PREF_PATH_FIELD_WIDTH, "path-field"
        );
        ContextMenu pathFieldContextMenu = new ContextMenu();
        pathField.setContextMenu(pathFieldContextMenu);
        pathField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, pathFieldContextMenu, pathField, PresetSetupTab.pathsMap)
        );

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", wsl);
        Button pathBrowser = Buttons.browsePathBtn(pathField, wslToggle.getCheckBox());
        HBox pathFieldContainer = new HBox(5, pathField, pathBrowser);

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
