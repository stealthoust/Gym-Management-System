package com.trener.ControllersAdmin;

import com.trener.Classes.ChangeScene;
import com.trener.Classes.Request;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuAdmin implements Initializable {
    public Button menuBtn,panelUserBtn,panelCoachBtn,logoutBtn;
    public ImageView iconMainMenu,iconPanelUser,iconPanelCoach,iconLogout;
    public Label labelUsers,labelUnverifiedCoach,labelVerifiedCoach,labelTrainings,labelMeasurements;
    public ImageView iconCoachUnverified,iconUser,iconCoachVerified,iconTrainingsCount,iconMeasurement;


    private void setStatistics()  {

        try {
            JSONObject jsonObject;
            jsonObject=Request.getStatisticsAdmin();
            labelUsers.setText("Liczba użytkowników systemu : "+jsonObject.getString("users_all")+" ("+jsonObject.getString("users_active")+" aktywnych)");
            labelUnverifiedCoach.setText("Liczba niezweryfikowanych trenerów : "+jsonObject.getString("personel_unverified"));
            labelVerifiedCoach.setText("Liczba zweryfikowanych trenerów : "+jsonObject.getString("personel_verified"));
            labelTrainings.setText("Liczba wykonanych dzisiaj treningów : "+jsonObject.getString("trening"));
            labelMeasurements.setText("Liczba wykonanych dzisiaj pomiarów wagi : "+jsonObject.getString("pomiar"));


        } catch (Exception e) {
            labelUsers.setText("Liczba użytkowników systemu : Nie udało się wczytać danych");
            labelUnverifiedCoach.setText("Liczba niezweryfikowanych trenerów : Nie udało się wczytać danych");
            labelVerifiedCoach.setText("Liczba zweryfikowanych trenerów : Nie udało się wczytać danych");
            labelTrainings.setText("Liczba wykonanych dzisiaj treningów : Nie udało się wczytać danych");
            labelMeasurements.setText("Liczba wykonanych dzisiaj pomiarów wagi : Nie udało się wczytać danych");

            e.printStackTrace();
        }

    }
    private void setIcons() {
        iconMainMenu.setImage(new Image(new File("src/main/resources/com/trener/images/mainMenuicon.png").toURI().toString()));
        iconPanelUser.setImage(new Image(new File("src/main/resources/com/trener/images/userIcon.png").toURI().toString()));
        iconPanelCoach.setImage(new Image(new File("src/main/resources/com/trener/images/coachIcon.png").toURI().toString()));
        iconLogout.setImage(new Image(new File("src/main/resources/com/trener/images/logoutIcon.png").toURI().toString()));

        iconUser.setImage(new Image(new File("src/main/resources/com/trener/images/usernameIcon.png").toURI().toString()));
        iconCoachUnverified.setImage(new Image(new File("src/main/resources/com/trener/images/unverifiedUserIcon.png").toURI().toString()));
        iconCoachVerified.setImage(new Image(new File("src/main/resources/com/trener/images/verifiedUserIcon.png").toURI().toString()));
        iconTrainingsCount.setImage(new Image(new File("src/main/resources/com/trener/images/benchIcon.png").toURI().toString()));
        iconMeasurement.setImage(new Image(new File("src/main/resources/com/trener/images/measurementIcon.png").toURI().toString()));

    }
    public void mainMenuScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneAdminButton("mainMenuAdmin.fxml", 1300, 700, menuBtn, "Menu główne");
    }

    public void userPanelScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneAdminButton("managementPanelUser.fxml", 1300, 700, menuBtn, "Menu główne");
    }

    public void panelCoachScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneAdminButton("managementPanelCoach.fxml", 1300, 700, menuBtn, "Menu główne");
    }

    public void logout(ActionEvent actionEvent) throws Exception {
        ChangeScene.wyloguj();
        ChangeScene.zmienSceneCoachButton("loginForm.fxml", 700, 700, menuBtn, "Panel logowania");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        try {
            setIcons();
            setStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
