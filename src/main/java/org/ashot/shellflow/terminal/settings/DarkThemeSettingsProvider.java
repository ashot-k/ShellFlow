package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

public class DarkThemeSettingsProvider extends DefaultSettingsProvider {

    @Override
    public @NotNull TerminalColor getDefaultBackground() {
        return new TerminalColor(23, 27, 33);
    }

    @Override
    public @NotNull TerminalColor getDefaultForeground() {
        return new TerminalColor(255, 255, 255);
    }

    @Override
    public Font getTerminalFont() {
        return Font.font("Consolas", 16);
    }

    @Override
    public ColorPalette getTerminalColorPalette() {
        return ColorPaletteImpl.WINDOWS_PALETTE;
    }
}