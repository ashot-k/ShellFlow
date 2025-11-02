package org.ashot.shellflow.controller;

import atlantafx.base.theme.Styles;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.entry.Execution;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CouldNotWriteDataToFileException;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.entry.EntrySetupTab;
import org.ashot.shellflow.node.entry.EntrySetupToolBar;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.peristence.ExecutionRepository;
import org.ashot.shellflow.utils.GUIAnimations;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.utils.Utils.checkIfWindows;

public class EntryManagementController {
    private static final Logger log = LoggerFactory.getLogger(EntryManagementController.class);
    private final EntrySetupTab view;
    private final ExecutionRepository entryRepository;
    private final EntryMapper entryMapper;
    private final ObservableList<EntryBox> entryBoxes = FXCollections.observableArrayList();
    private final EntrySetupToolBar toolBar;
    private final IntegerProperty delay = new SimpleIntegerProperty();
    private final BooleanProperty sequenceOption = new SimpleBooleanProperty();
    private final StringProperty executionName = new SimpleStringProperty();
    private final StringProperty currentFileAbsolutePath = new SimpleStringProperty();
    private final BooleanProperty optimizationMode = new SimpleBooleanProperty();
    private final ExecutionManagementController executionManagement;
    private final VariableManagementController variableManagement;

    public EntryManagementController(ExecutionManagementController executionManagement, VariableManagementController variableController) {
        this.view = new EntrySetupTab();
        this.entryRepository = new ExecutionRepository();
        this.executionManagement = executionManagement;
        this.variableManagement = variableController;
        this.entryMapper = new EntryMapper(variableController.getVariables());

        Tab executionOptionsTab = new Tab("Execution Options", this.view.getEntryExecutionOptions());
        Tab variablesTab = new Tab("Variables", this.variableManagement.getView());
        this.toolBar = this.view.getEntrySetupToolBar();
        this.toolBar.getTabPane().getTabs().addAll(executionOptionsTab, variablesTab);

        this.executionManagement.getView().getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (this.executionManagement.getView().getTabs().isEmpty()) {
                this.view.setPlaceHolder();
            } else {
                this.view.setExecutionsSplit(this.executionManagement.getView());
            }
        });
        this.view.setExecutionsSplit(executionManagement.getView());
        setupEvents();
    }

    private void setupEvents() {
        view.getEntryExecutionOptions().getDelayPerCmdSpinner().valueProperty().addListener((_, _, newVal) -> delay.set(newVal));
        delay.addListener((_, _, newVal) -> view.getEntryExecutionOptions().getDelayPerCmdSpinner().getValueFactory().setValue(newVal.intValue()));
        sequenceOption.bindBidirectional(view.getEntryExecutionOptions().getSequenceOptionCheckbox().selectedProperty());
        executionName.bindBidirectional(view.getEntryExecutionOptions().getExecutionNameField().textProperty());
        entryBoxes.addListener((ListChangeListener<EntryBox>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    view.getEntryListContainer().getChildren().addAll(c.getAddedSubList());
                    view.getEntryListContainer().getChildren().forEach(e -> setupDragging((EntryBox) e));
                } else if (c.wasRemoved()) {
                    view.getEntryListContainer().getChildren().removeAll(c.getRemoved());
                }
            }
        });
        view.getEntryInfoBar().hoverProperty().addListener((_, _, hovering) ->
                view.getEntryInfoBar().setFileLoadedText(hovering ? currentFileAbsolutePath.get() : getFileName(currentFileAbsolutePath.get()))
        );
        view.getFileLoadedText().setOnMouseClicked(this::handleEntryInfoTextClick);
        view.getEntryExecutionOptions().getExpandAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(true)));
        view.getEntryExecutionOptions().getCollapseAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(false)));
        view.getEntryExecutionOptions().getClearAllEntriesButton().setOnAction(_ -> clearEntryBoxes());
        view.getEntryExecutionOptions().getExecuteAllButton().setOnAction(_ -> executeAll());
        view.getEntryExecutionOptions().getAddEntryButton().setOnAction(_ -> addEntryBox());
        addEntryBoxChangeListener(_ -> view.getEntryExecutionOptions().getExecuteAllButton().setDisable(entryBoxes.isEmpty()));
        addEntryBoxChangeListener(_ -> view.getEntryExecutionOptions().getClearAllEntriesButton().setDisable(entryBoxes.isEmpty()));
    }

    private void handleEntryInfoTextClick(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            ClipboardContent content = new ClipboardContent();
            content.putString(currentFileAbsolutePath.get());
            Clipboard.getSystemClipboard().setContent(content);
        }
        if (e.getButton() != MouseButton.PRIMARY || e.getClickCount() != 1 || !e.isStillSincePress()) {
            return;
        }
        File file = new File(currentFileAbsolutePath.get());
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                new AlertPopup("Could not open file", "File could not be opened: " + file.getAbsolutePath() + "\n" + ex.getMessage(), false).show();
            }
        } else {
            new AlertPopup("Could not open file", "File does not exist: " + file.getAbsolutePath(), false).show();
        }
    }

    public void addEntryBox() {
        log.debug("Adding empty Entry box");
        addEntryBox(new Entry());
    }

    public void addEntryBox(Entry entry) {
        log.debug("Adding entry box with name: {}, path: {}, command: {}", entry.getName(), entry.getPath(), entry.getCommand());
        EntryBox entryBox = entryMapper.entryToEntryBox(entry);
        entryBox.setOnDeleteButtonAction(_ -> removeEntryBox(entryBox));
        entryBox.animatedProperty().bind(optimizationMode.not());
        entryBox.setOnExecuteButtonAction(_ -> {
            GUIAnimations.shakeY(entryBox.getExecuteButton(), 2.5).play();
            Command command = entryMapper.entryToCommand(entryMapper.entryBoxToEntry(entryBox), false);
            SingularExecutionTask singularExecutionTask = executionManagement.createExecutionTask(command);
            ExecutionManagementController.executeTask(singularExecutionTask);
        });

        setupDragging(entryBox);
        GUIAnimations.fadeInBeforeAdditionToList(entryBox);
        setupEntryBoxEvents(entryBox);
        entryBoxes.add(entryBox);
    }

    private void setupEntryBoxEvents(EntryBox entryBox) {
        entryBox.getCommandField().focusedProperty().addListener((_, _, focused) -> {
            if (!focused) {
                handleVariableValidation(entryBox);
            }
        });
        entryBox.getPathField().focusedProperty().addListener((_, _, focused) -> {
            if (!focused) {
                handleVariableValidation(entryBox);
            }
        });
        variableManagement.changedProperty().addListener((_, _, _) -> entryBoxes.forEach(this::handleVariableValidation));
        handleVariableValidation(entryBox);
    }

    private void handleVariableValidation(EntryBox entryBox) {
        List<String> commandFieldVariableValidationErrors = validateFieldForVariables(entryBox.getCommandField().getText());
        List<String> pathFieldVariableValidationErrors = validateFieldForVariables(entryBox.getPathField().getText());
        if (commandFieldVariableValidationErrors.isEmpty() && pathFieldVariableValidationErrors.isEmpty()) {
            entryBox.hidePrompt();
        } else if (commandFieldVariableValidationErrors.isEmpty()) {
            showPromptForErrors("Path", entryBox, pathFieldVariableValidationErrors);
        } else if (pathFieldVariableValidationErrors.isEmpty()) {
            showPromptForErrors("Command", entryBox, commandFieldVariableValidationErrors);
        } else {
            List<String> errors = Stream.of(commandFieldVariableValidationErrors, pathFieldVariableValidationErrors).flatMap(Collection::stream).toList();
            showPromptForErrors("Command and Path", entryBox, errors);
        }
    }

    public void showPromptForErrors(String fieldName, EntryBox entryBox, List<String> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fieldName).append(" ").append("contains unknown variables: ");
        for (int i = 0; i < errors.size(); i++) {
            String error = errors.get(i);
            stringBuilder.append(error);
            if (i < errors.size() - 1) {
                stringBuilder.append(",");
            }
            stringBuilder.append(" ");
        }
        entryBox.showPromptMessageToField(null, stringBuilder.toString(), Styles.DANGER, Icons.getErrorIcon(IconSizeDefaults.ENTRY_VALIDATION_MESSAGE_ICON.getSize()));
    }

    public List<String> validateFieldForVariables(String fieldValue) {
        List<String> errors = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(fieldValue);
        while (matcher.find()) {
            boolean found = false;
            String occurrence = matcher.group(1);
            for (VariableEntry variableEntry : variableManagement.getVariables()) {
                if (variableEntry.getName().equals(occurrence)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.add(occurrence);
            }
        }
        return errors;
    }

    public void removeEntryBox(EntryBox entryBox) {
        GUIAnimations.removeFromListAndFadeOut(entryBox, entryBoxes);
    }

    private void clearEntryBoxes() {
        entryBoxes.clear();
    }

    public void addEntryBoxChangeListener(ListChangeListener<Node> changeListener) {
        entryBoxes.addListener(changeListener);
    }

    public void executeAll() {
        log.debug("Executing all entries, sequence: {}", sequenceOption.get());
        runLater(() -> {
            if (sequenceOption.get()) {
                CommandSequence commandSequence = entryMapper.buildSequence(getEntries(), executionName.get());
                if (!commandSequence.commandList().isEmpty()) {
                    SequenceExecutionTask sequenceExecutionTask = executionManagement.createSequenceExecutionTask(commandSequence);
                    ExecutionManagementController.executeTask(sequenceExecutionTask);
                }
            } else {
                List<Command> commandList = entryMapper.buildCommands(getEntries());
                if (!commandList.isEmpty()) {
                    List<SingularExecutionTask> task = executionManagement.createExecutionTasks(commandList, executionName.get(), getDelayPerCmd());
                    task.forEach(ExecutionManagementController::executeTask);
                }
            }
        });
    }

    public void load(File fileToLoad) {
        try {
            Execution execution = entryRepository.openFile(fileToLoad);
            entryBoxes.clear();
            for (Entry entry : execution.getEntries()) {
                addEntryBox(entry);
            }
            delay.setValue(execution.getDelay());
            executionName.setValue(execution.getName());
            sequenceOption.setValue(execution.isSequence());
            handleFileOperationOccurred(fileToLoad, false);
        } catch (CouldNotReadFromFileException e) {
            log.error("Error while loading executions file: {}", e.getMessage());
            runLater(() -> new AlertPopup("Error while loading executions file", e.getMessage(), Alert.AlertType.ERROR).show());
        }
    }

    private void handleFileOperationOccurred(File mostRecentFile, boolean saved) {
        RecentFileUtils.saveRecentFile(mostRecentFile.getAbsolutePath());
        if (saved) {
            refreshEdited();
            RecentFileUtils.refreshDirLocation(JSONField.LAST_SAVED, mostRecentFile.getParent());
        } else {
            refreshFileLoaded(mostRecentFile.getAbsolutePath());
            RecentFileUtils.refreshDirLocation(JSONField.LAST_LOADED, mostRecentFile.getParent());
        }
    }

    public void save(File fileToSave) {
        try {
            entryRepository.writeToFile(fileToSave, getExecution());
            RecentFileUtils.saveRecentFile(fileToSave.getAbsolutePath());
            handleFileOperationOccurred(fileToSave, true);
        } catch (CouldNotWriteDataToFileException e) {
            runLater(() -> new AlertPopup(
                    "Error",
                    "Could not save data to file: " + fileToSave.getAbsolutePath() + "\n" + (e.getMessage() != null ? e.getMessage() : ""),
                    "Data:\n" + getExecution(),
                    false)
                    .show());
        }
    }

    public void refreshEdited() {
        log.debug("Reset edited state for all entries");
        entryBoxes.forEach(EntryBox::refreshEdited);
        entryBoxes.forEach(this::handleVariableValidation);
    }

    private void refreshFileLoaded(String path) {
        currentFileAbsolutePath.set(path);
        view.getEntryInfoBar().setFileLoadedText(getFileName(path));
    }

    public static String getFileName(String path) {
        String delimiter = checkIfWindows() ? "\\\\" : "/";
        String[] splitPath = path.split(delimiter);
        return splitPath[splitPath.length - 1];
    }

    private Execution getExecution() {
        return new Execution(getEntries(), getExecutionName(), getDelayPerCmd(), getSequenceOption());
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        for (Node node : view.getEntryListContainer().getChildren()) {
            if (node instanceof EntryBox entryBox) {
                entries.add(entryMapper.entryBoxToEntry(entryBox));
            }
        }
        return entries;
    }

    private int dragSourceIndex = -1;

    private void setupDragging(EntryBox entryBox) {
        entryBox.setOnDragDetected(e -> {
            dragSourceIndex = view.getEntryListContainer().getChildren().indexOf(entryBox);
            Dragboard dragboard = entryBox.startDragAndDrop(TransferMode.MOVE);
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            HashMap<DataFormat, Object> dataFormatStringHashMap = new HashMap<>();
            //placeholder just to allow dragover detection
            dataFormatStringHashMap.put(DataFormat.PLAIN_TEXT, "");
            dragboard.setContent(dataFormatStringHashMap);
            dragboard.setDragView(entryBox.snapshot(snapshotParameters, new WritableImage((int) entryBox.getWidth(), (int) entryBox.getHeight())));
            e.consume();
        });
        entryBox.setOnDragOver(e -> {
            if (dragSourceIndex != -1 && entryBox != view.getEntryListContainer().getChildren().get(dragSourceIndex)) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        entryBox.setOnDragDropped(e -> {
            int dropIndex = view.getEntryListContainer().getChildren().indexOf(entryBox);
            if (dragSourceIndex != -1) {
                var node = view.getEntryListContainer().getChildren().remove(dragSourceIndex);
                view.getEntryListContainer().getChildren().add(dropIndex, node);
            }
            dragSourceIndex = -1;
            e.setDropCompleted(true);
            e.consume();
        });
        entryBox.setOnDragDone(e -> {
            dragSourceIndex = -1;
            e.consume();
        });
    }

    public int getDelayPerCmd() {
        return view.getEntryExecutionOptions().getDelayPerCmdSpinner().getValue();
    }

    public String getExecutionName() {
        return view.getEntryExecutionOptions().getExecutionNameField().getText();
    }

    public boolean getSequenceOption() {
        return view.getEntryExecutionOptions().getSequenceOptionCheckbox().isSelected();
    }

    public EntrySetupTab getView() {
        return view;
    }

    public StringProperty getCurrentFileAbsolutePathProperty() {
        return currentFileAbsolutePath;
    }

    public BooleanProperty optimizationModeProperty() {
        return optimizationMode;
    }
}
