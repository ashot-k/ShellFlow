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
import org.ashot.shellflow.config.Config;
import org.ashot.shellflow.config.DefaultConfig;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.utils.Animator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class ShellFlow extends Application {

    private static final Logger log = LoggerFactory.getLogger(ShellFlow.class);
    public static final int SIZE_X = 1400;
    public static final int SIZE_Y = 800;
    public static final String WINDOW_TITLE = "ShellFlow";
    private static final boolean RESIZABLE = true;
    private static ThemeOption selectedTheme = ThemeOption.DARK_MODE;
    private static Font applicationFont;
    private static Stage primaryStage;
    private static Config config;

    public static void main(String[] args) {
        handleJVMArgs(args);
        launch();
    }

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            config = new DefaultConfig();
            loadAdditionalFonts();
            setTheme(getThemeFromConfig());
            applicationFont = Font.font("Cascadia Mono");

            URL url = ShellFlow.class.getResource("/fxml/shellflow-main.fxml");
            String styleSheet = ShellFlow.class.getResource("/style/main.css").toExternalForm();
            if(url == null){
                throw new IllegalStateException("Could not load FXML");
            }
            if(styleSheet == null){
                throw new IllegalStateException("Could not load css stylesheet");
            }

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.load();

            Controller controller = fxmlLoader.getController();
            Parent root = fxmlLoader.getRoot();
            Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
            scene.getStylesheets().add(styleSheet);
            root.getStyleClass().add(getThemeFromConfig().isDark() ? "dark" : "light");
            primaryStage.setScene(scene);
            controller.init();
            primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.setResizable(RESIZABLE);
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(800);
            primaryStage.setOnCloseRequest(_ -> Platform.exit());
            updateFont(applicationFont.getFamily(), 14);
            primaryStage.show();
            log.info("JavaFX Version: {}", System.getProperty("javafx.runtime.version"));
            log.info("Java Version: {}", System.getProperty("java.version"));
            log.debug("Loaded FXML: {}", url);
            log.debug("Loaded CSS: {}", styleSheet);
            log.debug("Loaded Theme: {}", selectedTheme);
            log.debug("Is main stage Resizable: {}", RESIZABLE);
        } catch (Exception e) {
            log.error(e.getClass().getName());
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            stop();
        }
    }

    @Override
    public void stop() {
        TerminalRegistry.stopAllTerminals();
        Platform.exit();
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
            Timeline fadeOutTransition = Animator.fadeOut(root);
            fadeOutTransition.setOnFinished(_ -> {
                root.getChildren().remove(imageView);
                root.getStyleClass().removeAll("dark", "light");
                root.getStyleClass().add(selectedTheme.isDark() ? "dark" : "light");
                getConfig().saveProperty(ConfigProperty.THEME, selectedTheme.getTheme().getName());
                Application.setUserAgentStylesheet(selectedTheme.getTheme().getUserAgentStylesheet());
                Animator.fadeIn(root).play();
            });
            fadeOutTransition.play();
        }else{
            Application.setUserAgentStylesheet(selectedTheme.getTheme().getUserAgentStylesheet());
        }
    }


    private static void updateFont(Font font) {
        updateFont(font.getFamily(), font.getSize());
    }

    public static void updateFont(String fontFamily, double size) {
        getPrimaryStage().getScene().getRoot().setStyle("-fx-font-family: '" + fontFamily + "'; -fx-font-size: " + size + "px;");
    }

    private void loadAdditionalFonts(){
        Font.loadFont(String.valueOf(ShellFlow.class.getResource("/fonts/CascadiaMono-VariableFont_wght.ttf")), 14);
    }

    public static ThemeOption getSelectedThemeOption() {
        return selectedTheme;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static ThemeOption getThemeFromConfig() {
        return ThemeOption.getByValue(getConfig().getTheme());
    }

    public static Config getConfig() {
        return config;
    }

    public static Font getApplicationFont() {
        return applicationFont;
    }
}
