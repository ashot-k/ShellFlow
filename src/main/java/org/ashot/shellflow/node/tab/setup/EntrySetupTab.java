package org.ashot.shellflow.node.tab.setup;

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
import org.ashot.shellflow.node.entry.variable.VariableEntry;
import org.ashot.shellflow.node.toolbar.EntrySetupToolBar;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.utils.Animations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.ashot.shellflow.mapper.EntryMapper.*;

public class EntrySetupTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(EntrySetupTab.class);
    private final FlowPane entryListContainer;
    private final SidePanel sidePanel;
    private final EntrySetupToolBar entrySetupToolBar;
    private final EntryInfoBar entryInfoBar;
    private final int entriesContainerGap = 15;

    private int dragSourceIndex = -1;

    public EntrySetupTab() {
        entryListContainer = new FlowPane();
        entryListContainer.setPadding(new Insets(10, 15, 0, 15));
        entryListContainer.setHgap(entriesContainerGap);
        entryListContainer.setVgap(entriesContainerGap);
        entryListContainer.setRowValignment(VPos.TOP);
        entryListContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane entryListScrollPane = new ScrollPane();
        entryListScrollPane.setFitToWidth(true);
        entryListScrollPane.setFitToHeight(true);
        entryListScrollPane.setContent(entryListContainer);
        VBox entryListWrapper = new VBox(entryListScrollPane);
        VBox.setVgrow(entryListWrapper, Priority.ALWAYS);
        entryListWrapper.getStyleClass().add("bordered-container-no-hover");

        sidePanel = new SidePanel();
        entryInfoBar = new EntryInfoBar();
        entrySetupToolBar = setupToolBar();

        VBox entriesSection = new VBox();
        entriesSection.setFillWidth(true);
        entriesSection.setPadding(new Insets(10));
        entriesSection.setAlignment(Pos.TOP_CENTER);
        entriesSection.setSpacing(5);
        entriesSection.getChildren().addAll(entryInfoBar, entryListWrapper, entrySetupToolBar);

        HBox.setHgrow(entriesSection, Priority.ALWAYS);
        HBox.setHgrow(sidePanel, Priority.ALWAYS);

        HBox contentWrapper = new HBox(entriesSection);
        StackPane stackPane = new StackPane(contentWrapper, sidePanel);
        StackPane.setAlignment(sidePanel, Pos.CENTER_LEFT);
        setContent(stackPane);
        setClosable(false);
        setText("Entry Setup");
        log.debug("EntrySetupTab initialized");
    }

    private EntrySetupToolBar setupToolBar() {
        EntrySetupToolBar entrySetupToolBar = new EntrySetupToolBar();
        entrySetupToolBar.getExpandAllButton().setOnAction(_ -> getEntryBoxes().forEach(e -> e.setExpanded(true)));
        entrySetupToolBar.getCollapseAllButton().setOnAction(_ -> getEntryBoxes().forEach(e -> e.setExpanded(false)));
        entrySetupToolBar.getClearAllEntriesButton().setOnAction(_ -> clearEntryBoxes());
        entrySetupToolBar.getExecuteAllButton().setOnAction(_ -> executeAll());
        entrySetupToolBar.getAddEntryButton().setOnAction(_ -> addEntryBox());

        addEntryListChangeListener(_ -> entrySetupToolBar.getExecuteAllButton().setDisable(getEntryBoxes().isEmpty()));
        addEntryListChangeListener(_ -> entrySetupToolBar.getClearAllEntriesButton().setDisable(getEntryBoxes().isEmpty()));
        return entrySetupToolBar;
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
            atlantafx.base.util.Animations.shakeY(entryBox.getExecuteButton(), 1.5).play();
            new CommandExecutor().execute(entryToCommand(entryBoxToEntry(entryBox), false));
        });
        setupDragging(entryBox);
        Animations.fadeInBeforeAdditionToList(entryBox);
        entryListContainer.getChildren().add(entryBox);
    }

    public void refreshEdited() {
        log.debug("Reset edited state for all entries");
        entryListContainer.getChildren().stream()
                .filter(e -> e instanceof EntryBox)
                .map(e -> (EntryBox) e).forEach(EntryBox::refreshEdited);
    }

    private void setupDragging(EntryBox entryBox) {
        entryBox.setOnDragDetected(e -> {
            dragSourceIndex = entryListContainer.getChildren().indexOf(entryBox);
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
        entryBox.setOnDragOver(e -> {
            if (dragSourceIndex != -1 && entryBox != entryListContainer.getChildren().get(dragSourceIndex)) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        entryBox.setOnDragDropped(e -> {
            int dropIndex = entryListContainer.getChildren().indexOf(entryBox);
            if (dragSourceIndex != -1) {
                var node = entryListContainer.getChildren().remove(dragSourceIndex);
                entryListContainer.getChildren().add(dropIndex, node);
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
        Animations.removeFromListAndFadeOut(entryBox, entryListContainer);
    }

    public void clearEntryBoxes() {
        entryListContainer.getChildren().clear();
    }

    public List<EntryBox> getEntryBoxes() {
        List<EntryBox> entryBoxes = new ArrayList<>();
        for (Node node : entryListContainer.getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entryBoxes.add(entryBox);
            }
        }
        return entryBoxes;
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        for (Node node : entryListContainer.getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entries.add(entryBoxToEntry(entryBox));
            }
        }
        return entries;
    }

    public void executeAll() {
        new Thread(() -> {
            log.debug("Executing all entries, sequence: {}", getSequentialOption().isSelected());
            if (getSequentialOption().isSelected()) {
                new SequenceExecutor().executeSequence(getEntries(), getExecutionName().getText());
            } else {
                new CommandExecutor().executeAll(getEntries(), getExecutionName().getText(), getDelayPerCmd());
            }
        }
        ).start();
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        Controller controller = ControllerRegistry.getMainController();
        TerminalRegistry.stopAllTerminals();
        controller.getExecutionsTab().getExecutionsTabPane().getTabs().clear();
    }

    public void addEntryListChangeListener(ListChangeListener<Node> changeListener) {
        entryListContainer.getChildren().addListener(changeListener);
    }

    public CheckBox getSequentialOption() {
        return entrySetupToolBar.getSequenceOption();
    }

    public TextField getExecutionName() {
        return entrySetupToolBar.getExecutionNameField();
    }

    public Spinner<Integer> getDelayPerCmdSlider() {
        return entrySetupToolBar.getDelayPerCmd();
    }

    public int getDelayPerCmd() {
        return getDelayPerCmdSlider().getValue();
    }

    public EntryInfoBar getEntryInfoBar() {
        return entryInfoBar;
    }

    public void setFileLoadedText(String text) {
        getEntryInfoBar().setFileLoadedText(text);
    }

    public List<VariableEntry> getVariableEntries() {
        return sidePanel.getVariableEntries();
    }
}
