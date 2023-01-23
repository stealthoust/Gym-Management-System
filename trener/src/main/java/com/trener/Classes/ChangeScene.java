package com.trener.Classes;

import com.trener.RequestClasses.Account;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;

public class ChangeScene {
    public static void zmienSceneCoachButton(String fxml, int x, int y, Button button, String title) throws Exception {
        Parent root = FXMLLoader.load(ChangeScene.class.getResource("/com/trener/CoachFXML/" + fxml));
        Stage window = (Stage) button.getScene().getWindow();
        window.setTitle(title);
        Scene scene = new Scene(root, x, y);
        scene.getStylesheets().add((new File("src/main/resources/com/trener/style.css")).toURI().toURL().toExternalForm());
        window.setScene(scene);
        window.setResizable(false);
    }

    public static void zmienSceneCoachLabel(String fxml, int x, int y, Label label, String title) throws Exception {
        Parent root = FXMLLoader.load(ChangeScene.class.getResource("/com/trener/CoachFXML/" + fxml));
        Stage window = (Stage) label.getScene().getWindow();
        window.setTitle(title);
        Scene scene = new Scene(root, x, y);
        scene.getStylesheets().add((new File("src/main/resources/com/trener/style.css")).toURI().toURL().toExternalForm());
        window.setScene(scene);
        window.setResizable(false);
    }
    public static void zmienSceneAdminButton(String fxml, int x, int y, Button button, String title) throws Exception {
        Parent root = FXMLLoader.load(ChangeScene.class.getResource("/com/trener/AdminFXML/" + fxml));
        Stage window = (Stage) button.getScene().getWindow();
        window.setTitle(title);
        Scene scene = new Scene(root, x, y);
        scene.getStylesheets().add((new File("src/main/resources/com/trener/style.css")).toURI().toURL().toExternalForm());
        window.setScene(scene);
        window.setResizable(false);
    }

    public static void zmienSceneAdminLabel(String fxml, int x, int y, Label label, String title) throws Exception {
        Parent root = FXMLLoader.load(ChangeScene.class.getResource("/com/trener/AdminFXML/" + fxml));
        Stage window = (Stage) label.getScene().getWindow();
        window.setTitle(title);
        Scene scene = new Scene(root, x, y);
        scene.getStylesheets().add((new File("src/main/resources/com/trener/style.css")).toURI().toURL().toExternalForm());
        window.setScene(scene);
        window.setResizable(false);
    }
    public static void wyloguj() {
        Account.clearFields();
        Alerts.alertSuccess("Sukces", "Wylogowano pomyślnie");
    }
}
