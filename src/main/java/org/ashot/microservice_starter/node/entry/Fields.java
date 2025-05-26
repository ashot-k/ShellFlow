package org.ashot.microservice_starter.node.entry;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.constant.TextStyleClass;
import org.ashot.microservice_starter.node.tab.PresetSetupTab;
import org.ashot.microservice_starter.utils.Animator;

import java.util.List;
import java.util.Map;


public class Fields {
    private final static int TEXT_AREA_HEIGHT = 40;

    public static TextArea createField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
        return setupTextField(type, text, promptText, toolTip, width, styleClass);
    }

    public static TextArea createField(FieldType type, String text) {
        return setupTextField(type, text, null, null, null, null);
    }
    public static CheckBoxField createCheckBox(FieldType type, String text, boolean initialSelection){
        return setupCheckBoxField(type, text, initialSelection);
    }

    private static CheckBoxField setupCheckBoxField(FieldType type, String text, boolean initialSelection){
        CheckBox checkBox = new CheckBox();
        checkBox.setId(type.getValue());
        checkBox.setSelected(initialSelection);
        Label label = new Label(text);
        label.setLabelFor(checkBox);
        return new CheckBoxField(checkBox, label);
    }

    private static TextArea setupTextField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
        if (text == null) {
            text = "";
        }
        if (type == null) {
            throw new NullPointerException("TextFieldType can't be null");
        }
        TextArea field = new TextArea(text);
        field.setId(type.getValue());
        field.setWrapText(true);
        field.setPrefHeight(TEXT_AREA_HEIGHT);
        field.setMaxHeight(TEXT_AREA_HEIGHT);
        field.setMinHeight(TEXT_AREA_HEIGHT);
        if(promptText != null && !promptText.isBlank()){
            field.setPromptText(promptText);
        }
        if(width != null){
            field.setPrefWidth(width);
        }
        if(toolTip != null){
            field.setTooltip(new Tooltip(toolTip));
        }
        if(styleClass != null){
            field.getStyleClass().add(styleClass);
        }
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if(isFocused){
                Animator.animateHeightChange(field, field.getHeight() * 3, Duration.millis(150));
            }
            else {
                field.setMinHeight(TEXT_AREA_HEIGHT);
                field.setTranslateY(0);
            }
        });

        field.textProperty().addListener((_, _, input) -> {
            field.setContextMenu(Fields.setupAutoComplete(input, field, getAutoCompleteMap(type)));
        });
        return field;
    }

    private static Map<String, String> getAutoCompleteMap(FieldType type){
        switch (type){
            case PATH -> {
                return PresetSetupTab.pathsMap;
            }
            case COMMAND -> {
                return PresetSetupTab.commandsMap;
            }
            default -> {
                return null;
            }
        }
    }

    public static String getTextFieldContentFromContainer(Pane v, FieldType type) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + type.getValue());
        if (!(n instanceof TextArea field)) {
            return null;
        }
        return field.getText();
    }

    static ContextMenu setupAutoComplete(String input, TextArea field, Map<String, String> searchMap) {
        if(searchMap == null){
            return null;
        }
        if(field.getContextMenu() != null) {
            field.getContextMenu().getItems().clear();
            field.getContextMenu().hide();
        }
        field.setContextMenu(null);
        ContextMenu menu = new ContextMenu();
        for (String preset : searchMap.keySet().stream().limit(10).toList()) {
            if (preset.toLowerCase().trim().contains(input.toLowerCase().trim())) {
                List<MenuItem> existing = menu.getItems().filtered(item -> {
                    String existingKey = searchMap.keySet().stream().filter(key -> key.equals(item.getText())).findFirst().orElse(null);
                    return preset.equals(existingKey);
                });
                if (existing.isEmpty()) {
                    MenuItem menuItem = new MenuItem();
                    double height = 50;
                    double width = 400;
                    TextArea presetNameArea = new TextArea();
                    presetNameArea.appendText(preset);
//                    presetNameArea.setWrapText(true);
                    presetNameArea.setEditable(false);
                    presetNameArea.setMaxHeight(height);
                    presetNameArea.setBackground(Background.EMPTY);
                    presetNameArea.getStyleClass().add(TextStyleClass.boldTextStyleClass());

                    String preview = searchMap.getOrDefault(preset, "NO VALUE SET");
                    TextArea presetValueArea = new TextArea();
                    presetValueArea.appendText(preview);
                    presetValueArea.setWrapText(false);
                    presetValueArea.setEditable(false);
                    presetValueArea.setMaxHeight(height);
                    presetValueArea.setBackground(Background.EMPTY);
                    presetValueArea.getStyleClass().add(TextStyleClass.smallTextStyleClass());

                    HBox menuItemContent = new HBox(presetNameArea, new Separator(Orientation.VERTICAL), presetValueArea);
                    menuItemContent.setAlignment(Pos.CENTER_LEFT);
                    menuItemContent.setPrefWidth(width);

                    presetNameArea.setMaxWidth(150);
                    HBox.setHgrow(presetNameArea, Priority.NEVER);
                    HBox.setHgrow(presetValueArea, Priority.ALWAYS);

                    menuItem.setGraphic(new VBox(menuItemContent, new Separator(Orientation.HORIZONTAL)));
                    menuItem.setOnAction((_)-> field.setText(searchMap.get(preset)));
                    presetValueArea.setOnMouseClicked(_ -> field.setText(searchMap.get(preset)));
                    menu.getItems().add(menuItem);
                }
            }
        }
        menu.setMaxHeight(100);
        menu.setPrefHeight(100);
        menu.show(field, Side.BOTTOM, 0, 0);
        return menu;
    }
}
