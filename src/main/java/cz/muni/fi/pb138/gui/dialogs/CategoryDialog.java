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
    private CategoryManager categoryManager;
    private CategoryDTO categoryDTO;

    @FXML
    public ListView categoryListView;
    @FXML
    public TextField newColumnField;
    @FXML
    public TextField categoryNameField;

    private final Button okButton;
    private final Button cancelButton;

    public CategoryDialog(CategoryManager categoryManager, CategoryDTO categoryDTO) {
        super();
        this.categoryManager = categoryManager;
        this.categoryDTO = categoryDTO;

        String fxmlFile = "/fxml/editCategory.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(this);

        BorderPane pane = new BorderPane();
        loader.setRoot(pane);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(pane);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        cancelButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setDefaultButton(false);
        initStyle(StageStyle.DECORATED);

        setResultConverter(dialogButton -> (dialogButton == ButtonType.OK) ? createCategory() : null);

        okButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    if (createCategory()==null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("You need to fill name and at least one column!");
                        alert.showAndWait();
                        event.consume();
                    }
                }
        );



        System.out.println("editing");

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

        categoryDTO = new CategoryDTO();
        //todo generate unique ID
        categoryDTO.setId(Integer.toString(categoryManager.getCategories().size()+1));
        categoryDTO.setName(categoryNameField.getText());
        List<String> columnList = new ArrayList<String>(categoryListView.getItems());
        categoryDTO.setColumns(columnList);

        if (categoryDTO.isValid())
            return categoryDTO;
        else
        {
            return null;
        }


    }


}
