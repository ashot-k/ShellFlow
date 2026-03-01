package org.ashot.shellflow.controller;

import atlantafx.base.controls.ModalPane;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.exception.io.FileReadFailureException;
import org.ashot.shellflow.node.header.ShellFlowHeader;
import org.ashot.shellflow.node.menu.MainMenuBar;
import org.ashot.shellflow.node.notification.Notifications;
import org.ashot.shellflow.node.notification.SystemTray;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.utils.GUIAnimations;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static javafx.scene.layout.HeaderDragType.DRAGGABLE_SUBTREE;


@SuppressWarnings("deprecation")
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private static final double MAX_ENTRIES_VARIABLES_SPLIT_POS = 0.85;
    private static final double INIT_ENTRIES_VARIABLES_SPLIT_POS = 0.77;
    private static final double INIT_ENTRIES_EXECUTIONS_SPLIT_POS = 0.30;
    private static final double MIN_ENTRIES_EXECUTIONS_SPLIT_POS = 0.15;
    private static final double MIN_ENTRIES_VARIABLES_SPLIT_POS = 0.80;
    private double entriesExecutionsSplitCurrentPos = 0;

    @FXML
    private StackPane mainWindowStackPane;
    @FXML
    private ModalPane mainModal;
    @FXML
    private VBox sceneContainer;
    @FXML
    private TabPane mainTabPane;

    private MainMenuBar mainMenuBar;

    private ExecutionManagementController executionManagementController = new ExecutionManagementController();
    private VariableManagementController variableManagementController = new VariableManagementController(new File(ShellFlow.getConfig().variablesConfigLocation()));
    private EntryManagementController entryManagementController = new EntryManagementController(executionManagementController, variableManagementController);
    private BooleanProperty optimizationMode;
    private SplitPane topVerticalSplit;
    private SplitPane bottomHorizontalSplit;

    public void init() {
        setupControllers();
        setupTabs();

        SystemTray.init(executionManagementController);
        Notifications.init(mainWindowStackPane);

        handlePerformanceMode();
        loadInitialData();
    }

    public void loadInitialData() {
        try {
            log.info("Initializing entries");
            entryManagementController.load(RecentFileUtils.getMostRecentlyOpenedFile());
        } catch (FileReadFailureException e) {
            log.error("Could not initialize entries: {}", e.getMessage());
        }
    }

    public ShellFlowHeader createHeader() {
        setupMenuBar();
        HeaderBar.setDragType(mainMenuBar, DRAGGABLE_SUBTREE);
        return new ShellFlowHeader(null, mainMenuBar, null);
    }

    private void setupMenuBar() {
        mainMenuBar = new MainMenuBar(file -> entryManagementController.load(file), file -> entryManagementController.save(file), mainModal);
        optimizationMode = mainMenuBar.getSettingsMenu().getPerformanceSettingMenuItem().performanceModePropertyProperty();
    }

    private void setupControllers() {
        executionManagementController = new ExecutionManagementController();
        variableManagementController = new VariableManagementController(new File(ShellFlow.getConfig().variablesConfigLocation()));
        entryManagementController = new EntryManagementController(executionManagementController, variableManagementController);
    }

    private void setupTabs() {
        Tab mainTab = new Tab("Main");
        mainTab.setClosable(false);
        topVerticalSplit = new SplitPane(entryManagementController.getView(), executionManagementController.getView());
        bottomHorizontalSplit = new SplitPane(topVerticalSplit, variableManagementController.getView());
        mainTab.setContent(bottomHorizontalSplit);
        setSplitsLimits();

        mainTabPane.getTabs().add(TabIndices.MAIN.ordinal(), mainTab);
        mainTabPane.prefWidthProperty().bind(sceneContainer.widthProperty());
        sceneContainer.getScene().setOnKeyPressed(this::handleUserInput);
    }

    private void setSplitsLimits() {
        topVerticalSplit.setDividerPosition(0, entriesExecutionsSplitCurrentPos != 0 ? entriesExecutionsSplitCurrentPos : INIT_ENTRIES_EXECUTIONS_SPLIT_POS);
        topVerticalSplit.getDividers().getFirst().positionProperty().addListener((_, _, position) -> {
            double pos = (double) position;
            if (pos > MAX_ENTRIES_VARIABLES_SPLIT_POS) {
                topVerticalSplit.getDividers().getFirst().setPosition(MAX_ENTRIES_VARIABLES_SPLIT_POS);
            } else if (pos < MIN_ENTRIES_EXECUTIONS_SPLIT_POS) {
                topVerticalSplit.getDividers().getFirst().setPosition(MIN_ENTRIES_EXECUTIONS_SPLIT_POS);
            }
            entriesExecutionsSplitCurrentPos = topVerticalSplit.getDividers().getFirst().getPosition();
        });

        bottomHorizontalSplit.setOrientation(Orientation.VERTICAL);
        bottomHorizontalSplit.setDividerPosition(0, INIT_ENTRIES_VARIABLES_SPLIT_POS);
        bottomHorizontalSplit.getDividers().getFirst().positionProperty().addListener((_, _, position) -> {
            if ((double) position > MIN_ENTRIES_VARIABLES_SPLIT_POS) {
                bottomHorizontalSplit.getDividers().getFirst().setPosition(MIN_ENTRIES_VARIABLES_SPLIT_POS);
            }
        });
        VBox.setVgrow(bottomHorizontalSplit, Priority.ALWAYS);
        HBox.setHgrow(bottomHorizontalSplit, Priority.ALWAYS);
    }

    private void handlePerformanceMode() {
        entryManagementController.optimizationModeProperty().bind(optimizationMode);
        ThemedSettingsProvider.optimizationModeProperty().bind(optimizationMode);
        GUIAnimations.performanceMode.bind(optimizationMode);
    }

    private void handleUserInput(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyEvent.isControlDown()) {
            SelectionModel<Tab> tabSelectionModel = executionManagementController.getView().getSelectionModel();
            if (keyEvent.isShiftDown()) {
                Node node = tabSelectionModel.getSelectedItem().getContent();
                if (node instanceof TabPane tabPane) {
                    moveToAdjacentTab(keyCode, tabPane.getSelectionModel());
                }
            } else {
                moveToAdjacentTab(keyCode, tabSelectionModel);
            }
        }
    }

    private void moveToAdjacentTab(KeyCode keyCode, SelectionModel<Tab> tabSelectionModel) {
        if (keyCode.equals(KeyCode.PERIOD)) {
            tabSelectionModel.selectNext();
        } else if (keyCode.equals(KeyCode.COMMA)) {
            tabSelectionModel.selectPrevious();
        }
    }
}
