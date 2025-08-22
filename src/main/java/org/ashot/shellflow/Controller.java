package org.ashot.shellflow;

import atlantafx.base.controls.ModalPane;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.node.menu.MainMenuBar;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.node.tab.preset.PresetSetupTab;
import org.ashot.shellflow.node.tab.profiler.ProfilerTab;
import org.ashot.shellflow.node.tab.setup.EntrySetupTab;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.ashot.shellflow.utils.FileUtils.openMostRecentFile;
import static org.ashot.shellflow.utils.Utils.*;


public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private ModalPane mainModal;
    @FXML
    private VBox sceneContainer;
    @FXML
    private TabPane mainTabPane;

    private EntrySetupTab entrySetupTab;
    private PresetSetupTab presetSetupTab;
    private ExecutionsTab executionsTab;
    private ProfilerTab profilerTab;
    private static String currentFile;
    private static String currentFileAbsolutePath;

    public void init(){
        ControllerRegistry.register("main", this);
        sceneContainer.getChildren().addFirst(new MainMenuBar(this::openFile, this::writeEntriesToFile));
        setupTabs();
        if (openMostRecentFile(this::openFile) == null) {
            currentFile = null;
        }
        mainModal.getStyleClass().add("modal");
    }

    private void setupTabs() {
        entrySetupTab = new EntrySetupTab();
//        presetSetupTab = new PresetSetupTab();
        executionsTab = new ExecutionsTab();
//        profilerTab = new ProfilerTab();
        mainTabPane.getTabs().add(TabIndices.ENTRIES.ordinal(), entrySetupTab);
//        mainTabPane.getTabs().add(TabIndices.PRESET_TAB.ordinal(), presetSetupTab);
        mainTabPane.getTabs().add(TabIndices.EXECUTIONS.ordinal() - 1, executionsTab);

        mainTabPane.prefWidthProperty().bind(sceneContainer.widthProperty());
        executionsTab.getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) _ -> {
            entrySetupTab.getCloseAllButton().setDisable(executionsTab.getExecutionsTabPane().getTabs().isEmpty());
        });
        sceneContainer.getScene().setOnKeyPressed(this::handleUserInput);
    }

    private void writeEntriesToFile(File file) {
        log.debug("Saving file: {}", file.getAbsolutePath());
        JSONObject jsonObject = createSaveJSONObject(entrySetupTab.getEntries(),
                (int) entrySetupTab.getDelayPerCmdSlider().getValue(),
                entrySetupTab.getSequentialOption().isSelected(),
                entrySetupTab.getExecutionName().getText());
        log.debug("Saving: {}", jsonObject.toString(1));
        FileUtils.writeJSONDataToFile(file, jsonObject);
        log.debug("Saved: {}", file.getAbsolutePath());
        refreshFileLoaded(file.getAbsolutePath());
        RecentFileUtils.saveRecentFile(file.getAbsolutePath());
        RecentFileUtils.refreshDirLocation(DirType.LAST_SAVED, file.getParent());
        entrySetupTab.resetEdited();
        openFile(file);
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
                String name = getOrDefault(entryJSON.opt(FieldType.NAME.getId()), FieldType.NAME);
                String path = getOrDefault(entryJSON.opt(FieldType.PATH.getId()), FieldType.PATH);
                String cmd = getOrDefault(entryJSON.opt(FieldType.COMMAND.getId()), FieldType.COMMAND);
                String wsl = getOrDefault(entryJSON.opt(FieldType.WSL.getId()), FieldType.WSL);
                String enabled = getOrDefault(entryJSON.opt(FieldType.ENABLED.getId()), FieldType.ENABLED);
                entrySetupTab.addEntryBox(new Entry(name, path, cmd, Boolean.parseBoolean(wsl), Boolean.parseBoolean(enabled)));
            }
        }
        entrySetupTab.getDelayPerCmdSlider().setValue(jsonData.getInt(FieldType.DELAY.getId()));
        entrySetupTab.getSequentialOption().setSelected(jsonData.getBoolean(FieldType.SEQUENTIAL.getId()));
        entrySetupTab.getExecutionName().setText(jsonData.getString(FieldType.EXECUTION_NAME.getId()));
        RecentFileUtils.saveRecentFile(fileToLoad.getAbsolutePath());
        RecentFileUtils.refreshDirLocation(DirType.LAST_LOADED, fileToLoad.getParent());
        refreshFileLoaded(fileToLoad.getAbsolutePath());
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
        this.mainTabPane.getSelectionModel().select(TabIndices.ENTRIES.ordinal());
    }

    public ExecutionsTab getExecutionsTab() {
        return executionsTab;
    }

    public EntrySetupTab getEntrySetupTab() {
        return entrySetupTab;
    }

    public PresetSetupTab getPresetSetupTab() {
        return presetSetupTab;
    }

    public ProfilerTab getProfilerTab() {
        return profilerTab;
    }

    public void refreshFileLoaded(String path) {
        currentFileAbsolutePath = path;
        String formattedPath = "";
        String delimiter = checkIfWindows() ? "\\\\" : "/";
        String[] splitPath = path.split(delimiter);
        formattedPath = splitPath[splitPath.length -1];
        currentFile = formattedPath;
        entrySetupTab.setFileLoadedText(currentFile);
    }

    private static void resetFileLoaded() {
        currentFileAbsolutePath = "";
    }

    public static String getCurrentFile() {
        return currentFile;
    }

    public static String getCurrentFileAbsolutePath() {
        if (new File(currentFileAbsolutePath).exists()) {
            return currentFileAbsolutePath;
        }
        resetFileLoaded();
        return "";
    }

    private void handleUserInput(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (getExecutionsTab().isSelected() && keyEvent.isControlDown()) {
            TabPane executionsTabPane = executionsTab.getExecutionsTabPane();
            if (keyEvent.isShiftDown()) {
                Node node = executionsTabPane.getSelectionModel().getSelectedItem().getContent();
                if(node instanceof TabPane tabPane){
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

    public ModalPane getMainModal() {
        return mainModal;
    }
}
