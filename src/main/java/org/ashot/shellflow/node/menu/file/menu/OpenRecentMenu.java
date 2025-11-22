package org.ashot.shellflow.node.menu.file.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.utility.Recent;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class OpenRecentMenu extends Menu {
    private static final Logger log = LoggerFactory.getLogger(OpenRecentMenu.class);
    private static final int MAX_ENTRIES = 15;
    private final Consumer<File> open;

    public OpenRecentMenu(Consumer<File> open, Menu parentMenu) {
        this.open = open;
        setText("Open Recent");
        setGraphic(Icons.getOpenRecentIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        parentMenu.setOnShowing(_ -> refreshRecentFiles());
    }

    public void refreshRecentFiles() {
        try {
            getItems().clear();
            Recent recents = RecentFileUtils.getRecents();
            for (Object s : recents.recentlyOpenedFiles().stream().limit(MAX_ENTRIES).toList()) {
                String recentFile = s.toString();
                MenuItem m = createRecentMenuItemOption(recentFile);
                getItems().add(m);
            }
        } catch (Exception e) {
            log.error("Could not refresh files in Open Recent menu: {}", e.getMessage());
        }
    }

    private MenuItem createRecentMenuItemOption(String recentFile) {
        MenuItem m = new MenuItem(recentFile);
        m.setOnAction(_ -> {
            try {
                Path path = Paths.get(recentFile);
                if (FileUtils.fileExists(path)) {
                    open.accept(path.toFile());
                } else {
                    AlertPopup alertPopup = new AlertPopup(
                            "Error",
                            "Could not open file \"" + recentFile + "\", it does not exist",
                            false
                    );
                    alertPopup.show();
                    RecentFileUtils.removeRecentFile(recentFile);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        return m;
    }

}
