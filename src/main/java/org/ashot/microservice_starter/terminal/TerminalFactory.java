package org.ashot.microservice_starter.terminal;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class TerminalFactory {

    public static @NotNull JediTermFxWidget createTerminalWidget(PtyProcess process) {
        JediTermFxWidget widget = new JediTermFxWidget(80, 24, new DarkThemeSettingsProvider());
        widget.setTtyConnector(createTtyConnector(process));
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    private static @NotNull ProcessTtyConnector createTtyConnector(PtyProcess process) {
        try {
//                envs = new HashMap<>(System.getenv());
//                envs.put("TERM", "xterm-256color");
            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
