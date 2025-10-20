package org.ashot.shellflow.data.entry;

import java.util.ArrayList;
import java.util.List;

public class Execution {
    private List<Entry> entries = new ArrayList<>();
    private String name;
    private int delay;
    private boolean sequence;

    public Execution() {
    }

    public Execution(List<Entry> entries, String name, int delay, boolean sequence) {
        this.entries = entries;
        this.name = name;
        this.delay = delay;
        this.sequence = sequence;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isSequence() {
        return sequence;
    }

    public void setSequence(boolean sequence) {
        this.sequence = sequence;
    }
}
