package org.ashot.microservice_starter.data;

import org.ashot.microservice_starter.Utils;

import java.io.InputStream;

public class Icons {
    private static final String ARROW_UP_ICON_FILE_NAME = "arrow-up.png";
    private static final String ARROW_DOWN_ICON_FILE_NAME = "arrow-down.png";

    public static InputStream getArrowUpIcon(){
        return Utils.getIconAsInputStream(ARROW_UP_ICON_FILE_NAME);
    }
    public static InputStream getArrowDownIcon(){
        return Utils.getIconAsInputStream(ARROW_DOWN_ICON_FILE_NAME);
    }
}
