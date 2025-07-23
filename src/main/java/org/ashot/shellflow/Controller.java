package org.ashot.shellflow;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.node.Recents;
import org.ashot.shellflow.node.entry.Entry;
import org.ashot.shellflow.node.menu.file.FileMenu;
import org.ashot.shellflow.node.menu.settings.SettingsMenu;
import org.ashot.shellflow.node.tab.OutputTab;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.node.tab.preset.PresetSetupTab;
import org.ashot.shellflow.node.tab.profiler.ProfilerTab;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static org.ashot.shellflow.utils.FileUtils.openMostRecentFile;


public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private VBox sceneContainer;
    @FXML
    private MenuBar menuBar;
    @FXML
    private FlowPane container;
    @FXML
    private TabPane tabPane;
    @FXML
    private Text fileLoaded;
    @FXML
    private Button osInfo;
    @FXML
    private Button newEntryBtn;
    @FXML
    private Slider delayPerCmd;
    @FXML
    private CheckBox sequentialOption;
    @FXML
    private TextField sequentialName;
    @FXML
    private Button executeAllBtn;
    @FXML
    private Button stopAllBtn;
    @FXML
    private Button clearEntriesBtn;

    private static String currentlyLoadedFileLocation;

    public static final int SETUP_TABS = 3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register("main", this);
        Utils.setupOSInfo(osInfo);

        container.getChildren().addListener((ListChangeListener<Node>) _ -> executeAllBtn.setDisable(container.getChildren().isEmpty()));
        sequentialOption.selectedProperty().addListener((_, _, newValue) -> sequentialName.setVisible(newValue));
        tabPane.getSelectionModel().selectedIndexProperty().addListener((_, _, newSelection) -> {
            if (newSelection.intValue() != TabIndices.ENTRIES.ordinal()) {
                newEntryBtn.setDisable(true);
                clearEntriesBtn.setDisable(true);
            } else {
                newEntryBtn.setDisable(false);
                clearEntriesBtn.setDisable(false);
            }
        });
        PresetSetupTab presetSetupTab = new PresetSetupTab();
        ExecutionsTab executionsTab = new ExecutionsTab();
        ProfilerTab profilerTab = new ProfilerTab();
        tabPane.getTabs().addAll(presetSetupTab);
        tabPane.getTabs().addAll(executionsTab);
        tabPane.getTabs().addAll(profilerTab);
        tabPane.prefWidthProperty().bind(sceneContainer.widthProperty());

        executionsTab.getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) _ -> {
            profilerTab.refreshProcesses(executionsTab.getExecutionsTabPane());
            stopAllBtn.setDisable(tabPane.getTabs().size() <= SETUP_TABS);
        });

        setupIcons();
        addNewEntry();
        if(openMostRecentFile(this::openFile) == null){
            currentlyLoadedFileLocation = null;
        };
        initializeMenuBarItems();
    }

    private void initializeMenuBarItems(){
        menuBar.getMenus().addAll(
                new FileMenu(this::openFile, this::writeEntriesToFile, tabPane),
                new SettingsMenu()
        );
    }

    private void setupIcons() {
        newEntryBtn.setGraphic(Icons.getAddButtonIcon(CustomButton.BUTTON_ICON_SIZE));
        clearEntriesBtn.setGraphic(Icons.getClearIcon(CustomButton.BUTTON_ICON_SIZE));
        executeAllBtn.setGraphic(Icons.getExecuteAllButtonIcon(CustomButton.BUTTON_ICON_SIZE));
        stopAllBtn.setGraphic(Icons.getCloseButtonIcon(CustomButton.BUTTON_ICON_SIZE));
    }

    public void addNewEntry() {
        container.getChildren().add(new Entry().buildEmptyEntry(container));
    }

    private void addNewEntry(String name, String path, String cmd, boolean wsl) {
        container.getChildren().add(new Entry().buildEntry(container, name, path, cmd, wsl));
    }

    public void executeAll() {
        CommandExecutor.executeAll(container, sequentialOption.isSelected(), sequentialName.getText(), (int) delayPerCmd.getValue());
    }

    public void stopAll() {
        this.tabPane.getTabs().forEach(tab -> {
            if (tab instanceof OutputTab outputTab) {
                Platform.runLater(() -> {
                    outputTab.closeTerminal();
                    tabPane.getTabs().remove(outputTab);
                });
            }
        });
        ProcessRegistry.killAllProcesses();
        Platform.runLater(() -> tabPane.getSelectionModel().selectFirst());
    }

    private void writeEntriesToFile(File file) {
        log.debug("Saving file: {}", file.getAbsolutePath());
        JSONObject jsonObject = Utils.createSaveJSONObject(container,
                (int) delayPerCmd.getValue(),
                sequentialOption.isSelected(),
                sequentialName.getText());
        log.debug("Saving: {}", jsonObject.toString(1));
        FileUtils.writeJSONDataToFile(file, jsonObject);
        log.debug("Saved: {}", file.getAbsolutePath());
        refreshFileLoaded(file.getAbsolutePath());
        Recents.saveRecentFile(file.getAbsolutePath());
        Recents.refreshDir(DirType.LAST_SAVED, file.getParent());
    }

    public void clearEntries() {
        Platform.runLater(() -> {
            container.getChildren().clear();
            addNewEntry();
        });
    }

    private void openFile(File fileToLoad) {
        log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
        JSONObject jsonData = Utils.createJSONObject(fileToLoad);
        if (jsonData == null || jsonData.isEmpty()) {
            return;
        }
        log.debug("Loading: {}", jsonData.toString(1));
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        container.getChildren().clear();
        for (Object j : jsonArray) {
            if (j instanceof JSONObject entry) {
                if (Utils.checkEntryFieldsFromJSON(entry)) {
                    String name = Utils.getOrDefault(entry.opt(FieldType.NAME.getValue()), FieldType.NAME);
                    String path = Utils.getOrDefault(entry.opt(FieldType.PATH.getValue()), FieldType.PATH);
                    String cmd = Utils.getOrDefault(entry.opt(FieldType.COMMAND.getValue()), FieldType.COMMAND);
                    String wsl = Utils.getOrDefault(entry.opt(FieldType.WSL.getValue()), FieldType.WSL);
                    addNewEntry(name, path, cmd, Boolean.parseBoolean(wsl));
                } else {
                    addNewEntry();
                }
            }
        }
        delayPerCmd.setValue(jsonData.getDouble("delay"));
        sequentialOption.setSelected(jsonData.getBoolean("sequential"));
        sequentialName.setText(jsonData.getString("sequentialName"));
        Recents.saveRecentFile(fileToLoad.getAbsolutePath());
        Recents.refreshDir(DirType.LAST_LOADED, fileToLoad.getParent());
        refreshFileLoaded(fileToLoad.getAbsolutePath());
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public ExecutionsTab getExecutionsTab() {
        return (ExecutionsTab) getTabPane().getTabs().get(TabIndices.EXECUTIONS.ordinal());
    }

    public void refreshFileLoaded(String path) {
        currentlyLoadedFileLocation = path;
        fileLoaded.setText("File loaded: " + path);
    }

    private static void resetFileLoaded() {
        currentlyLoadedFileLocation = null;
    }

    public static String getCurrentlyLoadedFileLocation() {
        if (new File(currentlyLoadedFileLocation).exists()) {
            return currentlyLoadedFileLocation;
        }
        resetFileLoaded();
        return null;
    }

}
