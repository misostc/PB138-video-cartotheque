package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.gui.view.CategoryListCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 06.06.2017.
 */
public class MediumDialog extends Dialog<MediumDTO> {
    private final Button okButton;
    @FXML
    private
    GridPane valuesGridPane;
    @FXML
    private
    ComboBox<CategoryDTO> categoryComboBox;
    private final CategoryManager categoryManager;
    private MediumDTO mediumDTO;
    private final String[] values = new String[100];
    private CategoryDTO preselectedCategory;


    public MediumDialog(CategoryManager categoryManager, MediumDTO mediumDTO) {
        this(categoryManager, mediumDTO, null);
    }

    public MediumDialog(CategoryManager categoryManager, MediumDTO mediumDTO, CategoryDTO preselectedCategory) {
        super();
        this.categoryManager = categoryManager;
        this.mediumDTO = mediumDTO;
        this.preselectedCategory = preselectedCategory;

        String fxmlFile = "/fxml/medium.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(this);

        VBox pane = new VBox();
        loader.setRoot(pane);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(pane);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(dialogButton -> (dialogButton == ButtonType.OK) ? validateMedium() : null);
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        initStyle(StageStyle.DECORATED);

        okButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {

                    if (validateMedium() == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("You need to fill at least one column!");
                        alert.showAndWait();
                        event.consume();
                    }
                }
        );

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MediumDTO validateMedium() {
        CategoryDTO selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            return null;
        }

        mediumDTO = new MediumDTO();
        mediumDTO.setCategory(selectedCategory);
        mediumDTO.setValues(constructValues(selectedCategory));
        if (mediumDTO.isValid()) {
            return mediumDTO;
        } else {
            return null;
        }
    }

    private List<String> constructValues(CategoryDTO selectedCategory) {
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(values).subList(0, selectedCategory.getColumns().size()));
        return result;
    }

    @FXML
    private void initialize() {
        getValues(mediumDTO);
        if (preselectedCategory != null) {
            updateCategoryComboBox(preselectedCategory);
            updateCategoryComboBox(preselectedCategory);
        } else {
            updateCategoryComboBox(mediumDTO.getCategory());
            updateValuesGrid(mediumDTO.getCategory());
        }

    }

    private void getValues(MediumDTO mediumDTO) {
        int index = 0;
        for (String value : mediumDTO.getValues()) {
            this.values[index] = value;
            index++;
        }
    }

    private void updateValuesGrid(CategoryDTO category) {
        valuesGridPane.getChildren().clear();
        valuesGridPane.setHgap(10.0);
        valuesGridPane.setVgap(10.0);

        if (category == null) {
            return;
        }

        List<String> columns = category.getColumns();
        int rowIndex = 0;
        for (String columnDTO : columns) {
            valuesGridPane.add(new Label(columnDTO), 0, rowIndex);
            TextField field = new TextField();
            field.setId(Integer.toString(rowIndex));
            final int id = rowIndex;
            field.textProperty().addListener((observable, oldValue, newValue) -> values[id] = newValue);
            valuesGridPane.add(field, 1, rowIndex);
            rowIndex++;
        }

        okButton.setDisable(false);
        updateTextFields();
    }

    private void updateTextFields() {
        FilteredList<Node> textFields = valuesGridPane.getChildren().filtered(node -> node instanceof TextField);

        int index = 0;
        for (Node node : textFields) {
            TextField textField = (TextField) node;
            if (values[index] != null) {
                textField.textProperty().setValue(values[index]);
            }
            index++;
        }
    }

    private void updateCategoryComboBox(CategoryDTO category) {
        try {
            categoryComboBox.setItems(FXCollections.observableArrayList(categoryManager.getCategories()));
            categoryComboBox.setPlaceholder(new TextField("Select Category"));
            categoryComboBox.setCellFactory(new CategoryListCellFactory());
            categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateValuesGrid(newValue));
            if (category != null) {
                categoryComboBox.getSelectionModel().select(category);
            }
        } catch (CategoriesNotAvailableException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An Error occurred.");
            alert.setHeaderText("There was a problem loading a list of categories.");
            alert.showAndWait();
            close();
        }

    }
}
