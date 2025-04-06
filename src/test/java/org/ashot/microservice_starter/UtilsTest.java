package org.ashot.microservice_starter;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.Fields;
import org.ashot.microservice_starter.data.TextFieldType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class UtilsTest {

    @Test
    void getIcon() {
        URL iconURL = Utils.getIcon("close-icon.png");
        Assertions.assertAll(
                () -> assertNotNull(iconURL),
                () -> assertTrue(new File(iconURL.toURI()).exists())
        );
    }

    @Test
    void getIconAsInputStream() {
        InputStream iconStream = Utils.getIconAsInputStream("close-icon.png");
        Assertions.assertAll(
                () -> assertNotNull(iconStream),
                () -> assertDoesNotThrow(()-> assertNotNull(iconStream.readAllBytes()))
        );
    }

    @Test
    void writeDataToFile(@TempDir File tempDir) {
        File fileToSave = new File(tempDir, "test.json");
        JSONObject object = new JSONObject();
        object.put("test", "testValue");
        Assertions.assertAll(
                () -> assertDoesNotThrow(() -> Utils.writeDataToFile(fileToSave, object)),
                () -> assertTrue(fileToSave.exists()),
                () -> assertEquals(readDataFromFile(fileToSave).get("test"), object.get("test"))
        );
    }

    private JSONObject readDataFromFile(File file) throws IOException {
        return new JSONObject(Files.readString(file.toPath()));
    }

    @Test
    void createJSONArrayConstructsCorrectObjectFromFile() {
        JSONObject object = Utils.createJSONArray(new File("src/test/resources/test_1.json"));
        JSONArray entries = (JSONArray) object.get("entries");
        Assertions.assertAll(
                () -> assertNotNull(entries),
                () -> assertEquals(2, entries.length()),
                () -> assertEquals("cmd_1", ((JSONObject)entries.get(0)).get("name")),
                () -> assertEquals(12, object.get("delay")),
                () -> assertEquals(true, object.get("sequential")),
                () -> assertEquals("whatever", object.get("sequentialName"))
        );
    }

    @Test
    void testCreateJSONArray() {
        VBox container = new VBox();
        TextField field = Fields.createField(TextFieldType.NAME, "test_name", 0);
        TextField field2 = Fields.createField(TextFieldType.PATH, "test_path", 0);
        TextField field3 = Fields.createField(TextFieldType.COMMAND, "test_command", 0);
        HBox hBox = new HBox(field, field2, field3);
        container.getChildren().add(hBox);
        JSONArray object = Utils.createJSONArray(container);
        Assertions.assertAll(
                () -> assertEquals(1, object.length()),
                () -> assertEquals(3, object.getJSONObject(0).length())
        );
    }

    @Test
    void getTerminalArgument() {
        Assertions.assertDoesNotThrow(Utils::getTerminalArgument);
    }
}