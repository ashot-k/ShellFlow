package org.ashot.shellflow.node.toolbar;

import atlantafx.base.theme.Styles;
import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.TtyConnector;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;

public class TerminalToolBar extends FloatingToolBar {
    private final Button findButton;
    private final Button clearConsoleButton;
    private final Button stopProcessButton;
    private final Button restartButton;

    public TerminalToolBar() {
        super();
        clearConsoleButton = new Button("", Icons.getClearIcon(DEFAULT_ICON_SIZE.getSize()));
        clearConsoleButton.setTooltip(new Tooltip(ToolTipMessages.CLEAR_OUTPUT_BUTTON));
        clearConsoleButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        stopProcessButton = new Button("", Icons.getCloseButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        stopProcessButton.setTooltip(new Tooltip(ToolTipMessages.STOP_PROCESS_BUTTON));
        stopProcessButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        findButton = new Button("", Icons.getBrowseIcon(DEFAULT_ICON_SIZE.getSize()));
        findButton.setTooltip(new Tooltip(ToolTipMessages.FIND_BUTTON));
        findButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        restartButton = new Button("", Icons.getResetIcon(DEFAULT_ICON_SIZE.getSize()));
        restartButton.setTooltip(new Tooltip(ToolTipMessages.RESTART_PROCESS_BUTTON));
        restartButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.FLAT);

        setContent(restartButton, findButton, stopProcessButton, clearConsoleButton);
        setMaxWidth(185);
        autoHiding();
    }

    public void setTerminalWidget(ShellFlowTerminalWidget terminalWidget) {
        // todo refactor
        clearConsoleButton.setOnAction(_ -> terminalWidget.getTerminalPanel().clearBuffer());
        stopProcessButton.setOnAction(_ -> TerminalRegistry.stopTerminal((PtyProcess) ((PtyProcessTtyConnector) terminalWidget.getTtyConnector()).getProcess()));
        findButton.setOnAction(_ -> terminalWidget.toggleFind());
        TtyConnector ttyConnector = terminalWidget.getTtyConnector();
        if (ttyConnector instanceof PtyProcessTtyConnector ptyProcessTtyConnector) {
            ptyProcessTtyConnector.getProcess().onExit().thenRun(() -> stopProcessButton.setDisable(true));
        }
        stopProcessButton.setDisable(false);
    }

    public Button getFindButton() {
        return findButton;
    }

    public Button getClearConsoleButton() {
        return clearConsoleButton;
    }

    public Button getStopProcessButton() {
        return stopProcessButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }
}
