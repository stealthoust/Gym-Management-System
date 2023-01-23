package com.trener.ControllersCoach;

import com.trener.Classes.Alerts;
import com.trener.Classes.ChangeScene;
import com.trener.Classes.DataRequest;
import com.trener.Classes.Request;
import com.trener.RequestClasses.Cwiczenie;
import com.trener.RequestClasses.Kategoria;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifyExercise implements Initializable {

    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn, modyfikujPlanBtn, modyfikujCwiczeniaBtn, statystykiBtn, btn_modyfikuj, btn_usun;
    public ImageView iconMainMenu, iconAddPlan, iconModifyPlan, iconModifyExercise, iconStatisticsUser, iconSettings, iconAboutApp, iconLogout;

    public ComboBox combo_kategoria, combo_cwiczenie;
    public TextField txt_nazwa;
    public TextArea txt_pozycja, txt_ruch, txt_wskazowki, txt_url;

    private ObservableList<Kategoria> lista_kategorii;
    private ObservableList<Cwiczenie> lista_cwiczen;

    public static int success = 0;


    private void displayCategoryCombobox() {
        try {
            clearComboExercises();
            lista_kategorii = DataRequest.getCategories();
            for (int i = 0; i < lista_kategorii.size(); i++) {
                combo_kategoria.getItems().add(lista_kategorii.get(i).nazwa);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void displayExercisesAll() {
        boolean isEmpty = combo_kategoria.getSelectionModel().isEmpty();
        if (!isEmpty || !(combo_kategoria.getSelectionModel().getSelectedIndex() == -1)) {
            int id = lista_kategorii.get(combo_kategoria.getSelectionModel().getSelectedIndex()).id;
            try {
                displayExercisesCombobox(id);
            } catch (Exception e) {
                System.out.println(e);
                Alerts.alertError("Błąd", "Nie udało się wczytać ćwiczeń");
            }
        }
    }

    private void displayExercisesCombobox(int id) {
        try {
            clearComboExercises();
            lista_cwiczen = DataRequest.getExercisesFromCategory(id);
            for (int i = 0; i < lista_cwiczen.size(); i++) {
                combo_cwiczenie.getItems().add(lista_cwiczen.get(i).nazwa);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void clearComboExercises() {
        combo_cwiczenie.getItems().clear();
        combo_cwiczenie.getItems().add("Dodaj ćwiczenie");
        //combo_cwiczenie.getSelectionModel().clearSelection();
        combo_cwiczenie.getSelectionModel().select(0);
    }

    private void clearTextFields() {
        txt_nazwa.clear();
        txt_pozycja.clear();
        txt_ruch.clear();
        txt_wskazowki.clear();
        txt_url.clear();
    }

    private void setTextFieldsDataExercise() {
        txt_nazwa.setText(lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).nazwa);
        txt_pozycja.setText(lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).pozycja_wyjsciowa);
        txt_ruch.setText(lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).ruch);
        txt_wskazowki.setText(lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).wskazowki);
        txt_url.setText(lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).video_url);
    }

    public void setExerciseData() {
        if (combo_cwiczenie.getSelectionModel().getSelectedIndex() == 0) {
            //dodaj cwiczenie
            clearTextFields();
            btn_modyfikuj.setText("Dodaj ćwiczenie");
            btn_usun.setDisable(true);
        } else if (combo_cwiczenie.getSelectionModel().getSelectedIndex() == -1) {
            //nie wybrano nic
        } else {
            setTextFieldsDataExercise();
            btn_modyfikuj.setText("Zaaktualizuj ćwiczenie");
            btn_usun.setDisable(false);
        }
    }

    public void exerciseDelete(ActionEvent actionEvent) {
        if (combo_cwiczenie.getSelectionModel().getSelectedIndex() < 1) {
            Alerts.alertInformation("Błąd", "Nie wybrano ćwiczenia");
        } else {
            int id = lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).id;
            Request.deleteExercise(id);
            if (success == 1) {
                Alerts.alertSuccess("Sukces", "Ćwiczenie zostało usunięte");
                success = 0;
                displayExercisesAll();
                clearTextFields();
            } else {
                Alerts.alertError("Błąd", "Nie udało się usunąć ćwiczenia");
            }


        }
    }

    public void exerciseModify(ActionEvent actionEvent) {
        if (validateExercise()) {
            if (btn_modyfikuj.getText().contains("Dodaj")) {
                fillEmptyExercise();
                Request.addExercise(txt_nazwa.getText(), txt_pozycja.getText(), txt_ruch.getText(), txt_wskazowki.getText(), txt_url.getText(), lista_kategorii.get(combo_kategoria.getSelectionModel().getSelectedIndex()).id);
                if (success == 1) {
                    Alerts.alertSuccess("Sukces", "Pomyślnie dodano ćwiczenie");
                    clearTextFields();
                    displayExercisesAll();
                    success = 0;
                } else Alerts.alertError("Błąd", "Nie udało się dodać ćwiczenia");

            } else if (btn_modyfikuj.getText().contains("Zaaktualizuj")) {
                fillEmptyExercise();
                Request.updateExercise(txt_nazwa.getText(), txt_pozycja.getText(), txt_ruch.getText(), txt_wskazowki.getText(), txt_url.getText(), lista_cwiczen.get(combo_cwiczenie.getSelectionModel().getSelectedIndex() - 1).id);
                if (success == 1) {
                    Alerts.alertSuccess("Sukces", "Pomyślnie zaaktualizowano ćwiczenie");
                    clearTextFields();
                    displayExercisesAll();
                    success = 0;
                } else Alerts.alertError("Błąd", "Nie udało się zaaktualizować ćwiczenia");
            }
        }


    }

    private boolean validateExercise() {
        if (txt_nazwa.getText().length() < 5 || txt_nazwa.getText().length() > 150) {
            Alerts.alertError("Błąd", "Nazwa ćwiczenia musi mieć od 5 do 150 znaków");
            return false;
        } else return true;
    }

    private void fillEmptyExercise() {
        if (txt_pozycja.getText().length() == 0) {
            txt_pozycja.setText("Brak danych");
        }
        if (txt_ruch.getText().length() == 0) {
            txt_ruch.setText("Brak danych");
        }
        if (txt_wskazowki.getText().length() == 0) {
            txt_wskazowki.setText("Brak danych");
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
        displayCategoryCombobox();
        btn_usun.setDisable(true);
        combo_cwiczenie.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                super.setPrefWidth(100);
                            }

                            @Override
                            public void updateItem(String item,
                                                   boolean empty) {
                                super.updateItem(item, empty);
                                setText(item);
                            }
                        };
                        return cell;
                    }

                });
    }
}
