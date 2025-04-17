package org.ashot.microservice_starter;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class Utils {
    public static URL getIcon(String iconName) {
        return Main.class.getResource("icons/" + iconName);
    }

    public static InputStream getIconAsInputStream(String iconName) {
        return Main.class.getResourceAsStream("icons/" + iconName);
    }
    public static JSONObject createSaveJSONObject(Pane container, int delayPerCmd, boolean seqOption, String seqName){
        JSONArray entries= Utils.createJSONArray(container);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entries", entries);
        jsonObject.put("delay", delayPerCmd);
        jsonObject.put("sequential", seqOption);
        jsonObject.put("sequentialName", seqName);
        return jsonObject;
    }

    public static void writeDataToFile(File fileToSave, JSONObject data) {
        try {
            FileWriter f = new FileWriter(fileToSave);
            data.write(f, 1, 1);
            f.close();
        } catch (IOException ex) {
            ErrorPopup.errorPopup(ex.getMessage());
        }
    }

    private static JSONObject addEntryToJSONObject(JSONObject object, Node node) {
        if (node instanceof TextField field) {
            String id = node.getId();
            String nameType = TextFieldType.typeToShort(TextFieldType.NAME);
            String cmdType = TextFieldType.typeToShort(TextFieldType.COMMAND);
            String pathType = TextFieldType.typeToShort(TextFieldType.PATH);
            if (id.contains(nameType)) {
                object.put(nameType, field.getText());
            } else if (id.contains(cmdType)) {
                object.put(cmdType, field.getText());
            } else if (id.contains(pathType)) {
                object.put(pathType, field.getText());
            }
        }
        return object;
    }

    public static JSONArray createJSONArray(Pane container) {
        JSONArray jsonArray = new JSONArray();
        for (int idx = 0; idx < container.getChildren().size(); idx++) {
            Node current = container.getChildren().get(idx);
            if (!(current instanceof HBox currentRow)) continue;
            JSONObject object = new JSONObject();
            for (Node n : currentRow.getChildren()) {
                addEntryToJSONObject(object, n);
            }
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public static JSONObject createJSONArray(File file) {
        try {
            String jsonContent = Files.readString(file.toPath());
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
        return null;
    }

    public static File chooseFile(boolean saveMode) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("JSON File", (".json"));
        fileChooser.setSelectedExtensionFilter(extensions);
        if (saveMode) {
            fileChooser.setInitialFileName("entries.json");
            fileChooser.setTitle("Choose file destination");
            return fileChooser.showSaveDialog(null);
        } else {
            fileChooser.setTitle("Choose file");
            return fileChooser.showOpenDialog(null);
        }
    }

    public static int calculateDelay(int multiplier, int delayPerCmd) {
        return multiplier * delayPerCmd * 1000;
    }

    public static String getTerminalArgument() {
        return System.getenv("TERM").equalsIgnoreCase("gnome-terminal") ? "--" : "-e";
    }

    public static String getSystemOS(){
        return System.getProperty("os.name").toLowerCase();
    }
}
