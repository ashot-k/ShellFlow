package org.ashot.shellflow.node.tab.setup;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.ashot.shellflow.data.constant.Fonts;

public class BottomPanel extends VBox {
    private final Text fileLoaded;

    public BottomPanel() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText);
        TextFlow textFlow = new TextFlow(fileLoaded);
        textFlow.setPadding(new Insets(0, 10, 10 ,10));
        getChildren().add(new Separator(Orientation.HORIZONTAL));
        getChildren().add(textFlow);
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
