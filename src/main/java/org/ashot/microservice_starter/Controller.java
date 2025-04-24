package org.ashot.microservice_starter;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.Icons;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.OutputTab;
import org.ashot.microservice_starter.node.TabOutputOptions;
import org.ashot.microservice_starter.node.popup.OutputPopup;
import org.ashot.microservice_starter.node.setup.PresetSetupTab;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.ashot.microservice_starter.data.constant.TextFieldType.typeToShort;

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
    private Button currentCmd;

    private String currentCmdText = "";

    private String lastSaved;
    private String lastLoaded;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerRegistry.register("main", this);
        Utils.setupOSInfo(osInfo);
        loadBtn.setGraphic(Icons.getOpenIcon(18));
        openRecent.setGraphic(Icons.getOpenRecentIcon(18));
        saveBtn.setGraphic(Icons.getSaveIcon(18));
        newEntryBtn.setGraphic(Icons.getAddButtonIcon(24));
        newEntryBtn.setContentDisplay(ContentDisplay.RIGHT);
        executeAllBtn.setGraphic(Icons.getExecuteAllButtonIcon(24));
        executeAllBtn.setContentDisplay(ContentDisplay.RIGHT);
        executeAllBtn.setGraphicTextGap(12);
        container.getChildren().addListener((ListChangeListener<Node>) c -> executeAllBtn.setDisable(container.getChildren().isEmpty()));
        sequentialOption.selectedProperty().addListener((_, _, newValue) -> sequentialName.setVisible(newValue));
        openRecent.setOnShowing(_ -> refreshRecentlyOpenedFolders());
        loadRecentFolders();
        tabs.getTabs().add(PresetSetupTab.setupPresetTab());
        List<Node> setupOptions = setupSettings.getChildren().stream().toList();
        tabs.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            setupSettings.getChildren().clear();
            if(newValue.getId() != null && (newValue.getId().equals("setupTab") || newValue.getId().equals("presetSetupTab"))){
                setupSettings.getChildren().setAll(setupOptions);
            }else{
                if(newValue instanceof  OutputTab){
                    setupSettings.getChildren().setAll(new TabOutputOptions((OutputTab) newValue).getOptions());
                }
            }
        });
        newEntry(null);
    }

    private void loadRecentFolders(){
        JSONObject dirs = Utils.setupFolders();
        lastSaved = (String) dirs.get(DirType.LAST_SAVED.name());
        lastLoaded = (String) dirs.get(DirType.LAST_LOADED.name());
        refreshRecentlyOpenedFolders();
    }

    private void refreshRecentlyOpenedFolders(){
        List<String> toRemove = disabledRecentFoldersToRemove();
        openRecent.getItems().clear();
        JSONArray recentFolders = Utils.getRecentFiles();
        for (Object s : recentFolders.toList()){
            String recentFolder = s.toString();
            if(toRemove.contains(recentFolder)){
                Utils.removeRecentFile(recentFolder);
            }
            MenuItem m = new MenuItem();
            m.setText(recentFolder);
            m.setOnAction(_ ->{
                File file = new File(recentFolder);
                if(file.exists()){
                    loadFromFile(file);
                }
            });
            m.setDisable(!new File(recentFolder).exists());
            openRecent.getItems().add(m);
        }
    }

    private List<String> disabledRecentFoldersToRemove(){
        List<String> list = new ArrayList<>();
        for(MenuItem m : openRecent.getItems()){
            if(m.isDisable()){
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

    public void save(ActionEvent e) {
        loadRecentFolders();
        File savedFile = chooseFile(true);
        if(savedFile != null) {
            saveToFile(savedFile);
            Utils.saveDirReference(DirType.LAST_SAVED, savedFile.getParent());
        }
    }

    public void load(ActionEvent e) {
        loadRecentFolders();
        File loadedFile = chooseFile(false);
        if(loadedFile != null) {
            loadFromFile(loadedFile);
            Utils.saveDirReference(DirType.LAST_LOADED, loadedFile.getParent());
            Utils.saveRecentDir(loadedFile.getAbsolutePath());
            refreshRecentlyOpenedFolders();
        }
    }

    private File saveToFile(File fileToSave) {
        JSONObject jsonObject = Utils.createSaveJSONObject(container, (int) delayPerCmd.getValue(), sequentialOption.isSelected(), sequentialName.getText());
        log.debug("Saving: {}", jsonObject.toString(1));
        Utils.writeDataToFile(fileToSave, jsonObject);
        return fileToSave;
    }

    private File loadFromFile(File fileToLoad) {
        JSONObject jsonData = Utils.createJSONObject(fileToLoad);
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        container.getChildren().clear();
        for (Object j : jsonArray) {
            if (j instanceof JSONObject jsonObject) {
                String name = jsonObject.get(typeToShort(TextFieldType.NAME)).toString();
                String path = jsonObject.get(typeToShort(TextFieldType.PATH)).toString();
                String cmd = jsonObject.get(typeToShort(TextFieldType.COMMAND)).toString();
                newEntry(name, path, cmd);
            }
        }
        delayPerCmd.setValue(jsonData.getDouble("delay"));
        sequentialOption.setSelected(jsonData.getBoolean("sequential"));
        sequentialName.setText(jsonData.getString("sequentialName"));
        sequentialName.setVisible(sequentialOption.isSelected());
        executeAllBtn.setDisable(container.getChildren().isEmpty());
        setCurrentCmdText("", currentCmd, false);
        log.debug("Loaded: {}", jsonData.toString(1));
        return fileToLoad;
    }

    private File chooseFile(boolean save){
        return Utils.chooseFile(save, save ? lastSaved : lastLoaded);
    }

    private void setCurrentCmdText(String text, Button currentCmd, boolean visible){
        currentCmdText = text;
        currentCmd.setVisible(visible);
    }

    public void printCurrentCmd(ActionEvent e){
        OutputPopup.outputPopup(currentCmdText, "Commands Executed", sequentialOption.isSelected());
    }
    public void lightMode(ActionEvent e){
        Main.setThemeMode(false);
    }
    public void darkMode(ActionEvent e){
        Main.setThemeMode(true);
    }

    public TabPane getTabs(){
        return tabs;
    }

    public GridPane getSetupSettings() {
        return setupSettings;
    }
}
