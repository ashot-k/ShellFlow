package org.ashot.microservice_starter.registry;

import java.util.HashMap;
import java.util.Map;

public class ProcessRegistry {
    private static final Map<String, Process> processes = new HashMap<>();

    public static void register(String key, Process process) {
        processes.put(key, process);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(processes.get(key));
    }

    public static void killAllProcesses(){
        processes.values().forEach(Process::destroy);
    }
}
