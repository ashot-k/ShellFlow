package org.ashot.shellflow.node.entry;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class EntrySetupToolBar extends HBox {
    private final TabPane tabPane;

    public EntrySetupToolBar() {
        super();
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().addAll(Styles.DENSE);

        HBox.setHgrow(tabPane, Priority.ALWAYS);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        getChildren().addAll(tabPane);
        setPadding(new Insets(0, 10, 10, 10));
        setFillHeight(true);
    }

    public TabPane getTabPane() {
        return tabPane;
    }
}
