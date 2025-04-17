package org.ashot.microservice_starter;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.CommandExecution;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.popup.OutputPopup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static org.ashot.microservice_starter.data.constant.TextFieldType.typeToShort;

public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu openRecent;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane container;
    @FXML
    private AnchorPane optionsPane;
    @FXML
    private GridPane options;
    @FXML
    private Text osInfo;
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
        osInfo.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
        container.getChildren().addListener((ListChangeListener<Node>) c -> {
            executeAllBtn.setDisable(container.getChildren().isEmpty());
        });
        sequentialOption.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sequentialName.setVisible(newValue);
        });
        openRecent.setOnShowing(event -> refreshRecentlyOpenedFolders());
        loadRecentFolders();
    }
    private void loadRecentFolders(){
        JSONObject dirs = Utils.setupFolders();
        lastSaved = (String) dirs.get(DirType.LAST_SAVED.name());
        lastLoaded = (String) dirs.get(DirType.LAST_LOADED.name());
        refreshRecentlyOpenedFolders();
    }

    public void newEntry(ActionEvent e) {
        Entry entry = new Entry();
        container.getChildren().add(entry.buildEmptyEntry(container));
    }

    private void newEntry(String name, String path, String cmd) {
        Entry entry = new Entry();
        container.getChildren().add(entry.buildEntry(container, name, path, cmd));
    }

    public void executeAll() {
        resetCurrentCmdText();
        String commands = CommandExecution.executeAll(container, sequentialOption.isSelected(), sequentialName.getText(), (int) delayPerCmd.getValue());
        setCurrentCmdText(commands, currentCmd);
    }

    public void save(ActionEvent e) {
        loadRecentFolders();
        File savedFile = chooseFile(true);
        saveToFile(savedFile);
        if(savedFile != null) {
            Utils.saveDirReference(DirType.LAST_SAVED, savedFile.getParent());
        }
    }

    public void load(ActionEvent e) {
        loadRecentFolders();
        File loadedFile = chooseFile(false);
        loadFromFile(loadedFile);
        if(loadedFile != null) {
            Utils.saveDirReference(DirType.LAST_LOADED, loadedFile.getParent());
            Utils.saveRecentDir(loadedFile.getAbsolutePath());
            refreshRecentlyOpenedFolders();
        }
    }
    private void refreshRecentlyOpenedFolders(){
        openRecent.getItems().clear();
        JSONArray recentFolders = Utils.getRecentFiles();
        for (Object s : recentFolders.toList()){
            String recentFolder = s.toString();
            MenuItem m = new MenuItem();
            m.setText(recentFolder);
            m.setOnAction((actionEvent)->{
                File file = new File(recentFolder);
                if(file.exists()){
                    loadFromFile(file);
                }
            });
            m.setDisable(!new File(recentFolder).exists());
            openRecent.getItems().add(m);
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
        resetCurrentCmdText();
        log.debug("Loaded: {}", jsonData.toString(1));
        return fileToLoad;
    }

    private File chooseFile(boolean save){
        File file = null;
        if(save){
            file = Utils.chooseFile(true, lastSaved);
        }
        else{
            file = Utils.chooseFile(false, lastLoaded);
        }
        return file;
    }

    private void setCurrentCmdText(String text, Button currentCmd){
        currentCmdText = text;
        currentCmd.setVisible(true);
    }
    private void resetCurrentCmdText(){
        currentCmdText = "";
        currentCmd.setVisible(false);
    }
    public void printCurrentCmd(ActionEvent e){
        OutputPopup.outputPopup(currentCmdText, "Commands Executed");
    }

}
