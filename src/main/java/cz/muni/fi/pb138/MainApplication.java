package cz.muni.fi.pb138;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

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

        setupUngaughtExceptionHandler();
    }

    private void setupUngaughtExceptionHandler() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception occured.");
            alert.setHeaderText("There has been an unexpected error.");

            StringWriter exceptionWriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(exceptionWriter));

            TextArea textArea = new TextArea();

            textArea.setText(exceptionWriter.toString());
            alert.getDialogPane().setContent(textArea);

            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CLOSE);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.CLOSE) {
                System.exit(0);
            }
        });
    }
}
