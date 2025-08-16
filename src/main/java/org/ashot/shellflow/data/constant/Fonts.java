package org.ashot.shellflow.data.constant;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.ashot.shellflow.ShellFlow;

public class Fonts {

    private static Font getApplicationFont(){
        Font font = ShellFlow.getApplicationFont();
        if(font == null){
            font = Font.getDefault();
        }
        return font;
    }

    private static String getFontFamily(){
        return getApplicationFont().getFamily();
    }

    public static Font title() {
        return Font.font(getFontFamily(), FontWeight.BOLD, 16);
    }

    public static Font subTitle(){
        return Font.font(getFontFamily(), FontWeight.BOLD, 14);
    }

    public static Font buttonText(){
        return Font.font(getFontFamily(), FontWeight.NORMAL, 12);
    }

    public static Font detailText(){
        return Font.font(getFontFamily(), FontWeight.NORMAL, 12);
    }

    public static Font fileLabelText(){
        return Font.font(getFontFamily(), FontWeight.EXTRA_BOLD, 14);
    }

    public static Font fieldLabel(){
        return Font.font(getFontFamily(), FontWeight.NORMAL, 10);
    }
    public static Font fieldText(){
        return Font.font(getFontFamily(), FontWeight.NORMAL, 12);
    }
}
