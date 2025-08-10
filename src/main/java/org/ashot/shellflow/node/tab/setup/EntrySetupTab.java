package org.ashot.shellflow.node.tab.setup;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.execution.SequenceExecutor;
import org.ashot.shellflow.mapper.EntryToCommandMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntrySetupTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(EntrySetupTab.class);
    private final VBox entriesContainer;
    private final SidePanel sidePanel;

    private int dragSourceIndex = -1;


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
        Region spacer2 = new Region();
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
        setupDragging(entryBox);
    }

    private void setupDragging(EntryBox entryBox){
        entryBox.setOnDragDetected(e ->{
            dragSourceIndex = entriesContainer.getChildren().indexOf(entryBox);
            Dragboard dragboard = entryBox.startDragAndDrop(TransferMode.MOVE);
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            HashMap<DataFormat, Object> dataFormatStringHashMap = new HashMap<>();
            //placeholder just to allow dragover detection
            dataFormatStringHashMap.put(DataFormat.PLAIN_TEXT, "");
            dragboard.setContent(dataFormatStringHashMap);
            dragboard.setDragView(entryBox.snapshot(snapshotParameters, new WritableImage((int) entryBox.getWidth(), (int) entryBox.getHeight())));
            e.consume();
        });
        entryBox.setOnDragOver(e ->{
            if(dragSourceIndex != -1 && entryBox != entriesContainer.getChildren().get(dragSourceIndex)){
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        entryBox.setOnDragDropped(e -> {
            int dropIndex = entriesContainer.getChildren().indexOf(entryBox);
            if (dragSourceIndex != -1) {
                var node = entriesContainer.getChildren().remove(dragSourceIndex);
                entriesContainer.getChildren().add(dropIndex, node);
            }
            dragSourceIndex = -1;
            e.setDropCompleted(true);
            e.consume();
        });
        entryBox.setOnDragDone(e -> {
            dragSourceIndex = -1;
            e.consume();
        });

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
        if(getSequentialOption().isSelected()){
            SequenceExecutor.execute(getEntries(), getSequentialNameField().getText(), getDelayPerCmd());
        }
        else {
            CommandExecutor.executeAll(getEntries(), getDelayPerCmd());
        }
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

    public int getDelayPerCmd(){
        return (int) sidePanel.getDelayPerCmdSlider().getValue();
    }
}
