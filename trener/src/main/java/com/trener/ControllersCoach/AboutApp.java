package com.trener.ControllersCoach;

import com.trener.Classes.ChangeScene;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutApp implements Initializable {
    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn,modyfikujPlanBtn,modyfikujCwiczeniaBtn,statystykiBtn;
    public ImageView iconMainMenu,iconAddPlan,iconModifyPlan,iconModifyExercise,iconStatisticsUser,iconSettings,iconAboutApp,iconLogout;
    public ImageView logoImg,fsImg,iconsImg;


    private void setIcons() {
        iconMainMenu.setImage(new Image(new File("src/main/resources/com/trener/images/mainMenuicon.png").toURI().toString()));
        iconAddPlan.setImage(new Image(new File("src/main/resources/com/trener/images/addPlanIcon.png").toURI().toString()));
        iconModifyPlan.setImage(new Image(new File("src/main/resources/com/trener/images/modifyPlanIcon.png").toURI().toString()));
        iconModifyExercise.setImage(new Image(new File("src/main/resources/com/trener/images/modifyExerciseIcon.png").toURI().toString()));
        iconStatisticsUser.setImage(new Image(new File("src/main/resources/com/trener/images/statisticsIcon.png").toURI().toString()));
        iconSettings.setImage(new Image(new File("src/main/resources/com/trener/images/settingsIcon.png").toURI().toString()));
        iconAboutApp.setImage(new Image(new File("src/main/resources/com/trener/images/aboutAppIcon.png").toURI().toString()));
        iconLogout.setImage(new Image(new File("src/main/resources/com/trener/images/logoutIcon.png").toURI().toString()));

        logoImg.setImage(new Image(new File("src/main/resources/com/trener/images/logo.png").toURI().toString()));
        fsImg.setImage(new Image(new File("src/main/resources/com/trener/images/fs.png").toURI().toString()));
        iconsImg.setImage(new Image(new File("src/main/resources/com/trener/images/icons.png").toURI().toString()));

    }

    public void mainMenuScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("mainMenuCoach.fxml", 1200, 700, ustawieniaBtn, "Strona g????wna");
    }

    public void addPlanScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("addTrainingPlan.fxml", 1200, 700, ustawieniaBtn, "Panel dodawania planu treningowego");
    }

    public void updatePlanScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("modifyTrainingPlan.fxml", 1200, 700, ustawieniaBtn, "Panel modyfikacji planu treningowego");
    }

    public void exercisesScene(ActionEvent actionEvent) throws Exception {
        ChangeScene.zmienSceneCoachButton("modifyExercise.fxml", 1200, 700, ustawieniaBtn, "Panel modyfikacji ??wicze??");
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
    }

    public void openFsBrowser(ActionEvent actionEvent) {

        try {
            Desktop.getDesktop().browse(new URL("https://www.fabrykasily.pl/").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void openIconsBrowser(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URL("https://icons8.com").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
