package org.ashot.microservice_starter;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.RecentFolders;
import org.ashot.microservice_starter.node.tabs.OutputTab;
import org.ashot.microservice_starter.node.tabs.PresetSetupTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.utils.FileUtils;
import org.ashot.microservice_starter.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private VBox sceneContainer;
    @FXML
    private MenuItem loadBtn;
    @FXML
    private Menu openRecent;
    @FXML
    private MenuItem saveBtn;
    @FXML
    private FlowPane container;
    @FXML
    private TabPane tabs;
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
    @FXML
    private Button currentCmd;
    @FXML
    private CheckBox textWrapToggle;
    @FXML
    private Button clearOutput;

    private String lastSaved;
    private String lastLoaded;

    public static final int SETUP_TABS = 2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register("main", this);
        Utils.setupOSInfo(osInfo);
        FileUtils.initializeSaveFolder();

        container.getChildren().addListener((ListChangeListener<Node>) _ -> executeAllBtn.setDisable(container.getChildren().isEmpty()));
        sequentialOption.selectedProperty().addListener((_, _, newValue) -> sequentialName.setVisible(newValue));
        tabs.getTabs().addListener((ListChangeListener<Tab>) _ -> stopAllBtn.setDisable(tabs.getTabs().size() <= SETUP_TABS));
        tabs.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
                clearOutput.setDisable(Utils.getSelectedOutputTab(tabs) == null)
        );
        tabs.getTabs().add(new PresetSetupTab());
        tabs.prefWidthProperty().bind(sceneContainer.widthProperty());

        setupIcons();
        newEntry(null);
        openRecent.setOnShowing(_ -> refreshRecentlyOpenedFolders());
        loadRecentFolders();
        loadMostRecentFile();
        //todo fix
        textWrapToggle.setDisable(true);
        textWrapToggle.setTooltip(new Tooltip("Currently not working properly"));
    }

    private void loadRecentFolders() {
        JSONObject dirs = Utils.setupFolders();
        lastSaved = (String) dirs.get(DirType.LAST_SAVED.name());
        lastLoaded = (String) dirs.get(DirType.LAST_LOADED.name());
        refreshRecentlyOpenedFolders();
    }

    private void refreshRecentlyOpenedFolders() {
        RecentFolders.refreshCurrentlyOpenedFolders(openRecent, this.tabs, this::loadFromFile);
    }

    private void loadMostRecentFile(){
        RecentFolders.loadMostRecentFile(this::loadFromFile);
    }

    private void setupIcons() {
        int MENU_ITEM_ICON_SIZE = 18;
        loadBtn.setGraphic(Icons.getOpenIcon(MENU_ITEM_ICON_SIZE));
        openRecent.setGraphic(Icons.getOpenRecentIcon(MENU_ITEM_ICON_SIZE));
        saveBtn.setGraphic(Icons.getSaveIcon(MENU_ITEM_ICON_SIZE));
        int BUTTON_ICON_SIZE = 20;
        newEntryBtn.setGraphic(Icons.getAddButtonIcon(BUTTON_ICON_SIZE));
        clearEntriesBtn.setGraphic(Icons.getClearIcon(BUTTON_ICON_SIZE));
        clearOutput.setGraphic(Icons.getClearIcon(BUTTON_ICON_SIZE));
        executeAllBtn.setGraphic(Icons.getExecuteAllButtonIcon(BUTTON_ICON_SIZE));
        stopAllBtn.setGraphic(Icons.getCloseButtonIcon(BUTTON_ICON_SIZE));
    }

    public void newEntry(ActionEvent e) {
        container.getChildren().add(new Entry().buildEmptyEntry(container));
    }

    private void newEntry(String name, String path, String cmd, boolean wsl) {
        container.getChildren().add(new Entry().buildEntry(container, name, path, cmd, wsl));
    }

    public void executeAll() {
       CommandExecution.executeAll(container, sequentialOption.isSelected(), sequentialName.getText(), (int) delayPerCmd.getValue());
    }

    public void stopAll() {
        ProcessRegistry.killAllProcesses();
        this.tabs.getTabs().forEach(tab -> {
            if (tab instanceof OutputTab outputTab) {
                Platform.runLater(() -> tabs.getTabs().remove(outputTab));
            }
        });
        Platform.runLater(() -> tabs.getSelectionModel().selectFirst());
    }

    public void save(ActionEvent e) {
        loadRecentFolders();
        File savedFile = chooseFile(true);
        if (savedFile != null) {
            if(!savedFile.getAbsolutePath().endsWith(".json")){
                savedFile = new File(savedFile.getAbsolutePath() + ".json");
            }
            saveToFile(savedFile);
            RecentFolders.saveDirReference(DirType.LAST_SAVED, savedFile.getParent());
        }
    }

    public void load(ActionEvent e) {
        loadRecentFolders();
        File loadedFile = chooseFile(false);
        if (loadedFile != null) {
            loadFromFile(loadedFile);
            RecentFolders.saveDirReference(DirType.LAST_LOADED, loadedFile.getParent());
        }
    }

    private void saveToFile(File fileToSave) {
        log.debug("Saving file: {}", fileToSave.getAbsolutePath());
        JSONObject jsonObject = Utils.createSaveJSONObject(container, (int) delayPerCmd.getValue(), sequentialOption.isSelected(), sequentialName.getText());
        log.debug("Saving: {}", jsonObject.toString(1));
        FileUtils.writeJSONDataToFile(fileToSave, jsonObject);
        log.debug("Saved: {}", fileToSave.getAbsolutePath());
        RecentFolders.saveRecentDir(fileToSave.getAbsolutePath());
    }

    public void toggleTextWrap(ActionEvent e) {
        for (Tab t : tabs.getTabs()) {
            if (t instanceof OutputTab outputTab) {
                outputTab.toggleWrapText(textWrapToggle.isSelected());
            }
        }
    }

    public void clearOutput(ActionEvent e){
        OutputTab tab = Utils.getSelectedOutputTab(tabs);
        if(tab != null){
            tab.getCodeArea().clear();
            tab.getOutputSearchOptions().setUsedScrolling(false);
        }
    }

    public void clearAllEntries(ActionEvent e) {
        Platform.runLater(() -> {
            container.getChildren().clear();
            newEntry(null);
        });
    }

    private void loadFromFile(File fileToLoad) {
        log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
        JSONObject jsonData = Utils.createJSONObject(fileToLoad);
        if(jsonData == null || jsonData.isEmpty()){
            return;
        }
        log.debug("Loading: {}", jsonData.toString(1));
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        container.getChildren().clear();
        for (Object j : jsonArray) {
            if (j instanceof JSONObject entry) {
                if(Utils.checkEntryFieldsFromJSON(entry)) {
                    String name = Utils.getOrDefault(entry.opt(FieldType.NAME.getValue()), FieldType.NAME);
                    String path = Utils.getOrDefault(entry.opt(FieldType.PATH.getValue()), FieldType.PATH);
                    String cmd = Utils.getOrDefault(entry.opt(FieldType.COMMAND.getValue()), FieldType.COMMAND);
                    String wsl = Utils.getOrDefault(entry.opt(FieldType.WSL.getValue()), FieldType.WSL);
                    newEntry(name, path, cmd, Boolean.parseBoolean(wsl));
                } else{
                    newEntry(null);
                }
            }
        }
        delayPerCmd.setValue(jsonData.getDouble("delay"));
        sequentialOption.setSelected(jsonData.getBoolean("sequential"));
        sequentialName.setText(jsonData.getString("sequentialName"));
        RecentFolders.saveRecentDir(fileToLoad.getAbsolutePath());
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
    }

    private File chooseFile(boolean save) {
        return FileUtils.chooseFile(save, save ? lastSaved : lastLoaded);
    }

    public void lightMode(ActionEvent e) {
        Main.setThemeMode(false);
    }

    public void darkMode(ActionEvent e) {
        Main.setThemeMode(true);
    }

    public TabPane getTabs() {
        return tabs;
    }

    public boolean getTextWrapOption() {
        return textWrapToggle.isSelected();
    }

}
