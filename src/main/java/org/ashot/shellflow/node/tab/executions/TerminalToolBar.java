package org.ashot.shellflow.node.tab.executions;

import atlantafx.base.controls.ModalPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.modal.FontSelectionDialog;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;

public class TerminalToolBar extends VBox {
    private ShellFlowTerminalWidget terminalWidget;

    public TerminalToolBar(ShellFlowTerminalWidget termFxWidget){
        this.terminalWidget = termFxWidget;

        Button clearConsoleButton = new Button("", Icons.getClearIcon(DEFAULT_BUTTON_ICON_SIZE));
        clearConsoleButton.setOnAction(_-> terminalWidget.getTerminalPanel().clearBuffer());
        clearConsoleButton.setTooltip(new Tooltip(ToolTipMessages.clearOutput()));

        Button stopProcessButton = new Button("", Icons.getCloseButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        stopProcessButton.setOnAction(_-> TerminalRegistry.stopTerminal(terminalWidget.getTtyConnector()));
        stopProcessButton.setTooltip(new Tooltip(ToolTipMessages.stopProcess()));

        Button findButton = new Button("", Icons.getBrowseIcon(DEFAULT_BUTTON_ICON_SIZE));
        findButton.setOnAction(_-> terminalWidget.toggleFind());
        findButton.setTooltip(new Tooltip(ToolTipMessages.find()));

        Button fontEditButton = new Button("", Icons.getFontSelectionMenuIcon(DEFAULT_BUTTON_ICON_SIZE));
        fontEditButton.setOnAction(_->{
            ModalPane modal = ControllerRegistry.getMainController().getMainModal();
            ControllerRegistry.getMainController().getMainModal().show(new FontSelectionDialog(()-> modal.hide(true)));
        });
        new Thread(()->{
            try {
                if(terminalWidget.getTtyConnector() != null) {
                    terminalWidget.getTtyConnector().waitFor();
                }
                stopProcessButton.setDisable(true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ).start();
        fontEditButton.setTooltip(new Tooltip(ToolTipMessages.font()));

        double initialOpacity = 0.45;
        hoverProperty().addListener((_, _, hovering) -> setOpacity(hovering ? 0.95 : initialOpacity));
        setOpacity(initialOpacity);
        setMaxHeight(50);
        setMaxWidth(250);
        setFillWidth(false);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        HBox buttonsBar = new HBox(clearConsoleButton, fontEditButton, findButton, stopProcessButton);
        buttonsBar.setAlignment(Pos.CENTER);
        buttonsBar.setSpacing(10);
        setPadding(new Insets(10, 8, 10 ,8));
        getChildren().addAll(buttonsBar);
        getStyleClass().addAll( "terminal-toolBar");
    }

}
