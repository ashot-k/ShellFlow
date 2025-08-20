package org.ashot.shellflow.utils;

import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;

public class IconUtils {

    private IconUtils(){}

    public static void setHoveredColor(Glyph icon, Color off, Color hoveredColor) {
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (Boolean.TRUE.equals(newValue)) {
                        icon.color(hoveredColor);
                    } else {
                        icon.color(off);
                    }
                });
            }
        });
    }

    public static void setHoverToBrighter(Glyph icon, Color off) {
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (Boolean.TRUE.equals(newValue)) {
                        icon.color(off.brighter());
                    } else {
                        icon.color(off);
                    }
                });

            }
        });
    }

    public static void setHoverToColor(Glyph icon, Color from, Color to){
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (Boolean.TRUE.equals(newValue)) {
                        icon.color(to);
                    } else {
                        icon.color(from);
                    }
                });

            }
        });
    }

    public static void setHoverToDarker(Glyph icon, Color off) {
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (Boolean.TRUE.equals(newValue)) {
                        icon.color(off.darker());
                    } else {
                        icon.color(off);
                    }
                });

            }
        });
    }
}
