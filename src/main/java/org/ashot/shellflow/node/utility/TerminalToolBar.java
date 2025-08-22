package org.ashot.shellflow.node.utility;

import atlantafx.base.controls.ModalPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.modal.FontSelectionDialog;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;

public class TerminalToolBar extends FloatingToolBar {
    private ShellFlowTerminalWidget terminalWidget;

    public TerminalToolBar(ShellFlowTerminalWidget termFxWidget){
        super();
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

        disableWhenProcessFinishes(stopProcessButton);
        setContent(clearConsoleButton, fontEditButton, stopProcessButton, findButton);
        setMaxWidth(220);
        getStyleClass().addAll( "terminal-toolBar");
        autoHiding();
    }

    private void disableWhenProcessFinishes(Node node){
        new Thread(()->{
            try {
                if(terminalWidget.getTtyConnector() != null) {
                    terminalWidget.getTtyConnector().waitFor();
                }
                node.setDisable(true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ).start();
    }

}
