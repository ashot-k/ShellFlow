module org.ashot.microservice_starter {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires java.xml;
    requires org.slf4j;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;


    opens org.ashot.microservice_starter to javafx.fxml;
    exports org.ashot.microservice_starter;
    exports org.ashot.microservice_starter.node.popup;
    opens org.ashot.microservice_starter.node.popup to javafx.fxml;
}