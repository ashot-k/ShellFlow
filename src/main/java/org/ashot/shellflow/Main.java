package org.ashot.shellflow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ashot.shellflow.config.Config;
import org.ashot.shellflow.config.DefaultConfig;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Main extends Application {

    public static final int SIZE_X = 1400;
    public static final int SIZE_Y = 800;
    public static final String windowTitle = "ShellFlow";
    private static final boolean RESIZABLE = true;
    public static final String CSS_FILE_LOCATION = Main.class.getResource("/style/main.css").toExternalForm();
    public static final URL MAIN_FXML_LOCATION = Main.class.getResource("/fxml/shellflow-main.fxml");
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static ThemeOption selectedTheme = ThemeOption.DARK_MODE;
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
            setTheme(getThemeFromConfig());

            Parent root = FXMLLoader.load(MAIN_FXML_LOCATION);
            Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
            scene.getStylesheets().add(CSS_FILE_LOCATION);
            root.getStyleClass().add(getThemeFromConfig().isDark() ? "dark" : "light");
            primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.setTitle(windowTitle);
            primaryStage.setScene(scene);
            primaryStage.setResizable(RESIZABLE);
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(800);
            primaryStage.setOnCloseRequest(_ -> Platform.exit());
            primaryStage.show();
            log.debug("Loaded FXML: {}", MAIN_FXML_LOCATION);
            log.debug("Loaded CSS: {}", CSS_FILE_LOCATION);
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
        //add timeout for process killing
        System.exit(0);
    }

    private static void handleJVMArgs(String[] args) {
        for (String arg : args){
            log.info("Found argument: {}", arg);
            if(arg.equals("debug")){
                log.info("Running in mode: {}", arg);
            }
        }
    }

    public static void setTheme(ThemeOption option) {
        selectedTheme = option;
        Application.setUserAgentStylesheet(option.getTheme().getUserAgentStylesheet());
        if (getPrimaryStage() != null && getPrimaryStage().getScene() != null) {
            Parent root = getPrimaryStage().getScene().getRoot();
            root.getStyleClass().removeAll("dark", "light");
            root.getStyleClass().add(selectedTheme.isDark() ? "dark" : "light");
        }
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
}
