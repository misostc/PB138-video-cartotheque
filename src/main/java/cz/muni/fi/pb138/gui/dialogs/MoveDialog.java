package cz.muni.fi.pb138.gui.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by Roman on 08.06.2017.
 */
public class MoveDialog extends Dialog {
    @FXML
    public ListView categoryListView;
    @FXML
    public TextField newColumnField;
    @FXML
    public TextField categoryNameField;

    public MoveDialog() {
        super();

        String fxmlFile = "/fxml/move.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(this);

        BorderPane pane = new BorderPane();
        loader.setRoot(pane);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(pane);

        dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        setResultConverter(dialogButton -> {
            return dialogButton.equals(ButtonType.YES);
        });


        setTitle("Move Media");
        initStyle(StageStyle.UTILITY);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
