package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.SelectableTextFlow;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.Fonts;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText());
        SelectableTextFlow textFlow = new SelectableTextFlow(fileLoaded);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(10, 0, 10, 0));
        getChildren().addAll(textFlow, separator);
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
