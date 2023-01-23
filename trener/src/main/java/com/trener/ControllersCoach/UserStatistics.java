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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserStatistics implements Initializable {

    public Button menuBtn, ustawieniaBtn, infoBtn, wylogujBtn,
            dodajPlanBtn, modyfikujPlanBtn, modyfikujCwiczeniaBtn, statystykiBtn;
    public ImageView iconMainMenu,iconAddPlan,iconModifyPlan,iconModifyExercise,iconStatisticsUser,iconSettings,iconAboutApp,iconLogout;


    public ComboBox comboPodopieczny;
    public Label txtLiczbaTreningow;

    public TableView tableView = new TableView();

    public TableColumn<UserInfo, Double> colWaga;
    public TableColumn<UserInfo, Double> colWzrost;
    public TableColumn<UserInfo, String> colAktywnosc;
    public TableColumn<UserInfo, String> colCel;

    public PieChart pieChart;
    ;
    public LineChart lineChart;
    public BarChart barChart;


    String[] liczbaTreningow = new String[3];
    JSONArray tygodnioweKalorie = new JSONArray();
    private ObservableList<User> listaPodopiecznych = FXCollections.observableArrayList();
    private ObservableList<PlanCounter> listaPlanowTreningowych;
    private ObservableList<UserPomiar> listaPomiarow;
    private ObservableList<UserInfo> userInfo = FXCollections.observableArrayList();
    ;
    private ObservableList<PieChart.Data> listaPodzialTreningi = FXCollections.observableArrayList();


    private void tableViewSetup() {
        colWaga.setCellValueFactory(new PropertyValueFactory<>("Waga"));
        colWzrost.setCellValueFactory(new PropertyValueFactory<>("Wzrost"));
        colAktywnosc.setCellValueFactory(new PropertyValueFactory<>("Aktywnosc"));
        colCel.setCellValueFactory(new PropertyValueFactory<>("Cel"));
        colWaga.setStyle("-fx-alignment: CENTER;");
        colWzrost.setStyle("-fx-alignment: CENTER;");
        colAktywnosc.setStyle("-fx-alignment: CENTER;");
        colCel.setStyle("-fx-alignment: CENTER;");

    }

    private void displayUsersCombobox() {
        try {
            listaPodopiecznych = DataRequest.getVerifiedUsersCoach(Account.getId());
            for (int i = 0; i < listaPodopiecznych.size(); i++) {
                comboPodopieczny.getItems().add(listaPodopiecznych.get(i).imie + " " + listaPodopiecznych.get(i).nazwisko);

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void getStatistics(int id) {
        try {
            userInfo = DataRequest.getUserInfo(id);
            tableView.setItems(userInfo);
            liczbaTreningow = Request.getTrainingCount(id);
            listaPlanowTreningowych = DataRequest.getTrainingPlansCount(id);
            listaPomiarow = DataRequest.getUserMeasurements(id);
            tygodnioweKalorie = Request.getWeekCalories(id);
        } catch (Exception e) {
            System.out.println(e);
            Alerts.alertError("Błąd", "Błąd połączenia z serwerem");

        }
    }

    private void fillPieChart() {
        barChart.getData().clear();
        listaPodzialTreningi.clear();
        if (listaPlanowTreningowych == null || listaPlanowTreningowych.size() < 1) {
            pieChart.setTitle("Żaden trening nie został wykonany z aktywnymi planami treningowymi");
            listaPodzialTreningi.add(new PieChart.Data("Brak", 0));
            pieChart.setData(listaPodzialTreningi);
        } else {
            for (PlanCounter plan : listaPlanowTreningowych) {
                listaPodzialTreningi.add(new PieChart.Data(plan.getNazwa()+" ("+plan.getLiczba()+")", plan.getLiczba()));
            }
            pieChart.setTitle("Podział zanotowanych treningów na aktywne plany treningowe");
            pieChart.setData(listaPodzialTreningi);
        }


    }

    private void fillLineChart() {
        XYChart.Series series = new XYChart.Series<>();
        lineChart.getData().clear();
        series.setName("Wykres wagi z czasem");
        if (listaPomiarow == null || listaPomiarow.size() < 1) {
            series.getData().add(new XYChart.Data("Brak", 0.0));
            lineChart.getData().add(series);

        } else {
            for (UserPomiar pomiar : listaPomiarow) {
                series.getData().add(new XYChart.Data(pomiar.getData(), pomiar.getWaga()));
                System.out.println(pomiar.getWaga());
            }
            lineChart.getData().add(series);

        }


    }

    private void fillBarChart() throws JSONException {
        XYChart.Series series = new XYChart.Series<>();
        XYChart.Series series2 = new XYChart.Series<>();
        series2.setName("Dziennie zapotrzebowanie kaloryczne" );
        series2.getData().add(new XYChart.Data("Zapotrzebowanie\n"+ userInfo.get(0).getZapotrzebowanie()+" kcal", userInfo.get(0).getZapotrzebowanie()));
        series.setName("Kalorie");
        for (int i = 0; i < tygodnioweKalorie.length(); i++) {
            series.getData().add(new XYChart.Data(tygodnioweKalorie.getJSONObject(i).getString("day") + "\n" + tygodnioweKalorie.getJSONObject(i).getString("kcal") + " kcal"
                    , tygodnioweKalorie.getJSONObject(i).getDouble("kcal")));


        }
        barChart.getData().addAll(series2, series);


    }

    public void setStatistics(ActionEvent actionEvent) {
        boolean isEmpty = comboPodopieczny.getSelectionModel().isEmpty();
        if (!isEmpty || !(comboPodopieczny.getSelectionModel().getSelectedIndex() == -1)) {
            int id = listaPodopiecznych.get(comboPodopieczny.getSelectionModel().getSelectedIndex()).id;
            try {
                getStatistics(id);
                txtLiczbaTreningow.setText("Liczba treningów w bieżącym tygodniu/miesiącu/roku : " + liczbaTreningow[0] + "/" + liczbaTreningow[1] + "/" + liczbaTreningow[2]);
                fillPieChart();
                fillBarChart();
                fillLineChart();
            } catch (Exception e) {
                Alerts.alertError("Błąd", "Nie udało się wczytać danych");
            }
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
        displayUsersCombobox();
        tableViewSetup();
        //fillPieChart();
    }
}
