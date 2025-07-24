package org.ashot.shellflow.registry;

import java.util.HashMap;
import java.util.Map;

public class ProcessRegistry {
    private static final Map<String, Process> processes = new HashMap<>();

    private ProcessRegistry(){}

    public static void register(String key, Process process) {
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        processes.put(key, process);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(processes.get(key));
    }

    public static Map<String, Process> getProcesses() {
        return processes;
    }

    public static void killAllProcesses() {
        processes.values().forEach(Process::destroyForcibly);
        processes.clear();
    }
}
