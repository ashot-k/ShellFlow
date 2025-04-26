package org.ashot.microservice_starter;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ashot.microservice_starter.registry.ProcessRegistry;

import java.net.URL;

public class Main extends Application {

    public static final int SIZE_X = 1200;
    public static final int SIZE_Y = 750;
    private static final boolean RESIZABLE = true;
    public static final String CSS_FILE_LOCATION = Main.class.getResource("main.css").toExternalForm();
    public static final URL MAIN_FXML_LOCATION = Main.class.getResource("microservice-main.fxml");
    private static final String DARK_MODE = new PrimerDark().getUserAgentStylesheet();
    private static final String LIGHT_MODE = new PrimerLight().getUserAgentStylesheet();
    private static boolean isDark = false;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        setThemeMode(true);
        Parent root = FXMLLoader.load(MAIN_FXML_LOCATION);
        Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
        scene.getStylesheets().add(CSS_FILE_LOCATION);
        stage.setTitle("Microservice Starter");
        stage.setScene(scene);
        stage.setResizable(RESIZABLE);
        stage.show();
        stage.setOnCloseRequest(_ -> {
            ProcessRegistry.killAllProcesses();
            stage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void setThemeMode(boolean darkMode) {
        if (darkMode) {
            Application.setUserAgentStylesheet(DARK_MODE);
        } else {
            Application.setUserAgentStylesheet(LIGHT_MODE);
        }
        isDark = darkMode;
    }

    public static boolean getDarkModeSetting() {
        return isDark;
    }
}
