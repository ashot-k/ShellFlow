package org.ashot.shellflow.node.menu.settings.menuitem;

import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.ConfigProperty;

import static org.ashot.shellflow.Main.getConfig;
import static org.ashot.shellflow.utils.Animator.*;

public class PerformanceSettingMenuItem extends MenuItem {

    public PerformanceSettingMenuItem(){
        CheckBox checkBox = new CheckBox();
        setText("Optimized Animations");
        setGraphic(checkBox);
        this.setOnAction(_ ->{
            checkBox.setSelected(!checkBox.isSelected());
            Main.getConfig().saveProperty(ConfigProperty.OPTIMIZED_MODE, String.valueOf(checkBox.isSelected()));
        });
        checkBox.setMouseTransparent(true);
        checkBox.selectedProperty().addListener((_, _, newValue) -> {
            if(newValue){
                setFrameRateForSpin(PERFORMANCE_OPTIMIZATION_FRAME_RATE);
            }else {
                setFrameRateForSpin(DEFAULT_FRAME_RATE);
            }
        });
        checkBox.setSelected(getConfig().getOptimizedMode());
    }
}
