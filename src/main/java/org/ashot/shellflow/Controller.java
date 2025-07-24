package org.ashot.shellflow;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.node.Recents;
import org.ashot.shellflow.node.menu.file.FileMenu;
import org.ashot.shellflow.node.menu.settings.SettingsMenu;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.node.tab.preset.PresetSetupTab;
import org.ashot.shellflow.node.tab.profiler.ProfilerTab;
import org.ashot.shellflow.node.tab.setup.EntrySetupTab;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.ashot.shellflow.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static org.ashot.shellflow.utils.FileUtils.openMostRecentFile;
import static org.ashot.shellflow.utils.Utils.*;


public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private VBox sceneContainer;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane mainTabPane;
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

    private EntrySetupTab entrySetupTab;
    private static String currentlyLoadedFileLocation;

    public static final int SETUP_TABS = 3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register("main", this);
        sequentialName.visibleProperty().bind(sequentialOption.selectedProperty());
        setupMenuBar();
        setupIcons();
        setupTabs();
        setupOSInfo(osInfo);
        if (openMostRecentFile(this::openFile) == null) {
            currentlyLoadedFileLocation = null;
        }
    }

    private void setupMenuBar() {
        menuBar.getMenus().addAll(
                new FileMenu(this::openFile, this::writeEntriesToFile, mainTabPane),
                new SettingsMenu()
        );
    }

    private void setupTabs() {
        entrySetupTab = new EntrySetupTab();
        PresetSetupTab presetSetupTab = new PresetSetupTab();
        ExecutionsTab executionsTab = new ExecutionsTab();
        ProfilerTab profilerTab = new ProfilerTab();
        mainTabPane.getTabs().addAll(entrySetupTab, presetSetupTab, executionsTab, profilerTab);
        mainTabPane.prefWidthProperty().bind(sceneContainer.widthProperty());
        mainTabPane.getSelectionModel().selectedIndexProperty().addListener((_, _, newSelection) -> {
            if (newSelection.intValue() != TabIndices.ENTRIES.ordinal()) {
                newEntryBtn.setDisable(true);
                clearEntriesBtn.setDisable(true);
            } else {
                newEntryBtn.setDisable(false);
                clearEntriesBtn.setDisable(false);
            }
        });
        entrySetupTab.addEntryListChangeListener(_ -> executeAllBtn.setDisable(entrySetupTab.getEntryBoxes().isEmpty()));
        executionsTab.getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) _ -> {
            profilerTab.refreshProcesses(executionsTab.getExecutionsTabPane());
            stopAllBtn.setDisable(mainTabPane.getTabs().size() <= SETUP_TABS);
        });
    }

    private void setupIcons() {
        newEntryBtn.setGraphic(Icons.getAddButtonIcon(CustomButton.BUTTON_ICON_SIZE));
        clearEntriesBtn.setGraphic(Icons.getClearIcon(CustomButton.BUTTON_ICON_SIZE));
        executeAllBtn.setGraphic(Icons.getExecuteAllButtonIcon(CustomButton.BUTTON_ICON_SIZE));
        stopAllBtn.setGraphic(Icons.getCloseButtonIcon(CustomButton.BUTTON_ICON_SIZE));
    }

    public void executeAll() {
        CommandExecutor.executeAll(entrySetupTab.getEntries(), sequentialOption.isSelected(), sequentialName.getText(), (int) delayPerCmd.getValue());
    }

    public void stopAll() {
        ProcessRegistry.killAllProcesses();
    }

    private void writeEntriesToFile(File file) {
        log.debug("Saving file: {}", file.getAbsolutePath());
        JSONObject jsonObject = createSaveJSONObject(entrySetupTab.getEntries(),
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
            entrySetupTab.clearEntryBoxes();
            entrySetupTab.addEntryBox();
        });
    }

    private void openFile(File fileToLoad) {
        log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
        JSONObject jsonData = createJSONObject(fileToLoad);
        if (jsonData == null || jsonData.isEmpty()) {
            return;
        }
        log.debug("Loading: {}", jsonData.toString(1));
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        entrySetupTab.clearEntryBoxes();
        for (Object object: jsonArray) {
            if (object instanceof JSONObject entryJSON) {
                String name = getOrDefault(entryJSON.opt(FieldType.NAME.getValue()), FieldType.NAME);
                String path = getOrDefault(entryJSON.opt(FieldType.PATH.getValue()), FieldType.PATH);
                String cmd = getOrDefault(entryJSON.opt(FieldType.COMMAND.getValue()), FieldType.COMMAND);
                String wsl = getOrDefault(entryJSON.opt(FieldType.WSL.getValue()), FieldType.WSL);
                entrySetupTab.addEntryBox(new Entry(name, path, cmd, Boolean.parseBoolean(wsl)));
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

    public void addEntry() {
        entrySetupTab.addEntryBox();
    }

    public TabPane getTabPane() {
        return mainTabPane;
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
