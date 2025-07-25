module org.ashot.shellflow {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;
    requires com.techsenger.jeditermfx.ui;
    requires com.techsenger.jeditermfx.core;
    requires org.jetbrains.annotations;
    requires pty4j;
    requires java.desktop;


    exports org.ashot.shellflow;
    exports org.ashot.shellflow.node.popup;
    opens org.ashot.shellflow to javafx.fxml;
    opens org.ashot.shellflow.node.popup to javafx.fxml;
    exports org.ashot.shellflow.registry;
    opens org.ashot.shellflow.registry to javafx.fxml;
    exports org.ashot.shellflow.utils;
    opens org.ashot.shellflow.data.constant to javafx.base;
    opens org.ashot.shellflow.node.icon to javafx.base;
    exports org.ashot.shellflow.data;
    opens org.ashot.shellflow.data.message to javafx.fxml;
    opens org.ashot.shellflow.node.tab.profiler to javafx.base;
    opens org.ashot.shellflow.node.tab.executions to javafx.base;
    opens org.ashot.shellflow.node.tab.preset to javafx.base;
    opens org.ashot.shellflow.utils to javafx.base, javafx.fxml;
}