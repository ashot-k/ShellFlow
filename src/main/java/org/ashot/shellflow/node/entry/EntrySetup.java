package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.ashot.shellflow.node.entry.misc.EntryInfoBar;
import org.ashot.shellflow.node.execution.ExecutionsPanel;


public class EntrySetup extends StackPane {
    private final VBox entryListContainer;
    private final EntrySetupOptions entrySetupOptions;
    private final Pane panel;

    public EntrySetup(ExecutionsPanel executionsPanel) {
        panel = executionsPanel;
        entrySetupOptions = new EntrySetupOptions();
        entryListContainer = new VBox();
        entryListContainer.setAlignment(Pos.TOP_CENTER);
        entryListContainer.setSpacing(10);

        VBox entryListContainerContent = new VBox(10, entryListContainer);
        entryListContainerContent.setPadding(new Insets(10));
        entryListContainerContent.setAlignment(Pos.TOP_CENTER);

        ScrollPane entryListScrollPane = new ScrollPane();
        entryListScrollPane.setFitToWidth(true);
        entryListScrollPane.setFitToHeight(true);
        entryListScrollPane.setContent(entryListContainerContent);

        VBox entryListWrapper = new VBox(entryListScrollPane, entrySetupOptions);
        entryListWrapper.setFillWidth(true);
        entryListWrapper.setAlignment(Pos.TOP_CENTER);
        entryListWrapper.setPadding(new Insets(8));
        VBox.setVgrow(entryListScrollPane, Priority.ALWAYS);
        getChildren().add(new HBox(entryListWrapper));
        HBox.setHgrow(entryListWrapper, Priority.ALWAYS);
    }

    public Text getFileLoadedText() {
        return entrySetupOptions.getInfoBar().getFileLoaded();
    }

    public VBox getEntryListContainer() {
        return entryListContainer;
    }

    public EntryInfoBar getEntryInfoBar() {
        return entrySetupOptions.getInfoBar();
    }

    public Pane getPanel() {
        return panel;
    }

    public EntrySetupOptions getEntryExecutionOptions() {
        return entrySetupOptions;
    }
}
