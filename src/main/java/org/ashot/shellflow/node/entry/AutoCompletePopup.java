package org.ashot.shellflow.node.entry;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.ashot.shellflow.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoCompletePopup extends PopupControl {
    TextArea field;
    List<AutoCompleteEntry> entries = new ArrayList<>();

    public AutoCompletePopup(TextArea field) {
        this.field = field;
        this.setAutoFix(true);
    }

    public void show(String input, Map<String, String> searchMap) {
        if (searchMap == null || searchMap.isEmpty()) {
            return;
        }
        entries.clear();
        for (String preset : searchMap.keySet().stream().toList()) {
            if (entries.size() >= 3) {
                break;
            }
            String presetName = preset.toLowerCase().trim();
            String presetValue = searchMap.get(preset).toLowerCase().trim();
            String inputTrimmed = input.toLowerCase().trim();
            if ((!presetName.equals(inputTrimmed) && presetName.contains(inputTrimmed)) || (!presetValue.equals(inputTrimmed) && presetValue.contains(inputTrimmed))) {
                List<AutoCompleteEntry> existing = entries.stream().filter(item -> {
                    String existingKey = searchMap.keySet().stream().filter(key -> key.equals(item.getName())).findFirst().orElse(null);
                    return preset.equals(existingKey);
                }).toList();
                if (existing.isEmpty()) {
                    AutoCompleteEntry menuItemContent = new AutoCompleteEntry(preset, searchMap.get(preset), e -> {
                        if (e.getCode().equals(KeyCode.ENTER) && !e.isShiftDown()) {
                            field.setText(searchMap.get(preset));
                            this.hide();
                        }
                    });

                    menuItemContent.setOnMouseClicked(e -> {
                        if (e.getButton().equals(MouseButton.PRIMARY)) {
                            field.setText(menuItemContent.getPreview());
                            this.hide();
                        }
                    });
                    entries.add(menuItemContent);
                }
            }
        }
        if (entries.isEmpty()) {
            this.hide();
            return;
        }
        getScene().setRoot(setupList());

        getScene().setUserAgentStylesheet(Main.getSelectedThemeOption().getTheme().getUserAgentStylesheet());
        Bounds bounds = field.localToScreen(field.getBoundsInLocal());
        this.show(field, bounds.getMinX(), bounds.getMaxY() + 5);
    }

    private ScrollPane setupList() {
        ScrollPane s = new ScrollPane();
        s.setPrefHeight(300);
        s.setFitToWidth(true);
        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(entries);
        vBox.setPadding(new Insets(5));
        vBox.setBackground(new Background(new BackgroundFill(new Color(0.021, 0.026, 0.033, 0.95), new CornerRadii(1), Insets.EMPTY)));
        s.setContent(vBox);
        vBox.getChildren().getFirst().requestFocus();
        return s;
    }
}
