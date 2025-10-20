package org.ashot.shellflow.terminal.settings;

import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.core.emulator.ColorPalette;
import com.techsenger.jeditermfx.core.emulator.ColorPaletteImpl;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.text.Font;
import org.ashot.shellflow.ShellFlow;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemedSettingsProvider extends DefaultSettingsProvider {

    private static final TerminalColor DEFAULT_DARK_MODE_BACKGROUND = new TerminalColor(0, 0, 0);
    private static final TerminalColor DEFAULT_DARK_MODE_FOREGROUND = new TerminalColor(255, 255, 255);

    private static final TerminalColor DEFAULT_LIGHT_MODE_BACKGROUND = new TerminalColor(255, 255, 255);
    private static final TerminalColor DEFAULT_LIGHT_MODE_FOREGROUND = new TerminalColor(16, 17, 23);
    private static final Logger log = LoggerFactory.getLogger(ThemedSettingsProvider.class);

    private static String fontFamily = ShellFlow.getConfig().terminalFontFamily().getFamily();
    private static double fontSize = ShellFlow.getConfig().terminalFontSize();
    private static final BooleanProperty optimizationMode = new SimpleBooleanProperty();

    @Override
    public @NotNull TerminalColor getDefaultBackground() {
        if (ShellFlow.getSelectedThemeOption().isDark()) {
            return DEFAULT_DARK_MODE_BACKGROUND;
        } else {
            return DEFAULT_LIGHT_MODE_BACKGROUND;
        }
    }

    @Override
    public @NotNull TerminalColor getDefaultForeground() {
        if (ShellFlow.getSelectedThemeOption().isDark()) {
            return DEFAULT_DARK_MODE_FOREGROUND;
        } else {
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

    @Override
    public int getBufferMaxLinesCount() {
        log.info("called line count");
        return isOptimizationMode() ? 6000 : 9999;
    }

    @Override
    public boolean useAntialiasing() {
        return !isOptimizationMode();
    }

    @Override
    public int maxRefreshRate() {
        return isOptimizationMode() ? 30 : 50;
    }

    private static void setFontSize(double fontSize) {
        ThemedSettingsProvider.fontSize = fontSize;
    }

    private static void setFontFamily(String fontFamily) {
        ThemedSettingsProvider.fontFamily = fontFamily;
    }

    public static boolean isOptimizationMode() {
        return optimizationMode.get();
    }

    public static BooleanProperty optimizationModeProperty() {
        return optimizationMode;
    }

    public static void updateFont(String fontFamily, double fontSize) {
        setFontFamily(fontFamily);
        setFontSize(fontSize);
    }
}
