package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.ashot.shellflow.node.entry.misc.EntryInfoBar;
import org.ashot.shellflow.node.execution.tab.ExecutionsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntrySetupTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(EntrySetupTab.class);
    private static final double MAX_ENTRIES_TOOLBAR_SPLIT_POS = 0.85;
    private static final double INIT_ENTRIES_TOOLBAR_SPLIT_POS = 0.75;
    private static final double INIT_ENTRIES_EXECUTIONS_SPLIT_POS = 0.26;
    private static final double MIN_ENTRIES_EXECUTIONS_SPLIT_POS = 0.15;
    private static final double MIN_ENTRIES_TOOLBAR_SPLIT_POS = 0.80;
    private double entriesExecutionsSplitCurrentPos = 0;
    private final VBox entryListContainer;
    private final EntrySetupToolBar entrySetupToolBar;
    private final EntryInfoBar entryInfoBar;
    private final StackPane stackPane;
    private final SplitPane entryListExecutionsSplitPane;
    private final EntrySetupOptions entrySetupOptions;
    private final Pane panel;

    public EntrySetupTab(ExecutionsPanel executionsPanel) {
        panel = executionsPanel;
        entryInfoBar = new EntryInfoBar();
        entrySetupOptions = new EntrySetupOptions();
        entrySetupToolBar = new EntrySetupToolBar();
        entryListContainer = new VBox();
        entryListContainer.setAlignment(Pos.TOP_CENTER);
        entryListContainer.setSpacing(10);

        VBox entryListContainerContent = new VBox(10, entryInfoBar, entryListContainer);
        entryListContainerContent.setAlignment(Pos.TOP_CENTER);

        ScrollPane entryListScrollPane = new ScrollPane();
        entryListScrollPane.setFitToWidth(true);
        entryListScrollPane.setFitToHeight(true);
        entryListScrollPane.setContent(entryListContainerContent);

        VBox entryListWrapper = new VBox(entryListScrollPane, entrySetupOptions);
        entryListWrapper.setFillWidth(true);
        entryListWrapper.setAlignment(Pos.TOP_CENTER);
        entryListWrapper.setPadding(new Insets(0, 8, 0, 8));

        entryListExecutionsSplitPane = new SplitPane(entryListWrapper, executionsPanel);
        setupEntryListExecutionsSplitLimits();

        SplitPane splitPane = new SplitPane(entryListExecutionsSplitPane, entrySetupToolBar);
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPosition(0, INIT_ENTRIES_TOOLBAR_SPLIT_POS);
        splitPane.getDividers().getFirst().positionProperty().addListener((_, _, position) -> {
            if ((double) position > MIN_ENTRIES_TOOLBAR_SPLIT_POS) {
                splitPane.getDividers().getFirst().setPosition(MIN_ENTRIES_TOOLBAR_SPLIT_POS);
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

    public Text getFileLoadedText() {
        return entryInfoBar.getFileLoaded();
    }

    public VBox getEntryListContainer() {
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

    public Pane getPanel() {
        return panel;
    }

    public EntrySetupOptions getEntryExecutionOptions() {
        return entrySetupOptions;
    }
}
