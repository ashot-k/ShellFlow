package org.ashot.shellflow.terminal;

import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import com.techsenger.jeditermfx.ui.settings.SettingsProvider;
import org.ashot.shellflow.node.modal.FontSelectionDialog;
import org.jetbrains.annotations.NotNull;

public class ShellFlowTerminalWidget extends JediTermFxWidget {

    public ShellFlowTerminalWidget(@NotNull SettingsProvider settingsProvider) {
        super(settingsProvider);
        FontSelectionDialog.selectedSize.addListener((_, _, _) -> {
            getTerminalPanel().reinitFontAndResize();
        });
        FontSelectionDialog.selectedFont.addListener((_, _, _) -> {
            getTerminalPanel().reinitFontAndResize();
        });
    }

    public void toggleFind(){
        if(super.isShowingFind()) {
            super.hideFindComponent();
        }else{
            super.showFindComponent();
        }
    }
}
