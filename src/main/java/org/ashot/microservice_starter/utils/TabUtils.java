package org.ashot.microservice_starter.utils;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.tab.ExecutionsTab;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;

public class TabUtils {

    private static final int titleIconsSize = 18;

    public static void setInProgress(Tab tab){
        Platform.runLater(()->{
            tab.setGraphic(Icons.getExecutionInProgressIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if(tab instanceof OutputTab outputTab){
            outputTab.setInProgress(true);
        }
    }

    public static void setFailed(Tab tab){
        Platform.runLater(()->{
            tab.setGraphic(Icons.getExecutionErrorIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if(tab instanceof OutputTab outputTab){
             outputTab.setInProgress(false);
             outputTab.setFailed(true);
        }
    }

    public static void setFinished(Tab tab){
        Platform.runLater(()->{
            tab.setGraphic(Icons.getExecutionFinishedIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if(tab instanceof OutputTab outputTab){
            outputTab.setInProgress(false);
            outputTab.setFinished(true);
        }
    }

    public static void setCanceled(Tab tab){
        Platform.runLater(()->{
            tab.setGraphic(Icons.getExecutionCanceledIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if(tab instanceof OutputTab outputTab){
            outputTab.setInProgress(false);
            outputTab.setCanceled(true);
        }
    }

    public static void addToExecutions(Tab tab){
        Controller controller = ControllerRegistry.get("main", Controller.class);
        Platform.runLater(()->{
            ExecutionsTab executionsTab = controller.getExecutionsTab();
            executionsTab.getExecutionsTabPane().getTabs().add(tab);
            executionsTab.getExecutionsTabPane().getSelectionModel().select(tab);
        });
    }

}
