package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.controls.Popover;
import com.pty4j.PtyProcess;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.execution.container.TerminalContainer;
import org.ashot.shellflow.execution.manager.TerminalSession;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.SystemTray;
import org.ashot.shellflow.node.toolbar.ExecutionTabPopover;
import org.ashot.shellflow.node.toolbar.TerminalToolBar;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;
import org.ashot.shellflow.utils.GUIAnimations;
import org.ashot.shellflow.utils.ThemeHandler;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;

import static org.ashot.shellflow.data.constant.ExecutionState.*;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;

public class SingleExecutionTab extends ExecutionTab implements TerminalContainer {
    private static final Logger log = LoggerFactory.getLogger(SingleExecutionTab.class);

    private ShellFlowTerminalWidget terminal;
    private final VBox terminalWrapper = new VBox();
    private final StackPane stackPane = new StackPane();

    private final SimpleObjectProperty<ExecutionState> state = new SimpleObjectProperty<>();
    private final boolean sequence;

    public SingleExecutionTab() {
        this.sequence = false;
    }

    public SingleExecutionTab(boolean sequence) {
        super();
        this.sequence = sequence;
    }

    public void setupTerminalWidget(TerminalSession terminalSession) {
        terminal = terminalSession.terminalWidget();
        TerminalToolBar terminalToolBar = terminal.getTerminalToolBar();
        terminalWrapper.getChildren().setAll(terminal.getPane());
        terminalWrapper.setFillWidth(true);
        terminalWrapper.setPadding(new Insets(5));
        terminalWrapper.getStyleClass().addAll(ThemeHandler.getSelectedTheme().isDark() ? "dark" : "light", "terminal-wrapper");

        VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
        StackPane.setAlignment(terminalToolBar, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(terminalToolBar, new Insets(0, 25, 15, 0));

        stackPane.getChildren().setAll(terminalWrapper, terminalToolBar);
        setContent(stackPane);
    }

    @Override
    public void attachSession(TerminalSession session) {
        setupTerminalWidget(session);
    }

    @Override
    public ShellFlowTerminalWidget getTerminal() {
        return terminal;
    }

    public void updateState(ExecutionState newState) {
        log.info("Execution: {} [{}], state: {}", getText(), getTooltip().getText(), newState);
        switch (newState) {
            case INITIALIZING -> setInitializing();
            case IN_PROGRESS -> setInProgress();
            case INTERNAL_FAILURE, FAILURE -> setFailed();
            case FINISHED -> setFinished();
            case CANCELLED -> setCancelled();
        }
    }

    private Node createTabGraphic(Glyph icon, Node... content) {
        Popover popoverToolbar = new ExecutionTabPopover();
        HBox popoverContent = new HBox(10);

        Hyperlink toolbarLink = new Hyperlink("", Icons.getExtrasIcon(TAB_ICON_SIZE));
        toolbarLink.setOnAction(_ -> popoverToolbar.show(toolbarLink));
        HBox toolBarButtons = new HBox(5);

        if (!sequence) {
            toolBarButtons.getChildren().addAll(restartButton, renameField);
            popoverContent.getChildren().addAll(toolbarLink);
        }

        toolBarButtons.getChildren().addAll(content);
        toolBarButtons.setAlignment(Pos.CENTER);
        popoverToolbar.setContentNode(toolBarButtons);
        popoverContent.setPadding(new Insets(1));
        popoverContent.getChildren().add(icon);
        return popoverContent;
    }

    public void setInitializing() {
        Glyph icon = Icons.getExecutionInitializingIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        state.setValue(INITIALIZING);
    }

    public void setInitializing(long delay) {
        LocalTime end = LocalTime.now().plusSeconds(delay / 1000);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (Duration.between(LocalTime.now(), end).isNegative()) {
                    stop();
                }
                long seconds = Duration.between(LocalTime.now(), end).getSeconds();
                Text text = new Text(String.valueOf(seconds > 0 ? seconds : ""));
                Platform.runLater(() -> setGraphic(text));
            }
        };
        timer.start();
        state.setValue(INITIALIZING);
    }

    public void setInProgress() {
        Glyph icon = Icons.getExecutionInProgressIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setDisable(false);
        setClosable(!sequence);
        state.setValue(IN_PROGRESS);
    }

    public void setFailed() {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setDisable(false);
        setClosable(!sequence);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(FAILURE);
        SystemTray.displayNotification(FAILURE.getValue(), failNotificationMessage(getText(), getProcessExitValue()), NotificationType.EXECUTION_FAILURE);
    }

    public void setFinished() {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setClosable(!sequence);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(FINISHED);
        SystemTray.displayNotification(FINISHED.getValue(), finishedNotificationMessage(getText()), NotificationType.SUCCESS);
    }

    public void setCancelled() {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setDisable(false);
        setClosable(!sequence);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(CANCELLED);
    }

    public void setOnClose(EventHandler<Event> event) {
        this.setOnCloseRequest(closeEvent -> {
            if (event != null) {
                event.handle(closeEvent);
                return;
            }
            if (!closeEvent.isConsumed()) {
                this.terminal.close();
            }
        });
    }

    @Override
    public void triggerErrorMode(String errorMessage) {
        Text errorText = new Text(errorMessage);
        errorText.setTextAlignment(TextAlignment.CENTER);
        HBox hbox = new HBox(errorText);
        hbox.setAlignment(Pos.CENTER);
        hbox.setFillHeight(true);
        hbox.setPadding(new Insets(5));
        setContent(hbox);

        getTabPane().widthProperty().addListener((_, _, newValue) -> errorText.setWrappingWidth(newValue.doubleValue() - 50));
        setFailed();
    }

    @Override
    public SimpleObjectProperty<ExecutionState> stateProperty() {
        return state;
    }

    public boolean isInProgress() {
        return state.get().equals(IN_PROGRESS);
    }

    public boolean isFinished() {
        return state.get().equals(FINISHED);
    }

    public boolean isCanceled() {
        return state.get().equals(CANCELLED);
    }

    public boolean isFailed() {
        return state.get().equals(FAILURE);
    }

    public PtyProcess getProcess() {
        return (PtyProcess) ((PtyProcessTtyConnector) terminal.getTtyConnector()).getProcess();
    }

    public void setRestartHandler(EventHandler<ActionEvent> restartHandler) {
        restartButton.setOnAction(restartHandler);
        terminal.getTerminalToolBar().getRestartButton().setOnAction(restartHandler);
    }

    private int getProcessExitValue() {
        if (terminal != null && terminal.getTtyConnector() != null && terminal.getTtyConnector() instanceof PtyProcessTtyConnector processTtyConnector) {
            return processTtyConnector.getProcess().exitValue();
        }
        return -999;
    }
}
