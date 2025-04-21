module org.ashot.microservice_starter {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires java.desktop;


    exports org.ashot.microservice_starter;
    exports org.ashot.microservice_starter.node.popup;
    opens org.ashot.microservice_starter to javafx.fxml;
    opens org.ashot.microservice_starter.node.setup to javafx.base;
    opens org.ashot.microservice_starter.node.popup to javafx.fxml;
}