package org.ashot.microservice_starter.terminal;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TerminalFactory {

    public static @NotNull JediTermFxWidget createTerminalWidget(PtyProcess process) {
        JediTermFxWidget widget = new JediTermFxWidget(80, 24, new DarkThemeSettingsProvider());
        widget.setTtyConnector(createTtyConnector(process, null));
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    public static @NotNull JediTermFxWidget createTerminalWidget(PtyProcess process, Consumer<String> outputMonitor) {
        JediTermFxWidget widget = new JediTermFxWidget(80, 24, new DarkThemeSettingsProvider());
        widget.setTtyConnector(createTtyConnector(process, outputMonitor));
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    private static @NotNull ProcessTtyConnector createTtyConnector(PtyProcess process, Consumer<String> outputMonitor) {
        try {
            PtyProcessTtyConnector baseConnector = new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
            return baseConnector;
//            return new MonitoringTtyConnector(baseConnector, outputMonitor);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
