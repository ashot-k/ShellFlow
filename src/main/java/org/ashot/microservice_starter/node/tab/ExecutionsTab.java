package org.ashot.microservice_starter.node.tab;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ExecutionsTab extends Tab {
    TabPane executionsTabPane;
    String name = "Executions";

    public ExecutionsTab(){
        Platform.runLater(()->{
            this.setClosable(false);
            this.executionsTabPane = new TabPane();
            this.setContent(executionsTabPane);
            this.setText(name);
            this.getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) change -> {
                if(this.executionsTabPane.getTabs().isEmpty()){
                    this.getTabPane().getSelectionModel().selectFirst();
                    this.setDisable(true);
                }
                else{
                    this.setDisable(false);
                }
            });
            this.setDisable(true);
        });
    }

    public TabPane getExecutionsTabPane() {
        return executionsTabPane;
    }
}
