package com.trener.ControllersCoach;

import com.trener.Classes.Alerts;
import com.trener.Classes.ChangeScene;
import com.trener.Classes.Dialogs;
import com.trener.Classes.Request;
import com.trener.RequestClasses.Account;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsCoach implements Initializable {

    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn,modyfikujPlanBtn,modyfikujCwiczeniaBtn,statystykiBtn,
            btnZmienDanePersonalne, btnZmienEmail, btnZmienHaslo;
    public ImageView iconMainMenu,iconAddPlan,iconModifyPlan,iconModifyExercise,iconStatisticsUser,iconSettings,iconAboutApp,iconLogout;

    public TextField txtImie, txtNazwisko, txtEmail;
    private String imie,nazwisko;
    public static int success = 0;


    public void changePersonalData(ActionEvent actionEvent) throws Exception {
        if(txtImie.isDisabled())
        {
            txtImie.setDisable(false);
            txtNazwisko.setDisable(false);

            btnZmienDanePersonalne.setText("Zapisz");
        }
        else
        {

            btnZmienDanePersonalne.setText("Edytuj");
            if(!imie.equals(txtImie.getText())||!nazwisko.equals(txtNazwisko.getText()))
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz zmienić",  ButtonType.NO,ButtonType.YES);

                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    Request.changePersonalData(Account.getId(), txtImie.getText(), txtNazwisko.getText());
                    if(success == 1)
                    {
                        success=0;
                        ChangeScene.wyloguj();
                        ChangeScene.zmienSceneCoachButton("loginForm.fxml", 700, 700, ustawieniaBtn, "Panel logowania");
                    }
                    else
                    {
                        Alerts.alertError("Błąd","Nie udało się zmienić danych");
                    }
                }
                else {
                    txtImie.setText(imie);
                    txtNazwisko.setText(nazwisko);
                    txtImie.setDisable(true);
                    txtNazwisko.setDisable(true);
                }

            }
            else
            {
                txtImie.setText(imie);
                txtNazwisko.setText(nazwisko);
                txtImie.setDisable(true);
                txtNazwisko.setDisable(true);
            }




        }
    }

    public void changeEmail(ActionEvent actionEvent) throws Exception {
        Dialogs.changeEmailDialog();
        if(success==1)
        {
            success=0;
            ChangeScene.wyloguj();
            ChangeScene.zmienSceneCoachButton("loginForm.fxml", 700, 700, ustawieniaBtn, "Panel logowania");
        }
    }

    public void changePassword(ActionEvent actionEvent) throws Exception {
        Dialogs.changePasswordDialog();
        if(success==1)
        {
            success=0;
            ChangeScene.wyloguj();
            ChangeScene.zmienSceneCoachButton("loginForm.fxml", 700, 700, ustawieniaBtn, "Panel logowania");
        }

    }
    private void setIcons() {
        iconMainMenu.setImage(new Image(new File("src/main/resources/com/trener/images/mainMenuicon.png").toURI().toString()));
        iconAddPlan.setImage(new Image(new File("src/main/resources/com/trener/images/addPlanIcon.png").toURI().toString()));
        iconModifyPlan.setImage(new Image(new File("src/main/resources/com/trener/images/modifyPlanIcon.png").toURI().toString()));
        iconModifyExercise.setImage(new Image(new File("src/main/resources/com/trener/images/modifyExerciseIcon.png").toURI().toString()));
        iconStatisticsUser.setImage(new Image(new File("src/main/resources/com/trener/images/statisticsIcon.png").toURI().toString()));
        iconSettings.setImage(new Image(new File("src/main/resources/com/trener/images/settingsIcon.png").toURI().toString()));
        iconAboutApp.setImage(new Image(new File("src/main/resources/com/trener/images/aboutAppIcon.png").toURI().toString()));
        iconLogout.setImage(new Image(new File("src/main/resources/com/trener/images/logoutIcon.png").toURI().toString()));

    }

    public void mainMenuScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("mainMenuCoach.fxml", 1200, 700, ustawieniaBtn, "Strona główna");
    }

    public void addPlanScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("addTrainingPlan.fxml", 1200, 700, ustawieniaBtn, "Panel dodawania planu treningowego");
    }

    public void updatePlanScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("modifyTrainingPlan.fxml", 1200, 700, ustawieniaBtn, "Panel modyfikacji planu treningowego");
    }

    public void exercisesScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("modifyExercise.fxml", 1200, 700, ustawieniaBtn, "Panel modyfikacji ćwiczeń");
    }

    public void statisticsScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("userStatistics.fxml", 1200, 700, ustawieniaBtn, "Statystyki podopiecznych");
    }

    public void settingsScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("settingsCoach.fxml", 1200, 700, ustawieniaBtn, "Ustawienia");
    }

    public void aboutAppScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("aboutApp.fxml", 1200, 700, ustawieniaBtn, "Informacje o aplikacji");
    }

    public void logout(ActionEvent actionEvent) throws Exception {
        ChangeScene.wyloguj();
        ChangeScene.zmienSceneCoachButton("loginForm.fxml", 700, 700, ustawieniaBtn, "Panel logowania");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        txtImie.setText(Account.getImie());
        txtNazwisko.setText(Account.getNazwisko());
        txtEmail.setText(Account.getEmail());

        imie= Account.getImie();
        nazwisko= Account.getNazwisko();
    }

}
