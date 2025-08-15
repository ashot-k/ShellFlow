package org.ashot.shellflow.terminal;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import org.ashot.shellflow.terminal.settings.ThemedSettingsProvider;
import org.ashot.shellflow.terminal.tty.MonitoringTtyConnector;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TerminalFactory {

    public static @NotNull ShellFlowTerminalWidget createTerminalWidget(PtyProcess process) {
        ShellFlowTerminalWidget widget = new ShellFlowTerminalWidget(new ThemedSettingsProvider());
        widget.setTtyConnector(createTtyConnector(process));
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    public static @NotNull ShellFlowTerminalWidget createTerminalWidget(PtyProcess process, Consumer<String> outputMonitor) {
        ShellFlowTerminalWidget widget = new ShellFlowTerminalWidget(new ThemedSettingsProvider());
        widget.setTtyConnector(createTtyConnector(process, outputMonitor));
        widget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        return widget;
    }

    private static @NotNull ProcessTtyConnector createTtyConnector(PtyProcess process) {
        try {
            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static @NotNull ProcessTtyConnector createTtyConnector(PtyProcess process, Consumer<String> outputMonitor) {
        try {
            PtyProcessTtyConnector baseConnector = new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
            return new MonitoringTtyConnector(baseConnector, outputMonitor);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
