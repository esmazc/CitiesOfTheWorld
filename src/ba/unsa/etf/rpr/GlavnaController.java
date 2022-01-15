package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {
    public TableView<Grad> tableViewGradovi;
    public TableColumn colGradId;
    public TableColumn colGradNaziv;
    public TableColumn colGradStanovnika;
    /*public TableColumn<Grad, String> colGradId;
    public TableColumn<Grad, String> colGradNaziv;
    public TableColumn<Grad, String> colGradStanovnika;*/
    public TableColumn<Grad, String> colGradDrzava;
    private GeografijaDAO dao;

    /*public GlavnaController(GeografijaDAO dao) {
        this.dao = dao;
    }*/

    public GlavnaController() {
        dao = GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize() {
        tableViewGradovi.setItems(FXCollections.observableArrayList(dao.gradovi()));
        colGradId.setCellValueFactory(new PropertyValueFactory("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory("brojStanovnika"));
        /*colGradId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colGradNaziv.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNaziv()));
        colGradStanovnika.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBrojStanovnika())));*/
        colGradDrzava.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDrzava().getNaziv()));
    }

    public void dodajGradAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
            GradController gradController = new GradController(null, dao.drzave());
            loader.setController(gradController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Grad");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
            stage.setOnHidden(e -> {
                Grad grad = gradController.getGrad();
                if(grad != null) {
                    dao.dodajGrad(grad);
                    ArrayList<Grad> gradovi = dao.gradovi();
                    tableViewGradovi.setItems(FXCollections.observableArrayList(gradovi));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavuAction(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        Drzava drzava = null;
        if(grad != null) drzava = grad.getDrzava();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"));
            DrzavaController drzavaController = new DrzavaController(drzava, dao.gradovi());
            loader.setController(drzavaController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Država");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
            stage.setOnHidden(e -> {
                Drzava d = drzavaController.getDrzava();
                if(d != null) {
                    dao.dodajDrzavu(d);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGradAction(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        try {
            if(grad != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
                GradController gradController = new GradController(grad, dao.drzave());
                loader.setController(gradController);
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Grad");
                stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                stage.show();
                stage.setOnHidden(e -> {
                    Grad g = gradController.getGrad();
                    if(g != null) {
                        dao.izmijeniGrad(grad);
                        ArrayList<Grad> gradovi = dao.gradovi();
                        tableViewGradovi.setItems(FXCollections.observableArrayList(gradovi));
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obrisiGradAction(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if(grad != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrdite brisanje");
            alert.setHeaderText("Brisanje grada " + grad.getNaziv());
            alert.setContentText("Da li ste sigurni da želite obrisati grad " + grad.getNaziv() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                dao.obrisiGrad(grad);
                ArrayList<Grad> gradovi = dao.gradovi();
                tableViewGradovi.setItems(FXCollections.observableArrayList(gradovi));
            }
        }
    }
}