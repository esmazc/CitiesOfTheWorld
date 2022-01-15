package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    private ArrayList<Drzava> drzave;
    private Grad grad;
    private boolean ok = false;
    public ImageView imgGrad;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        this.drzave = drzave;
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(FXCollections.observableArrayList(drzave));
        choiceDrzava.getSelectionModel().selectFirst();
        if(grad != null) {
            choiceDrzava.getSelectionModel().select(grad.getDrzava());
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(String.valueOf(grad.getBrojStanovnika()));
            imgGrad.setImage(new Image(grad.getSlika()));
        }
        else imgGrad.setImage(new Image("/img/question_mark.jpg"));
    }

    public void onOkClick(ActionEvent actionEvent) {
        boolean fieldNazivOk, fieldBrojStanovnikaOk;
        fieldNazivOk = !fieldNaziv.getText().isBlank();
        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException ignored) {}
        fieldBrojStanovnikaOk = brojStanovnika > 0;

        if(fieldNazivOk && fieldBrojStanovnikaOk) {
            ok = true;
            ((Stage) fieldNaziv.getScene().getWindow()).close();
        }
        if(!fieldNazivOk) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNeispravno");
        }
        else {
            fieldNaziv.getStyleClass().removeAll("poljeNeispravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }
        if(!fieldBrojStanovnikaOk) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNeispravno");
        }
        else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNeispravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }
    }

    public void onCancelClick(ActionEvent actionEvent) {
        ((Stage)fieldNaziv.getScene().getWindow()).close();
    }

    public Grad getGrad() {
        if(!ok) return null;
        if(grad == null) grad = new Grad();
        grad.setNaziv(fieldNaziv.getText());
        grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        grad.setDrzava(choiceDrzava.getValue());
        grad.setSlika(imgGrad.getImage().getUrl());
        return grad;
    }

    public void promijeniAction(ActionEvent actionEvent) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Promjena slike");
        textInputDialog.setHeaderText("Unesite put gdje se nalazi fotografija grada:");
        textInputDialog.setContentText("Put:");

        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(url -> {
            imgGrad.setImage(new Image(url));
        });
    }
}
