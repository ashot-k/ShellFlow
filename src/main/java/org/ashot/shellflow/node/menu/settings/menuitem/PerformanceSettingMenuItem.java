package org.ashot.shellflow.node.menu.settings.menuitem;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckMenuItem;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PerformanceSettingMenuItem extends CheckMenuItem {
    private static final Logger log = LoggerFactory.getLogger(PerformanceSettingMenuItem.class);

    public PerformanceSettingMenuItem() {
        setText("Optimized Animations");
        setGraphic(Icons.getPerformanceOptionIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> ShellFlow.getConfig().saveProperty(ConfigProperty.OPTIMIZED_MODE, String.valueOf(isSelected())));
        setSelected(ShellFlow.getConfig().optimizedMode());
    }

    public BooleanProperty performanceModePropertyProperty() {
        return selectedProperty();
    }
}
