package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.util.Animations;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.execution.SequenceExecutor;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.utils.Animator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.ashot.shellflow.mapper.EntryMapper.*;

public class EntrySetupTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(EntrySetupTab.class);
    private final FlowPane entriesContainer;
    private final SidePanel sidePanel;
    private final EntryInfoBar entryInfoBar;
    private final int entriesContainerGap = 15;

    private int dragSourceIndex = -1;

    public EntrySetupTab() {
        entriesContainer = new FlowPane();
        entriesContainer.setPadding(new Insets(10, 15, 0, 15));
        entriesContainer.setHgap(entriesContainerGap);
        entriesContainer.setVgap(entriesContainerGap);
        entriesContainer.setRowValignment(VPos.TOP);
        entriesContainer.setAlignment(Pos.TOP_CENTER);

        HBox.setHgrow(entriesContainer, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        entryInfoBar = new EntryInfoBar();
        VBox entriesContainerWrapper = new VBox(entryInfoBar, entriesContainer);
        entriesContainerWrapper.getStyleClass().add("bordered-container-no-hover");
        entriesContainerWrapper.setAlignment(Pos.TOP_LEFT);
        scrollPane.setContent(entriesContainerWrapper);

        sidePanel = new SidePanel(_ -> executeAll(), _ -> stopAll(), _ -> addEntryBox(), _ -> clearEntryBoxes());
        sidePanel.setMaxWidth(300);

        Region spacer = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox pane = new HBox(5, sidePanel, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(sidePanel, Priority.ALWAYS);
        HBox.setHgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(sidePanel, Priority.ALWAYS);
        pane.setAlignment(Pos.CENTER);

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
        log.debug("EntrySetupTab initialized");
    }

    public void addEntryBox() {
        log.debug("Adding empty entry box");
        addEntryBox(new Entry());
    }

    public void addEntryBox(Entry entry) {
        log.debug("Adding entry box with name: {}, path: {}, command: {}", entry.getName(), entry.getPath(), entry.getCommand());
        EntryBox entryBox = entryToEntryBox(entry);
        entryBox.setOnDeleteButtonAction(_ -> removeEntryBox(entryBox));
        entryBox.setOnExecuteButtonAction(_ -> {
            Animations.shakeY(entryBox.getExecuteButton(), 1.5).play();
            new CommandExecutor().execute(entryToCommand(entryBoxToEntry(entryBox), false));
        });
        setupDragging(entryBox);
        Animator.fadeInBeforeAdditionToList(entryBox);
        entriesContainer.getChildren().add(entryBox);
    }

    public void resetEdited(){
        log.debug("Reset edited state for all entries");
        entriesContainer.getChildren().stream()
                .filter(e -> e instanceof EntryBox)
                .map(e -> (EntryBox) e).forEach(EntryBox::refreshEdited);
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
        Animator.removeFromListAndFadeOut(entryBox, entriesContainer);
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
                entries.add(entryBoxToEntry(entryBox));
            }
        }
        return entries;
    }

    public void executeAll() {
        log.debug("Executing all entries, sequence: {}", getSequentialOption().isSelected());
        if(getSequentialOption().isSelected()){
            new SequenceExecutor().executeSequence(getEntries(), getExecutionName().getText());
        }
        else {
            new CommandExecutor().executeAll(getEntries(), getExecutionName().getText(), getDelayPerCmd());
        }
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        Controller controller = ControllerRegistry.getMainController();
        TerminalRegistry.stopAllTerminals();
        controller.getExecutionsTab().getExecutionsTabPane().getTabs().clear();
    }

    public void addEntryListChangeListener(ListChangeListener<Node> changeListener) {
        entriesContainer.getChildren().addListener(changeListener);
    }

    public ToggleButton getSequentialOption() {
        return sidePanel.getSequentialOption();
    }

    public TextField getExecutionName() {
        return sidePanel.getExecutionNameField();
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

    public EntryInfoBar getEntryInfoBar() {
        return entryInfoBar;
    }

    public void setFileLoadedText(String text){
        getEntryInfoBar().setFileLoadedText(text);
    }
}
