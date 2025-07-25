package org.ashot.shellflow.data;

import javafx.scene.control.TextArea;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.node.entry.Fields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

}