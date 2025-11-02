package org.ashot.shellflow.controller;

import atlantafx.base.controls.ModalPane;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.node.menu.MainMenuBar;
import org.ashot.shellflow.node.notification.ShellFlowTray;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.utils.GUIAnimations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static javafx.scene.layout.HeaderDragType.DRAGGABLE_SUBTREE;
import static org.ashot.shellflow.utils.FileUtils.getMostRecentlyOpenedFile;


@SuppressWarnings("deprecation")
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private ModalPane mainModal;
    @FXML
    private VBox sceneContainer;
    @FXML
    private TabPane mainTabPane;

    private MainMenuBar mainMenuBar;

    private EntryManagementController entryManagementController;
    private ExecutionManagementController executionManagementController;
    private VariableManagementController variableManagementController;
    private BooleanProperty optimizationMode;

    public HeaderBar init() {
        setupControllers();
        setupTabs();
        setupMenuBar();
        ShellFlowTray.init(executionManagementController);
        handlePerformanceMode();
        return createHeader();
    }

    public void loadInit() {
        log.info("Initializing entries");
        try {
            entryManagementController.load(getMostRecentlyOpenedFile());
        } catch (CouldNotReadFromFileException e) {
            log.error("Could not initialize entries: {}", e.getMessage());
        }
    }

    private HeaderBar createHeader() {
        HeaderBar.setDragType(mainMenuBar, DRAGGABLE_SUBTREE);
        return new HeaderBar(null, mainMenuBar, null);
    }

    private void setupMenuBar() {
        mainMenuBar = new MainMenuBar(file -> entryManagementController.load(file), file -> entryManagementController.save(file), entryManagementController.getCurrentFileAbsolutePathProperty(), mainModal);
        optimizationMode = mainMenuBar.getSettingsMenu().getPerformanceSettingMenuItem().performanceModePropertyProperty();
    }

    private void setupControllers() {
        executionManagementController = new ExecutionManagementController();
        variableManagementController = new VariableManagementController(new File(ShellFlow.getConfig().variablesConfigLocation()));
        entryManagementController = new EntryManagementController(executionManagementController, variableManagementController);
    }

    private void setupTabs() {
        mainTabPane.getTabs().add(TabIndices.ENTRIES.ordinal(), entryManagementController.getView());
        mainTabPane.prefWidthProperty().bind(sceneContainer.widthProperty());
        sceneContainer.getScene().setOnKeyPressed(this::handleUserInput);
    }

    private void handlePerformanceMode() {
        entryManagementController.optimizationModeProperty().bind(optimizationMode);
        ThemedSettingsProvider.optimizationModeProperty().bind(optimizationMode);
        GUIAnimations.performanceMode.bind(optimizationMode);
    }

    private void handleUserInput(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyEvent.isControlDown()) {
            TabPane executionsTabPane = executionManagementController.getView();
            if (keyEvent.isShiftDown()) {
                Node node = executionsTabPane.getSelectionModel().getSelectedItem().getContent();
                if (node instanceof TabPane tabPane) {
                    moveToAdjacentTab(keyCode, tabPane);
                }
            } else {
                moveToAdjacentTab(keyCode, executionsTabPane);
            }
        }
    }

    private void moveToAdjacentTab(KeyCode keyCode, TabPane tabPane) {
        if (keyCode.equals(KeyCode.PERIOD)) {
            tabPane.getSelectionModel().selectNext();
        } else if (keyCode.equals(KeyCode.COMMA)) {
            tabPane.getSelectionModel().selectPrevious();
        }
    }
}
