package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;
import org.ashot.shellflow.Main;
import org.jetbrains.annotations.NotNull;

public class ThemedSettingsProvider extends DefaultSettingsProvider {
    @Override
    public @NotNull TerminalColor getDefaultBackground() {
        if(Main.getSelectedThemeOption().isDark()) {
            return new TerminalColor(23, 27, 33);
        }
        else{
            return new TerminalColor(248, 248, 248);
        }
    }

    @Override
    public @NotNull TerminalColor getDefaultForeground() {
        if(Main.getSelectedThemeOption().isDark()){
            return new TerminalColor(255, 255, 255);
        }
        else{
            return new TerminalColor(16, 17, 23);
        }
    }

    @Override
    public Font getTerminalFont() {
        return Font.font("Courier New", 16);
    }

    @Override
    public ColorPalette getTerminalColorPalette() {
        return ColorPaletteImpl.WINDOWS_PALETTE;
    }
}
