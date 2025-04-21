package org.ashot.microservice_starter.node;

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.setup.PresetSetupTab;

import java.util.List;
import java.util.Map;

public class Entry {
    private static final double PREF_NAME_FIELD_WIDTH = 200;
    private static final double PREF_PATH_FIELD_WIDTH = 350;
    private static final double PREF_COMMAND_FIELD_WIDTH = 350;

    public HBox buildEmptyEntry(Pane v) {
        return buildEntry(v, "", "", "");
    }

    public HBox buildEntry(Pane container, String name, String path, String command) {
        HBox row = new HBox();

        TextField nameField = Fields.createField(TextFieldType.NAME, name);
        nameField.getStyleClass().add("name-field");
        nameField.setPromptText("Name");
        nameField.setPrefWidth(PREF_NAME_FIELD_WIDTH);
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command);
        commandField.getStyleClass().add("command-field");
        commandField.setPromptText("Command");
        commandField.setPrefWidth(PREF_COMMAND_FIELD_WIDTH);
        ContextMenu commandFieldContextMenu = new ContextMenu();
        commandField.setContextMenu(commandFieldContextMenu);
        commandField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, commandFieldContextMenu, commandField, PresetSetupTab.commandsMap)
        );
        TextField pathField = Fields.createField(TextFieldType.PATH, path);
        pathField.getStyleClass().add("path-field");
        pathField.setPromptText("Path");
        pathField.setPrefWidth(PREF_PATH_FIELD_WIDTH);
        ContextMenu pathFieldContextMenu = new ContextMenu();
        pathField.setContextMenu(pathFieldContextMenu);
        pathField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, pathFieldContextMenu, pathField, PresetSetupTab.pathsMap)
        );

        Button execute = Buttons.executeBtn(nameField, commandField, pathField);
        Button deleteEntryBtn = Buttons.deleteEntryButton(container, row);
        row.getChildren().addAll(deleteEntryBtn, nameField, pathField, commandField, execute);
        row.getStyleClass().add("entry");

        Button moveUpBtn = Buttons.orderingButton(true);
        Button moveDownBtn = Buttons.orderingButton(false);
        VBox orderingContainer = new VBox();
        orderingContainer.getChildren().addAll(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        row.getChildren().add(orderingContainer);
        return row;
    }

    private void setupAutoComplete(String input, ContextMenu menu, TextField field, Map<String, String> searchMap) {
        menu.getItems().clear();
        for (String p : searchMap.keySet()) {
            if (p.toLowerCase().trim().contains(input.toLowerCase().trim())) {
                List<MenuItem> existing = menu.getItems().filtered(item -> {
                    String existingKey = searchMap.keySet().stream().filter(key -> key.equals(item.getText())).findFirst().get();
                    return p.equals(existingKey);
                });
                if (existing.isEmpty()) {
                    MenuItem menuItem = new MenuItem();
                    menuItem.setText(p);
                    menuItem.setOnAction(_ -> field.setText(searchMap.get(p)));
                    menu.getItems().add(menuItem);
                }
            }
        }
        Bounds boundsInScreen = field.localToScreen(field.getBoundsInLocal());
        menu.show(field, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
    }

}
