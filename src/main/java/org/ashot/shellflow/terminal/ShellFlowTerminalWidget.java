package org.ashot.shellflow.terminal;

import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import com.techsenger.jeditermfx.ui.settings.SettingsProvider;
import org.ashot.shellflow.node.modal.FontSelectionDialog;
import org.ashot.shellflow.node.toolbar.TerminalToolBar;
import org.jetbrains.annotations.NotNull;

public class ShellFlowTerminalWidget extends JediTermFxWidget {
    private final TerminalToolBar terminalToolBar;

    public ShellFlowTerminalWidget(@NotNull SettingsProvider settingsProvider) {
        super(settingsProvider);
        FontSelectionDialog.selectedSize.addListener((_, _, _) -> getTerminalPanel().reinitFontAndResize());
        FontSelectionDialog.selectedFont.addListener((_, _, _) -> getTerminalPanel().reinitFontAndResize());
        this.terminalToolBar = new TerminalToolBar();
    }

    @Override
    public void start() {
        super.start();
        terminalToolBar.setTerminalWidget(this);
    }

    public void toggleFind() {
        if (isShowingFind()) {
            hideFindComponent();
        } else {
            showFindComponent();
        }
    }

    public TerminalToolBar getTerminalToolBar() {
        return terminalToolBar;
    }
}
