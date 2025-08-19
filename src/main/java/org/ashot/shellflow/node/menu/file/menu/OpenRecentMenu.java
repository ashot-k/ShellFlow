package org.ashot.shellflow.node.menu.file.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.node.icon.Icons;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;
import static org.ashot.shellflow.utils.RecentFileUtils.getRecentFiles;

public class OpenRecentMenu extends Menu {

    private static final int MAX_ENTRIES = 15;
    private static final Logger log = LoggerFactory.getLogger(OpenRecentMenu.class);
    private final Consumer<File> open;

    public OpenRecentMenu(Consumer<File> open, Menu parentMenu) {
        this.open = open;
        setText("Open Recent");
        setGraphic(Icons.getOpenRecentIcon(MENU_ITEM_ICON_SIZE));
        parentMenu.setOnShowing(_ -> refreshRecentFiles());
    }

    public void refreshRecentFiles() {
        getItems().clear();
        JSONArray recentFiles = getRecentFiles();
        for (Object s : recentFiles.toList().stream().limit(MAX_ENTRIES).toList()) {
            String recentFile = s.toString();
            MenuItem m = createRecentMenuItemOption(recentFile);
            if(m != null) {
                getItems().add(m);
            }
        }
    }

    private MenuItem createRecentMenuItemOption(String recentFile){
        MenuItem m = new MenuItem(recentFile);
        m.setOnAction(_ -> {
            File file = new File(recentFile);
            if (file.exists()) {
                open.accept(file);
            }
        });
        if(!new File(recentFile).exists()){
            return null;
        }
        return m;
    }

}
