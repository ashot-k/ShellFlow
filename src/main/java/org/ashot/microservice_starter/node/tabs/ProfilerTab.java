package org.ashot.microservice_starter.node.tabs;

import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ProfilerTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(ProfilerTab.class);
    private static final HashMap<String, ProcessHandle> processMap = new HashMap<>();

    public ProfilerTab(){

    }

    private void setupProfilerTab(){

    }


    public static HashMap<String, ProcessHandle> getProcessMap() {
        return processMap;
    }
}
