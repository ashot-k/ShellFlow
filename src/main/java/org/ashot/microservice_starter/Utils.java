package org.ashot.microservice_starter;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.ashot.microservice_starter.data.TextFieldType;
import org.ashot.microservice_starter.popup.ErrorPopup;
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

    public static void writeDataToFile(File fileToSave, JSONObject data) {
        try {
            FileWriter f = new FileWriter(fileToSave);
            data.write(f, 1, 1);
            f.close();
        } catch (IOException ex) {
            ErrorPopup.errorPopup(ex.getMessage());
        }
    }

    public static void writeDataToFile(File fileToSave, JSONArray data) {
        try {
            FileWriter f = new FileWriter(fileToSave);
            data.write(f, 1, 1);
            f.close();
        } catch (IOException ex) {
            ErrorPopup.errorPopup(ex.getMessage());
        }
    }

    public static JSONObject addEntryToJSONObject(JSONObject object, Node node, int idx) {
        if (node instanceof TextField field) {
            String id = node.getId();
            String nameType = TextFieldType.typeToShort(TextFieldType.NAME);
            String cmdType = TextFieldType.typeToShort(TextFieldType.COMMAND);
            String pathType = TextFieldType.typeToShort(TextFieldType.PATH);
            if (id.contains(nameType + "-" + idx)) {
                object.put(nameType, field.getText());
            } else if (id.contains(cmdType + "-" + idx)) {
                object.put(cmdType, field.getText());
            } else if (id.contains(pathType + "-" + idx)) {
                object.put(pathType, field.getText());
            }
        }
        return object;
    }

    public static JSONArray createJSONArray(VBox container) {
        JSONArray jsonArray = new JSONArray();
        for (int idx = 0; idx < container.getChildren().size(); idx++) {
            Node current = container.getChildren().get(idx);
            if (!(current instanceof HBox currentRow)) continue;
            JSONObject object = new JSONObject();
            for (Node n : currentRow.getChildren()) {
                object = addEntryToJSONObject(object, n, idx);
            }
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public static JSONArray createJSONArray(File file) {
        try {
            String jsonContent = Files.readString(file.toPath());
            return new JSONArray(jsonContent);
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

    public static String getTerminalArgument() {
        return System.getenv("TERM").equalsIgnoreCase("gnome-terminal") ? "--" : "-e";
    }
}
