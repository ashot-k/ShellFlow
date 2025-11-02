package org.ashot.shellflow.registry;

import com.techsenger.jeditermfx.core.TtyConnector;
import org.ashot.shellflow.node.popup.AlertPopup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerminalRegistry {
    private static final Map<String, TtyConnector> ttyConnectors = new HashMap<>();

    private TerminalRegistry() {
    }

    public static void register(String key, TtyConnector process) {
        ttyConnectors.put(key, process);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(ttyConnectors.get(key));
    }

    public static Map<String, TtyConnector> getAllTerminalProcesses() {
        return ttyConnectors;
    }

    public static void stopAllTerminals() {
        ttyConnectors.values().forEach(TerminalRegistry::stopTerminal);
    }

    public static void stopTerminal(TtyConnector ttyConnector) {
        new Thread(() -> {
            try {
                ttyConnector.write("\u0003");
                ttyConnector.waitFor();
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                new AlertPopup("Termination Error", "Could not termiate process: " + e.getMessage(), false);
            }
        }).start();
    }
}
