package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //GeografijaDAO dao = GeografijaDAO.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/glavna.fxml"), bundle);
        //GlavnaController glavnaController = new GlavnaController(dao);
        GlavnaController glavnaController = new GlavnaController();
        loader.setController(glavnaController);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("gradoviSvijeta"));
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
    }
}
