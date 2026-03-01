package org.ashot.shellflow.data.variable;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import org.ashot.shellflow.node.entry.button.CloseButton;

public class VariableEntry {
    private String name;
    private String value;
    private SimpleBooleanProperty enabled;
    private final Button removeButton;

    public VariableEntry(String name, String value, SimpleBooleanProperty enabled) {
        this.name = name;
        this.value = value;
        this.enabled = enabled;
        this.removeButton = new CloseButton();
        this.removeButton.setCursor(Cursor.HAND);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public SimpleBooleanProperty isEnabledProperty() {
        return enabled;
    }

    public void setEnabledProperty(SimpleBooleanProperty enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public Button getRemoveButton() {
        return removeButton;
    }
}
