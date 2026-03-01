package org.ashot.shellflow.registry;

import com.pty4j.PtyProcess;
import org.ashot.shellflow.node.popup.AlertPopup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.application.Platform.runLater;

public class TerminalRegistry {
    private static final Map<String, PtyProcess> ptyProcesses = new HashMap<>();

    private TerminalRegistry() {
    }

    public static void register(String key, PtyProcess process) {
        ptyProcesses.put(key, process);
    }

    public static void remove(String key) {
        ptyProcesses.remove(key);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(ptyProcesses.get(key));
    }

    public static Map<String, PtyProcess> getAllTerminalProcesses() {
        return ptyProcesses;
    }

    public static void stopAllTerminals() {
        ptyProcesses.values().forEach(TerminalRegistry::stopTerminal);
    }

    public static void stopTerminal(PtyProcess ptyProcess) {
        if (ptyProcess == null || !ptyProcess.isAlive()) {
            return;
        }
        new Thread(() -> {
            try (BufferedWriter writer = ptyProcess.outputWriter()) {
                writer.write("\u0003");
                writer.flush();
                remove(String.valueOf(ptyProcess.pid()));
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                runLater(() -> new AlertPopup("Termination Error", "Could not terminate process: " + e.getMessage(), false).show());
            }
        }).start();
    }
}
