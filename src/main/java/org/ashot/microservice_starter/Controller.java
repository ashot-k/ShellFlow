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
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.popup.OutputPopup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static org.ashot.microservice_starter.data.constant.TextFieldType.typeToShort;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuBar menuBar;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        osInfo.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
        container.getChildren().addListener((ListChangeListener<Node>) c -> {
            executeAllBtn.setDisable(container.getChildren().isEmpty());
        });
        sequentialOption.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sequentialName.setVisible(newValue);
        });
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
        saveToFile();
    }

    public void load(ActionEvent e) {
        loadFromFile();
    }

    private void saveToFile() {
        File fileToSave = Utils.chooseFile(true);
        if (fileToSave == null) return;
        JSONObject jsonObject = Utils.createSaveJSONObject(container, (int) delayPerCmd.getValue(), sequentialOption.isSelected(), sequentialName.getText());
        Utils.writeDataToFile(fileToSave, jsonObject);
    }

    private void loadFromFile() {
        File fileToLoad = Utils.chooseFile(false);
        if (fileToLoad == null) return;
        JSONObject jsonData = Utils.createJSONArray(fileToLoad);
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
