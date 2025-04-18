package org.ashot.microservice_starter;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static final int SIZE_X = 1200;
    private static final int SIZE_Y = 650;
    private static final boolean RESIZABLE = false;
    private final String cssFileLocation = getClass().getResource("main.css").toExternalForm();
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
        scene.getStylesheets().add(cssFileLocation);
        stage.setTitle("Microservice Starter");
        stage.setScene(scene);
        stage.setResizable(RESIZABLE);
        stage.show();
    }
    public static void setThemeMode(boolean darkMode){
        if(darkMode) {
            isDark = true;
            Application.setUserAgentStylesheet(DARK_MODE);
        }else {
            isDark = false;
            Application.setUserAgentStylesheet(LIGHT_MODE);
        }
    }
    public static boolean getDarkModeSetting(){
        return isDark;
    }
}
