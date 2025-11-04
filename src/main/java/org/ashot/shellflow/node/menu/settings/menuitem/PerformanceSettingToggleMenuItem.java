package org.ashot.shellflow.node.menu.settings.menuitem;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckMenuItem;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;


public class PerformanceSettingToggleMenuItem extends CheckMenuItem {

    public PerformanceSettingToggleMenuItem() {
        setText("Optimized Animations");
        setGraphic(Icons.getPerformanceOptionIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> ShellFlow.getConfig().saveProperty(ConfigProperty.OPTIMIZED_MODE, String.valueOf(isSelected())));
        setSelected(ShellFlow.getConfig().optimizedMode());
    }

    public BooleanProperty performanceModePropertyProperty() {
        return selectedProperty();
    }
}
