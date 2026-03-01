package org.ashot.shellflow.node.variable;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.variable.VariableEntry;
import org.ashot.shellflow.node.icon.Icons;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.tableview2.cell.TextField2TableCell;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;


public class VariableSetup extends VBox {
    private final VBox container;
    private final TableView2<VariableEntry> variablesTable;
    private final Button addVariableButton;
    private final Button saveAllButton;
    private final Button resetButton;
    private final Button deleteRowButton;

    public VariableSetup() {
        TableColumn2<VariableEntry, Boolean> enabledColumn = new TableColumn2<>("Enabled");
        enabledColumn.setSortable(false);
        enabledColumn.setCellValueFactory(c -> c.getValue().isEnabledProperty());
        enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));
        enabledColumn.setOnEditCommit(commit -> commit.getRowValue().setEnabled(commit.getNewValue()));
        enabledColumn.setResizable(false);
        enabledColumn.setEditable(true);

        variablesTable = new TableView2<>();

        TableColumn2<VariableEntry, String> nameColumn = new TableColumn2<>("Name");
        nameColumn.setCellFactory(TextField2TableCell.forTableColumn());
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nameColumn.setOnEditCommit(commit -> commit.getRowValue().setName(commit.getNewValue()));

        TableColumn2<VariableEntry, String> valueColumn = new TableColumn2<>("Value");
        valueColumn.setCellFactory(TextField2TableCell.forTableColumn());
        valueColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getValue()));
        valueColumn.setOnEditCommit(commit -> commit.getRowValue().setValue(commit.getNewValue()));

        variablesTable.setPadding(new Insets(2.5, 5, 2.5, 5));
        variablesTable.getColumns().setAll(enabledColumn, nameColumn, valueColumn);
        variablesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        variablesTable.getSelectionModel().selectFirst();
        variablesTable.setMaxWidth(Double.MAX_VALUE);
        variablesTable.setEditable(true);

        Styles.toggleStyleClass(variablesTable, Styles.STRIPED);
        Styles.toggleStyleClass(variablesTable, Styles.TEXT_SMALL);
        Styles.toggleStyleClass(variablesTable, Styles.DENSE);
        Styles.toggleStyleClass(variablesTable, Tweaks.EDGE_TO_EDGE);

        container = new VBox(10, variablesTable);
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        addVariableButton = new Button("", Icons.getAddButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        addVariableButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        saveAllButton = new Button("", Icons.getSaveIcon(DEFAULT_ICON_SIZE.getSize()));
        saveAllButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        resetButton = new Button("", Icons.getResetIcon(DEFAULT_ICON_SIZE.getSize()));
        resetButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        deleteRowButton = new Button("", Icons.getCloseButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        deleteRowButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);
        deleteRowButton.setDisable(true);

        HBox variableOptions = new HBox(5, deleteRowButton, resetButton, saveAllButton, addVariableButton);
        variableOptions.setAlignment(Pos.TOP_RIGHT);
        variableOptions.setPadding(new Insets(2.5, 5, 5, 5));

        getChildren().addAll(scrollPane, new Spacer(Orientation.HORIZONTAL), variableOptions);
        setSpacing(2.5);
        setAlignment(Pos.BOTTOM_CENTER);
    }

    public Button getAddVariableButton() {
        return addVariableButton;
    }

    public Button getSaveAllButton() {
        return saveAllButton;
    }

    public TableView<VariableEntry> getVariablesTable() {
        return variablesTable;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getDeleteRowButton() {
        return deleteRowButton;
    }
}

