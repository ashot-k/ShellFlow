package org.ashot.microservice_starter.data;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
class FieldsTest {
    private static Logger logger = Logger.getLogger(FieldsTest.class.getName());

    @Test
    void createFieldReturnsProperField() {
        logger.info("createFieldReturnsProperField");
        TextFieldType type = TextFieldType.COMMAND;
        String text = "test";
        int idx = 1;
        TextField field = Fields.createField(type, text, idx);
        assertEquals(TextFieldTypeId.getIdPrefix(type) + idx, field.getId(), "Field ID is not correct");
        assertEquals(text, field.getText(), "Field text is not correct");
    }

    @Test
    void createFieldReturnsProperFieldThrowsExceptionForNullType() {
        logger.info("createFieldReturnsProperFieldThrowsExceptionForNullType");
        assertThrows(NullPointerException.class, () -> Fields.createField(null, null, 1));
    }
    @Test
    void createFieldSetsEmptyTextForNullText() {
        logger.info("createFieldSetsEmptyTextForNullText");
        TextField field = Fields.createField(TextFieldType.PATH, null, 1);
        assertEquals("", field.getText(), "Field text was not set to empty for null text argument");
    }
    @Test
    void getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField() {
        logger.info("getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField");
        Pane v = new Pane();
        Text f = new Text();
        v.getChildren().add(f);
        assertEquals(null, Fields.getTextFieldContentFromContainer(v, TextFieldType.COMMAND, 0), "Text field is not null");
    }
    @Test
    void getTextFieldContentFromContainerReturnsProperText() {
        logger.info("getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField");
        Pane v = new Pane();
        TextField f = Fields.createField(TextFieldType.NAME, "test", 0);
        v.getChildren().add(f);
        assertEquals("test", Fields.getTextFieldContentFromContainer(v, TextFieldType.NAME, 0), "Text content returned is not correct");
    }
}