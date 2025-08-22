package org.ashot.shellflow.node.utility;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import java.util.List;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;

public class EntrySetupToolBar extends FloatingToolBar{
    private Button expandAllButton;
    private Button collapseAllButton;
    private Button clearAllEntriesButton;
    private Button executeAllButton;
    private Button toggleToolBar;
    private CheckBox sequenceOption;
    private boolean show = true;
    private final double EXPANDED_WIDTH = 420;
    private final double EXPANDED_HEIGHT = 90;
    private final double COLLAPSED_WIDTH = 65;
    private final double COLLAPSED_HEIGHT  = 60;

    public EntrySetupToolBar(){
        super();
        expandAllButton = new Button("", Icons.getExpandAllEntriesIcon(DEFAULT_BUTTON_ICON_SIZE));
        expandAllButton.setTooltip(new Tooltip(ToolTipMessages.expandAllEntries()));

        collapseAllButton = new Button("", Icons.getCollapseAllEntriesIcon(DEFAULT_BUTTON_ICON_SIZE));
        collapseAllButton.setTooltip(new Tooltip(ToolTipMessages.collapseAllEntries()));

        clearAllEntriesButton = new Button("Clear All", Icons.getClearIcon(DEFAULT_BUTTON_ICON_SIZE));
        clearAllEntriesButton.setContentDisplay(ContentDisplay.RIGHT);
        clearAllEntriesButton.setFont(Fonts.buttonText());

        executeAllButton = new Button("Execute All", Icons.getExecuteAllButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        executeAllButton.setContentDisplay(ContentDisplay.RIGHT);
        executeAllButton.setFont(Fonts.buttonText());

        toggleToolBar = new Button("", Icons.getToggleToolbarIcon(DEFAULT_BUTTON_ICON_SIZE, show));
        toggleToolBar.setContentDisplay(ContentDisplay.RIGHT);
        toggleToolBar.setBackground(Background.EMPTY);

        sequenceOption = new CheckBox();
        Text text = new Text(" Sequence");
        text.setFont(Fonts.detailText());
        sequenceOption.setGraphic(text);
        sequenceOption.setFont(Fonts.detailText());

        VBox executeOptions = new VBox(5, sequenceOption, executeAllButton);
        List<Node> nodes = List.of(executeOptions, collapseAllButton, expandAllButton, clearAllEntriesButton, toggleToolBar);
        setContent(nodes);

        toggleToolBar.setOnAction(_->{
            show = !show;
            if(show){
                setExpandedSize();
                setContent(nodes);
                setPadding(new Insets(5));
            }else{
                setCollapsedSize();
                setContent(toggleToolBar);
                setPadding(Insets.EMPTY);
            }
            toggleToolBar.setGraphic(Icons.getToggleToolbarIcon(DEFAULT_BUTTON_ICON_SIZE, show));
        });
        setExpandedSize();
        autoHiding(0.25);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5));
    }

    private void setExpandedSize(){
        setMaxWidth(EXPANDED_WIDTH);
        setMaxHeight(EXPANDED_HEIGHT);
    }

    private void setCollapsedSize(){
        setMaxWidth(COLLAPSED_WIDTH);
        setMaxHeight(COLLAPSED_HEIGHT);
    }

    public Button getExpandAllButton() {
        return expandAllButton;
    }

    public Button getCollapseAllButton() {
        return collapseAllButton;
    }

    public Button getClearAllEntriesButton() {
        return clearAllEntriesButton;
    }

    public Button getExecuteAllButton() {
        return executeAllButton;
    }

    public CheckBox getSequenceOption() {
        return sequenceOption;
    }
}
