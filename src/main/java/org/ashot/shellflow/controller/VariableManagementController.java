package org.ashot.shellflow.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.ashot.shellflow.data.constant.SettingsFilePaths;
import org.ashot.shellflow.data.variable.Variable;
import org.ashot.shellflow.data.variable.VariableEntry;
import org.ashot.shellflow.data.variable.Variables;
import org.ashot.shellflow.exception.io.CouldNotCreateRequiredFile;
import org.ashot.shellflow.node.notification.Notifications;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.variable.VariableSetup;
import org.ashot.shellflow.peristence.VariableRepository;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VariableManagementController {
    private final VariableSetup view;
    private final VariableRepository variableRepository;
    private final ObservableList<VariableEntry> variableList = FXCollections.observableArrayList();
    private final BooleanProperty saved = new SimpleBooleanProperty();
    private final File current;

    public VariableManagementController(File init) {
        this.view = new VariableSetup();
        this.variableRepository = new VariableRepository();
        this.current = init;
        variableList.addListener((ListChangeListener<VariableEntry>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    view.getVariablesTable().getItems().addAll(c.getAddedSubList());
                } else if (c.wasRemoved()) {
                    view.getVariablesTable().getItems().removeAll(c.getRemoved());
                }
            }
        });
        load(init);
        setupEvents();
    }

    private void setupEvents() {
        view.getSaveAllButton().setOnAction(_ -> save());
        view.getAddVariableButton().setOnAction(_ -> addVariableEntry());
        view.getDeleteRowButton().setOnAction(_ -> removeVariableEntry(view.getVariablesTable().getSelectionModel().getSelectedItem()));
        view.getVariablesTable().getSelectionModel().selectedItemProperty().addListener((_, _, _) -> view.getDeleteRowButton().setDisable(view.getVariablesTable().getSelectionModel().getSelectedItem() == null));
        view.getResetButton().setOnAction(_ -> {
            load(current);
            Notifications.showNotif("Variables were reset successfully!");
        });
    }


    private List<Variable> getVariablesList() {
        List<Variable> variables = new ArrayList<>();
        for (VariableEntry variableEntry : view.getVariablesTable().getItems()) {
            variables.add(new Variable(variableEntry.getName(), variableEntry.getValue(), variableEntry.isEnabledProperty().get()));
        }
        return variables;
    }

    private void save() {
        try {
            File fileToSave = Paths.get(SettingsFilePaths.VARIABLES.getPath()).toFile();
            variableRepository.saveToFile(fileToSave, new Variables(getVariablesList()));
            saved.set(!saved.get());
            Notifications.showNotif("Saved variables successfully!");
        } catch (CouldNotCreateRequiredFile e) {
            new AlertPopup("Error while saving Variables", e.getMessage(), false).show();
        }
    }

    private void load(File init) {
        try {
            Variables variables = variableRepository.openFromFile(init);
            variableList.clear();
            variables.variables().forEach(e -> addVariableEntry(new VariableEntry(e.name(), e.value(), new SimpleBooleanProperty(e.enabled()))));
        } catch (CouldNotCreateRequiredFile e) {
            new AlertPopup("Error while loading Variables", e.getMessage(), false).show();
        }
    }

    private void addVariableEntry() {
        VariableEntry variableEntry = new VariableEntry("", "", new SimpleBooleanProperty());
        addVariableEntry(variableEntry);
    }

    private void addVariableEntry(VariableEntry variableEntry) {
        variableList.add(variableEntry);
    }

    private void removeVariableEntry(VariableEntry variableEntry) {
        variableList.remove(variableEntry);
    }

    public ObservableList<VariableEntry> getVariables() {
        return variableList;
    }

    public VariableSetup getView() {
        return view;
    }

    public BooleanProperty changedProperty() {
        return saved;
    }
}
