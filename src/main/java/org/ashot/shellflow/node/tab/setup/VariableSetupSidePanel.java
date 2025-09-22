package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.Spacer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.icon.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;


public class VariableSetupSidePanel extends VBox {
    private static final Logger log = LoggerFactory.getLogger(VariableSetupSidePanel.class);
    private static final double COLLAPSED_WIDTH = 20;
    private static final double EXPANDED_WIDTH = 600;
    private final BooleanProperty animated = new SimpleBooleanProperty();

    private final StackPane stackPane;
    private boolean expanded = false;
    private final Timeline animation = new Timeline();
    private final Button toggleButton;
    private final VBox variableRows;
    private final VBox variableSetupContainer;
    private final Button addVariableButton;
    private final Button saveAllButton;

    public VariableSetupSidePanel() {
        toggleButton = new Button("", Icons.getSidePanelToggle(10, expanded));
        toggleButton.setPadding(Insets.EMPTY);
        toggleButton.setBackground(Background.EMPTY);
        toggleButton.setOnAction(_ -> {
            expanded = !expanded;
            toggleSidePanel(expanded);
        });

        Label variableSetupTitle = new Label("Variables");
        variableSetupTitle.setFont(Fonts.title());
        variableRows = new VBox(8);
        variableRows.setPadding(new Insets(5));
        variableRows.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(variableRows);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(EXPANDED_WIDTH - 30);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        addVariableButton = new Button("", Icons.getAddButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        saveAllButton = saveButton();
        saveAllButton.prefHeightProperty().bind(addVariableButton.heightProperty());

        HBox variableOptions = new HBox(5, saveAllButton, addVariableButton);
        variableOptions.setAlignment(Pos.TOP_RIGHT);
        variableOptions.setPadding(new Insets(5));
        variableOptions.getStyleClass().add("bordered-container");

        variableSetupContainer = new VBox(5, variableSetupTitle, scrollPane, new Spacer(), variableOptions);
        variableSetupContainer.setAlignment(Pos.TOP_CENTER);
        variableSetupContainer.setPadding(new Insets(2, 10, 2, 10));

        stackPane = new StackPane(variableSetupContainer, toggleButton);
        stackPane.setPadding(new Insets(5, 2, 5, 2));
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);

        VBox.setVgrow(stackPane, Priority.ALWAYS);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setMaxWidth(COLLAPSED_WIDTH);
        toggleSidePanel(false);
        getChildren().addAll(stackPane);
        getStyleClass().addAll("solid-bg-container");
    }

    private void toggleSidePanel(boolean expanded) {
        if (expanded) {
            if (animated.get()) {
                animation.stop();
                animation.getKeyFrames().setAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(maxWidthProperty(), COLLAPSED_WIDTH)),
                        new KeyFrame(Duration.millis(200), new KeyValue(maxWidthProperty(), EXPANDED_WIDTH))
                );
                animation.play();
            } else {
                setMaxWidth(EXPANDED_WIDTH);
            }
            if (!stackPane.getChildren().contains(variableSetupContainer)) {
                stackPane.getChildren().addFirst(variableSetupContainer);
            }
        } else {
            if (animated.get()) {
                animation.stop();
            }
            setMaxWidth(COLLAPSED_WIDTH);
            stackPane.getChildren().remove(variableSetupContainer);
        }
        toggleButton.setGraphic(Icons.getSidePanelToggle(DEFAULT_ICON_SIZE.getSize(), expanded));
    }

    private Button saveButton() {
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(80);
        return saveButton;
    }

    public Button getAddVariableButton() {
        return addVariableButton;
    }

    public Button getSaveAllButton() {
        return saveAllButton;
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }

    public VBox getVariableRows() {
        return variableRows;
    }
}

