package org.ashot.microservice_starter.validation;

import javafx.application.Platform;
import org.ashot.microservice_starter.data.message.PopupMessages;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

import java.io.File;

public class EntryValidation {

    public static void validateField(String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) {
            Platform.runLater(() -> ErrorPopup.errorPopup(PopupMessages.INVALID_FIELDS));
            throw new IllegalArgumentException(fieldValue);
        }
    }

    public static void validatePath(String path){
        File f = new File(path.isBlank() ? "/" : path);
        if(!f.exists() || !f.isDirectory()){
            Platform.runLater(() -> ErrorPopup.errorPopup(PopupMessages.invalidPathPopupText(path.isBlank() ? "/" : path)));
            throw new IllegalArgumentException(path);
        }
    }
}
