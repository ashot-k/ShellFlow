package org.ashot.microservice_starter.data.icon;

import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;

public class IconUtils {
    public static void setHover(Glyph icon, Color off, Color hoveredColor){
        icon.hoverProperty().addListener((_, _, newValue) -> {
            if(newValue){
                icon.color(hoveredColor);
            }else {
                icon.color(off);
            }
        });
    }
    public static void setHoverToBrighter(Glyph icon, Color off){
        icon.hoverProperty().addListener((_, _, newValue) -> {
            if(newValue){
                icon.color(off.brighter());
            }else {
                icon.color(off);
            }
        });
    }
    public static void setHoverToDarker(Glyph icon, Color off){
        icon.hoverProperty().addListener((_, _, newValue) -> {
            if(newValue){
                icon.color(off.darker());
            }else {
                icon.color(off);
            }
        });
    }
}
