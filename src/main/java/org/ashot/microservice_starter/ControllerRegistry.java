package org.ashot.microservice_starter;

import java.util.HashMap;
import java.util.Map;

public class ControllerRegistry {
    private static Map<String, Object> controllers = new HashMap<>();

    public static void register(String key, Object controller) {
        controllers.put(key, controller);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(controllers.get(key));
    }
}
