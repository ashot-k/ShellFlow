package org.ashot.shellflow.node.tab.setup;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.ashot.shellflow.node.toolbar.EntrySetupToolBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntrySetupTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(EntrySetupTab.class);
    private final FlowPane entryListContainer;
    private final EntrySetupToolBar entrySetupToolBar;
    private final EntryInfoBar entryInfoBar;
    private final StackPane stackPane;

    public EntrySetupTab() {
        entryInfoBar = new EntryInfoBar();
        entrySetupToolBar = new EntrySetupToolBar();
        entryListContainer = new FlowPane();
        entryListContainer.setPadding(new Insets(10, 5, 0, 5));
        entryListContainer.setHgap(5);
        entryListContainer.setVgap(8);
        entryListContainer.setRowValignment(VPos.TOP);
        entryListContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane entryListScrollPane = new ScrollPane();
        entryListScrollPane.setFitToWidth(true);
        entryListScrollPane.setFitToHeight(true);
        entryListScrollPane.setContent(entryListContainer);
        VBox entryListWrapper = new VBox(entryListScrollPane);
        VBox.setVgrow(entryListWrapper, Priority.ALWAYS);
        entryListWrapper.getStyleClass().add("bordered-container-no-hover");

        VBox entriesSection = new VBox();
        entriesSection.setFillWidth(true);
        entriesSection.setPadding(new Insets(10));
        entriesSection.setAlignment(Pos.TOP_CENTER);
        entriesSection.setSpacing(5);
        entriesSection.getChildren().addAll(entryInfoBar, entryListWrapper, entrySetupToolBar);

        HBox.setHgrow(entriesSection, Priority.ALWAYS);

        HBox contentWrapper = new HBox(entriesSection);
        stackPane = new StackPane(contentWrapper);
        setContent(stackPane);
        setClosable(false);
        setText("Entry Setup");
        log.debug("EntrySetupTab initialized");
    }

    public CheckBox getSequenceOptionCheckBox() {
        return entrySetupToolBar.getSequenceOption();
    }

    public TextField getExecutionNameField() {
        return entrySetupToolBar.getExecutionNameField();
    }

    public Spinner<Integer> getDelayPerCmdSpinner() {
        return entrySetupToolBar.getDelayPerCmd();
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
}
