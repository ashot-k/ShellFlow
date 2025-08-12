package org.ashot.shellflow.node.tab.setup;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.ashot.shellflow.data.constant.Fonts;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText);
        TextFlow textFlow = new TextFlow(fileLoaded);
        textFlow.setPadding(new Insets(0, 10, 0, 10));
        textFlow.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(textFlow);
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
