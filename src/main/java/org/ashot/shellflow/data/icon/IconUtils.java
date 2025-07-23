package org.ashot.shellflow.data.icon;

import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;

public class IconUtils {
    public static void setHover(Glyph icon, Color off, Color hoveredColor) {
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
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
                    if (newValue) {
                        icon.color(off.brighter());
                    } else {
                        icon.color(off);
                    }
                });

            }
        });
    }

    public static void setHoverToDarker(Glyph icon, Color off) {
        icon.parentProperty().addListener((_, _, p) -> {
            if (p != null) {
                p.hoverProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
                        icon.color(off.darker());
                    } else {
                        icon.color(off);
                    }
                });

            }
        });
    }
}
