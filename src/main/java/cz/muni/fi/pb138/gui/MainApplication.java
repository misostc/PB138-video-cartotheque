package cz.muni.fi.pb138.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MainApplication extends Application {

    public void start(Stage stage) throws Exception {

        String fxmlFile = "/fxml/main.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        Scene scene = new Scene(rootNode, 600, 400);

        stage.setTitle("Video Cathotheque");
        stage.setScene(scene);
        stage.show();
    }

}
