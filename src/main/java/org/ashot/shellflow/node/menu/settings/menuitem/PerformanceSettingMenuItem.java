package org.ashot.shellflow.node.menu.settings.menuitem;

import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;

import static org.ashot.shellflow.utils.Animator.*;

public class PerformanceSettingMenuItem extends MenuItem {

    public PerformanceSettingMenuItem(){
        CheckBox checkBox = new CheckBox();
        setText("Optimized Animations");
        setGraphic(checkBox);
        this.setOnAction(_ ->{
            checkBox.setSelected(!checkBox.isSelected());
        });
        checkBox.setMouseTransparent(true);
        checkBox.selectedProperty().addListener((_, _, newValue) -> {
            if(newValue){
                setFrameRate(PERFORMANCE_OPTIMIZATION_FRAME_RATE);
            }else {
                setFrameRate(DEFAULT_FRAME_RATE);
            }
        });
    }
}
