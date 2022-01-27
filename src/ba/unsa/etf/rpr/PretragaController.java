package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

public class PretragaController {
    public TextField fieldUzorak;
    public ListView listOfPaths;
    private ObservableList<String> listaPuteva = FXCollections.observableArrayList();
    private String path;
    private boolean kraj = false;

    @FXML
    public void initialize() {
        //SLIKA SE BIRA DVOSTRUKIM KLIKOM MISA
        listOfPaths.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2) {
                kraj = true;
                path = listOfPaths.getSelectionModel().getSelectedItem().toString();
                ((Stage)fieldUzorak.getScene().getWindow()).close();
            }
        });
    }

    private void getPaths(String directory, String pattern) {
        new Thread(() -> {
            File[] files = new File(directory).listFiles();
            if (files != null) {
                for(File file : files) {
                    if(kraj) break;
                    if(file.isDirectory())
                        getPaths(file.getAbsolutePath(), pattern);
                    else if(file.getName().contains(pattern)) {
                        Platform.runLater(() -> {
                            listaPuteva.add(file.getAbsolutePath());
                            listOfPaths.setItems(listaPuteva);
                        });
                    }
                }
            }
        }).start();
    }

    public void traziAction(ActionEvent actionEvent) {
        listOfPaths.getItems().clear();
        listaPuteva.clear();
        String pattern = fieldUzorak.getText();
        File[] directories = File.listRoots();
        for(File directory : directories) {
            if(kraj) break;
            getPaths(directory.getAbsolutePath(), pattern);
        }
    }

    public String getPath() {
        return path;
    }
}
