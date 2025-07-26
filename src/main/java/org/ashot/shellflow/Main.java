package org.ashot.shellflow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ashot.shellflow.config.Config;
import org.ashot.shellflow.config.DefaultConfig;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Main extends Application {

    public static final int SIZE_X = 1400;
    public static final int SIZE_Y = 800;
    private static final boolean RESIZABLE = true;
    public static final String CSS_FILE_LOCATION = Main.class.getResource("main.css").toExternalForm();
    public static final URL MAIN_FXML_LOCATION = Main.class.getResource("shellflow-main.fxml");
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static ThemeOption selectedTheme;
    private static Stage primaryStage;
    private static final Config config = new DefaultConfig();

    public static void main(String[] args) {
        handleJVMArgs(args);
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            setTheme(getThemeFromConfig());
            Parent root = FXMLLoader.load(MAIN_FXML_LOCATION);
            root.getStyleClass().add(getThemeFromConfig().isDark() ? "dark" : "light");
            Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
            scene.getStylesheets().add(CSS_FILE_LOCATION);
            primaryStage = stage;
            primaryStage.setTitle("ShellFlow");
            primaryStage.setScene(scene);
            primaryStage.setResizable(RESIZABLE);
            primaryStage.show();
            primaryStage.setOnCloseRequest(_ -> Platform.exit());
        } catch (Exception e) {
            log.error(e.getClass().getName());
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
        }
    }

    @Override
    public void stop() {
        ProcessRegistry.killAllProcesses();
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
        if (config.getDarkMode()) {
            return ThemeOption.DARK_MODE;
        } else {
            return ThemeOption.LIGHT_MODE;
        }
    }

    public static Config getConfig() {
        return config;
    }
}
