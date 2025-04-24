package org.ashot.microservice_starter.node;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class TabOutputOptions {
    private CheckBox wrapTextOption;
    private OutputTab currentOutputTab;

    public TabOutputOptions(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
        setupOptions();
    }

    private void setupOptions() {
        this.wrapTextOption = new CheckBox("Wrap Text");
        ChangeListener<Number> e = (observable, oldValue, newValue) -> {
            currentOutputTab.getCodeArea().setPrefWidth(currentOutputTab.getTabPane().getWidth());
        };
        this.wrapTextOption.setOnAction((_) -> {
            if (this.wrapTextOption.isSelected()) {
                Platform.runLater(() -> {
                    currentOutputTab.getCodeArea().setWrapText(true);
                    currentOutputTab.getCodeArea().widthProperty().addListener(e);
                    currentOutputTab.getScrollPane().setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                });
            } else {
                Platform.runLater(() -> {
                    currentOutputTab.getCodeArea().setWrapText(false);
                    currentOutputTab.getCodeArea().widthProperty().removeListener(e);
                    currentOutputTab.getScrollPane().setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                });
            }
        });

    }

    public List<Node> getOptions() {
        return List.of(wrapTextOption);
    }

    public void selectTabOutput(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
    }
}
