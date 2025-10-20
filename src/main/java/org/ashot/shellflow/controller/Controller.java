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
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.menu.MainMenuBar;
import org.ashot.shellflow.node.notification.ShellFlowTray;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.utils.Animations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static javafx.scene.layout.HeaderDragType.DRAGGABLE_SUBTREE;
import static org.ashot.shellflow.utils.FileUtils.getMostRecentlyOpenedFile;


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
        setupTabs();
        setupMenuBar();
        mainModal.getStyleClass().add("modal");
        ShellFlowTray.init(executionManagementController);
        handlePerformanceMode();
        return createHeader();
    }

    private HeaderBar createHeader() {
        HeaderBar.setDragType(mainMenuBar, DRAGGABLE_SUBTREE);
        return new HeaderBar(null, mainMenuBar, null);
    }

    private void setupMenuBar() {
        mainMenuBar = new MainMenuBar(file -> entryManagementController.load(file), file -> entryManagementController.save(file), entryManagementController.getCurrentFileAbsolutePathProperty(), mainModal);
        optimizationMode = mainMenuBar.getSettingsMenu().getPerformanceSettingMenuItem().performanceModePropertyProperty();
    }

    private void setupTabs() {
        executionManagementController = new ExecutionManagementController();
        variableManagementController = new VariableManagementController(new File(ShellFlow.getConfig().variablesConfigLocation()));
        entryManagementController = new EntryManagementController(executionManagementController, variableManagementController, new EntryMapper(variableManagementController), getMostRecentlyOpenedFile());
        mainTabPane.getTabs().add(TabIndices.ENTRIES.ordinal(), entryManagementController.getView());
        mainTabPane.getTabs().add(TabIndices.EXECUTIONS.ordinal(), executionManagementController.getView());
        mainTabPane.prefWidthProperty().bind(sceneContainer.widthProperty());
        sceneContainer.getScene().setOnKeyPressed(this::handleUserInput);
    }

    private void handlePerformanceMode() {
        entryManagementController.optimizationModeProperty().bind(optimizationMode);
        variableManagementController.animatedProperty().bind(optimizationMode.not());
        ThemedSettingsProvider.optimizationModeProperty().bind(optimizationMode);
        Animations.performanceMode.bind(optimizationMode);
    }

    private void handleUserInput(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (executionManagementController.getView().isSelected() && keyEvent.isControlDown()) {
            TabPane executionsTabPane = executionManagementController.getView().getExecutionsTabPane();
            if (keyEvent.isShiftDown()) {
                Node node = executionsTabPane.getSelectionModel().getSelectedItem().getContent();
                if (node instanceof TabPane tabPane) {
                    if (keyCode.equals(KeyCode.PERIOD)) {
                        tabPane.getSelectionModel().selectNext();
                    } else if (keyCode.equals(KeyCode.COMMA)) {
                        tabPane.getSelectionModel().selectPrevious();
                    }
                }
            } else {
                if (keyCode.equals(KeyCode.PERIOD)) {
                    executionsTabPane.getSelectionModel().selectNext();
                } else if (keyCode.equals(KeyCode.COMMA)) {
                    executionsTabPane.getSelectionModel().selectPrevious();
                }
            }
        }
    }
}
