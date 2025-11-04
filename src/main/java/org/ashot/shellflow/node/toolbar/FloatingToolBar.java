package org.ashot.shellflow.node.toolbar;

import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.utils.GUIAnimations;

import java.util.List;


public class FloatingToolBar extends VBox {
    protected static final double INITIAL_OPACITY = 0.20;
    private boolean isAutoHide = false;
    private final HBox buttonsBar;

    public FloatingToolBar() {
        setMaxHeight(50);
        setFillWidth(false);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        buttonsBar = new HBox();
        buttonsBar.setAlignment(Pos.BOTTOM_CENTER);
        buttonsBar.setSpacing(5);
        HBox.setHgrow(this, Priority.NEVER);
        setPadding(new Insets(2.5));
        getChildren().addAll(buttonsBar);
        getStyleClass().addAll("floating-toolBar");
    }

    protected void setContent(Node... node) {
        buttonsBar.getChildren().setAll(node);
    }

    protected void setContent(List<Node> nodes) {
        buttonsBar.getChildren().setAll(nodes);
    }

    protected void autoHiding() {
        autoHiding(INITIAL_OPACITY);
    }

    protected void autoHiding(double initialOpacity) {
        if (!isAutoHide) {
            setOpacity(initialOpacity);
            hoverProperty().addListener((_, _, hovering) -> animateHover(hovering, initialOpacity));
        }
        isAutoHide = true;
    }

    private void animateHover(boolean hovering, double initialOpacity) {
        Timeline fadeIn = GUIAnimations.fade(this, initialOpacity, 1);
        Timeline fadeOut = GUIAnimations.fade(this, 1, initialOpacity);
        if (hovering) {
            fadeIn.play();
            fadeOut.stop();
        } else {
            fadeIn.stop();
            fadeOut.play();
        }
    }
}
