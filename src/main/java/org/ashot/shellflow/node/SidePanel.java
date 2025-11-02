package org.ashot.shellflow.node;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;

public class SidePanel extends VBox {
    private static final double COLLAPSED_WIDTH = 20;
    private static final double EXPANDED_WIDTH = 600;
    private final BooleanProperty animated = new SimpleBooleanProperty();

    private final StackPane stackPane;
    private boolean expanded = false;
    private final Timeline animation = new Timeline();
    private final Button toggleButton;
    private final Node content;

    public SidePanel(Node content) {
        this.content = content;
        toggleButton = new Button("", Icons.getSidePanelToggle(10, expanded));
        toggleButton.setPadding(Insets.EMPTY);
        toggleButton.setBackground(Background.EMPTY);
        toggleButton.setOnAction(_ -> {
            expanded = !expanded;
            toggleSidePanel(expanded);
        });

        stackPane = new StackPane(content, toggleButton);
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
            if (!stackPane.getChildren().contains(content)) {
                stackPane.getChildren().addFirst(content);
            }
        } else {
            if (animated.get()) {
                animation.stop();
            }
            setMaxWidth(COLLAPSED_WIDTH);
            stackPane.getChildren().remove(content);
        }
        toggleButton.setGraphic(Icons.getSidePanelToggle(DEFAULT_ICON_SIZE.getSize(), expanded));
    }

    public Node getContent() {
        return content;
    }

    public boolean isAnimated() {
        return animated.get();
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }
}
