module org.ashot.microservice_starter {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires org.slf4j;
    requires org.controlsfx.controls;


    opens org.ashot.microservice_starter to javafx.fxml;
    exports org.ashot.microservice_starter;
    exports org.ashot.microservice_starter.node.popup;
    opens org.ashot.microservice_starter.node.popup to javafx.fxml;
}