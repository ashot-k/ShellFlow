package org.ashot.shellflow.terminal;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class TerminalFactory {
    private TerminalFactory() {
    }

    public static @NotNull ShellFlowTerminalWidget createTerminalWidget() {
        ShellFlowTerminalWidget widget = new ShellFlowTerminalWidget(new ThemedSettingsProvider());
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    public static @NotNull ProcessTtyConnector createTtyConnector(PtyProcess process) {
        try {
            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
