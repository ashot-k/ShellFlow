package org.ashot.shellflow.node.icon;

import javafx.scene.paint.Color;
import org.ashot.shellflow.utils.GUIAnimations;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import static org.ashot.shellflow.utils.IconUtils.setHoverToBrighter;
import static org.ashot.shellflow.utils.IconUtils.setHoverToColor;
import static org.controlsfx.glyphfont.FontAwesome.Glyph.*;

public class Icons {
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private static final Color preferredColor = Color.SLATEBLUE;

    private Icons() {
    }

    private static Glyph decorateGlyph(Glyph icon) {
        icon.getStyleClass().add("icon");
        icon.setMouseTransparent(true);
        return icon;
    }

    public static Glyph getCloseButtonIcon(int size) {
        Glyph icon = fontAwesome.create(TIMES_CIRCLE);
        icon.size(size);
        icon.color(Color.LIGHTCORAL);
        setHoverToColor(icon, Color.LIGHTCORAL, Color.INDIANRED);
        return decorateGlyph(icon);
    }

    public static Glyph getExecuteButtonIcon(int size) {
        Glyph icon = fontAwesome.create(PLAY_CIRCLE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return decorateGlyph(icon);
    }

    public static Glyph getExecuteAllButtonIcon(int size) {
        Glyph icon = fontAwesome.create(PLAY);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getAddButtonIcon(int size) {
        Glyph icon = fontAwesome.create(PLUS);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getOpenIcon(int size) {
        Glyph icon = fontAwesome.create(FOLDER_OPEN);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getOpenRecentIcon(int size) {
        Glyph icon = fontAwesome.create(FOLDER_OPEN_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getSaveAsIcon(int size) {
        Glyph icon = fontAwesome.create(FILE_TEXT_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getResetIcon(int size) {
        Glyph icon = fontAwesome.create(UNDO);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getSaveIcon(int size) {
        Glyph icon = fontAwesome.create(SAVE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getClearIcon(int size) {
        Glyph icon = fontAwesome.create(TRASH_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getErrorIcon(int size) {
        Glyph icon = fontAwesome.create(EXCLAMATION_CIRCLE);
        icon.size(size);
        icon.color(Color.INDIANRED);
        return icon;
    }

    public static Glyph getBrowseIcon(double size) {
        Glyph icon = fontAwesome.create(SEARCH);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getThemeSettingIcon(double size) {
        Glyph icon = fontAwesome.create(ADJUST);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getExecutionInProgressIcon(double size) {
        Glyph icon = fontAwesome.create(SPINNER);
        icon.size(size);
        icon.color(preferredColor);
        GUIAnimations.spinIcon(icon);
        return icon;
    }

    public static Glyph getExecutionFinishedIcon(double size) {
        Glyph icon = fontAwesome.create(CHECK_CIRCLE);
        icon.size(size);
        icon.color(Color.GREEN);
        return icon;
    }

    public static Glyph getExecutionErrorIcon(double size) {
        Glyph icon = fontAwesome.create(MINUS_CIRCLE);
        icon.size(size);
        icon.color(Color.INDIANRED);
        return icon;
    }

    public static Glyph getExecutionCancelledIcon(double size) {
        Glyph icon = fontAwesome.create(MINUS);
        icon.size(size);
        icon.color(Color.INDIANRED);
        return icon;
    }

    public static Glyph getFontSelectionMenuIcon(double size) {
        Glyph icon = fontAwesome.create(FONT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getExpandAllEntriesIcon(double size) {
        Glyph icon = fontAwesome.create(TOGGLE_DOWN);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getCollapseAllEntriesIcon(double size) {
        Glyph icon = fontAwesome.create(TOGGLE_UP);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getSidePanelToggle(double size, boolean toggled) {
        Glyph icon = fontAwesome.create(toggled ? CHEVRON_LEFT : CHEVRON_RIGHT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getPerformanceOptionIcon(double size) {
        Glyph icon = fontAwesome.create(GEAR);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getDesktopNotificationIcon(double size) {
        Glyph icon = fontAwesome.create(INFO);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getInfoIcon(double size) {
        Glyph icon = fontAwesome.create(INFO_CIRCLE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getEnabledEntryToggleIcon(double size, boolean toggled) {
        Glyph icon = fontAwesome.create(toggled ? TOGGLE_ON : TOGGLE_OFF);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getWSLOptionToggleIcon(double size, boolean toggled) {
        Glyph icon = fontAwesome.create(toggled ? LINUX : WINDOWS);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }
}
