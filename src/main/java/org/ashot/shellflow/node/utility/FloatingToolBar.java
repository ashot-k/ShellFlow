package org.ashot.shellflow.node.utility;

import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.utils.Animator;

import java.util.List;


public class FloatingToolBar extends VBox {
    protected final static double INITIAL_OPACITY = 0.30;
    private boolean isAutoHide = false;
    private HBox buttonsBar;

    public FloatingToolBar(){
        setMaxHeight(50);
        setFillWidth(false);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        buttonsBar = new HBox();
        buttonsBar.setAlignment(Pos.BOTTOM_CENTER);
        buttonsBar.setSpacing(10);
        setPadding(new Insets(10, 8, 10 ,8));
        getChildren().addAll(buttonsBar);
        getStyleClass().addAll( "floating-toolBar");
    }

    protected void setContent(Node... node){
        buttonsBar.getChildren().setAll(node);
    }

    protected void setContent(List<Node> nodes){
        buttonsBar.getChildren().setAll(nodes);
    }

    protected void autoHiding(){
        if(!isAutoHide) {
            setOpacity(INITIAL_OPACITY);
            hoverProperty().addListener((_, _, hovering) -> animateHover(hovering));
        }
        isAutoHide = true;
    }

    protected void autoHiding(double initialOpacity){
        if(!isAutoHide) {
            setOpacity(initialOpacity);
            hoverProperty().addListener((_, _, hovering) -> animateHover(hovering, initialOpacity));
        }
        isAutoHide = true;
    }

    private void animateHover(boolean hovering){
        Timeline fadeIn = Animator.fade(this, INITIAL_OPACITY, 1);
        Timeline fadeOut = Animator.fade(this, 1, INITIAL_OPACITY);
        if(hovering) {
            fadeIn.play();
            fadeOut.stop();
        }else {
            fadeIn.stop();
            fadeOut.play();
        }
    }

    private void animateHover(boolean hovering, double initialOpacity){
        Timeline fadeIn = Animator.fade(this, initialOpacity, 1);
        Timeline fadeOut = Animator.fade(this, 1, initialOpacity);
        if(hovering) {
            fadeIn.play();
            fadeOut.stop();
        }else {
            fadeIn.stop();
            fadeOut.play();
        }
    }
}
