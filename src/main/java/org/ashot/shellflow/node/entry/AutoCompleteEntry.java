package org.ashot.shellflow.node.entry;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.Preset;
import org.ashot.shellflow.data.constant.TextStyleClass;

public class AutoCompleteEntry extends HBox {

    private final Preset preset;
    private static final double WIDTH = 600;

    public AutoCompleteEntry(String name, String value, EventHandler<KeyEvent> onAction) {
        preset = new Preset(name, value);

        Text nameText = new Text(name);
        nameText.getStyleClass().addAll(TextStyleClass.boldTextStyleClass());
        TextFlow presetNameArea = new TextFlow(nameText);
        presetNameArea.setTextAlignment(TextAlignment.CENTER);
        presetNameArea.setPrefWidth(WIDTH / 3);

        Text previewText = new Text(value);
        previewText.getStyleClass().addAll(TextStyleClass.smallTextStyleClass());
        TextFlow presetValueArea = new TextFlow(previewText);
        presetValueArea.setPrefWidth(WIDTH * 2 / 3);

        this.hoverProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                this.getStyleClass().add("autocomplete-entry-hovered");
            } else {
                this.getStyleClass().remove("autocomplete-entry-hovered");
            }
        });
        this.focusedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                this.getStyleClass().add("autocomplete-entry-hovered");
            } else {
                this.getStyleClass().remove("autocomplete-entry-hovered");
            }
        });
        this.setOnKeyPressed(event -> {
            VBox parent = (VBox) this.getParent();
            int index = parent.getChildren().indexOf(this);
            if (event.getCode() == KeyCode.UP && index > 0) {
                parent.getChildren().get(index - 1).requestFocus();
            } else if (event.getCode() == KeyCode.DOWN && index < parent.getChildren().size() - 1) {
                parent.getChildren().get(index + 1).requestFocus();
            }
            onAction.handle(event);
        });

        this.setPrefWidth(WIDTH);
        this.setFillHeight(true);
        this.setAlignment(Pos.TOP_LEFT);
        this.getStyleClass().addAll("autocomplete-entry", Main.getSelectedThemeOption().isDark() ? "dark" : "light");
        this.setBorder(new Border(new BorderStroke(Color.DARKSLATEBLUE, BorderStrokeStyle.SOLID, new CornerRadii(1), BorderStroke.DEFAULT_WIDTHS)));
        this.getChildren().addAll(presetNameArea, new Separator(Orientation.VERTICAL), presetValueArea);
        this.setFocusTraversable(true);
    }

    public String getPreview() {
        return this.preset.getValue();
    }

    public String getName() {
        return this.preset.getName();
    }
}
