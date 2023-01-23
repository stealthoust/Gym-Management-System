package com.trener.ControllersCoach;

import com.trener.Classes.ChangeScene;
import com.trener.Classes.Functions;
import com.trener.Classes.Request;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Collections;


public class RegisterController {
    public TextField tf_login_reg;
    public PasswordField tf_haslo_reg;
    public PasswordField tf_pow_haslo_reg;
    public TextField tf_imie_reg, tf_nazwisko_reg, tf_email_reg;
    public Label label_wroc_do_logowania;
    public Button btn_zarejesstruj;
    public Label loginDetail, hasloDetail, pow_hasloDetail, imieDetail, nazwiskoDetail, emailDetail;


    public static int success = 0;


    public void switchToLoginForm(MouseEvent mouseEvent) throws Exception {
        ChangeScene.zmienSceneCoachLabel("loginForm.fxml", 700, 700, label_wroc_do_logowania, "Panel logowania");

    }


    public void register(ActionEvent actionEvent) {
        if (validate())
            try {
                Request.registerCoach(tf_login_reg.getText(), tf_haslo_reg.getText(), tf_email_reg.getText(), tf_imie_reg.getText(), tf_nazwisko_reg.getText());
                if (success == 1) {
                    success = 0;
                    ChangeScene.zmienSceneCoachLabel("loginForm.fxml", 700, 650, label_wroc_do_logowania, "Panel logowania");
                }
            } catch (Exception e) {
                System.out.println("cos poszlo nie tak");
            }
    }

    public boolean validate() {
        clearErrorMessages();
        cleanErrorFields();
        int i = 0;
        if (tf_login_reg.getText().isEmpty()) {
            loginDetail.setText("Pole nie może być puste");
            i = 1;
            setRed(tf_login_reg);
        } else if (!tf_login_reg.getText().matches("[a-zA-Z0-9]+")) {
            loginDetail.setText("Login może składać się tylko z liter i cyfr");
            i = 1;
            setRed(tf_login_reg);
        } else if (tf_login_reg.getText().length() < 5 || tf_login_reg.getText().length() > 30) {
            loginDetail.setText("Login musi zawierać pomiędzy 5 a 30 znaków");
            i = 1;
            setRed(tf_login_reg);

        } else setGreen(tf_login_reg);
        if (tf_haslo_reg.getText().isEmpty()) {
            hasloDetail.setText("Pole nie może być puste");
            i = 1;
            setRed(tf_haslo_reg);
        } else if (!tf_haslo_reg.getText().matches("[a-zA-Z0-9]+")) {
            hasloDetail.setText("Hasło może składać się tylko z liter i cyfr");
            i = 1;
            setRed(tf_haslo_reg);
        } else if (tf_haslo_reg.getText().length() < 8 || tf_haslo_reg.getText().length() > 30) {
            hasloDetail.setText("Hasło musi zawierać pomiędzy 8 a 30 znaków");
            i = 1;
            setRed(tf_haslo_reg);
        } else setGreen(tf_haslo_reg);
        if (!tf_pow_haslo_reg.getText().equals(tf_haslo_reg.getText())) {
            pow_hasloDetail.setText("Hasła nie są identyczne");
            i = 1;
            setRed(tf_pow_haslo_reg);
        } else setGreen(tf_pow_haslo_reg);
        if (tf_imie_reg.getText().isEmpty()) {
            imieDetail.setText("Pole nie może być puste");
            i = 1;
            setRed(tf_imie_reg);
        } else if (!tf_imie_reg.getText().matches("[\\p{IsAlphabetic}]+")) {
            imieDetail.setText("Imię może składać się tylko z liter");
            i = 1;
            setRed(tf_imie_reg);
        } else if (tf_imie_reg.getText().length() < 3 || tf_imie_reg.getText().length() > 13) {
            imieDetail.setText("Imię musi zawierać pomiędzy 3 a 13 znaków");
            i = 1;
            setRed(tf_imie_reg);
        } else setGreen(tf_imie_reg);
        if (tf_nazwisko_reg.getText().isEmpty()) {
            nazwiskoDetail.setText("Pole nie może być puste");
            i = 1;
            setRed(tf_nazwisko_reg);

        } else if (!tf_nazwisko_reg.getText().matches("[\\p{IsAlphabetic}]+")) {
            nazwiskoDetail.setText("Nazwisko może składać się tylko z liter");
            i = 1;
            setRed(tf_nazwisko_reg);
        } else if (tf_nazwisko_reg.getText().length() < 2 || tf_nazwisko_reg.getText().length() > 35) {
            nazwiskoDetail.setText("Nazwisko musi zawierać pomiędzy 2 a 35 znaków");
            i = 1;
            setRed(tf_nazwisko_reg);
        } else setGreen(tf_nazwisko_reg);
        if (!Functions.validateEmail(tf_email_reg.getText())) {
            emailDetail.setText("Niepoprawny adres email");
            i = 1;
            setRed(tf_email_reg);
        } else setGreen(tf_email_reg);
        return i != 1;

    }

    private void clearErrorMessages() {
        loginDetail.setText("");
        hasloDetail.setText("");
        pow_hasloDetail.setText("");
        imieDetail.setText("");
        nazwiskoDetail.setText("");
        emailDetail.setText("");
    }

    private void cleanErrorFields() {
        remove(tf_login_reg);
        remove(tf_haslo_reg);
        remove(tf_pow_haslo_reg);
        remove(tf_imie_reg);
        remove(tf_nazwisko_reg);
        remove(tf_email_reg);
    }

    private void setRed(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();
        if (!styleClass.contains("tferror")) {
            styleClass.add("tferror");
        }
    }


    private void setGreen(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();
        if (!styleClass.contains("tfvalid")) {
            styleClass.add("tfvalid");
        }
    }

    private void remove(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();
        styleClass.removeAll(Collections.singleton("tfvalid"));
        styleClass.removeAll(Collections.singleton("tferror"));
    }
}




