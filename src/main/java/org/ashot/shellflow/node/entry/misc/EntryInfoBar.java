package org.ashot.shellflow.node.entry.misc;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        fileLoaded.setTextAlignment(TextAlignment.CENTER);
        setPadding(new Insets(1));
        setAlignment(Pos.CENTER);
        getChildren().addAll(fileLoaded);
    }

    public Text getFileLoaded() {
        return fileLoaded;
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
