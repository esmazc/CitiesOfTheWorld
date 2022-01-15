package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DrzavaController {
    public TextField fieldNaziv;
    public ChoiceBox<Grad> choiceGrad;
    private ArrayList<Grad> gradovi;
    private Drzava drzava;
    private boolean ok;

    public DrzavaController(Drzava drzava, ArrayList<Grad> gradovi) {
        this.drzava = drzava;
        this.gradovi = gradovi;
    }

    @FXML
    public void initialize() {
        choiceGrad.setItems(FXCollections.observableArrayList(gradovi));
        choiceGrad.getSelectionModel().selectFirst();
        if(drzava != null) {
            choiceGrad.getSelectionModel().select(drzava.getGlavniGrad());
            fieldNaziv.setText(drzava.getNaziv());
        }
    }

    public void onOkClick(ActionEvent actionEvent) {
        if(fieldNaziv.getText().isBlank()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNeispravno");
        }
        else {
            fieldNaziv.getStyleClass().removeAll("poljeNeispravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
            ok = true;
            ((Stage)fieldNaziv.getScene().getWindow()).close();
        }
    }

    public void onCancelClick(ActionEvent actionEvent) {
        ((Stage)fieldNaziv.getScene().getWindow()).close();
    }

    public Drzava getDrzava() {
        if(!ok) return null;
        if(drzava == null) drzava = new Drzava();
        drzava.setNaziv(fieldNaziv.getText());
        drzava.setGlavniGrad(choiceGrad.getValue());
        return drzava;
    }
}
