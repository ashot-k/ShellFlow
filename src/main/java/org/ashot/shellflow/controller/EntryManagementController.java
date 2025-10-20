package org.ashot.shellflow.controller;

import atlantafx.base.theme.Styles;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.entry.Execution;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.tab.setup.EntrySetupTab;
import org.ashot.shellflow.node.toolbar.EntrySetupToolBar;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.peristence.ExecutionRepository;
import org.ashot.shellflow.utils.Animations;
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
    private final ExecutionRepository repository;
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

    public EntryManagementController(ExecutionManagementController executionManagement, VariableManagementController variableController, EntryMapper entryMapper, File init) {
        this.view = new EntrySetupTab();
        this.repository = new ExecutionRepository();
        this.executionManagement = executionManagement;
        this.variableManagement = variableController;
        this.entryMapper = entryMapper;
        this.toolBar = this.view.getEntrySetupToolBar();
        view.setSidePanel(variableController.getView(), Pos.CENTER_LEFT);
        setupEvents();
        load(init);
    }

    private void setupEvents() {
        view.getDelayPerCmdSpinner().valueProperty().addListener((_, _, newVal) -> delay.set(newVal));
        delay.addListener((_, _, newVal) -> view.getDelayPerCmdSpinner().getValueFactory().setValue(newVal.intValue()));
        sequenceOption.bindBidirectional(view.getSequenceOptionCheckBox().selectedProperty());
        executionName.bindBidirectional(view.getExecutionNameField().textProperty());
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
                view.getFileLoadedText().setText(hovering ? currentFileAbsolutePath.get() : getFileName(currentFileAbsolutePath.get()))
        );
        view.getFileLoadedText().setOnMouseClicked(this::handleEntryInfoTextClick);
        toolBar.getExpandAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(true)));
        toolBar.getCollapseAllButton().setOnAction(_ -> entryBoxes.forEach(e -> e.setExpanded(false)));
        toolBar.getClearAllEntriesButton().setOnAction(_ -> clearEntryBoxes());
        toolBar.getExecuteAllButton().setOnAction(_ -> executeAll());
        toolBar.getAddEntryButton().setOnAction(_ -> addEntryBox());
        addEntryBoxChangeListener(_ -> toolBar.getExecuteAllButton().setDisable(entryBoxes.isEmpty()));
        addEntryBoxChangeListener(_ -> toolBar.getClearAllEntriesButton().setDisable(entryBoxes.isEmpty()));
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
                new AlertPopup("Could not open file", null, "File could not be opened: " + file.getAbsolutePath() + "\n" + ex.getMessage(), false).show();
            }
        } else {
            new AlertPopup("Could not open file", null, "File does not exist: " + file.getAbsolutePath(), false).show();
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
        entryBox.setOnExecuteButtonAction(_ -> {
            atlantafx.base.util.Animations.shakeY(entryBox.getExecuteButton(), 1.5).play();
            Command command = entryMapper.entryToCommand(entryMapper.entryBoxToEntry(entryBox), false);
            SingularExecutionTask singularExecutionTask = executionManagement.createExecutionTask(command);
            ExecutionManagementController.executeTask(singularExecutionTask);
        });
        setupDragging(entryBox);
        Animations.fadeInBeforeAdditionToList(entryBox);
        entryBox.animatedProperty().bind(optimizationMode.not());
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
        for (String error : errors) {
            stringBuilder.append(error).append(" ");
        }
        entryBox.showPromptMessageToField(null, stringBuilder.toString(), Styles.DANGER, Icons.getErrorIcon(IconSizeDefaults.ENTRY_VALIDATION_MESSAGE_ICON.getSize()));
    }

    public List<String> validateFieldForVariables(String fieldValue) {
        List<String> errors = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
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
        Animations.removeFromListAndFadeOut(entryBox, entryBoxes);
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
        Execution execution = repository.openFile(fileToLoad);
        entryBoxes.clear();
        for (Entry entry : execution.getEntries()) {
            addEntryBox(entry);
        }
        delay.setValue(execution.getDelay());
        executionName.setValue(execution.getName());
        sequenceOption.setValue(execution.isSequence());
        RecentFileUtils.saveRecentFile(fileToLoad.getAbsolutePath());
        RecentFileUtils.refreshDirLocation(DirType.LAST_LOADED, fileToLoad.getParent());
        refreshFileLoaded(fileToLoad.getAbsolutePath());
    }

    public void save(File file) {
        repository.writeToFile(file, getExecution());
        refreshFileLoaded(file.getAbsolutePath());
        RecentFileUtils.saveRecentFile(file.getAbsolutePath());
        RecentFileUtils.refreshDirLocation(DirType.LAST_SAVED, file.getParent());
        refreshEdited();
    }

    public void refreshEdited() {
        log.debug("Reset edited state for all entries");
        entryBoxes.forEach(EntryBox::refreshEdited);
        entryBoxes.forEach(this::handleVariableValidation);
    }

    private void refreshFileLoaded(String path) {
        currentFileAbsolutePath.set(path);
        view.getFileLoadedText().setText(getFileName(path));
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
        return view.getDelayPerCmdSpinner().getValue();
    }

    public String getExecutionName() {
        return view.getExecutionNameField().getText();
    }

    public boolean getSequenceOption() {
        return view.getSequenceOptionCheckBox().isSelected();
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
