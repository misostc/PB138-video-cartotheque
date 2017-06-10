package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import javafx.event.ActionEvent;
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

/**
 * Created by micha on 06.06.2017.
 */
public class CategoryDialog extends Dialog<CategoryDTO> {
    @FXML
    private ListView categoryListView;
    @FXML
    private TextField newColumnField;
    @FXML
    private TextField categoryNameField;
    private CategoryDTO categoryDTO;

    public CategoryDialog(CategoryManager categoryManager, CategoryDTO categoryDTO) {
        super();
        CategoryManager categoryManager1 = categoryManager;
        this.categoryDTO = categoryDTO;

        String fxmlFile = "/fxml/category.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(this);

        BorderPane pane = new BorderPane();
        loader.setRoot(pane);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(pane);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setDefaultButton(false);
        initStyle(StageStyle.DECORATED);

        setResultConverter(dialogButton -> (dialogButton == ButtonType.OK) ? createCategory() : null);

        okButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    if (createCategory() == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("You need to fill name and at least one column!");
                        alert.showAndWait();
                        event.consume();
                    }
                }
        );

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

    private CategoryDTO createCategory() {

        categoryDTO = new CategoryDTO();
        categoryDTO.setName(categoryNameField.getText());
        List<String> columnList = new ArrayList<String>(categoryListView.getItems());
        categoryDTO.setColumns(columnList);

        if (categoryDTO.isValid()) {
            categoryListView.getItems().add(0, "Id");
            return categoryDTO;
        } else {
            return null;
        }


    }


}
