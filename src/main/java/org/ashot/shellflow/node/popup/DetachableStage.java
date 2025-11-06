package org.ashot.shellflow.node.popup;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ashot.shellflow.ShellFlow;


@SuppressWarnings("deprecation")
public class DetachableStage extends Stage {
    private final BorderPane detachedSceneRoot;
    private final SimpleBooleanProperty detached = new SimpleBooleanProperty(false);

    public DetachableStage(Node node, String name, HeaderBar header) {
        this(node, name);
        detachedSceneRoot.setTop(header);
    }

    public DetachableStage(Node node, String name) {
        detached.bind(showingProperty());
        detachedSceneRoot = new BorderPane();
        detachedSceneRoot.setCenter(new VBox(node));
        detachedSceneRoot.setTop(new HeaderBar(null, null, null));
        Scene scene = new Scene(detachedSceneRoot, 800, 450, Color.BLACK);
        scene.getStylesheets().add(ShellFlow.getStyleSheet());
        setScene(scene);
        setTitle(name);
        initStyle(StageStyle.EXTENDED);
        getIcons().add(ShellFlow.getApplicationIcon());
        VBox.setVgrow(node, Priority.ALWAYS);
    }

    public boolean isDetached() {
        return detached.get();
    }

    public SimpleBooleanProperty detachedProperty() {
        return detached;
    }
}
