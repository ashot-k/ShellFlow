package org.ashot.shellflow.node.toolbar;

import atlantafx.base.theme.Styles;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;

public class TerminalToolBar extends FloatingToolBar {
    private final ShellFlowTerminalWidget terminalWidget;

    public TerminalToolBar(ShellFlowTerminalWidget termFxWidget) {
        super();
        this.terminalWidget = termFxWidget;
        Button clearConsoleButton = new Button("", Icons.getClearIcon(DEFAULT_ICON_SIZE.getSize()));
        clearConsoleButton.setOnAction(_ -> terminalWidget.getTerminalPanel().clearBuffer());
        clearConsoleButton.setTooltip(new Tooltip(ToolTipMessages.CLEAR_OUTPUT_BUTTON));
        clearConsoleButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

        Button stopProcessButton = new Button("", Icons.getCloseButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        stopProcessButton.setOnAction(_ -> TerminalRegistry.stopTerminal(terminalWidget.getTtyConnector()));
        stopProcessButton.setTooltip(new Tooltip(ToolTipMessages.STOP_PROCESS_BUTTON));
        stopProcessButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

        Button findButton = new Button("", Icons.getBrowseIcon(DEFAULT_ICON_SIZE.getSize()));
        findButton.setOnAction(_ -> terminalWidget.toggleFind());
        findButton.setTooltip(new Tooltip(ToolTipMessages.FIND_BUTTON));
        findButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

        disableWhenProcessFinishes(stopProcessButton);
        setContent(clearConsoleButton, stopProcessButton, findButton);
        setMaxWidth(170);
        autoHiding();
    }

    private void disableWhenProcessFinishes(Node node) {
        new Thread(() -> {
            try {
                if (terminalWidget.getTtyConnector() != null) {
                    terminalWidget.getTtyConnector().waitFor();
                }
                node.setDisable(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        ).start();
    }

}
