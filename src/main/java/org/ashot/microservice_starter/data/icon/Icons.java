package org.ashot.microservice_starter.data.icon;

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
        return icon;
    }

    public static Glyph getChevronDownIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.CHEVRON_DOWN);
        icon.size(size);
        icon.color(Color.GRAY);
        setHoverToBrighter(icon, Color.GRAY);
        return icon;
    }

    public static Glyph getCloseButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.TIMES_CIRCLE);
        icon.size(size);
        icon.color(Color.INDIANRED);
        setHover(icon, Color.INDIANRED, Color.CRIMSON);
        return icon;
    }

    public static Glyph getExecuteButtonIcon(int size) {
        Glyph icon = fontAwesome.create(FontAwesome.Glyph.PLAY_CIRCLE);
        icon.size(size);
        icon.color(Color.DARKSLATEBLUE);
        setHoverToBrighter(icon, Color.DARKSLATEBLUE);
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

    public static Glyph getClearEntriesIcon(int size) {
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
}
