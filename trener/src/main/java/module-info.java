module com.trener {
    requires javafx.controls;
    requires javafx.fxml;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires json;
    requires java.net.http;
    requires commons.validator;
    requires java.desktop;
    requires com.google.gson;
    requires json.simple;
    requires rest.assured;
    opens com.trener to javafx.fxml;
    exports com.trener;
    exports com.trener.Classes;
    exports com.trener.RequestClasses;
    exports com.trener.ControllersCoach;
    exports com.trener.ControllersAdmin;
    opens com.trener.ControllersCoach to javafx.fxml;
    opens com.trener.ControllersAdmin to javafx.fxml;

}