package org.ashot.shellflow.data;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.constant.Direction;
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
    void orderingButtonUpIsCreatedProperly() {
        VBox v = new VBox();
        HBox row = new HBox();
        HBox orderingButtonsContainer = new HBox();
        row.getChildren().add(orderingButtonsContainer);

        javafx.scene.control.Button b = EntryButton.orderingButton(Direction.UP);
        orderingButtonsContainer.getChildren().add(b);
        v.getChildren().add(row);
        Assertions.assertAll(
                () -> assertDoesNotThrow(b::fire)
        );
    }

    @Test
    void orderingButtonDownMovesEntryDownProperly() {
        VBox v = new VBox();
        HBox row = new HBox();
        HBox orderingButtonsContainer = new HBox();
        javafx.scene.control.Button b = EntryButton.orderingButton(Direction.DOWN);
        orderingButtonsContainer.getChildren().add(b);
        orderingButtonsContainer.setId("ordering-container-0");
        HBox row2 = new HBox();
        HBox orderingButtonsContainer2 = new HBox();
        orderingButtonsContainer2.setId("ordering-container-1");

        row.getChildren().add(orderingButtonsContainer);
        row2.getChildren().add(orderingButtonsContainer2);

        v.getChildren().addAll(row, row2);
        //todo change assertion
//        checkOrderingResult(orderingButtonsContainer, orderingButtonsContainer2, b);
    }

    @Test
    void orderingButtonUpMovesEntryUpProperly() {
        VBox v = new VBox();
        HBox row = new HBox();
        HBox orderingButtonsContainer = new HBox();
        orderingButtonsContainer.setId("ordering-container-0");
        HBox row2 = new HBox();
        HBox orderingButtonsContainer2 = new HBox();
        javafx.scene.control.Button b2 = EntryButton.orderingButton(Direction.UP);
        orderingButtonsContainer2.getChildren().add(b2);
        orderingButtonsContainer2.setId("ordering-container-1");

        row.getChildren().add(orderingButtonsContainer);
        row2.getChildren().add(orderingButtonsContainer2);

        v.getChildren().addAll(row, row2);
        //todo change assertion
//        checkOrderingResult(orderingButtonsContainer, orderingButtonsContainer2, b2);
    }


    @Test
    void executeBtn() {
    }
}