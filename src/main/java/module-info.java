module org.ashot.microservice_starter {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;


    exports org.ashot.microservice_starter;
    exports org.ashot.microservice_starter.node.popup;
    opens org.ashot.microservice_starter to javafx.fxml;
    opens org.ashot.microservice_starter.node.popup to javafx.fxml;
    exports org.ashot.microservice_starter.registry;
    opens org.ashot.microservice_starter.registry to javafx.fxml;
    exports org.ashot.microservice_starter.utils;
    opens org.ashot.microservice_starter.utils to javafx.fxml;
    opens org.ashot.microservice_starter.data.constant to javafx.base;
    opens org.ashot.microservice_starter.node.tabs to javafx.base;
    opens org.ashot.microservice_starter.data to javafx.base;
    opens org.ashot.microservice_starter.data.icon to javafx.base;
}