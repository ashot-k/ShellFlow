package org.ashot.microservice_starter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.ashot.microservice_starter.config.Config;
import org.ashot.microservice_starter.config.DefaultConfig;
import org.ashot.microservice_starter.data.constant.ThemeMode;
import org.ashot.microservice_starter.registry.ProcessRegistry;

import java.net.URL;

public class Main extends Application {

    public static final int SIZE_X = 1400;
    public static final int SIZE_Y = 800;
    private static final boolean RESIZABLE = true;
    public static final String CSS_FILE_LOCATION = Main.class.getResource("main.css").toExternalForm();
    public static final URL MAIN_FXML_LOCATION = Main.class.getResource("microservice-main.fxml");
    private static boolean isDark = false;
    private static Stage primaryStage;
    private static final Config config = new DefaultConfig();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        setTheme(getThemeFromConfig());
        Font.loadFont(getClass().getResource("font/roboto-regular.ttf").toString(), 52);

        Parent root = FXMLLoader.load(MAIN_FXML_LOCATION);
        Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
        scene.getStylesheets().add(CSS_FILE_LOCATION);
        primaryStage = stage;
        primaryStage.setTitle("Microservice Starter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(RESIZABLE);
        primaryStage.show();
        primaryStage.setOnCloseRequest(_ -> {
            Platform.exit();
        });

    }

    @Override
    public void stop(){
        ProcessRegistry.killAllProcesses();
        //add timeout for process killing
        System.exit(0);
    }

    public static void setTheme(ThemeMode theme) {
        if (theme.equals(ThemeMode.DARK_MODE)) {
            Application.setUserAgentStylesheet(ThemeMode.DARK_MODE_THEME.getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(ThemeMode.LIGHT_MODE_THEME.getUserAgentStylesheet());
        }
        isDark = theme.equals(ThemeMode.DARK_MODE);
    }

    public static boolean getDarkModeSetting() {
        return isDark;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static ThemeMode getThemeFromConfig(){
        if(!config.getDarkMode()){
            return ThemeMode.LIGHT_MODE;
        }
        else {
            return ThemeMode.DARK_MODE;
        }
    }

    public static Config getConfig(){
        return config;
    }
}
