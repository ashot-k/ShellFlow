package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.SelectableTextFlow;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.Fonts;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText());
        fileLoaded.setCursor(Cursor.HAND);
        SelectableTextFlow textFlow = new SelectableTextFlow(fileLoaded);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        getChildren().addAll(textFlow);
        getStyleClass().add("bordered-container");
    }


    public Text getFileLoaded() {
        return fileLoaded;
    }
}
