package org.ashot.shellflow.node.menu.settings.menu;

import atlantafx.base.controls.ModalPane;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.modal.FontSelectionDialog;


public class FontSelectionMenuItem extends MenuItem {

    private FontSelectionDialog fontSelectionDialog;
    private final ModalPane modal;

    public FontSelectionMenuItem(ModalPane modalPane) {
        modal = modalPane;
        setOnAction(_ -> {
            if (fontSelectionDialog == null) {
                fontSelectionDialog = new FontSelectionDialog(() -> modal.hide(true));
            }
            showFontModal();
        });
        setText("Font...");
        setGraphic(Icons.getFontSelectionMenuIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
    }

    public void showFontModal() {
        modal.show(fontSelectionDialog);
    }

}
