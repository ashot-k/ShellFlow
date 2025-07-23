package org.ashot.shellflow.node.menu.file;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.TabIndices;
import org.ashot.shellflow.data.icon.Icons;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;
import static org.ashot.shellflow.node.Recents.*;

public class OpenRecentMenu extends Menu {

    private static final int MAX_ENTRIES = 10;
    private static final Logger log = LoggerFactory.getLogger(OpenRecentMenu.class);
    private final Consumer<File> open;

    public OpenRecentMenu(Consumer<File> open, TabPane tabPane, Menu parentMenu) {
        this.open = open;
        setText("Open Recent");
        setGraphic(Icons.getOpenRecentIcon(MENU_ITEM_ICON_SIZE));
        parentMenu.setOnShowing(_ -> refreshRecentlyOpenedFiles(tabPane));
    }

    public void refreshRecentlyOpenedFiles(TabPane tabs) {
        List<String> toRemove = getInvalidRecentFolders(this);
        this.getItems().clear();
        JSONArray recentFolders = getRecents().getJSONArray(DirType.RECENT.name());
        if (recentFolders == null) {
            log.debug("No recent folders found");
            return;
        }
        log.debug("Recent folders found: {}", recentFolders.length());
        for (Object s : recentFolders.toList().stream().limit(MAX_ENTRIES).toList()) {
            String recentFolder = s.toString();
            if (toRemove.contains(recentFolder)) {
                removeRecentFile(recentFolder);
            }
            MenuItem m = new MenuItem(recentFolder);
            m.setOnAction(_ -> {
                File file = new File(recentFolder);
                if (file.exists()) {
                    open.accept(file);
                    tabs.getSelectionModel().select(TabIndices.ENTRIES.ordinal());
                }
            });
            m.setDisable(!new File(recentFolder).exists());
            this.getItems().add(m);
        }
    }

}
