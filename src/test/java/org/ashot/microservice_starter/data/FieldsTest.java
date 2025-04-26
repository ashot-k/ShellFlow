package org.ashot.microservice_starter.data;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.Fields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
class FieldsTest {
    private static final Logger logger = LoggerFactory.getLogger(FieldsTest.class);

    @Test
    void createFieldReturnsProperField() {
        logger.info("createFieldReturnsProperField");
        TextFieldType type = TextFieldType.COMMAND;
        String text = "test";
        TextField field = Fields.createField(type, text);
        assertEquals(type.getValue(), field.getId(), "Field ID is not correct");
        assertEquals(text, field.getText(), "Field text is not correct");
    }

    @Test
    void createFieldReturnsProperFieldThrowsExceptionForNullType() {
        logger.info("createFieldReturnsProperFieldThrowsExceptionForNullType");
        assertThrows(NullPointerException.class, () -> Fields.createField(null, null));
    }

    @Test
    void createFieldSetsEmptyTextForNullText() {
        logger.info("createFieldSetsEmptyTextForNullText");
        TextField field = Fields.createField(TextFieldType.PATH, null);
        assertEquals("", field.getText(), "Field text was not set to empty for null text argument");
    }

    @Test
    void getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField() {
        logger.info("getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField");
        Pane v = new Pane();
        Text f = new Text();
        v.getChildren().add(f);
        assertNull(Fields.getTextFieldContentFromContainer(v, TextFieldType.COMMAND), "Text field is not null");
    }

    @Test
    void getTextFieldContentFromContainerReturnsProperText() {
        logger.info("getTextFieldContentFromContainerReturnsProperText");
        Pane v = new Pane();
        TextField f = Fields.createField(TextFieldType.NAME, "test");
        v.getChildren().add(f);
        assertEquals("test", Fields.getTextFieldContentFromContainer(v, TextFieldType.NAME), "Text content returned is not correct");
    }
}