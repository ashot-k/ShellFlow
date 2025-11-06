package org.ashot.shellflow.utils;

import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.node.icon.Icons;
import org.controlsfx.glyphfont.Glyph;

public class TabUtils {
    public static final int TAB_ICON_SIZE = 18;

    private TabUtils() {
    }

    public static void setFailed(SingleExecutionTab tab) {
        setFailed(tab, false);
    }

    public static void setCancelled(SingleExecutionTab tab) {
        setCancelled(tab, false);
    }

    public static void setFinished(SingleExecutionTab tab) {
        setFinished(tab, false);
    }

    public static void setInProgress(SingleExecutionTab tab) {
        tab.setGraphic(Icons.getExecutionInProgressIcon(TAB_ICON_SIZE));
        tab.setDisable(false);
        tab.setClosable(true);
        tab.setInProgress();
    }

    public static void setFailed(SingleExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setDisable(false);
        tab.setClosable(!sequence);
        tab.setFailed();
        GUIAnimations.rotateInAndWobble(icon);
    }


    public static void setFinished(SingleExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setClosable(!sequence);
        tab.setDisable(false);
        tab.setFinished();
        GUIAnimations.rotateInAndWobble(icon);
    }

    public static void setCancelled(SingleExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setDisable(false);
        tab.setClosable(!sequence);
        GUIAnimations.rotateInAndWobble(icon);
        tab.setCancelled();
    }


}
