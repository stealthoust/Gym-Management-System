package com.trener.ControllersAdmin;

import com.trener.Classes.*;
import com.trener.RequestClasses.Coach;
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

public class ManagementPanelCoach implements Initializable {
    public Button menuBtn,panelUserBtn,panelCoachBtn,logoutBtn,btnDelete,btnChangeData;
    public ImageView iconMainMenu,iconPanelUser,iconPanelCoach,iconLogout;
    public TableView tableView=new TableView();
    public TableColumn <Coach,String>colId,colImie,colNazwisko,colEmail,colWeryfikacja,colRola;
    private ObservableList<Coach> trenerzy = FXCollections.observableArrayList();

    private void tableViewSetup() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        colNazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colWeryfikacja.setCellValueFactory(new PropertyValueFactory<>("weryfikacja"));
        colRola.setCellValueFactory(new PropertyValueFactory<>("rola"));


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
            trenerzy = DataRequest.getCoachesAdmin();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        tableView.setItems(trenerzy);
    }
    public void deleteUser(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz usunąć " + trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getImie()
                    + " "+trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getNazwisko()+" oraz wszystkie jego dane z systemu?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.setHeaderText(null);
            alert.setTitle("Usuwanie trenera");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                Request.deleteCoach(trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getId());
                refreshTable();
            }

        } else Alerts.alertInformation("Informacja", "Nie wybrano żadnego użytkownika");
    }

    public void changeData(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Dialogs dialogs=new Dialogs();
            dialogs.managementPanelCoach=this;
                    dialogs.changeCoachDataDialog(trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getId(),trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getImie()
                    ,trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getNazwisko(),trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getRola(),
                    trenerzy.get(tableView.getSelectionModel().getSelectedIndex()).getWeryfikacja());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        tableViewSetup();

        try {
            trenerzy = DataRequest.getCoachesAdmin();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        tableView.setItems(trenerzy);
    }

}
