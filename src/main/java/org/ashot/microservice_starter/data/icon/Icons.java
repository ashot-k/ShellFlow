package org.ashot.microservice_starter.data.icon;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import static org.ashot.microservice_starter.data.icon.IconUtils.setHover;
import static org.ashot.microservice_starter.data.icon.IconUtils.setHoverToBrighter;

public class Icons {
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    public static Glyph getChevronUpIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.CHEVRON_UP);
        icon.size(size);
        icon.color(Color.GRAY);
        setHoverToBrighter(icon, Color.GRAY);
        icon.setEffect(new DropShadow());
        return icon;
    }

    public static Glyph getChevronDownIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.CHEVRON_DOWN);
        icon.size(size);
        icon.color(Color.GRAY);
        setHoverToBrighter(icon, Color.GRAY);
        icon.setEffect(new DropShadow());
        return icon;
    }

    public static Glyph getCloseButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TIMES_CIRCLE);
        icon.size(size);
        icon.color(Color.INDIANRED);
        setHover(icon, Color.INDIANRED, Color.CRIMSON);
        icon.setEffect(new DropShadow());
        return icon;
    }

    public static Glyph getExecuteButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLAY_CIRCLE);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
        icon.setEffect(new DropShadow());
        return icon;
    }

    public static Glyph getExecuteAllButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLAY);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
        return icon;
    }

    public static Glyph getAddButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLUS_CIRCLE);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
        return icon;
    }

    public static Glyph getLinuxIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.LINUX);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
        return icon;
    }

    public static Glyph getWindowsIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.WINDOWS);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
        return icon;
    }

    public static Glyph getOpenIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHover(icon, Color.DARKSLATEBLUE, Color.MEDIUMSLATEBLUE);
        return icon;
    }

    public static Glyph getOpenRecentIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN_ALT);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHover(icon, Color.DARKSLATEBLUE, Color.MEDIUMSLATEBLUE);
        return icon;
    }

    public static Glyph getSaveIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.SAVE);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHover(icon, Color.DARKSLATEBLUE, Color.MEDIUMSLATEBLUE);
        return icon;
    }

    public static Glyph getClearIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TRASH_ALT);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHover(icon, Color.DARKSLATEBLUE, Color.MEDIUMSLATEBLUE);
        return icon;
    }

    public static Glyph getBrowseIcon(double size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.SEARCH);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHover(icon, Color.DARKSLATEBLUE, Color.MEDIUMSLATEBLUE);
        return icon;
    }

    public static Glyph getErrorNotifIcon(double size){
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TIMES_CIRCLE);
        icon.size(size);
        icon.setFontSize(size);
        icon.setStyle("-fx-fill: indianred !important;-fx-text-fill: indianred !important; -fx-font-size: " + size + "px");
        return icon;
    }

    public static Glyph getInfoNotifIcon(double size){
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.INFO_CIRCLE);
        icon.size(size);
        icon.setFontSize(size);
        icon.setStyle("-fx-padding: 0; -fx-fill: white !important;-fx-text-fill: white !important; -fx-font-size: " + size + "px");
        return icon;
    }
}
