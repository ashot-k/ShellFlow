package org.ashot.microservice_starter;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int SIZE_X = 1200;
    private static final int SIZE_Y = 650;
    private static final boolean RESIZABLE = false;
    private final String cssFileLocation = this.getClass().getResource("main.css").toExternalForm();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Parent root = FXMLLoader.load(getClass().getResource("microservice-main.fxml"));
        Scene scene = new Scene(root, SIZE_X, SIZE_Y, Color.BLACK);
        scene.getStylesheets().add(cssFileLocation);
        stage.setTitle("Microservice Starter");
        stage.setScene(scene);
        stage.setResizable(RESIZABLE);
        stage.show();
    }
}
