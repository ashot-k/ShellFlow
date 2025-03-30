package org.ashot.microservice_starter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private VBox container;
    @FXML
    private Text osInfo;
    @FXML
    private Slider delayPerCmd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        osInfo.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }

    public void newEntry(ActionEvent e) {
        Entry entry = new Entry(container);
        container.getChildren().add(entry.buildEmptyEntry(container.getChildren() != null ? container.getChildren().size() : 0));
    }
    private void newEntry(String cmd, String path, String name){
        Entry entry = new Entry(container);
        container.getChildren().add(entry.buildEntry(cmd, path, name, container.getChildren() != null ? container.getChildren().size() : 0));
    }

    public void executeAll() {
        ObservableList<Node> children = container.getChildren();
        for (int j = 0; j < children.size(); j++) {
            Node i = children.get(j);
            if (i instanceof HBox) {
                Optional<Node> optionalBtn = ((HBox) i).getChildren()
                        .stream()
                        .filter(node -> node.getId() != null && node.getId().contains("execute"))
                        .findFirst();
                if (optionalBtn.isPresent()) {
                    Button b = (Button) optionalBtn.get();
                    long timeInMS = (long) (j * delayPerCmd.getValue());
                    //TODO fix delay
                    Thread t = new Thread(b::fire);
                    try {
                        Thread.sleep(timeInMS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    t.start();
                }
            }
        }
    }

    public void save(ActionEvent e) {
        saveToFile();
    }

    private void saveToFile() {
        File fileToSave = Utils.chooseFile(true);
        if(fileToSave == null) return;
        JSONArray jsonData = Utils.createJSONArray(container);
        Utils.writeDataToFile(fileToSave, jsonData);
    }

    public void load(ActionEvent e) throws IOException {
        loadFromFile();
    }
    private void loadFromFile() throws IOException{
        File fileToLoad = Utils.chooseFile(false);
        if(fileToLoad == null) return;
        JSONArray jsonData = Utils.createJSONArray(fileToLoad);
        container.getChildren().clear();
        for (Object j: jsonData){
            if(j instanceof JSONObject jsonObject){
                String name = jsonObject.get("name").toString();
                String path = jsonObject.get("path").toString();
                String cmd = jsonObject.get("cmd").toString();
                newEntry(cmd, path, name);
            }
        }
    }

}
