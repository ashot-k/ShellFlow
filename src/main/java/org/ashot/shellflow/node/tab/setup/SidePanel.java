package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.Spacer;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SidePanel extends VBox {
    private static final Logger log = LoggerFactory.getLogger(SidePanel.class);
    private final ListView<Text> list = new ListView<>();
    private final int MAX_ENTRIES = 5;

    public SidePanel() {
        Spacer spacer = new Spacer(Orientation.VERTICAL);
        getChildren().addAll(spacer);
        setSpacing(25);
        setMaxWidth(250);
        getStyleClass().addAll("bordered-container");
    }


}

