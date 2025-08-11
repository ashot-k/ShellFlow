package org.ashot.shellflow.registry;

import com.techsenger.jeditermfx.core.TtyConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerminalRegistry {
    private static final Map<String, TtyConnector> ttyConnectors = new HashMap<>();

    private TerminalRegistry(){}

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
        ttyConnectors.values().forEach(ttyConnector -> {
            new Thread(()->{
                try {
                    ttyConnector.write("\u0003");
                    ttyConnector.waitFor();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}
