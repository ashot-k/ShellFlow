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
        terminalToolBar = new TerminalToolBar(this);
    }

    public void toggleFind() {
        if (super.isShowingFind()) {
            super.hideFindComponent();
        } else {
            super.showFindComponent();
        }
    }

    public TerminalToolBar getTerminalToolBar() {
        return terminalToolBar;
    }
}
