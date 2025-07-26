package org.ashot.shellflow.node.tab.setup;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.mapper.EntryToCommandMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.registry.ProcessRegistry;

import java.util.ArrayList;
import java.util.List;

public class EntrySetupTab extends Tab {
    private final VBox entryContainer;
    private final SidePanel sidePanel;

    public EntrySetupTab() {
        entryContainer = new VBox();
        entryContainer.setAlignment(Pos.TOP_CENTER);
        entryContainer.setPadding(new Insets(2, 15, 0, 15));
        entryContainer.getStyleClass().addAll("entry-container");
        entryContainer.setSpacing(15);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(entryContainer);

        sidePanel = new SidePanel(_ -> executeAll(), _ -> stopAll(), _ -> addEntryBox(), _ -> clearEntryBoxes());

        BorderPane pane = new BorderPane();
        pane.setLeft(sidePanel);
        sidePanel.setPrefHeight(400);
        sidePanel.setMaxHeight(400);
        pane.setCenter(scrollPane);

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(sidePanel, Priority.SOMETIMES);
        HBox.setHgrow(pane, Priority.ALWAYS);

        selectedProperty().addListener((_, _, isSelected) -> {
            sidePanel.getAddEntryButton().setDisable(!isSelected);
            sidePanel.getClearAllEntriesButton().setDisable(!isSelected || getEntryBoxes().isEmpty());
        });

        addEntryListChangeListener(_ -> sidePanel.getExecuteAllButton().setDisable(getEntryBoxes().isEmpty()));
        addEntryListChangeListener(_ -> sidePanel.getClearAllEntriesButton().setDisable(getEntryBoxes().isEmpty()));

        HBox hBox = new HBox();
        hBox.setFillHeight(false);
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().add(pane);

        setContent(hBox);
        setClosable(false);
        setText("Entry Setup");
    }

    public void addEntryBox() {
        addEntryBox(new Entry());
    }

    public void addEntryBox(Entry entry) {
        EntryBox entryBox = new EntryBox(entry);
        entryBox.setOnDeleteButtonAction(_ -> removeEntryBox(entryBox));
        entryBox.setOnExecuteButtonAction(_ -> CommandExecutor.execute(EntryToCommandMapper.entryToCommand(entryBox, false)));
        entryContainer.getChildren().add(entryBox);
    }

    public void removeEntryBox(EntryBox entryBox) {
        entryContainer.getChildren().remove(entryBox);
    }

    public void clearEntryBoxes() {
        entryContainer.getChildren().clear();
    }

    public List<EntryBox> getEntryBoxes() {
        List<EntryBox> entryBoxes = new ArrayList<>();
        for (Node node : entryContainer.getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entryBoxes.add(entryBox);
            }
        }
        return entryBoxes;
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        for (Node node : entryContainer.getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entries.add(new Entry(entryBox.getNameProperty(), entryBox.getPathProperty(), entryBox.getCommandProperty(), entryBox.isWslProperty()));
            }
        }
        return entries;
    }

    public void executeAll() {
        CommandExecutor.executeAll(
                getEntries(),
                getSequentialOption().isSelected(),
                getSequentialNameField().getText(),
                (int) getDelayPerCmdSlider().getValue()
        );
    }

    public void stopAll() {
        //todo implement
        ProcessRegistry.killAllProcesses();
    }

    public void addEntryListChangeListener(ListChangeListener<Node> changeListener) {
        entryContainer.getChildren().addListener(changeListener);
    }

    public CheckBox getSequentialOption() {
        return sidePanel.getSequentialOption();
    }

    public TextField getSequentialNameField() {
        return sidePanel.getSequentialNameField();
    }

    public Slider getDelayPerCmdSlider() {
        return sidePanel.getDelayPerCmdSlider();
    }
    public Button getCloseAllButton() {
        return sidePanel.getCloseAllButton();
    }
}
