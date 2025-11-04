package org.ashot.shellflow.node.entry.misc;

import atlantafx.base.controls.SelectableTextFlow;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.Fonts;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;
    private static final String PREFIX = "Current file: ";

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText());
        fileLoaded.setCursor(Cursor.HAND);
        SelectableTextFlow textFlow = new SelectableTextFlow(fileLoaded);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        setPadding(new Insets(15, 15, 5, 15));
        getChildren().addAll(textFlow);
    }

    public Text getFileLoaded() {
        return fileLoaded;
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(PREFIX + text);
    }
}
