package org.ashot.microservice_starter.node.entry;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.message.ToolTipMessages;
import org.ashot.microservice_starter.node.tab.PresetSetupTab;
import org.ashot.microservice_starter.utils.Utils;
import org.fxmisc.richtext.StyleClassedTextArea;

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
                ToolTipMessages.nameField(), PREF_NAME_FIELD_WIDTH, "name-field"
        );
        commandField = Fields.createField(
                FieldType.COMMAND, command,  "Command",
                ToolTipMessages.commandField(), PREF_COMMAND_FIELD_WIDTH, "command-field"
        );
        ContextMenu commandFieldContextMenu = new ContextMenu();
        commandField.setContextMenu(commandFieldContextMenu);
        commandField.textProperty().addListener((_, _, input) ->
                setupAutoComplete(input, commandFieldContextMenu, commandField, PresetSetupTab.commandsMap)
        );

        pathField = Fields.createField(
                FieldType.PATH, path, "Path",
                ToolTipMessages.pathField(), PREF_PATH_FIELD_WIDTH, "path-field"
        );
        ContextMenu pathFieldContextMenu = new ContextMenu();
        pathField.setContextMenu(pathFieldContextMenu);
        pathField.textProperty().addListener((_, _, newValue) ->
                setupAutoComplete(newValue, pathFieldContextMenu, pathField, PresetSetupTab.pathsMap)
        );

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", wsl);
        Button pathBrowser = EntryButton.browsePathBtn(pathField, wslToggle.getCheckBox());

        deleteEntry = EntryButton.deleteEntryButton(container, this);
        deleteEntry.setPadding(new Insets(2, 0, 0,0));
        execute = EntryButton.executeBtn(nameField, commandField, pathField, wslToggle);
        execute.setPadding(new Insets(2, 0, 0,0));

        HBox deleteEntryContainer = new HBox(deleteEntry);
        HBox nameFieldContainer = new HBox(nameField);
        HBox pathFieldContainer = new HBox(5, pathField, pathBrowser);
        HBox commandFieldContainer = new HBox(commandField);
        HBox executeContainer = new HBox(execute);
        VBox orderingContainer = EntryButton.createOrderingContainer();

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(deleteEntryContainer, nameFieldContainer, pathFieldContainer, commandFieldContainer, wslToggle, executeContainer, orderingContainer);
        this.getStyleClass().add("entry");
        return this;
    }

    private void setupAutoComplete(String input, ContextMenu menu, TextArea field, Map<String, String> searchMap) {
        menu.getItems().clear();
        for (String preset : searchMap.keySet()) {
            if (preset.toLowerCase().trim().contains(input.toLowerCase().trim())) {
                List<MenuItem> existing = menu.getItems().filtered(item -> {
                    String existingKey = searchMap.keySet().stream().filter(key -> key.equals(item.getText())).findFirst().orElse(null);
                    return preset.equals(existingKey);
                });
                if (existing.isEmpty()) {
                    MenuItem menuItem = new MenuItem();
                    double height = 180;
                    double width = 400;
                    StyleClassedTextArea presetNameArea = new StyleClassedTextArea();
                    presetNameArea.setWrapText(true);
                    presetNameArea.setEditable(false);
                    presetNameArea.setPrefWidth(width);
                    presetNameArea.setMaxHeight(height / 5);
                    presetNameArea.setBackground(Background.EMPTY);
                    presetNameArea.appendText(preset);
                    presetNameArea.setStyleClass(0, presetNameArea.getLength(), Utils.boldTextStyleClass());
                    presetNameArea.setDisable(true);

                    StyleClassedTextArea presetValueArea = new StyleClassedTextArea();
                    presetValueArea.setWrapText(true);
                    presetValueArea.setEditable(false);
                    presetValueArea.setPrefWidth(width);
                    presetValueArea.setBackground(Background.EMPTY);
                    String preview = searchMap.getOrDefault(preset, "NO VALUE SET");
                    presetValueArea.appendText(preview);
                    presetValueArea.setStyleClass(0, presetValueArea.getLength(), Utils.smallTextStyleClass());
                    presetValueArea.setMaxHeight(height / 4);
                    presetValueArea.moveTo(0);
                    presetValueArea.requestFollowCaret();
                    presetValueArea.setDisable(true);
                    VBox menuItemContent = new VBox(new HBox(presetNameArea), new HBox(presetValueArea), new Separator(Orientation.HORIZONTAL));
                    menuItemContent.setFillWidth(true);

                    VBox.setVgrow(presetNameArea, Priority.NEVER);
                    VBox.setVgrow(presetValueArea, Priority.NEVER);
                    HBox.setHgrow(presetNameArea, Priority.ALWAYS);
                    HBox.setHgrow(presetValueArea, Priority.ALWAYS);

                    menuItem.setGraphic(menuItemContent);
                    menuItem.setOnAction((_)-> field.setText(searchMap.get(preset)));
                    presetValueArea.setOnMouseClicked(_ -> field.setText(searchMap.get(preset)));
                    menu.getItems().add(menuItem);
                }
            }
        }
        Bounds boundsInScreen = field.localToScreen(field.getBoundsInLocal());
        menu.show(field, boundsInScreen.getMinX() + 100, boundsInScreen.getMaxY());
    }

    public static List<Entry> getEntriesFromPane(Pane container){
        return container.getChildren().stream().filter(e -> e instanceof Entry).map(e -> (Entry) e).toList();
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
