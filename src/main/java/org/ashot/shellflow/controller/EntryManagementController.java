package org.ashot.shellflow.controller;

import atlantafx.base.theme.Styles;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.execution.Execution;
import org.ashot.shellflow.data.variable.VariableEntry;
import org.ashot.shellflow.exception.app.ActionFailureException;
import org.ashot.shellflow.exception.entry.InvalidEntryException;
import org.ashot.shellflow.exception.io.CouldNotCreateRequiredFile;
import org.ashot.shellflow.exception.io.FileWriteFailureException;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.entry.EntrySetup;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.Notifications;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.peristence.ExecutionRepository;
import org.ashot.shellflow.utils.GUIAnimations;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.ashot.shellflow.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;

public class EntryManagementController {
    private static final Logger LOG = LoggerFactory.getLogger(EntryManagementController.class);
    private final EntrySetup view;
    private final ExecutionRepository entryRepository;
    private final EntryMapper entryMapper;
    private final ObservableList<EntryBox> entryBoxes = FXCollections.observableArrayList();
    private final IntegerProperty delay = new SimpleIntegerProperty();
    private final BooleanProperty sequenceOption = new SimpleBooleanProperty();
    private final StringProperty executionName = new SimpleStringProperty();
    private final BooleanProperty optimizationMode = new SimpleBooleanProperty();
    private final ExecutionManagementController executionManagement;
    private final VariableManagementController variableManagement;

    public EntryManagementController(ExecutionManagementController executionManagement, VariableManagementController variableController) {
        this.view = new EntrySetup(executionManagement.getView());
        this.entryRepository = new ExecutionRepository();
        this.executionManagement = executionManagement;
        this.variableManagement = variableController;
        this.entryMapper = new EntryMapper(variableController.getVariables());

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
        view.getEntryInfoBar().hoverProperty().addListener((_, _, hovering) -> {
            StringProperty currentFileAbsolutePath = RecentFileUtils.getPathToCurrentFile();
            if (!currentFileAbsolutePath.get().isBlank()) {
                view.getEntryInfoBar().setFileLoadedText(hovering ? currentFileAbsolutePath.get() : getFileName(currentFileAbsolutePath.get()));
            }
        });
        view.getFileLoadedText().setOnMouseClicked(this::handleEntryInfoTextClick);
        view.getEntryExecutionOptions().getExpandAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(true)));
        view.getEntryExecutionOptions().getCollapseAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(false)));
        view.getEntryExecutionOptions().getClearAllButton().setOnAction(_ -> clearEntryBoxes());
        view.getEntryExecutionOptions().getExecuteAllButton().setOnAction(_ -> executeAll());
        view.getEntryExecutionOptions().getAddButton().setOnAction(_ -> addEntryBox());
        view.getEntryExecutionOptions().getResetButton().setOnAction(_ -> {
            load(Paths.get(RecentFileUtils.getPathToCurrentFile().get()).toFile());
            Notifications.showNotif("Execution was reset successfully!");
        });
        addEntryBoxChangeListener(_ -> view.getEntryExecutionOptions().getExecuteAllButton().setDisable(entryBoxes.isEmpty()));
        addEntryBoxChangeListener(_ -> view.getEntryExecutionOptions().getClearAllButton().setDisable(entryBoxes.isEmpty()));
    }

    private void handleEntryInfoTextClick(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            ClipboardContent content = new ClipboardContent();
            content.putString(RecentFileUtils.getPathToCurrentFile().get());
            Clipboard.getSystemClipboard().setContent(content);
        }
        if (e.getButton() != MouseButton.PRIMARY || e.getClickCount() != 1 || !e.isStillSincePress()) {
            return;
        }
        File file = Paths.get(RecentFileUtils.getPathToCurrentFile().get()).toFile();
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
        addEntryBox(new Entry());
    }

    public void addEntryBox(Entry entry) {
        LOG.debug("Adding EntryBox with [name: {}, path: {}, command: {}]", entry.name(), entry.path(), entry.command());
        EntryBox entryBox = entryMapper.entryToEntryBox(entry);
        entryBox.setOnDeleteButtonAction(_ -> removeEntryBox(entryBox));
        entryBox.setOnExecuteButtonAction(_ -> {
            GUIAnimations.shakeY(entryBox.getExecuteButton(), 2.5).play();
            try {
                Command command = entryMapper.entryToCommand(entryMapper.entryBoxToEntry(entryBox));
                executionManagement.beginSingular(command);
            } catch (InvalidEntryException e) {
                handleInvalidEntryException(e);
            }
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

    private void showPromptForErrors(String fieldName, EntryBox entryBox, List<String> errors) {
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

    private List<String> validateFieldForVariables(String fieldValue) {
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

    private void addEntryBoxChangeListener(ListChangeListener<Node> changeListener) {
        entryBoxes.addListener(changeListener);
    }

    public void executeAll() {
        LOG.debug("Executing all entries, sequence: {}", sequenceOption.get());
        runLater(() -> {
            if (sequenceOption.get()) {
                executeSequence();
            } else {
                executeParallel();
            }
        });
    }

    private void executeSequence() {
        new Thread(() -> {
            try {
                CommandSequence commandSequence = entryMapper.buildSequence(getEntries(), executionName.get());
                if (!commandSequence.commandList().isEmpty()) {
                    executionManagement.beginSequence(commandSequence);
                }
            } catch (InvalidEntryException e) {
                handleInvalidEntryException(e);
            }
        }).start();
    }

    private void executeParallel() {
        try {
            List<Command> commandList = entryMapper.buildCommands(getEntries());
            if (!commandList.isEmpty()) {
                executionManagement.beginParallel(commandList, executionName.get(), getDelayPerCmd());
            }
        } catch (InvalidEntryException e) {
            handleInvalidEntryException(e);
        }
    }

    private void handleInvalidEntryException(InvalidEntryException invalidEntryException) {
        Entry entry = invalidEntryException.getEntry();
        if (invalidEntryException.getCause() != null) {
            Throwable cause = invalidEntryException.getCause();
            LOG.error("Invalid entry: name [{}], command [{}],  path [{}], exception [{}]: {}", entry.name(), entry.command(), entry.path(), cause.getClass().getSimpleName(), cause.getMessage());
        } else {
            LOG.error("Invalid entry: name [{}], command [{}],  path [{}], exception : {}", entry.name(), entry.command(), entry.path(), invalidEntryException.getMessage());
        }
    }

    public void load(File fileToLoad) {
        try {
            Execution execution = entryRepository.openFromFile(fileToLoad);
            entryBoxes.clear();
            for (Entry entry : execution.entries()) {
                addEntryBox(entry);
            }
            delay.setValue(execution.delay());
            executionName.setValue(execution.executionName());
            sequenceOption.setValue(execution.sequence());
            handleFileOperationOccurred(fileToLoad);
        } catch (CouldNotCreateRequiredFile e) {
            LOG.error("Error while loading executions file: {}", e.getMessage());
            runLater(() -> new AlertPopup("Error while loading executions file", e.getMessage(), false).show());
        }
    }

    private void handleFileOperationOccurred(File mostRecentFile) {
        try {
            RecentFileUtils.saveRecentFile(mostRecentFile.getAbsolutePath());
            RecentFileUtils.refreshLastAccessedDirectory(mostRecentFile.getParent());
            refreshFileLoaded(mostRecentFile.getAbsolutePath());
        } catch (FileWriteFailureException e) {
            LOG.error("Could not refresh recents: {}", e.getMessage());
        }
        refreshEdited();
    }

    public void save(File fileToSave) {
        try {
            entryRepository.saveToFile(fileToSave, getExecution());
            handleFileOperationOccurred(fileToSave);
            Notifications.showNotif("Saved execution " + fileToSave.getName());
        } catch (CouldNotCreateRequiredFile e) {
            StringBuilder exceptionMsgBuilder = new StringBuilder();
            exceptionMsgBuilder.append("Could not save data to file: \"").append(fileToSave.getAbsolutePath()).append("\"");
            if (e.getMessage() != null) {
                exceptionMsgBuilder.append(", ");
                if (e.getMessage().isBlank()) {
                    Throwable cause = e.getCause();
                    exceptionMsgBuilder.append(cause.getClass().getSimpleName());
                    if (!cause.getMessage().isBlank()) {
                        exceptionMsgBuilder.append(": ").append(cause.getMessage());
                    }
                } else {
                    exceptionMsgBuilder.append(e.getMessage());
                }
            }
            String exceptionMsg = exceptionMsgBuilder.toString();
            runLater(() -> new AlertPopup("Error", exceptionMsg, "Data:\n" + getExecution(), false).show());
            throw new ActionFailureException(exceptionMsg, e.getCause());
        }
    }

    private void refreshEdited() {
        LOG.debug("Reset edited state for all entries");
        entryBoxes.forEach(EntryBox::refreshEdited);
        entryBoxes.forEach(this::handleVariableValidation);
    }

    private void refreshFileLoaded(String path) {
        RecentFileUtils.getPathToCurrentFile().set(path);
        view.getEntryInfoBar().setFileLoadedText(getFileName(path));
    }

    private static String getFileName(String path) {
        String delimiter = Utils.checkIfWindows() ? "\\\\" : "/";
        String[] splitPath = path.split(delimiter);
        return splitPath[splitPath.length - 1];
    }

    private Execution getExecution() {
        return new Execution(getEntries(), getExecutionName(), getDelayPerCmd(), getSequenceOption());
    }

    private List<Entry> getEntries() {
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

    public EntrySetup getView() {
        return view;
    }

    public BooleanProperty optimizationModeProperty() {
        return optimizationMode;
    }
}
