package com.trener.ControllersAdmin;

import com.trener.Classes.*;
import com.trener.RequestClasses.Cwiczenie;
import com.trener.RequestClasses.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class ManagementPanelUser implements Initializable {
    public Button menuBtn,panelUserBtn,panelCoachBtn,logoutBtn;
    public Button btnDelete,btnChangeData;
    public ImageView iconMainMenu,iconPanelUser,iconPanelCoach,iconLogout;

    public TableView tableView=new TableView();
    public TableColumn <User,String> colId;
    public TableColumn <User,String> colImie;
    public TableColumn <User,String> colDataUtworzenia;
    public TableColumn <User,String> colNazwisko;
    public TableColumn <User,String> colEmail;
    public TableColumn <User,String> colDataUrodzenia;
    public TableColumn <User,String> colWeryfikacja;
    public TableColumn <User,String> colTrener;
    public Button btnBlockUser;
    private ObservableList<User> uzytkownicy = FXCollections.observableArrayList();

    private void tableViewSetup() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        colNazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDataUrodzenia.setCellValueFactory(new PropertyValueFactory<>("dataUrodzenia"));
        colDataUtworzenia.setCellValueFactory(new PropertyValueFactory<>("dataUtworzenia"));
        colWeryfikacja.setCellValueFactory(new PropertyValueFactory<>("weryfikacja"));
        colTrener.setCellValueFactory(new PropertyValueFactory<>("trener"));

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    if(uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getWeryfikacja().contains("Aktywny")||
                            uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getWeryfikacja().contains("Zmienia")||
                            uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getWeryfikacja().contains("aktywacja")||
                            uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getWeryfikacja().contains("Nieaktywny")){
                        btnBlockUser.setText("Zablokuj");
                }
                    else {
                        btnBlockUser.setText("Odblokuj");
                    }
            }
            }
        });

    }
    private void setIcons() {
        iconMainMenu.setImage(new Image(new File("src/main/resources/com/trener/images/mainMenuicon.png").toURI().toString()));
        iconPanelUser.setImage(new Image(new File("src/main/resources/com/trener/images/userIcon.png").toURI().toString()));
        iconPanelCoach.setImage(new Image(new File("src/main/resources/com/trener/images/coachIcon.png").toURI().toString()));
        iconLogout.setImage(new Image(new File("src/main/resources/com/trener/images/logoutIcon.png").toURI().toString()));

    }
    public void refreshTable()
    {
        try {
            uzytkownicy = DataRequest.getUsersAdmin();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        tableView.setItems(uzytkownicy);
    }

    public void changeUserData(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Dialogs dialogs=new Dialogs();
            dialogs.managementPanelUser=this;
            dialogs.changeUserDataDialog(uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getId(),uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getImie()
                    ,uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getNazwisko(),uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getDataUrodzenia(),
                    uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getDataUtworzenia());
        } else Alerts.alertInformation("Informacja", "Nie wybrano żadnego użytkownika");
    }
    public void deleteUser(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz usunąć " +
                    uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getImie()
                    + " "+uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getNazwisko()+
                    " oraz wszystkie jego dane z systemu?",  ButtonType.CANCEL,ButtonType.YES);
            alert.setHeaderText(null);
            alert.setTitle("Usuwanie użytkownika");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                Request.deleteUser(uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getId());
                refreshTable();
            }

        } else Alerts.alertInformation("Informacja", "Nie wybrano żadnego użytkownika");
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

    public void blockUser(ActionEvent actionEvent) throws Exception {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            if(btnBlockUser.getText().equals("Zablokuj"))
            {
Request.blockUser(uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getId());
                refreshTable();

            }
            else if(btnBlockUser.getText().equals("Odblokuj"))
            {
                Request.unblockUser(uzytkownicy.get(tableView.getSelectionModel().getSelectedIndex()).getId());
                refreshTable();
            }

        } else Alerts.alertInformation("Informacja", "Nie wybrano żadnego użytkownika");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        tableViewSetup();
        try {
            uzytkownicy = DataRequest.getUsersAdmin();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        tableView.setItems(uzytkownicy);
    }

}
