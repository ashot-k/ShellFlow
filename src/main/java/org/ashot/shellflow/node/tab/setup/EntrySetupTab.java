package org.ashot.shellflow.node.tab.setup;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.mapper.EntryToCommandMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.registry.ProcessRegistry;

import java.util.ArrayList;
import java.util.List;

public class EntrySetupTab extends Tab {
    private final VBox entriesContainer;
    private final SidePanel sidePanel;

    public EntrySetupTab() {
        entriesContainer = new VBox();
        entriesContainer.setAlignment(Pos.TOP_CENTER);
        entriesContainer.setPadding(new Insets(2, 15, 0, 15));
        entriesContainer.getStyleClass().addAll("entry-container");
        entriesContainer.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(entriesContainer);

        sidePanel = new SidePanel(_ -> executeAll(), _ -> stopAll(), _ -> addEntryBox(), _ -> clearEntryBoxes());
        sidePanel.setMaxWidth(300);

        Region spacer = new Region();
//        spacer.setMaxWidth(100);
        Region spacer2 = new Region();
//        spacer2.setMaxWidth(100);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox pane = new HBox(sidePanel, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(sidePanel, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(sidePanel, Priority.ALWAYS);
        pane.setAlignment(Pos.CENTER);

        selectedProperty().addListener((_, _, isSelected) -> {
            sidePanel.getAddEntryButton().setDisable(!isSelected);
            sidePanel.getClearAllEntriesButton().setDisable(!isSelected || getEntryBoxes().isEmpty());
        });

        addEntryListChangeListener(_ -> sidePanel.getExecuteAllButton().setDisable(getEntryBoxes().isEmpty()));
        addEntryListChangeListener(_ -> sidePanel.getClearAllEntriesButton().setDisable(getEntryBoxes().isEmpty()));

        HBox content = new HBox();
        content.setFillHeight(true);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().add(pane);

        setContent(content);
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
        entriesContainer.getChildren().add(entryBox);
    }

    public void removeEntryBox(EntryBox entryBox) {
        entriesContainer.getChildren().remove(entryBox);
    }

    public void clearEntryBoxes() {
        entriesContainer.getChildren().clear();
    }

    public List<EntryBox> getEntryBoxes() {
        List<EntryBox> entryBoxes = new ArrayList<>();
        for (Node node : entriesContainer.getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entryBoxes.add(entryBox);
            }
        }
        return entryBoxes;
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        for (Node node : entriesContainer.getChildren()) {
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
        entriesContainer.getChildren().addListener(changeListener);
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
