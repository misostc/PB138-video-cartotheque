package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.ColumnDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by micha on 06.06.2017.
 */
public class CategoryDialog extends Dialog<CategoryDTO> {
    @FXML
    public ListView categoryListView;
    @FXML
    public TextField newColumnField;
    @FXML
    public TextField categoryNameField;

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
        Button noButton = (Button) dialogPane.lookupButton(loginButtonType);
        noButton.setDefaultButton(false);


        setResultConverter(dialogButton -> {
            return createCategory();
        });


        setTitle("Create Category");
        initStyle(StageStyle.UTILITY);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addColumnButtonAction() {
        if (newColumnField.getText().trim().length() > 0)
            categoryListView.getItems().add(newColumnField.getText());
    }

    public void deletePressed(KeyEvent event) {
        if (categoryListView.getSelectionModel().getSelectedItem() != null && event.getCode().equals(KeyCode.DELETE))
            categoryListView.getItems().remove(categoryListView.getSelectionModel().getSelectedItem());
    }

    public void editAction() {
        System.out.println("editing");
    }

    public CategoryDTO createCategory() {

        CategoryDTO newCategory = new CategoryDTO();
        //todo generate unique ID
        newCategory.setId(UUID.randomUUID().toString());
        newCategory.setName(categoryNameField.getText());

        List<ColumnDTO> columnList = new ArrayList<>();
        List<String> valuesList = categoryListView.getItems();
        for (String item : valuesList) {
            ColumnDTO tmp = new ColumnDTO();
            tmp.setId(Integer.toString(columnList.size() + 1));
            tmp.setName(item);
            columnList.add(tmp);
        }
        newCategory.setColumns(columnList);
        System.out.println("Creating category " + newCategory.toString());

        if (newCategory.isValid())
            return newCategory;
        else
            return null;

    }
}
