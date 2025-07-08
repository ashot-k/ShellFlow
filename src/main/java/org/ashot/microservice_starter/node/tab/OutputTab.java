package org.ashot.microservice_starter.node.tab;

import com.techsenger.jeditermfx.ui.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.terminal.PtyProcessTtyConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OutputTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(OutputTab.class);
    private String commandDisplayName;
    private JediTermFxWidget terminal;

    private OutputTab(OutputTabBuilder outputTabBuilder){
        this.commandDisplayName = outputTabBuilder.commandDisplayName;
        this.terminal = outputTabBuilder.terminal;
        this.setTooltip(outputTabBuilder.tooltip);
        this.setText(outputTabBuilder.tabName);
        Platform.runLater(this::setupOutputTab);
    }

    public void setupOutputTab() {
        VBox container = new VBox(terminal.getPane());
        container.setFillWidth(true);
        container.setPadding(new Insets(5));
        VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
        this.setContent(container);
        this.setClosable(true);
        this.setOnClosed(_ -> this.terminal.close());
        this.getTerminal().addListener(new TerminalWidgetListener() {
            @Override
            public void allSessionsClosed(TerminalWidget widget) {
                System.out.println("closed");

            }
        });
    }



    public void appendTooltipLineText(String tooltipText){
        this.getTooltip().setText(this.getTooltip().getText() + "\n" + tooltipText);
    }

    public static List<OutputTab> getOutputTabsFromTabPane(TabPane tabPane){
        return tabPane.getTabs().stream().filter(e -> e instanceof OutputTab).map(o -> (OutputTab) o).toList();
    }

    public void setCommandDisplayName(String commandDisplayName) {
        this.commandDisplayName = commandDisplayName;
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public void closeTerminal(){
        this.terminal.close();
    }

    public JediTermFxWidget getTerminal() {
        return terminal;
    }

    public void setTerminal(JediTermFxWidget terminal) {
        this.terminal = terminal;
    }

    public static class OutputTabBuilder{

        private JediTermFxWidget terminal;

        private String tabName;
        private final Tooltip tooltip = new Tooltip();
        private String commandDisplayName;

        public OutputTabBuilder(JediTermFxWidget terminal){
            this.terminal = terminal;
        }

        public OutputTabBuilder setTabName(String tabName){
            this.tabName = tabName;
            return this;
        }

        public OutputTabBuilder setCommandDisplayName(String commandDisplayName){
            this.commandDisplayName = commandDisplayName;
            return this;
        }

        public OutputTabBuilder setTooltip(String tooltipText){
            this.tooltip.setText(tooltipText);
            return this;
        }

        public OutputTab build(){
            return new OutputTab(this);
        }
    }
}
