module org.ashot.microservice_starter {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.ashot.microservice_starter to javafx.fxml;
    exports org.ashot.microservice_starter;
}