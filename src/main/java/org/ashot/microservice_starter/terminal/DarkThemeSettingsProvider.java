package org.ashot.microservice_starter.terminal;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.scene.text.Font;

public class DarkThemeSettingsProvider extends DefaultSettingsProvider {

    @Override
    public TerminalColor getDefaultBackground() {
        return new TerminalColor(13, 17, 23);
    }

    @Override
    public TerminalColor getDefaultForeground() {
        return new TerminalColor(255, 255, 255);
    }

    @Override
    public Font getTerminalFont() {
//        return Font.font("Monospaced", 16);
        return Font.font("Consolas", 16);
    }

    @Override
    public ColorPalette getTerminalColorPalette() {
        return ColorPaletteImpl.WINDOWS_PALETTE;
//        return super.getTerminalColorPalette();
    }
}