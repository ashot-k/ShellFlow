package org.ashot.microservice_starter;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.*;
import org.ashot.microservice_starter.popup.ErrorPopup;
import org.ashot.microservice_starter.popup.OutputPopup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.ashot.microservice_starter.data.TextFieldType.typeToShort;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane container;
    @FXML
    private AnchorPane optionsPane;
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
        int idx = container.getChildren() != null ? container.getChildren().size() : 0;
        HBox newEntryContainer = entry.buildEmptyEntry(container, idx);
        container.addRow(container.getRowCount(), newEntryContainer);
    }

    private void newEntry(String cmd, String path, String name) {
        Entry entry = new Entry();
        int idx = container.getChildren() != null ? container.getChildren().size() : 0;
        HBox newEntryContainer = entry.buildEntry(container, cmd, path, name, idx);
        container.addRow(container.getRowCount(), newEntryContainer);
    }

    public void executeAll() {
        resetCurrentCmdText();
        ObservableList<Node> entryChildren = container.getChildren();
        StringBuilder seqCommands = new StringBuilder();
        for (int idx = 0; idx < entryChildren.size(); idx++) {
            Node node = entryChildren.get(idx);
            if (!(node instanceof HBox)) {
                continue;
            }
            String name = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.NAME, idx);
            String command = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.COMMAND, idx);
            String path = Fields.getTextFieldContentFromContainer((Pane) node, TextFieldType.PATH, idx);
            if (!sequentialOption.isSelected()) {
                currentCmdText = setCurrentCmdText(command);
                currentCmd.setVisible(true);
                long timeInMS = calculateDelay(idx);
                CommandExecutionThread t = new CommandExecutionThread(command, path, name, timeInMS);
                new Thread(t).start();
            } else {
                handleSequentialCommandChain(seqCommands, command, path, idx, entryChildren.size());
            }
        }
        if (!sequentialOption.isSelected()) return;
        try {
            currentCmd.setVisible(true);
            currentCmdText = setCurrentCmdText(seqCommands.toString());
            CommandExecution.execute(seqCommands.toString(), null, sequentialName.getText(), sequentialOption.isSelected());
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
    }

    private long calculateDelay(int multiplier) {
        return (long) (multiplier * delayPerCmd.getValue()) * 1000;
    }

    private void handleSequentialCommandChain(StringBuilder seqCommands, String command, String path, int idx, int total) {
        if (path != null && !path.isEmpty()) {
            seqCommands.append("cd ").append(path).append(" && ");
        }
        if(idx != 0 && delayPerCmd.getValue() > 0){
            seqCommands
                    .append("sleep ").append(delayPerCmd.getValue()).append("s").append(" && ");
        }
        seqCommands.append(command);
        if (idx == total - 1) {
            seqCommands.append(";");
        } else {
            seqCommands.append(" && ");
        }
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
        JSONArray jsonData = Utils.createJSONArray(container);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entries", jsonData);
        jsonObject.put("delay", delayPerCmd.getValue());
        jsonObject.put("sequential", sequentialOption.isSelected());
        jsonObject.put("sequentialName", sequentialName.getText());
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
                newEntry(cmd, path, name);
            }
        }
        delayPerCmd.setValue(jsonData.getDouble("delay"));
        sequentialOption.setSelected(jsonData.getBoolean("sequential"));
        sequentialName.setText(jsonData.getString("sequentialName"));
        sequentialName.setVisible(sequentialOption.isSelected());
        executeAllBtn.setDisable(container.getChildren().isEmpty());
        resetCurrentCmdText();
    }
    private String setCurrentCmdText(String text){
        String newText = currentCmdText + "\n" + text;
        currentCmd.setVisible(true);
        return newText;
    }
    private void resetCurrentCmdText(){
        currentCmdText = "";
        currentCmd.setVisible(false);
    }
    public void printCurrentCmd(ActionEvent e){
        OutputPopup.outputPopup(currentCmdText, "Commands Executed");
    }

}
