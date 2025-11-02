package org.ashot.shellflow.utils;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.ThemeOption;

public class ThemeHandler {
    private static ThemeOption selectedTheme;
    public static final String LIGHT_CLASS = "light";
    public static final String DARK_CLASS = "dark";

    private ThemeHandler() {
    }

    public static void transitionToTheme(Stage stage, ThemeOption option) {
        selectedTheme = option;
        boolean isDark = selectedTheme.isDark();
        if (stage != null && stage.getScene() != null) {
            Pane root = (Pane) stage.getScene().getRoot();
            ImageView imageView = takeSnapshotOfScene(stage);
            root.getChildren().addFirst(imageView); // add snapshot on top
            Timeline fadeOutTransition = GUIAnimations.fadeOut(root);
            fadeOutTransition.setOnFinished(_ -> {
                root.getChildren().remove(imageView);
                root.getStyleClass().removeAll(DARK_CLASS, LIGHT_CLASS);
                root.getStyleClass().add(isDark ? DARK_CLASS : LIGHT_CLASS);
                ShellFlow.getConfig().saveProperty(ConfigProperty.THEME, selectedTheme.getTheme().getName());
                Application.setUserAgentStylesheet(selectedTheme.getTheme().getUserAgentStylesheet());
                GUIAnimations.fadeIn(root).play();
                stage.getScene().setFill(isDark ? Color.BLACK : Color.WHITE);
            });
            fadeOutTransition.play();
        }
    }

    private static ImageView takeSnapshotOfScene(Stage stage) {
        return new ImageView(stage.getScene().snapshot(null));
    }

    public static ThemeOption getSelectedTheme() {
        if (selectedTheme == null) {
            return ShellFlow.getThemeFromConfig();
        }
        return selectedTheme;
    }
}
