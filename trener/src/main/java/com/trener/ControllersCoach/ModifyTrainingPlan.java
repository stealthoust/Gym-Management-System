package com.trener.ControllersCoach;

import com.trener.Classes.Alerts;
import com.trener.Classes.ChangeScene;
import com.trener.Classes.DataRequest;
import com.trener.Classes.Request;
import com.trener.RequestClasses.*;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class ModifyTrainingPlan implements Initializable {

    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn, modyfikujPlanBtn, modyfikujCwiczeniaBtn, statystykiBtn,
            btnDodajCwiczenie, btnUsunCwiczenie, btnPrzeniesUp, btnPrzeniesDown, btnDodajPlan;
    public ImageView iconMainMenu,iconAddPlan,iconModifyPlan,iconModifyExercise,iconStatisticsUser,iconSettings,iconAboutApp,iconLogout;

    public ComboBox comboKategoria, comboPlanyTreningowe, comboPodopieczni;

    public TextField txtNazwaPlanu;
    public TextArea txtOpis;

    public TableView tabelaCwiczeniaDatabase = new TableView();
    public TableView tabelaPlanTreningowy = new TableView();
    public TableColumn<Cwiczenie, String> cwiczeniaColId;
    public TableColumn<Cwiczenie, String> cwiczeniaColNazwa;
    public TableColumn<Cwiczenie, String> planColId;
    public TableColumn<Cwiczenie, String> planColNazwa;


    private ObservableList<Kategoria> listaKategorii;
    private ObservableList<User> listaPodopiecznych;
    private ObservableList<Plan> listaPlanowTreningowych;
    private ObservableList<Cwiczenie> listaCwiczen = FXCollections.observableArrayList();
    private final ArrayList<String> listaCwiczenRequest = new ArrayList<String>();

    public static int success = 0;

    public void updateTrainingPlan(ActionEvent actionEvent) {
        if (validatePlan()) {
            listaCwiczenRequest.clear();
            for (Cwiczenie cwiczenie : listaCwiczen) {
                listaCwiczenRequest.add(String.valueOf(cwiczenie.id));
            }
            try {
                Request.updatePlan(txtNazwaPlanu.getText(), txtOpis.getText(), String.valueOf(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).id), listaCwiczenRequest);
                if (success == 1) {
                    Alerts.alertSuccess("Sukces", "Plan zosta?? zaaktualizowany!");
                    clearEverything();
                    success = 0;
                } else Alerts.alertError("B????d", "Co?? posz??o nie tak. Nie uda??o si?? zaaktualizowa?? planu.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteTrainingPlan(ActionEvent actionEvent) {
        if (comboPlanyTreningowe.getSelectionModel().getSelectedIndex() == -1 || comboPlanyTreningowe.getSelectionModel().isEmpty()) {
            Alerts.alertInformation("B????d", "Nie wybrano planu treningowego!");

        } else {
            try {
                Request.deletePlan(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).id);
                if (success == 1) {
                    Alerts.alertSuccess("Sukces", "Plan zosta?? usuni??ty pomy??lnie!");
                    clearEverything();
                    success = 0;
                } else Alerts.alertError("B????d", "Co?? posz??o nie tak. Nie uda??o si?? usun???? planu.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tableViewSetup() {
        cwiczeniaColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cwiczeniaColNazwa.setCellValueFactory(new PropertyValueFactory<>("Nazwa"));
        planColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        planColNazwa.setCellValueFactory(new PropertyValueFactory<>("Nazwa"));
        planColNazwa.getStyleClass().add("leftAlignedTableColumnHeader");
        cwiczeniaColNazwa.getStyleClass().add("leftAlignedTableColumnHeader");
    }

    public void displayTrainingPlansCombobox(ActionEvent actionEvent) {
        boolean isEmpty = comboPodopieczni.getSelectionModel().isEmpty();
        if (!isEmpty) {
            clearTrainingPlan();
            int id = listaPodopiecznych.get(comboPodopieczni.getSelectionModel().getSelectedIndex()).id;
            try {
                displayTrainingPlans(id);

            } catch (Exception e) {
                Alerts.alertError("B????d", "Nie uda??o si?? wczyta?? plan??w treningowych");
            }
        }
    }

    private void displayCategoryAndUsersCombobox() {
        try {
            listaKategorii = DataRequest.getCategories();
            listaPodopiecznych = DataRequest.getVerifiedUsersCoach(Account.getId());
            for (int i = 0; i < listaKategorii.size(); i++) {
                comboKategoria.getItems().add(listaKategorii.get(i).nazwa);
            }
            for (int i = 0; i < listaPodopiecznych.size(); i++) {
                comboPodopieczni.getItems().add(listaPodopiecznych.get(i).imie + " " + listaPodopiecznych.get(i).nazwisko);

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void displayExercisesTrainingPlan(ActionEvent actionEvent) {
        boolean isEmpty = comboPlanyTreningowe.getSelectionModel().isEmpty();
        if (!isEmpty) {
            //wyczysc tablice i poprzednia liste
            int id = listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).id;
            try {
                training_planExercisesPopulate(id);
            } catch (Exception e) {
                Alerts.alertError("B????d", "Nie uda??o si?? wczyta?? plan??w treningowych");
            }
        }
    }

    public void displayExercisesAll() {
        boolean isEmpty = comboKategoria.getSelectionModel().isEmpty();
        if (!isEmpty || !(comboKategoria.getSelectionModel().getSelectedIndex() == -1)) {
            int id = listaKategorii.get(comboKategoria.getSelectionModel().getSelectedIndex()).id;
            try {
                databaseExercisesPopulate(id);
            } catch (Exception e) {
                Alerts.alertError("B????d", "Nie uda??o si?? wczyta?? ??wicze??");
            }
        }
    }

    private void databaseExercisesPopulate(int id) {
        try {
            tabelaCwiczeniaDatabase.setItems(DataRequest.getExercisesFromCategory(id));
        } catch (Exception e) {
            Alerts.alertError("B????d", "B????d po????czenia z serwerem");
        }
    }

    private void training_planExercisesPopulate(int id) {
        try {
            listaCwiczen = DataRequest.getExercisesFromTrainingPlan(id);
            tabelaPlanTreningowy.setItems(listaCwiczen);
            txtNazwaPlanu.setText(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).tytul);
            txtOpis.setText(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).opis);

        } catch (Exception e) {
            listaCwiczen.clear();
            tabelaPlanTreningowy.setItems(listaCwiczen);
            txtNazwaPlanu.setText(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).tytul);
            txtOpis.setText(listaPlanowTreningowych.get(comboPlanyTreningowe.getSelectionModel().getSelectedIndex()).opis);
            Alerts.alertError("B????d", "Dany plan nie ma ??wicze?? lub nie mo??na ich wczyta??!");
        }
    }

    private void displayTrainingPlans(int id) {
        try {
            listaPlanowTreningowych = DataRequest.getTrainingPlans(id);
            for (int i = 0; i < listaPlanowTreningowych.size(); i++) {
                comboPlanyTreningowe.getItems().add(listaPlanowTreningowych.get(i).tytul);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addExercise(ActionEvent actionEvent) {
        Cwiczenie cwiczenie;
        if (tabelaCwiczeniaDatabase.getSelectionModel().getSelectedItem() != null) {
            cwiczenie = (Cwiczenie) tabelaCwiczeniaDatabase.getSelectionModel().getSelectedItem();
            if (comboPlanyTreningowe.getSelectionModel().isEmpty())
                Alerts.alertInformation("Informacja", "Nie wybrano planu treningowego!");
            else if (exerciseExists(cwiczenie)) Alerts.alertInformation("Informacja", "Cwiczenie jest ju?? w planie");
            else {
                listaCwiczen.add(cwiczenie);
                tabelaPlanTreningowy.setItems(listaCwiczen);
                tabelaPlanTreningowy.refresh();

            }
        } else Alerts.alertInformation("Informacja", "Nie wybrano ??adnego ??wiczenia");
    }

    public void deleteExercise(ActionEvent actionEvent) {
        Cwiczenie cwiczenie;
        if (tabelaPlanTreningowy.getSelectionModel().getSelectedItem() != null) {
            cwiczenie = (Cwiczenie) tabelaPlanTreningowy.getSelectionModel().getSelectedItem();
            listaCwiczen.remove(cwiczenie);
            tabelaPlanTreningowy.setItems(listaCwiczen);
            tabelaPlanTreningowy.refresh();

        } else Alerts.alertInformation("Informacja", "Nie wybrano ??adnego ??wiczenia");
    }

    public void exerciseUp(ActionEvent actionEvent) {
        if (tabelaPlanTreningowy.getSelectionModel().getSelectedItem() != null) {
            if (listaCwiczen.size() < 2)
                Alerts.alertInformation("Za ma??o ??wicze??", "Potrzebujesz conajmniej dw??ch ??wicze??, aby m???? je przesun???? w planie!");
            else if (tabelaPlanTreningowy.getSelectionModel().getSelectedIndex() < 1)
                Alerts.alertInformation("Nie mo??esz przesun????", "Nie mo??esz przesun???? pierwszego ??wiczenia do g??ry");
            else {
                Collections.swap(listaCwiczen, tabelaPlanTreningowy.getSelectionModel().getSelectedIndex(), tabelaPlanTreningowy.getSelectionModel().getSelectedIndex() - 1);
            }
            tabelaPlanTreningowy.setItems(listaCwiczen);
            tabelaPlanTreningowy.refresh();

        } else Alerts.alertInformation("Informacja", "Nie wybrano ??adnego ??wiczenia");
    }

    public void exerciseDown(ActionEvent actionEvent) {
        if (tabelaPlanTreningowy.getSelectionModel().getSelectedItem() != null) {
            if (listaCwiczen.size() < 2)
                Alerts.alertInformation("Za ma??o ??wicze??", "Potrzebujesz conajmniej dw??ch ??wicze??, aby m???? je przesun???? w planie!");
            else if (tabelaPlanTreningowy.getSelectionModel().getSelectedIndex() == listaCwiczen.size() - 1)
                Alerts.alertInformation("Nie mo??esz przesun????", "Nie mo??esz przesun???? ostatniego ??wiczenia w d????");
            else {
                Collections.swap(listaCwiczen, tabelaPlanTreningowy.getSelectionModel().getSelectedIndex(), tabelaPlanTreningowy.getSelectionModel().getSelectedIndex() + 1);
            }
            tabelaPlanTreningowy.setItems(listaCwiczen);
            tabelaPlanTreningowy.refresh();

        } else Alerts.alertInformation("Informacja", "Nie wybrano ??adnego ??wiczenia");
    }

    private boolean exerciseExists(Cwiczenie cwiczenie) {
        if (listaCwiczen == null) {
            return false;
        } else for (Cwiczenie value : listaCwiczen) {
            if (cwiczenie.id == value.id) return true;
        }
        return false;
    }

    private boolean validatePlan() {
        if (txtNazwaPlanu.getText().length() < 3 || txtNazwaPlanu.getText().length() > 40) {
            Alerts.alertInformation("B????d", "Nazwa planu musi mie?? od 3 do 40 znak??w");
            return false;
        }
        if (txtOpis.getText().length() < 3 || txtOpis.getText().length() > 200) {
            Alerts.alertInformation("B????d", "Opis planu musi mie?? od 3 do 200 znak??w");
            return false;
        }
        if (comboPodopieczni.getSelectionModel().getSelectedIndex() == -1 || comboPodopieczni.getSelectionModel().isEmpty()) {
            Alerts.alertInformation("B????d", "Nie wybrano podopiecznego!");
            return false;
        }
        if (comboPlanyTreningowe.getSelectionModel().getSelectedIndex() == -1 || comboPlanyTreningowe.getSelectionModel().isEmpty()) {
            Alerts.alertInformation("B????d", "Nie wybrano planu treningowego!");
            return false;
        }
        if (listaCwiczen.size() < 2 || listaCwiczen.size() > 15) {
            Alerts.alertInformation("B????d", "Plan musi mie?? od 2 do 15 ??wicze??");
            return false;
        } else return true;
    }

    private void clearEverything() {
        txtNazwaPlanu.setText("");
        txtOpis.setText("");
        comboPlanyTreningowe.getItems().clear();
        comboPlanyTreningowe.getSelectionModel().clearSelection();
        comboPodopieczni.getSelectionModel().clearSelection();
        comboKategoria.getSelectionModel().clearSelection();
        listaCwiczen.clear();
        listaCwiczenRequest.clear();
        tabelaCwiczeniaDatabase.getItems().clear();
        tabelaPlanTreningowy.getItems().clear();
        tabelaPlanTreningowy.refresh();
        tabelaCwiczeniaDatabase.refresh();

    }

    private void clearTrainingPlan() {
        if (listaPlanowTreningowych != null) {
            listaPlanowTreningowych.clear();
            comboPlanyTreningowe.getSelectionModel().clearSelection();
            comboPlanyTreningowe.getItems().clear();
        }
        tabelaPlanTreningowy.setItems(null);
        tabelaPlanTreningowy.refresh();
        txtNazwaPlanu.setText("");
        txtOpis.setText("");
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
        displayCategoryAndUsersCombobox();
        tableViewSetup();
    }
}
