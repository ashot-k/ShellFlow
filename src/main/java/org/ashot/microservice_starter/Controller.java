package org.ashot.microservice_starter;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.RecentFolders;
import org.ashot.microservice_starter.node.popup.OutputPopup;
import org.ashot.microservice_starter.node.tabs.OutputTab;
import org.ashot.microservice_starter.node.tabs.PresetSetupTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private MenuItem loadBtn;
    @FXML
    private Menu openRecent;
    @FXML
    private MenuItem saveBtn;
    @FXML
    private FlowPane container;
    @FXML
    private GridPane setupSettings;
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

    private String currentCmdText = "";

    private String lastSaved;
    private String lastLoaded;

    private final int SETUP_TABS = 3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register("main", this);
        Utils.setupOSInfo(osInfo);
        setupIcons();
        container.getChildren().addListener((ListChangeListener<Node>) _ -> executeAllBtn.setDisable(container.getChildren().isEmpty()));
        tabs.getTabs().addListener((ListChangeListener<Tab>) _ -> stopAllBtn.setDisable(tabs.getTabs().size() <= SETUP_TABS));
        sequentialOption.selectedProperty().addListener((_, _, newValue) -> sequentialName.setVisible(newValue));
        openRecent.setOnShowing(_ -> refreshRecentlyOpenedFolders());
        loadRecentFolders();
        tabs.prefWidthProperty().bind(((VBox) tabs.getParent().getParent()).widthProperty());
        tabs.getTabs().add(new PresetSetupTab());
        newEntry(null);
        //todo add checks for null etc
        loadFromFile(new File(RecentFolders.getRecentFiles().getString(0)));
    }

    private void setupIcons() {
        int MENU_ITEM_ICON_SIZE = 18;
        loadBtn.setGraphic(Icons.getOpenIcon(MENU_ITEM_ICON_SIZE));
        openRecent.setGraphic(Icons.getOpenRecentIcon(MENU_ITEM_ICON_SIZE));
        saveBtn.setGraphic(Icons.getSaveIcon(MENU_ITEM_ICON_SIZE));
        int BUTTON_ICON_SIZE = 20;
        newEntryBtn.setGraphic(Icons.getAddButtonIcon(BUTTON_ICON_SIZE));
        clearEntriesBtn.setGraphic(Icons.getClearEntriesIcon(BUTTON_ICON_SIZE));
        executeAllBtn.setGraphic(Icons.getExecuteAllButtonIcon(BUTTON_ICON_SIZE));
        stopAllBtn.setGraphic(Icons.getCloseButtonIcon(BUTTON_ICON_SIZE));
    }

    private void loadRecentFolders() {
        JSONObject dirs = Utils.setupFolders();
        lastSaved = (String) dirs.get(DirType.LAST_SAVED.name());
        lastLoaded = (String) dirs.get(DirType.LAST_LOADED.name());
        refreshRecentlyOpenedFolders();
    }

    private void refreshRecentlyOpenedFolders() {
        List<String> toRemove = disabledRecentFoldersToRemove();
        openRecent.getItems().clear();
        JSONArray recentFolders = RecentFolders.getRecentFiles();
        for (Object s : recentFolders.toList()) {
            String recentFolder = s.toString();
            if (toRemove.contains(recentFolder)) {
                RecentFolders.removeRecentFile(recentFolder);
            }
            MenuItem m = new MenuItem();
            m.setText(recentFolder);
            m.setOnAction(_ -> {
                File file = new File(recentFolder);
                if (file.exists()) {
                    loadFromFile(file);
                    this.tabs.getSelectionModel().selectFirst();
                }
            });
            m.setDisable(!new File(recentFolder).exists());
            openRecent.getItems().add(m);
        }
    }

    private List<String> disabledRecentFoldersToRemove() {
        List<String> list = new ArrayList<>();
        for (MenuItem m : openRecent.getItems()) {
            if (m.isDisable()) {
                list.add(m.getText());
            }
        }
        return list;
    }

    public void newEntry(ActionEvent e) {
        container.getChildren().add(new Entry().buildEmptyEntry(container));
    }

    private void newEntry(String name, String path, String cmd) {
        container.getChildren().add(new Entry().buildEntry(container, name, path, cmd));
    }

    public void executeAll() {
        setCurrentCmdText("", currentCmd, false);
        String commands = CommandExecution.executeAll(container, sequentialOption.isSelected(), sequentialName.getText(), (int) delayPerCmd.getValue());
        setCurrentCmdText(commands, currentCmd, true);
    }

    public void stopAll() {
        ProcessRegistry.killAllProcesses();
        this.tabs.getTabs().forEach(tab -> {
            if (tab instanceof OutputTab outputTab) {
                Platform.runLater(() -> tabs.getTabs().remove(outputTab));
            }
        });
    }

    public void save(ActionEvent e) {
        loadRecentFolders();
        File savedFile = chooseFile(true);
        if (savedFile != null) {
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
            refreshRecentlyOpenedFolders();
        }
    }

    private void saveToFile(File fileToSave) {
        log.debug("Saving file: {}", fileToSave.getAbsolutePath());
        JSONObject jsonObject = Utils.createSaveJSONObject(container, (int) delayPerCmd.getValue(), sequentialOption.isSelected(), sequentialName.getText());
        log.debug("Saving: {}", jsonObject.toString(1));
        Utils.writeDataToFile(fileToSave, jsonObject);
        log.debug("Saved: {}", fileToSave.getAbsolutePath());
        RecentFolders.saveRecentDir(fileToSave.getAbsolutePath());
    }

    public void toggleTextWrap(ActionEvent e) {
        for (Tab t : tabs.getTabs()) {
            if (t instanceof OutputTab outputTab) {
                outputTab.getOutputTabOptions().toggleWrapText(textWrapToggle.isSelected());
            }
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
        log.debug("Loading: {}", jsonData.toString(1));
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        container.getChildren().clear();
        for (Object j : jsonArray) {
            if (j instanceof JSONObject jsonObject) {
                String name = jsonObject.get(TextFieldType.NAME.getValue()).toString();
                String path = jsonObject.get(TextFieldType.PATH.getValue()).toString();
                String cmd = jsonObject.get(TextFieldType.COMMAND.getValue()).toString();
                newEntry(name, path, cmd);
            }
        }
        delayPerCmd.setValue(jsonData.getDouble("delay"));
        sequentialOption.setSelected(jsonData.getBoolean("sequential"));
        sequentialName.setText(jsonData.getString("sequentialName"));
        setCurrentCmdText("", currentCmd, false);
        RecentFolders.saveRecentDir(fileToLoad.getAbsolutePath());
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
    }

    private File chooseFile(boolean save) {
        return Utils.chooseFile(save, save ? lastSaved : lastLoaded);
    }

    private void setCurrentCmdText(String text, Button currentCmd, boolean visible) {
        currentCmdText = text;
        currentCmd.setVisible(visible);
    }

    public void printCurrentCmd(ActionEvent e) {
        OutputPopup.outputPopup(currentCmdText, "Commands Executed", sequentialOption.isSelected());
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
