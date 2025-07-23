package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

public class WhiteThemeSettingsProvider extends DefaultSettingsProvider {

    @Override
    public @NotNull TerminalColor getDefaultBackground() {
        return new TerminalColor(255, 255, 255);
    }

    @Override
    public @NotNull TerminalColor getDefaultForeground() {
        return new TerminalColor(13, 17, 23);
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