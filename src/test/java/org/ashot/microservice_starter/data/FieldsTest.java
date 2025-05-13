package org.ashot.microservice_starter.data;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.node.entry.Fields;
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
        FieldType type = FieldType.COMMAND;
        String text = "test";
        TextArea field = Fields.createField(type, text);
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
        TextArea field = Fields.createField(FieldType.PATH, null);
        assertEquals("", field.getText(), "Field text was not set to empty for null text argument");
    }

    @Test
    void getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField() {
        logger.info("getTextFieldContentFromContainerReturnsNullForContainerWithoutTextField");
        Pane v = new Pane();
        Text f = new Text();
        v.getChildren().add(f);
        assertNull(Fields.getTextFieldContentFromContainer(v, FieldType.COMMAND), "Text field is not null");
    }

    @Test
    void getTextFieldContentFromContainerReturnsProperText() {
        logger.info("getTextFieldContentFromContainerReturnsProperText");
        Pane v = new Pane();
        TextArea f = Fields.createField(FieldType.NAME, "test");
        v.getChildren().add(f);
        assertEquals("test", Fields.getTextFieldContentFromContainer(v, FieldType.NAME), "Text content returned is not correct");
    }
}