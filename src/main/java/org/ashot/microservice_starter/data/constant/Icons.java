package org.ashot.microservice_starter.data.constant;

import org.ashot.microservice_starter.Utils;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.InputStream;

public class Icons {
    private static final String CHEVRON_UP_ICON_NAME = "bi-chevron-up";
    private static final String CHEVRON_DOWN_ICON_NAME = "bi-chevron-down";

    private static FontIcon getIcon(String name){
       return new FontIcon(BootstrapIcons.findByDescription(name));
    }

    public static FontIcon getChevronUpIcon(int size){
        FontIcon fontIcon = getIcon(CHEVRON_UP_ICON_NAME);
        fontIcon.setIconSize(size);
        return fontIcon;
    }
    public static FontIcon getChevronDownIcon(int size){
        FontIcon fontIcon = getIcon(CHEVRON_DOWN_ICON_NAME);
        fontIcon.setIconSize(size);
        return fontIcon;
    }
}
