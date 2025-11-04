package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.entry.misc.EntryInfoBar;


public class EntrySetupTab extends Tab {
    private static final double MAX_ENTRIES_TOOLBAR_SPLIT_POS = 0.85;
    private static final double INIT_ENTRIES_TOOLBAR_SPLIT_POS = 0.75;
    private static final double INIT_ENTRIES_EXECUTIONS_SPLIT_POS = 0.260;
    private static final double MIN_ENTRIES_EXECUTIONS_SPLIT_POS = 0.15;
    private double entriesExecutionsSplitCurrentPos = 0;
    private final FlowPane entryListContainer;
    private final EntrySetupToolBar entrySetupToolBar;
    private final EntryInfoBar entryInfoBar;
    private final StackPane stackPane;
    private final SplitPane entryListExecutionsSplitPane;
    private final EntrySetupOptions entrySetupOptions;
    private Node executionsPlaceHolder;

    public EntrySetupTab() {
        executionsPlaceHolder = createExecutionsPlaceholder();
        entryInfoBar = new EntryInfoBar();
        entrySetupOptions = new EntrySetupOptions();
        entrySetupToolBar = new EntrySetupToolBar();
        entryListContainer = new FlowPane();
        entryListContainer.setHgap(5);
        entryListContainer.setVgap(10);
        entryListContainer.setRowValignment(VPos.TOP);
        entryListContainer.setAlignment(Pos.TOP_LEFT);
        entryListContainer.setMaxWidth(EntryBox.MAX_WIDTH);

        VBox entryListContainerContent = new VBox(10, entryInfoBar, entryListContainer);
        entryListContainerContent.setAlignment(Pos.TOP_CENTER);

        ScrollPane entryListScrollPane = new ScrollPane();
        entryListScrollPane.setFitToWidth(true);
        entryListScrollPane.setFitToHeight(true);
        entryListScrollPane.setContent(entryListContainerContent);

        VBox entryListWrapper = new VBox(entryListScrollPane, entrySetupOptions);

        entryListExecutionsSplitPane = new SplitPane(entryListWrapper, executionsPlaceHolder);
        setupEntryListExecutionsSplitLimits();

        SplitPane splitPane = new SplitPane(entryListExecutionsSplitPane, entrySetupToolBar);
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPosition(0, INIT_ENTRIES_TOOLBAR_SPLIT_POS);
        splitPane.getDividers().getFirst().positionProperty().addListener((_, _, position) -> {
            if ((double) position > MAX_ENTRIES_TOOLBAR_SPLIT_POS) {
                splitPane.getDividers().getFirst().setPosition(MAX_ENTRIES_TOOLBAR_SPLIT_POS);
            }
        });

        VBox.setVgrow(entryListScrollPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        HBox.setHgrow(splitPane, Priority.ALWAYS);

        HBox contentWrapper = new HBox(splitPane);
        stackPane = new StackPane(contentWrapper);
        setContent(stackPane);
        setClosable(false);
        setText("Entry Setup");
    }

    private void setupEntryListExecutionsSplitLimits() {
        entryListExecutionsSplitPane.setDividerPosition(0, entriesExecutionsSplitCurrentPos != 0 ? entriesExecutionsSplitCurrentPos : INIT_ENTRIES_EXECUTIONS_SPLIT_POS);
        entryListExecutionsSplitPane.getDividers().getFirst().positionProperty().addListener((_, _, position) -> {
            double pos = (double) position;
            if (pos > MAX_ENTRIES_TOOLBAR_SPLIT_POS) {
                entryListExecutionsSplitPane.getDividers().getFirst().setPosition(MAX_ENTRIES_TOOLBAR_SPLIT_POS);
            } else if (pos < MIN_ENTRIES_EXECUTIONS_SPLIT_POS) {
                entryListExecutionsSplitPane.getDividers().getFirst().setPosition(MIN_ENTRIES_EXECUTIONS_SPLIT_POS);
            }
            entriesExecutionsSplitCurrentPos = entryListExecutionsSplitPane.getDividers().getFirst().getPosition();
        });
    }

    public void setPlaceHolder() {
        setExecutionsSplit(createExecutionsPlaceholder());
    }

    private Node createExecutionsPlaceholder() {
        Label label = new Label("No executions");
        label.setPadding(new Insets(10));
        label.setFont(Fonts.title());
        return new HBox(label);
    }

    public Text getFileLoadedText() {
        return entryInfoBar.getFileLoaded();
    }

    public FlowPane getEntryListContainer() {
        return entryListContainer;
    }

    public EntrySetupToolBar getEntrySetupToolBar() {
        return entrySetupToolBar;
    }

    public void setSidePanel(Pane panel, Pos position) {
        stackPane.getChildren().addLast(panel);
        StackPane.setAlignment(panel, position);
        HBox.setHgrow(panel, Priority.ALWAYS);
    }

    public EntryInfoBar getEntryInfoBar() {
        return entryInfoBar;
    }

    public SplitPane getEntryListExecutionsSplitPane() {
        return entryListExecutionsSplitPane;
    }

    public void setExecutionsSplit(Node node) {
        entryListExecutionsSplitPane.getItems().remove(executionsPlaceHolder);

        executionsPlaceHolder = node;
        entryListExecutionsSplitPane.getItems().add(executionsPlaceHolder);
        setupEntryListExecutionsSplitLimits();
    }

    public EntrySetupOptions getEntryExecutionOptions() {
        return entrySetupOptions;
    }
}
