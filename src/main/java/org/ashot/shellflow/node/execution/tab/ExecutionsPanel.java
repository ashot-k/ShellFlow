package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.theme.Styles;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.popup.DetachableStage;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
public class ExecutionsPanel extends HBox {
    private static final Logger log = LoggerFactory.getLogger(ExecutionsPanel.class);
    private static final String NO_EXECUTIONS_TEXT = "No executions";
    private static final String DETACHED_TEXT = "Detached";
    private final TabPane tabPane;
    private final VBox contentWrapper;
    private final HBox toolBar;
    private final HBox content;
    private final VBox detachedSceneRoot;
    private final DetachableStage stage;

    private final Button detachExecutionsButton;
    private final Button addNewTabButton;

    public ExecutionsPanel() {
        detachExecutionsButton = new Button("", Icons.getDetachExecutionsIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), false));
        detachExecutionsButton.setTooltip(new Tooltip(ToolTipMessages.DETACH_EXECUTIONS_BUTTON));
        detachExecutionsButton.getStyleClass().addAll(Styles.FLAT);

        addNewTabButton = new Button("", Icons.getAddButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        addNewTabButton.setTooltip(new Tooltip(ToolTipMessages.EXPAND_ALL_ENTRIES_BUTTON));
        addNewTabButton.getStyleClass().addAll(Styles.FLAT);

        toolBar = new HBox(detachExecutionsButton, addNewTabButton);
        content = new HBox(createExecutionsPlaceholder(NO_EXECUTIONS_TEXT));
        contentWrapper = new VBox(toolBar, content);

        tabPane = new TabPane();
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        HBox.setHgrow(tabPane, Priority.ALWAYS);
        HBox.setHgrow(contentWrapper, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);

        detachedSceneRoot = new VBox(tabPane);
        String detachTitle = "Executions";
        stage = new DetachableStage(detachedSceneRoot, detachTitle, createDetachHeaderNode(detachTitle));

        tabPane.getTabs().addListener((ListChangeListener<Tab>) _ -> {
            content.getChildren().setAll(tabPane.getTabs().isEmpty() ? createExecutionsPlaceholder(NO_EXECUTIONS_TEXT) : tabPane);
            if (!tabPane.getTabs().isEmpty() && stage.isDetached()) {
                stage.requestFocus();
            }
        });

        setupDetachableWindow();
        setupContextMenu();
        getChildren().setAll(contentWrapper);
    }

    private static HeaderBar createDetachHeaderNode(String detachTitle) {
        Label label = new Label(detachTitle);
        label.setFont(Fonts.title());
        label.setPadding(new Insets(8, 10, 0, 12.5));
        HeaderBar.setDragType(label, HeaderDragType.DRAGGABLE_SUBTREE);
        return new HeaderBar(label, null, null);
    }

    private void setupDetachableWindow() {
        stage.detachedProperty().addListener((_, _, newDetachedValue) -> detachExecutionsButton.setGraphic(Icons.getDetachExecutionsIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), newDetachedValue)));
        stage.setOnCloseRequest(_ -> contentWrapper.getChildren().setAll(toolBar, content));
        detachExecutionsButton.setOnAction(_ -> {
            if (stage.isDetached()) {
                detachedSceneRoot.getChildren().clear();
                contentWrapper.getChildren().setAll(toolBar, content);
                stage.hide();
            } else {
                contentWrapper.getChildren().setAll(toolBar, createExecutionsPlaceholder(DETACHED_TEXT));
                detachedSceneRoot.getChildren().setAll(content);
                stage.show();
            }
        });
    }

    private Node createExecutionsPlaceholder(String title) {
        Label label = new Label(title);
        label.setPadding(new Insets(10));
        label.setFont(Fonts.title());
        HBox hBox = new HBox(label);
        hBox.setAlignment(Pos.CENTER);
        hBox.setFillHeight(true);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        return hBox;
    }

    private void setupContextMenu() {
        MenuItem stopAllMenuItem = new MenuItem("Stop all", Icons.getCloseButtonIcon(IconSizeDefaults.CLOSE_ICON_SIZE.getSize()));
        stopAllMenuItem.setOnAction(_ -> stopAll());
        MenuItem closeAllMenuItem = new MenuItem("Close all", Icons.getClearIcon(IconSizeDefaults.CLOSE_ICON_SIZE.getSize()));
        closeAllMenuItem.setOnAction(_ -> closeAll());

        TextField textField = new TextField();
        CustomMenuItem editTabName = new CustomMenuItem(textField);
        editTabName.setHideOnClick(false);
        closeAllMenuItem.setOnAction(_ -> closeAll());

        tabPane.setContextMenu(new ContextMenu(editTabName, stopAllMenuItem, closeAllMenuItem));
        tabPane.setOnContextMenuRequested(menuRequestEvent -> {
            if (tabPane.getTabs().stream().anyMatch(e -> e.getContent().isHover())) {
                tabPane.getContextMenu().hide();
                menuRequestEvent.consume();
            }
        });
        tabPane.getContextMenu().showingProperty().addListener((_, _, showing) -> {
            if (!showing) {
                tabPane.getTabs().forEach(e -> e.textProperty().unbind());
            }
        });
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        TerminalRegistry.stopAllTerminals();
    }

    public void closeAll() {
        log.debug("Stopping all processes / terminals and clearing tabs");
        TerminalRegistry.stopAllTerminals();
        this.tabPane.getTabs().clear();
    }

    public ObservableList<Tab> getTabs() {
        return tabPane.getTabs();
    }

    public SelectionModel<Tab> getSelectionModel() {
        return tabPane.getSelectionModel();
    }

    public Button getAddNewTabButton() {
        return addNewTabButton;
    }
}
