package org.ashot.shellflow.node.menu.settings.menu;

import atlantafx.base.controls.ModalPane;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.modal.FontSelectionDialog;
import org.ashot.shellflow.registry.ControllerRegistry;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;

public class FontSelectionMenuItem extends MenuItem {

    private FontSelectionDialog fontSelectionDialog;
    private final ModalPane modal;

    public FontSelectionMenuItem(){
        modal = ControllerRegistry.getMainController().getMainModal();
        setOnAction(_-> {
            if(fontSelectionDialog == null) {
                fontSelectionDialog = new FontSelectionDialog(() -> modal.hide(true));
            }
            showFontModal();
        });
        setText("Font...");
        setGraphic(Icons.getFontSelectionMenuIcon(MENU_ITEM_ICON_SIZE));
    }

    public void showFontModal(){
        modal.show(fontSelectionDialog);
    }

}
