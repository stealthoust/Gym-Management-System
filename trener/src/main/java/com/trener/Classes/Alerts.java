package com.trener.Classes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Alerts {

    public static void alertInformation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/com/trener/images/infoIcon.png"));
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void alertSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/com/trener/images/successIcon.png"));
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void alertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/com/trener/images/errorIcon.png"));
        alert.showAndWait();
    }

    public static void alertConfirmation(String title, String content) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait().ifPresent((btnType) -> {
            if (btnType == ButtonType.OK) {
                System.out.println("OK");
            } else if (btnType == ButtonType.CANCEL) {
                System.out.println("CANCEL");
            }

        });
    }




}
