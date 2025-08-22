package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.SelectableTextFlow;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.popup.AlertPopup;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.ashot.shellflow.utils.NodeUtils.addPaddingVertical;

public class EntryInfoBar extends VBox {
    private final Text fileLoaded;

    public EntryInfoBar() {
        fileLoaded = new Text();
        fileLoaded.setFont(Fonts.fileLabelText());
        SelectableTextFlow textFlow = new SelectableTextFlow(fileLoaded);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setCursor(Cursor.HAND);
        textFlow.setOnMousePressed(e ->{
            if(!e.isPrimaryButtonDown()){
                return;
            }
            File file = new File(Controller.getCurrentFileAbsolutePath());
            if(file.exists()){
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    new AlertPopup("Could not open file", null,  "File could not be opened: " + file.getAbsolutePath(), false).show();
                }
            }
            else {
                new AlertPopup("Could not open file", null, "File does not exist: " + file.getAbsolutePath(), false).show();
            }
        });
        Separator separator = new Separator(Orientation.HORIZONTAL);
        addPaddingVertical(separator, 10);
        getChildren().addAll(textFlow, separator);
        hoverProperty().addListener((_, _, hovering) -> {
            fileLoaded.setText(hovering ? Controller.getCurrentFileAbsolutePath() : Controller.getCurrentFile());
        });
    }

    public void setFileLoadedText(String text) {
        fileLoaded.setText(text);
    }
}
