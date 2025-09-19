package org.ashot.shellflow;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.ashot.shellflow.config.DefaultConfig;
import org.ashot.shellflow.config.ShellFlowConfig;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.utils.Animations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ShellFlow extends Application {

    private static final Logger log = LoggerFactory.getLogger(ShellFlow.class);
    public static final int SIZE_X = 1500;
    public static final int SIZE_Y = 750;
    public static final String WINDOW_TITLE = "ShellFlow";
    private static final boolean RESIZABLE = true;
    private static ThemeOption selectedTheme = ThemeOption.DARK_MODE;
    private static Font applicationFont;
    private static Stage primaryStage;
    private static final ShellFlowConfig shellFlowConfig = new DefaultConfig();

    public static void main(String[] args) {
        handleJVMArgs(args);
        launch();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        setTheme(getThemeFromConfig());
        loadAdditionalFonts();
//        applicationFont = Font.font("Cascadia Mono");
        applicationFont = Font.getDefault();
        URL url = ShellFlow.class.getResource("/fxml/shellflow-main.fxml");
        String styleSheet = ShellFlow.class.getResource("/style/main.css").toExternalForm();
        if (url == null) {
            throw new IllegalStateException("Could not load FXML");
        }
        if (styleSheet == null) {
            throw new IllegalStateException("Could not load css stylesheet");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            log.error(e.getClass().getName());
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            stop();
        }
        Controller controller = fxmlLoader.getController();
        Parent root = fxmlLoader.getRoot();
        Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
        scene.getStylesheets().add(styleSheet);
        root.getStyleClass().add(getThemeFromConfig().isDark() ? "dark" : "light");
        primaryStage.setScene(scene);
        controller.init();
        configurePrimaryStage(primaryStage);
        log.info("JavaFX Version: {}", System.getProperty("javafx.runtime.version"));
        log.info("Java Version: {}", System.getProperty("java.version"));
        log.debug("Loaded FXML: {}", url);
        log.debug("Loaded CSS: {}", styleSheet);
        log.debug("Loaded Theme: {}", selectedTheme);
        log.debug("Is main stage Resizable: {}", RESIZABLE);
        primaryStage.show();
    }

    @Override
    public void stop() {
        TerminalRegistry.stopAllTerminals();
        Platform.exit();
        System.exit(0);
    }

    private void configurePrimaryStage(Stage stage) {
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(RESIZABLE);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setOnCloseRequest(_ -> stop());
        updateStageFont(stage, applicationFont.getFamily(), 14);
    }

    private static void handleJVMArgs(String[] args) {
        for (String arg : args) {
            log.info("Found argument: {}", arg);
            if (arg.equals("debug")) {
                log.info("Running in mode: {}", arg);
            }
        }
    }

    public static void setTheme(ThemeOption option) {
        selectedTheme = option;
        if (getPrimaryStage() != null && getPrimaryStage().getScene() != null) {
            Pane root = (Pane) getPrimaryStage().getScene().getRoot();
            Image snapshot = getPrimaryStage().getScene().snapshot(null);
            ImageView imageView = new ImageView(snapshot);
            root.getChildren().addFirst(imageView); // add snapshot on top
            Timeline fadeOutTransition = Animations.fadeOut(root);
            fadeOutTransition.setOnFinished(_ -> {
                root.getChildren().remove(imageView);
                root.getStyleClass().removeAll("dark", "light");
                root.getStyleClass().add(selectedTheme.isDark() ? "dark" : "light");
                getConfig().saveProperty(ConfigProperty.THEME, selectedTheme.getTheme().getName());
                Application.setUserAgentStylesheet(selectedTheme.getTheme().getUserAgentStylesheet());
                Animations.fadeIn(root).play();
            });
            fadeOutTransition.play();
        } else {
            Application.setUserAgentStylesheet(selectedTheme.getTheme().getUserAgentStylesheet());
        }
    }

    private void updateStageFont(Stage stage, String fontFamily, double size) {
        stage.getScene().getRoot().setStyle("-fx-font-family: '" + fontFamily + "'; -fx-font-size: " + size + "px;");
    }

    private void loadAdditionalFonts() {
        Font.loadFont(String.valueOf(ShellFlow.class.getResource("/fonts/CascadiaMono-VariableFont_wght.ttf")), 14);
    }

    public static ThemeOption getSelectedThemeOption() {
        return selectedTheme;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static ThemeOption getThemeFromConfig() {
        return ThemeOption.getByValue(getConfig().theme());
    }

    public static ShellFlowConfig getConfig() {
        return shellFlowConfig;
    }

    public static Font getApplicationFont() {
        return applicationFont;
    }
}
