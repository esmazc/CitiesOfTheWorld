package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

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
    //public TableColumn colGradPostanskiBroj;
    public TableColumn<Grad, String> colGradPostanskiBroj;
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
        //colGradPostanskiBroj.setCellValueFactory(new PropertyValueFactory("postanskiBroj"));
        colGradPostanskiBroj.setCellValueFactory(data -> {
            String postanskiBroj = String.valueOf(data.getValue().getPostanskiBroj());
            if(postanskiBroj.equals("0"))
                return new SimpleStringProperty("");
            return new SimpleStringProperty(postanskiBroj);
        });
    }

    public void dodajGradAction(ActionEvent actionEvent) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/grad.fxml" ), bundle);
            GradController gradController = new GradController(null, dao.drzave());
            loader.setController(gradController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(bundle.getString("grad"));
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
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/drzava.fxml" ), bundle);
            DrzavaController drzavaController = new DrzavaController(drzava, dao.gradovi());
            loader.setController(drzavaController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(bundle.getString("drzava"));
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
                ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/grad.fxml" ), bundle);
                GradController gradController = new GradController(grad, dao.drzave());
                loader.setController(gradController);
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle(bundle.getString("grad"));
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
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("potvrdaBrisanja"));
            alert.setHeaderText(bundle.getString("brisanjeGrada") + grad.getNaziv());
            alert.setContentText(bundle.getString("pitanje") + grad.getNaziv() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                dao.obrisiGrad(grad);
                ArrayList<Grad> gradovi = dao.gradovi();
                tableViewGradovi.setItems(FXCollections.observableArrayList(gradovi));
            }
        }
    }

    public void stampaAction(ActionEvent actionEvent) {
        try {
            new GradoviReport().showReport(dao.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void jezikAction(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>();
        ObservableList<String> list = choiceDialog.getItems();
        list.add("Bosanski");
        list.add("Deutsch");
        list.add("English");
        list.add("Français");
        choiceDialog.setSelectedItem(choiceDialog.getItems().get(2));
        choiceDialog.setTitle(bundle.getString("jezik"));
        choiceDialog.setHeaderText(bundle.getString("izaberiteJezik"));
        choiceDialog.showAndWait();
        if(choiceDialog.getSelectedItem() != null) {
            if (choiceDialog.getSelectedItem().equals("Bosanski"))
                Locale.setDefault(new Locale("bs", "BA"));
            else if (choiceDialog.getSelectedItem().equals("Deutsch"))
                Locale.setDefault(new Locale("de", "DE"));
            else if (choiceDialog.getSelectedItem().equals("English"))
                Locale.setDefault(new Locale("en", "US"));
            else if (choiceDialog.getSelectedItem().equals("Français"))
                Locale.setDefault(new Locale("fr", "FR"));
        }
        try {
            GlavnaController ctrl = new GlavnaController();
            Stage stage = (Stage)tableViewGradovi.getScene().getWindow();
            ResourceBundle bundle1 = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle1);
            loader.setController(ctrl);
            Parent root = loader.load();
            stage.setTitle(bundle.getString("gradoviSvijeta"));
            stage.setScene(new Scene(root, stage.getWidth() - 15, stage.getHeight() - 35));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
