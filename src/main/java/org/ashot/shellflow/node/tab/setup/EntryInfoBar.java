package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.SelectableTextFlow;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.Fonts;

import static org.ashot.shellflow.utils.NodeUtils.addPaddingVertical;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText());
        SelectableTextFlow textFlow = new SelectableTextFlow(fileLoaded);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        Separator separator = new Separator(Orientation.HORIZONTAL);
        addPaddingVertical(separator, 10);
        getChildren().addAll(textFlow, separator);
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
