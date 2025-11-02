package org.ashot.shellflow.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CouldNotWriteDataToFileException;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.node.variable.VariableSetup;
import org.ashot.shellflow.peristence.VariableRepository;

import java.io.File;

public class VariableManagementController {

    private final VariableSetup view;
    private final VariableRepository variableRepository;
    private final ObservableList<VariableEntry> variableList = FXCollections.observableArrayList();
    private final BooleanProperty saved = new SimpleBooleanProperty();

    public VariableManagementController(File init) {
        view = new VariableSetup();
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
            try {
                variableRepository.saveToFile(variableList);
                saved.set(!saved.get());
            } catch (CouldNotWriteDataToFileException e) {
                new AlertPopup("Error while saving Variables", e.getMessage(), false).show();
            }
        });
        view.getAddVariableButton().setOnAction(_ -> addVariableEntry());
    }

    private void load(File init) {
        try {
            variableRepository.loadExisting(init).forEach(this::addVariableEntry);
        } catch (CouldNotReadFromFileException e) {
            new AlertPopup("Error while loading Variables", e.getMessage(), false).show();
        }
    }

    private void addVariableEntry() {
        VariableEntry variableEntry = new VariableEntry("", "", true);
        addVariableEntry(variableEntry);
    }

    private void addVariableEntry(VariableEntry variableEntry) {
        variableEntry.setOnRemove(_ -> removeVariableEntry(variableEntry));
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
