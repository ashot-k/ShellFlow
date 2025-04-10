package org.ashot.microservice_starter.data;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class ButtonsTest {

    @Test
    void deleteEntryButton() {
        VBox v = new VBox();
        HBox h = new HBox();
        v.getChildren().add(h);
        Button b = Buttons.deleteEntryButton(v, h, 0);
        Assertions.assertAll(
                () -> assertEquals(ButtonType.typeToShort(ButtonType.DELETE) + "-0", b.getId()),
                () -> assertDoesNotThrow(b::fire),
                () -> assertEquals(0, v.getChildren().size())
        );
    }

    @Test
    void orderingButtonUpIsCreatedProperly() {
        VBox v = new VBox();
        HBox row = new HBox();
        HBox orderingButtonsContainer = new HBox();
        row.getChildren().add(orderingButtonsContainer);

        Button b = Buttons.orderingButton(true);
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
        Button b = Buttons.orderingButton(false);
        orderingButtonsContainer.getChildren().add(b);
        orderingButtonsContainer.setId("ordering-container-0");
        HBox row2 = new HBox();
        HBox orderingButtonsContainer2 = new HBox();
        orderingButtonsContainer2.setId("ordering-container-1");

        row.getChildren().add(orderingButtonsContainer);
        row2.getChildren().add(orderingButtonsContainer2);

        v.getChildren().addAll(row, row2);
        checkOrderingResult(orderingButtonsContainer, orderingButtonsContainer2, b);
    }

    @Test
    void orderingButtonUpMovesEntryUpProperly() {
        VBox v = new VBox();
        HBox row = new HBox();
        HBox orderingButtonsContainer = new HBox();
        orderingButtonsContainer.setId("ordering-container-0");
        HBox row2 = new HBox();
        HBox orderingButtonsContainer2 = new HBox();
        Button b2 = Buttons.orderingButton(true);
        orderingButtonsContainer2.getChildren().add(b2);
        orderingButtonsContainer2.setId("ordering-container-1");

        row.getChildren().add(orderingButtonsContainer);
        row2.getChildren().add(orderingButtonsContainer2);

        v.getChildren().addAll(row, row2);
        checkOrderingResult(orderingButtonsContainer, orderingButtonsContainer2, b2);
    }

    private void checkOrderingResult(Node orderingButtonsContainer, Node orderingButtonsContainer2, Button orderingBtn){
        Assertions.assertAll(
                () -> assertEquals("ordering-container-0", orderingButtonsContainer.getId()),
                () -> assertEquals("ordering-container-1", orderingButtonsContainer2.getId()),
                () -> assertDoesNotThrow(orderingBtn::fire),
                //ids get swapped
                () -> assertEquals("ordering-container-1", orderingButtonsContainer.getId()),
                () -> assertEquals("ordering-container-0", orderingButtonsContainer2.getId())
        );
    }

    @Test
    void executeBtn() {
    }
}