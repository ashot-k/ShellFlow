package org.ashot.microservice_starter;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Startup extends Application {
    private static final int SIZE_X = 900;
    private static final int SIZE_Y = 500;
    private static final boolean RESIZABLE = false;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, Color.GRAY);

//        Image icon = new Image();
//        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setTitle("Microservice Starter");
        stage.setWidth(SIZE_X);
        stage.setHeight(SIZE_Y);
        stage.setResizable(RESIZABLE);
        stage.show();
    }
}