package com.trener.ControllersCoach;

import com.trener.Classes.Alerts;
import com.trener.Classes.ChangeScene;
import com.trener.Classes.DataRequest;
import com.trener.Classes.Request;
import com.trener.RequestClasses.Account;
import com.trener.RequestClasses.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuCoach implements Initializable {

    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn,modyfikujPlanBtn,modyfikujCwiczeniaBtn,statystykiBtn, btn_dismiss, btn_accept;

    public TableView table_podopieczni = new TableView();
    public TableColumn<User, String> col_imie;
    public TableColumn<User, String> col_nazwisko;
    public Label labelCount;
    public ImageView iconMainMenu,iconAddPlan,iconModifyPlan,iconModifyExercise,iconStatisticsUser,iconSettings,iconAboutApp,iconLogout;



    private ObservableList<User> uzytkownicy = FXCollections.observableArrayList();




    private void tableViewSetup() {
        col_imie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        col_nazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        col_imie.getStyleClass().add("leftAlignedTableColumnHeader");
        col_nazwisko.getStyleClass().add("leftAlignedTableColumnHeader");
    }

    public void dismissUser(ActionEvent actionEvent) {
        if (checkIfSelected()) {
            try {
                Request.dismissUser(uzytkownicy.get(table_podopieczni.getSelectionModel().getSelectedIndex()).getId());
                uzytkownicy.clear();
                uzytkownicy = DataRequest.getUnverifiedUsersCoach(Account.getId());
                table_podopieczni.setItems(uzytkownicy);
                table_podopieczni.refresh();
            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public void acceptUser(ActionEvent actionEvent) {
        if (checkIfSelected()) {
            try {
                Request.verifyUser(uzytkownicy.get(table_podopieczni.getSelectionModel().getSelectedIndex()).getId());
                uzytkownicy.clear();
                uzytkownicy = DataRequest.getUnverifiedUsersCoach(Account.getId());
                table_podopieczni.setItems(uzytkownicy);
                table_podopieczni.refresh();


            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }

    }

    private boolean checkIfSelected() {
        if (table_podopieczni.getSelectionModel().getSelectedItem() == null) {
            Alerts.alertInformation("Błąd", "Nie wybrano żadnego użytkownika!");
            return false;
        } else {
            return true;
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
        tableViewSetup();
        try {
            if(Request.getAllUnverifiedUsers(Account.getId())>0)
            {
                labelCount.setText("Osoby czekające na weryfikacje: " + Request.getAllUnverifiedUsers(Account.getId()));
            }
            else
            {
                labelCount.setText("Brak osób czekających na weryfikację");
            }

            uzytkownicy = DataRequest.getUnverifiedUsersCoach(Account.getId());
            table_podopieczni.setItems(uzytkownicy);
        } catch (Exception e) {
            Alerts.alertError("Błąd", "Nie udało się połączyć z serwerem. Sprawdź połączenie!");
        }
    }
}
