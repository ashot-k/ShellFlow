package org.ashot.shellflow.node.icon;

import javafx.scene.paint.Color;
import org.ashot.shellflow.utils.Animator;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import static org.ashot.shellflow.utils.IconUtils.setHoverToBrighter;
import static org.ashot.shellflow.utils.IconUtils.setHoverToColor;

public class Icons {
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private static final Color preferredColor = Color.SLATEBLUE;

    private Icons(){}

    private static Glyph decorateGlyph(Glyph icon) {
        icon.getStyleClass().add("icon");
        icon.setMouseTransparent(true);
        return icon;
    }

    public static Glyph getCloseButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TIMES_CIRCLE);
        icon.size(size);
        icon.color(Color.LIGHTCORAL);
        setHoverToColor(icon, Color.LIGHTCORAL, Color.INDIANRED);
        return decorateGlyph(icon);
    }

    public static Glyph getExecuteButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLAY_CIRCLE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return decorateGlyph(icon);
    }

    public static Glyph getExecuteAllButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLAY);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getToggleToolbarIcon(int size, boolean show){
        Glyph icon = fontAwesome.create(show ? FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT: FontAwesome.Glyph.ANGLE_DOUBLE_LEFT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getAddButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLUS_CIRCLE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getLinuxIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.LINUX);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getWindowsIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.WINDOWS);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getOpenIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getOpenRecentIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getSaveAsIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FILE_TEXT_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getSaveIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.SAVE);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getClearIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TRASH_ALT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getBrowseIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.SEARCH);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getThemeSettingIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.ADJUST);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getErrorNotifIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TIMES_CIRCLE);
        icon.size(size);
        icon.setFontSize(size);
        icon.setStyle("-fx-fill: indianred !important;-fx-text-fill: indianred !important; -fx-font-size: " + size + "px");
        return icon;
    }

    public static Glyph getInfoNotifIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.INFO_CIRCLE);
        icon.size(size);
        icon.setFontSize(size);
        icon.setStyle("-fx-padding: 0; -fx-fill: white !important;-fx-text-fill: white !important; -fx-font-size: " + size + "px");
        return icon;
    }

    public static Glyph getExecutionInProgressIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.SPINNER);
        icon.size(size);
        icon.color(preferredColor);
        Animator.spinIcon(icon);
        return icon;
    }

    public static Glyph getExecutionFinishedIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.CHECK_CIRCLE);
        icon.size(size);
        icon.color(Color.GREEN);
        return icon;
    }

    public static Glyph getExecutionErrorIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.MINUS_CIRCLE);
        icon.size(size);
        icon.color(Color.INDIANRED);
        return icon;
    }

    public static Glyph getExecutionCanceledIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.MINUS);
        icon.size(size);
        icon.color(Color.INDIANRED);
        return icon;
    }

    public static Glyph getFontSelectionMenuIcon(double size){
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FONT);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getExpandAllEntriesIcon(double size){
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TOGGLE_DOWN);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

    public static Glyph getCollapseAllEntriesIcon(double size){
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TOGGLE_UP);
        icon.size(size);
        icon.color(preferredColor);
        setHoverToBrighter(icon, preferredColor);
        return icon;
    }

}
