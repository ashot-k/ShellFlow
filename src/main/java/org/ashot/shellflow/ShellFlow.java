package org.ashot.shellflow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ashot.shellflow.config.DefaultConfig;
import org.ashot.shellflow.config.ShellFlowConfig;
import org.ashot.shellflow.controller.Controller;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.exception.CriticalException;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.utils.ThemeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ShellFlow extends Application {

    private static final Logger log = LoggerFactory.getLogger(ShellFlow.class);
    private static final boolean RESIZABLE = true;
    public static final int SIZE_X = 1600;
    public static final int MIN_SIZE_X = 800;
    public static final int SIZE_Y = 900;
    public static final int MIN_SIZE_Y = 600;
    public static final double MAIN_APP_FONT_SIZE = 14;
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String JAVAFX_VERSION = System.getProperty("javafx.runtime.version");
    public static final String WINDOW_TITLE = "ShellFlow";

    private static final ThemeOption selectedTheme = ThemeOption.DARK_MODE;
    private static Font applicationFont;
    private static Stage primaryStage;
    private static ShellFlowConfig shellFlowConfig;

    static void main(String[] args) {
        handleJVMArgs(args);
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Application.setUserAgentStylesheet(ThemeOption.DARK_MODE.getTheme().getUserAgentStylesheet());
            primaryStage = stage;
            applicationFont = Font.getDefault();
            shellFlowConfig = new DefaultConfig();
            loadAdditionalFonts();

            URL url = ShellFlow.class.getResource("/fxml/shellflow-main.fxml");
            URL styleSheetURL = ShellFlow.class.getResource("/style/main.css");
            validateResources(url, styleSheetURL);
            String styleSheet = styleSheetURL.toExternalForm();

            BorderPane header = new BorderPane();
            Scene scene = new Scene(header, SIZE_X, SIZE_Y, Color.BLACK);

            configurePrimaryScene(scene, styleSheet, header);
            configurePrimaryStage(primaryStage, scene, applicationFont.getFamily());

            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.load();
            configureHeader(header, fxmlLoader.getController(), fxmlLoader.getRoot());
            ThemeHandler.transitionToTheme(primaryStage, getThemeFromConfig());

            log.info("Java Version: {}, JavaFX Version: {}", JAVA_VERSION, JAVAFX_VERSION);
            log.debug("Loaded\n FXML: {}\n CSS: {}\n Theme: {}", url, styleSheet, selectedTheme);
            log.debug("Resizable: {}", RESIZABLE);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof CriticalException criticalException) {
                AlertPopup alertPopup = new AlertPopup("Critical Error", criticalException.getMessage(), true);
                alertPopup.setOnCloseRequest(_ -> stop());
                alertPopup.show();
            }
        }
    }

    private void validateResources(URL url, URL styleSheet) {
        if (url == null) {
            throw new IllegalStateException("Could not load FXML");
        }
        if (styleSheet == null) {
            throw new IllegalStateException("Could not load css stylesheet");
        }
    }

    @Override
    public void stop() {
        TerminalRegistry.stopAllTerminals();
        Platform.exit();
        System.exit(0);
    }

    private void configurePrimaryScene(Scene scene, String styleSheet, BorderPane header) {
        scene.setFill(selectedTheme.isDark() ? Color.BLACK : Color.WHITE);
        scene.getStylesheets().add(styleSheet);
        scene.setRoot(header);
    }

    @SuppressWarnings("deprecation")
    private void configurePrimaryStage(Stage stage, Scene scene, String fontFamily) {
        stage.setScene(scene);
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(RESIZABLE);
        stage.setMinWidth(MIN_SIZE_X);
        stage.setMinHeight(MIN_SIZE_Y);
        stage.initStyle(StageStyle.EXTENDED);
        stage.setOnCloseRequest(_ -> stop());
        stage.getScene().getRoot().setStyle("-fx-font-family: '" + fontFamily + "'; -fx-font-size: " + ShellFlow.MAIN_APP_FONT_SIZE + "px;");
    }

    private void configureHeader(BorderPane header, Controller controller, Parent baseRoot) {
        header.setCenter(baseRoot);
        header.setTop(controller.init());
        header.getStyleClass().add(getThemeFromConfig().isDark() ? ThemeHandler.DARK_CLASS : ThemeHandler.LIGHT_CLASS);
        controller.loadInit();
    }

    private static void handleJVMArgs(String[] args) {
        for (String arg : args) {
            log.info("Found argument: {}", arg);
            if (arg.equals("debug")) {
                log.info("Running in mode: {}", arg);
            }
        }
    }

    private void loadAdditionalFonts() {
        URL customFont = ShellFlow.class.getResource("/fonts/CascadiaMono-VariableFont_wght.ttf");
        if (customFont == null) {
            throw new IllegalStateException("Could not load custom Font");
        }
        Font.loadFont(String.valueOf(customFont), 14);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ThemeOption getThemeFromConfig() {
        return ThemeOption.getByValue(shellFlowConfig.theme());
    }

    public static ShellFlowConfig getConfig() {
        return shellFlowConfig;
    }

    public static Font getApplicationFont() {
        return applicationFont;
    }
}
