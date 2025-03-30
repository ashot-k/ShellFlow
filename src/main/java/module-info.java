module org.ashot.microservice_starter {
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.json;
    requires java.xml;


    opens org.ashot.microservice_starter to javafx.fxml;
    exports org.ashot.microservice_starter;
    exports org.ashot.microservice_starter.popup;
    opens org.ashot.microservice_starter.popup to javafx.fxml;
}