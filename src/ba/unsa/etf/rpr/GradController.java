package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    private ArrayList<Drzava> drzave;
    private Grad grad;
    private boolean ok = false;
    public ImageView imgGrad;
    public TextField fldPostanskiBroj;

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
            if(grad.getPostanskiBroj() != 0) fldPostanskiBroj.setText(String.valueOf(grad.getPostanskiBroj()));
        }
        else imgGrad.setImage(new Image("/img/question_mark.jpg"));
    }

    public void onOkClick(ActionEvent actionEvent) { //KOMENTARE OVE METODE OTKOMENTARISATI DA BI PROGRAM ISPRAVNO RADIO I DA BI TEST testZadatak6 PROSAO
        //new Thread(() -> {
            boolean fieldNazivOk, fieldBrojStanovnikaOk, fldPostanskiBrojOk = false;
            fieldNazivOk = !fieldNaziv.getText().isBlank();
            int brojStanovnika = 0;
            try {
                brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
            } catch (NumberFormatException ignored) {}
            fieldBrojStanovnikaOk = brojStanovnika > 0;
            int postanskiBroj = 0;
            try {
                postanskiBroj = Integer.parseInt(fldPostanskiBroj.getText());
            } catch (NumberFormatException ignored) {}
            fldPostanskiBrojOk = postanskiBroj > 0 || fldPostanskiBroj.getText().isBlank();
            /*if(!fldPostanskiBroj.getText().isBlank()) {
                try {
                    URL url = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + fldPostanskiBroj.getText());
                    URLConnection yc = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    String inputLine = in.readLine();
                    //while ((inputLine = in.readLine()) != null);
                    fldPostanskiBrojOk = inputLine.equals("OK");
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            boolean finalFldPostanskiBrojOk = fldPostanskiBrojOk;
            Platform.runLater(() -> {
                if(fieldNazivOk && fieldBrojStanovnikaOk && finalFldPostanskiBrojOk) {
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
                if(!fldPostanskiBroj.getText().isBlank()) {
                    if (!finalFldPostanskiBrojOk) {
                        fldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                        fldPostanskiBroj.getStyleClass().add("poljeNeispravno");
                    } else {
                        fldPostanskiBroj.getStyleClass().removeAll("poljeNeispravno");
                        fldPostanskiBroj.getStyleClass().add("poljeIspravno");
                    }
                }
            });
        //}).start();
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
        if(!fldPostanskiBroj.getText().trim().equals(""))
            grad.setPostanskiBroj(Integer.parseInt(fldPostanskiBroj.getText()));
        return grad;
    }

    public void promijeniAction(ActionEvent actionEvent) {
        /*TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Promjena slike");
        textInputDialog.setHeaderText("Unesite put gdje se nalazi fotografija grada:");
        textInputDialog.setContentText("Put:");

        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(url -> {
            imgGrad.setImage(new Image(url));
        });*/
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/pretraga.fxml" ), bundle);
            PretragaController pretragaController = new PretragaController();
            loader.setController(pretragaController);
            Parent root = null;
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(bundle.getString("pretraga"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();

            stage.setOnHidden(event -> {
                if(pretragaController.getPath() != null)
                    imgGrad.setImage(new Image(pretragaController.getPath()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
