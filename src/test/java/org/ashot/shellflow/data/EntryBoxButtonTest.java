package org.ashot.shellflow.data;

import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.node.entry.EntryButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class EntryBoxButtonTest {

    @Test
    void deleteEntryButton() {
        javafx.scene.control.Button b = EntryButton.deleteEntryButton();
        Assertions.assertAll(
                () -> assertEquals(ButtonType.DELETE.getValue(), b.getId()),
                () -> assertDoesNotThrow(b::fire)
        );
    }

    @Test
    void executeBtn() {
    }
}