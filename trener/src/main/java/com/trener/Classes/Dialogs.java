package com.trener.Classes;

import com.trener.ControllersAdmin.ManagementPanelCoach;
import com.trener.ControllersAdmin.ManagementPanelUser;
import com.trener.RequestClasses.Account;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Dialogs {


    public ManagementPanelCoach managementPanelCoach;
    public ManagementPanelUser managementPanelUser;

    public static void changePasswordDialog() {

        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Zmiana Hasła");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        ButtonType btn_cofnij = new ButtonType("Cofnij", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btn_dodaj = new ButtonType("Zmień", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btn_cofnij, btn_dodaj);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField password_old = new PasswordField();
        password_old.setPromptText("Aktualne hasło");
        PasswordField password_new = new PasswordField();
        password_new.setPromptText("Nowe hasło");
        PasswordField password_new_repeat = new PasswordField();
        password_new_repeat.setPromptText("Powtórz nowe hasło");
        grid.add(new Label("Aktualne hasło:"), 0, 0);
        grid.add(password_old, 1, 0);
        grid.add(new Label("Nowe hasło:"), 0, 1);
        grid.add(password_new, 1, 1);
        grid.add(new Label("Powtórz nowe hasło:"), 0, 2);
        grid.add(password_new_repeat, 1, 2);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> password_old.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btn_dodaj) {
                Map<String, Object> lista = new HashMap<>();
                lista.put("password_old", password_old.getText());
                lista.put("password_new", password_new.getText());
                lista.put("password_new_repeat", password_new_repeat.getText());
                return lista;
            }
            return null;
        });
        Optional<Map<String, Object>> result = dialog.showAndWait();
        //result.ifPresent(password_old);
        if (result.isPresent()) {
            if (Functions.validatePassword(password_old.getText(), password_new.getText(), password_new_repeat.getText())) {
                Request.changePassword(Account.getId(), password_old.getText(), password_new.getText());
            }


        } else {
            System.out.println("Niepoprawne hasło");
        }
    }

    public static void changeEmailDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Zmiana adresu email");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        ButtonType btn_cofnij = new ButtonType("Cofnij", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btn_dodaj = new ButtonType("Zmień", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btn_cofnij, btn_dodaj);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField adres_email = new TextField();
        adres_email.setPromptText("adres email");
        grid.add(new Label("Podaj nowy adres email:"), 0, 0);
        grid.add(adres_email, 1, 0);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> adres_email.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btn_dodaj) {
                return adres_email.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (Functions.validateEmailMessage(adres_email.getText())) {
                Request.changeEmail(Account.getId(), adres_email.getText());
            }

        } else {
            System.out.println("Nic nie wybrano");
        }
    }

    public static void resetPasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Przypomnienie hasła");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        ButtonType btn_cofnij = new ButtonType("Cofnij", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btn_dodaj = new ButtonType("Wyślij wiadomość na podany email", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btn_cofnij, btn_dodaj);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        TextField adres_email = new TextField();
        adres_email.setMinWidth(300);
        adres_email.setPromptText("adres email");
        grid.add(new Label("Podaj adres email:"), 0, 0);
        grid.add(adres_email, 1, 0);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> adres_email.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btn_dodaj) {
                return adres_email.getText();
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (Functions.validateEmailMessage(adres_email.getText())) {
                Request.resetPassword(adres_email.getText());
            }

        } else {
            System.out.println("Nic nie wybrano");
        }
    }

    public void changeUserDataDialog(int id,String imie, String nazwisko, String dataUrodzenia, String dataUtworzenia) {

        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Zmiana danych użytkownika");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        ButtonType btn_cofnij = new ButtonType("Cofnij", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btn_zmien = new ButtonType("Zmień dane", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btn_cofnij, btn_zmien);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idTextField = new TextField();
        idTextField.setText(String.valueOf(id));
        idTextField.setDisable(true);
        TextField imieTextField = new TextField();
        imieTextField.setText(imie);
        TextField nazwiskoTextField = new TextField();
        nazwiskoTextField.setText(nazwisko);

        TextField dataUrodzeniaTextField = new TextField();
        dataUrodzeniaTextField.setText(dataUrodzenia);

        TextField dataUtworzeniaTextField = new TextField();
        dataUtworzeniaTextField.setDisable(true);
        dataUtworzeniaTextField.setText(dataUtworzenia);

        grid.add(new Label("ID użytkownika:"), 0, 0);
        grid.add(idTextField, 1, 0);
        grid.add(new Label("Imię:"), 0, 1);
        grid.add(imieTextField, 1, 1);
        grid.add(new Label("Nazwisko:"), 0, 2);
        grid.add(nazwiskoTextField, 1, 2);

        grid.add(new Label("Data urodzenia:"), 0, 3);
        grid.add(dataUrodzeniaTextField, 1, 3);
        grid.add(new Label("Data utworzenia:"), 0, 4);
        grid.add(dataUtworzeniaTextField, 1, 4);



        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> imieTextField.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btn_zmien) {
                Map<String, Object> lista = new HashMap<>();
                lista.put("id", idTextField.getText());
                lista.put("imie", imieTextField.getText());
                lista.put("nazwisko", nazwiskoTextField.getText());
                lista.put("dataUrodzenia", dataUrodzeniaTextField.getText());
                lista.put("dataUtworzenia", dataUtworzeniaTextField.getText());


                return lista;
            }
            return null;
        });
        Optional<Map<String, Object>> result = dialog.showAndWait();

        if (result.isPresent()) {
            if(Functions.validatePersonalData(imieTextField.getText(),nazwiskoTextField.getText()))
            {
        Request.changeUserDataAdmin(id, imieTextField.getText(), nazwiskoTextField.getText(),dataUrodzeniaTextField.getText());
        managementPanelUser.refreshTable();

            }
            else Alerts.alertError("Błędne dane","Wprowadziłeś niepoprawne dane");

        }
    }

    public void changeCoachDataDialog(int id,String imie, String nazwisko, String rola, String weryfikacja) {

        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Zmiana danych użytkownika");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        ButtonType btn_cofnij = new ButtonType("Cofnij", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btn_zmien = new ButtonType("Zmień dane", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btn_cofnij, btn_zmien);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idTextField = new TextField();
        idTextField.setText(String.valueOf(id));
        idTextField.setDisable(true);
        TextField imieTextField = new TextField();
        imieTextField.setText(imie);
        TextField nazwiskoTextField = new TextField();
        nazwiskoTextField.setText(nazwisko);
        TextField rolaTextField = new TextField();
        rolaTextField.setText(rola);
        ComboBox comboWeryfikacja=new ComboBox();
        comboWeryfikacja.getItems().addAll("Aktywny","Nieaktywny");
        comboWeryfikacja.setValue(weryfikacja);
        grid.add(new Label("ID trenera:"), 0, 0);
        grid.add(idTextField, 1, 0);
        grid.add(new Label("Imię:"), 0, 1);
        grid.add(imieTextField, 1, 1);
        grid.add(new Label("Nazwisko:"), 0, 2);
        grid.add(nazwiskoTextField, 1, 2);

        grid.add(new Label("Rola:"), 0, 3);
        grid.add(rolaTextField, 1, 3);
        grid.add(new Label("Status weryfikacji:"), 0, 4);
        grid.add(comboWeryfikacja, 1, 4);



        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> imieTextField.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btn_zmien) {
                Map<String, Object> lista = new HashMap<>();
                lista.put("id", idTextField.getText());
                lista.put("imie", imieTextField.getText());
                lista.put("nazwisko", nazwiskoTextField.getText());
                lista.put("rola", rolaTextField.getText());
                lista.put("weryfikacja", comboWeryfikacja.getValue());



                return lista;
            }
            return null;
        });
        Optional<Map<String, Object>> result = dialog.showAndWait();

        if (result.isPresent()) {
            String weryfikacja_skrot;
            switch(comboWeryfikacja.getValue().toString()){
                case "Aktywny":
                    weryfikacja_skrot="a";
                    break;
                case "Nieaktywny":
                    weryfikacja_skrot="n";
                    break;
                case "Oczekujący-weryfikacja":
                    weryfikacja_skrot="e";
                    break;
                case "Oczekujący-zmiana email":
                    weryfikacja_skrot="ez";
                    break;
                default:
                    weryfikacja_skrot="n";
                    break;
            }

            if(Functions.validatePersonalData(imieTextField.getText(),nazwiskoTextField.getText()))
            {
            Request.changeCoachDataAdmin(id, imieTextField.getText(), nazwiskoTextField.getText(),rolaTextField.getText(),weryfikacja_skrot);
            managementPanelCoach.refreshTable();
            }
            else Alerts.alertError("Błędne dane","Wprowadziłeś niepoprawne dane");

        }
    }
}
