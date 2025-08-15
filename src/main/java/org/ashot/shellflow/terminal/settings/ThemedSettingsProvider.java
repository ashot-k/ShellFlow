package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;
import org.ashot.shellflow.Main;
import org.jetbrains.annotations.NotNull;

public class ThemedSettingsProvider extends DefaultSettingsProvider {

    private static final TerminalColor DEFAULT_DARK_MODE_BACKGROUND = new TerminalColor(0, 0, 0);
    private static final TerminalColor DEFAULT_DARK_MODE_FOREGROUND = new TerminalColor(255, 255, 255);

    private static final TerminalColor DEFAULT_LIGHT_MODE_BACKGROUND = new TerminalColor(240, 240, 240);
    private static final TerminalColor DEFAULT_LIGHT_MODE_FOREGROUND = new TerminalColor(16, 17, 23);

    private static String fontFamily = Main.getConfig().getTerminalFontFamily().getFamily();
    private static double fontSize = Main.getConfig().getTerminalFontSize();

    @Override
    public @NotNull TerminalColor getDefaultBackground() {
        if(Main.getSelectedThemeOption().isDark()) {
            return DEFAULT_DARK_MODE_BACKGROUND;
        }
        else{
            return DEFAULT_LIGHT_MODE_BACKGROUND;
        }
    }

    @Override
    public @NotNull TerminalColor getDefaultForeground() {
        if(Main.getSelectedThemeOption().isDark()){
            return DEFAULT_DARK_MODE_FOREGROUND;
        }
        else{
            return DEFAULT_LIGHT_MODE_FOREGROUND;
        }
    }

    @Override
    public Font getTerminalFont() {
        return Font.font(fontFamily, fontSize);
    }

    @Override
    public ColorPalette getTerminalColorPalette() {
        return ColorPaletteImpl.WINDOWS_PALETTE;
    }

    private static void setFontSize(double fontSize) {
        ThemedSettingsProvider.fontSize = fontSize;
    }

    private static void setFontFamily(String fontFamily) {
        ThemedSettingsProvider.fontFamily = fontFamily;
    }

    public static void updateFont(String fontFamily, double fontSize){
        setFontFamily(fontFamily);
        setFontSize(fontSize);
    }
}
