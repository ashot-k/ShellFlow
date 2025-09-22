package org.ashot.shellflow.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.ashot.shellflow.node.tab.setup.VariableSetupSidePanel;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.peristence.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class VariableManagementController {
    private static final Logger log = LoggerFactory.getLogger(VariableManagementController.class);

    private final VariableSetupSidePanel view;
    private final VariableRepository variableRepository;
    //todo replace with TableView
    private final ObservableList<VariableEntry> variableList = FXCollections.observableArrayList();
    private final BooleanProperty saved = new SimpleBooleanProperty();

    public VariableManagementController(File init) {
        view = new VariableSetupSidePanel();
        variableRepository = new VariableRepository();

        variableList.addListener((ListChangeListener<VariableEntry>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    view.getVariableRows().getChildren().addAll(c.getAddedSubList());
                } else if (c.wasRemoved()) {
                    view.getVariableRows().getChildren().removeAll(c.getRemoved());
                }
            }
        });
        load(init);
        setupEvents();
    }

    private void setupEvents() {
        view.getSaveAllButton().setOnAction(_ -> {
            variableRepository.saveToFile(variableList);
            saved.set(!saved.get());
        });
        view.getAddVariableButton().setOnAction(_ -> addVariableEntry());
    }

    private void load(File init) {
        variableRepository.loadExisting(init).forEach(this::addVariableEntry);
    }

    private void addVariableEntry() {
        addVariableEntry("", "");
    }

    private void addVariableEntry(String name, String value) {
        addVariableEntry(name, value, true);
    }

    private void addVariableEntry(String name, String value, boolean enabled) {
        VariableEntry variableEntry = new VariableEntry(name, value, enabled);
        addVariableEntry(variableEntry);
    }

    private void addVariableEntry(VariableEntry variableEntry) {
        variableEntry.setOnRemove(_ -> removeVariableEntry(variableEntry));
        variableList.add(variableEntry);
    }

    private void removeVariableEntry(VariableEntry variableEntry) {
        variableList.remove(variableEntry);
    }

    public List<VariableEntry> getVariables() {
        return variableList;
    }

    public BooleanProperty animatedProperty() {
        return view.animatedProperty();
    }

    public VariableSetupSidePanel getView() {
        return view;
    }

    public BooleanProperty changedProperty() {
        return saved;
    }
}
