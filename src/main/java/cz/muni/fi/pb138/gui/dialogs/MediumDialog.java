package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.gui.view.CategoryListCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
    GridPane valuesGridPane;
    @FXML
    ComboBox<CategoryDTO> categoryComboBox;
    private CategoryManager categoryManager;
    private MediumDTO mediumDTO;
    private String[] values = new String[100];

    public MediumDialog(CategoryManager categoryManager, MediumDTO mediumDTO) {
        super();
        this.categoryManager = categoryManager;
        this.mediumDTO = mediumDTO;

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
        mediumDTO.setValues(new ArrayList<String>(Arrays.asList(values)));
        return mediumDTO;
    }

    @FXML
    private void initialize() {
        getValues(mediumDTO);
        updateCategoryComboBox(mediumDTO.getCategory());
        updateValuesGrid(mediumDTO.getCategory());
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
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                values[id] = newValue;
            });
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
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryManager.getCategories()));
        categoryComboBox.setPlaceholder(new TextField("Select Category"));
        categoryComboBox.setCellFactory(new CategoryListCellFactory());
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateValuesGrid(newValue));
        if (category != null) {
            categoryComboBox.getSelectionModel().select(category);
        }
    }

}