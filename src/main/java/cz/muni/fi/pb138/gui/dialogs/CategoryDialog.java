package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.entity.CategoryDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by micha on 06.06.2017.
 */
public class CategoryDialog extends Dialog<CategoryDTO> {
    public CategoryDialog() {
        super();

        String fxmlFile = "/fxml/editCategory.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(this);

        BorderPane pane = new BorderPane();
        loader.setRoot(pane);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(pane);

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            // called in case of OK button and valid form or in case of cancel returning null
            return null;
        });


        setTitle("todo change");
        initStyle(StageStyle.UTILITY);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addColumnButtonAction() {

    }
}
