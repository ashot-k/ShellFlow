package org.ashot.microservice_starter.node;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.ashot.microservice_starter.data.CheckBoxField;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.node.tabs.PresetSetupTab;
import org.ashot.microservice_starter.utils.ToolTips;

import java.util.List;
import java.util.Map;

public class Entry {
    private static final double PREF_NAME_FIELD_WIDTH = 200;
    private static final double PREF_PATH_FIELD_WIDTH = 350;
    private static final double PREF_COMMAND_FIELD_WIDTH = 350;

    public HBox buildEmptyEntry(Pane v) {
        return buildEntry(v, "", "", "", false);
    }

    public HBox buildEntry(Pane container, String name, String path, String command, boolean wsl) {
        HBox row = new HBox();
        row.setAlignment(Pos.TOP_CENTER);

        TextArea nameField = Fields.createField(FieldType.NAME, name);
        nameField.getStyleClass().add("name-field");
        nameField.setPromptText("Name");
        nameField.setPrefWidth(PREF_NAME_FIELD_WIDTH);
        nameField.setTooltip(new Tooltip(ToolTips.nameField()));

        TextArea commandField = Fields.createField(FieldType.COMMAND, command);
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
        TextArea pathField = Fields.createField(FieldType.PATH, path);
        pathField.getStyleClass().add("path-field");
        pathField.setPromptText("Path");
        pathField.setPrefWidth(PREF_PATH_FIELD_WIDTH);
        pathField.setTooltip(new Tooltip(ToolTips.pathField()));
        ContextMenu pathFieldContextMenu = new ContextMenu();
        pathField.setContextMenu(pathFieldContextMenu);
        pathField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, pathFieldContextMenu, pathField, PresetSetupTab.pathsMap)
        );

        CheckBoxField wslSetting = Fields.createCheckBox(FieldType.WSL, "WSL");
        wslSetting.getCheckBox().setSelected(wsl);

        Button pathBrowserBtn = Buttons.browsePathBtn(pathField, wslSetting.getCheckBox());
        pathFieldContainer.getChildren().addAll(pathField, pathBrowserBtn);

        Button deleteEntryBtn = Buttons.deleteEntryButton(container, row);
        Button execute = Buttons.executeBtn(nameField, commandField, pathField, wslSetting);
        VBox orderingContainer = Buttons.createOrderingContainer();

        row.getChildren().addAll(deleteEntryBtn, nameField, pathFieldContainer, commandField, wslSetting, execute, orderingContainer);
        row.getStyleClass().add("entry");
        return row;
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

}
