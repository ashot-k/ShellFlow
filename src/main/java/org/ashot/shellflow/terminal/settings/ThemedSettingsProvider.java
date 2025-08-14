package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;
import org.ashot.shellflow.Main;
import org.jetbrains.annotations.NotNull;

public class ThemedSettingsProvider extends DefaultSettingsProvider {

    private static final TerminalColor DEFAULT_DARK_MODE_BACKGROUND = new TerminalColor(23, 27, 33);
    private static final TerminalColor DEFAULT_DARK_MODE_FOREGROUND = new TerminalColor(255, 255, 255);

    private static final TerminalColor DEFAULT_LIGHT_MODE_BACKGROUND = new TerminalColor(220, 220, 220);
    private static final TerminalColor DEFAULT_LIGHT_MODE_FOREGROUND = new TerminalColor(16, 17, 23);
    private static double fontSize = 15;

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
        return Font.font("Courier New", fontSize);
    }

    @Override
    public ColorPalette getTerminalColorPalette() {
        return ColorPaletteImpl.WINDOWS_PALETTE;
    }

    public static void setFontSize(double fontSize) {
        ThemedSettingsProvider.fontSize = fontSize;
    }
}
