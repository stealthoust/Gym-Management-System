package com.trener.ControllersCoach;

import com.trener.Classes.Alerts;
import com.trener.Classes.ChangeScene;
import com.trener.Classes.Dialogs;
import com.trener.Classes.Request;
import com.trener.RequestClasses.Account;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public PasswordField tf_haslo_log;

    public TextField tf_login_log;

    public Button btn_zaloguj;

    public Label label_wroc_do_rejestracji, label_forget;

    public ImageView logo;

    public static int success = 0;


    public void switchToRegisterForm(MouseEvent mouseEvent) throws Exception {
        ChangeScene.zmienSceneCoachLabel("registerForm.fxml", 750, 950, label_wroc_do_rejestracji, "Rejestracja");
        // App.setRoot("register_form");
    }


    public void logInMethod(ActionEvent actionEvent) throws Exception {
        Request.zaloguj(tf_login_log.getText(), tf_haslo_log.getText());
        if (Account.weryfikacja != null && Account.weryfikacja.equals("a") && Account.rola.equals("trener")) {
            Alerts.alertSuccess("Sukces", "Zalogowano pomyślnie");
            ChangeScene.zmienSceneCoachLabel("mainMenuCoach.fxml", 1200, 700, label_wroc_do_rejestracji, "Strona główna");
        } else if (Account.weryfikacja != null && Account.weryfikacja.equals("e")) {
            Alerts.alertInformation("Nie zweryfikowano konta", "Twoje konto nie zostało zweryfikowane. Sprawdź swój email.");
        } else if (Account.weryfikacja != null && Account.weryfikacja.equals("n")) {
            Alerts.alertInformation("Nie zweryfikowano konta", "Twoje konto czeka na zaakceptowanie przez administrację! " +
                    "Jeśli status twojego konta się zmieni otrzymasz wiadomość. Mimo wszystko zalecamy sprawdzanie aplikacji co jakiś czas.");
        } else if (Account.weryfikacja != null && Account.weryfikacja.equals("ez")) {
            Alerts.alertInformation("Email w trakcie zmiany", "Email przypisany do tego konta jest w trakcie zmiany. Sprawdź swój email.");
        } else if (Account.weryfikacja != null && Account.rola.equals("admin")) {
            Alerts.alertSuccess("Sukces", "Zalogowano pomyślnie");
            ChangeScene.zmienSceneAdminLabel("mainMenuAdmin.fxml", 1300, 700, label_wroc_do_rejestracji, "Strona główna");
        }
    }

    public void forgotPassword(MouseEvent mouseEvent) {
        Dialogs.resetPasswordDialog();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


}
