package cz.muni.fi.pb138.gui.controllers;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.backend.MediumManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.ColumnDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.gui.view.CategoryListCellFactory;
import cz.muni.fi.pb138.gui.view.MediumTableCellValueFactory;
import cz.muni.fi.pb138.gui.viewmodel.MediumViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MainWindowController {

    private static MediumManager mediumManager;
    private static CategoryManager categoryManager;
    @FXML
    SplitPane mainPane;
    @FXML
    MenuItem saveMenuItem;
    @FXML
    ListView<CategoryDTO> categoriesList;
    @FXML
    TextField searchTextField;
    @FXML
    TableView<MediumViewModel> mediumsTable;

    public static void setMediumManager(MediumManager mediumManager) {
        MainWindowController.mediumManager = mediumManager;
    }

    public static void setCategoryManager(CategoryManager categoryManager) {
        MainWindowController.categoryManager = categoryManager;
    }

    @FXML
    public void initialize() {
        dataUpdated();
    }

    private void dataUpdated() {
        if (mediumManager == null || categoryManager == null) {
            mainPane.setDisable(true);
            saveMenuItem.setDisable(true);
        } else {
            updateCategoriesList();
        }
    }

    private void updateCategoriesList() {
        ObservableList<CategoryDTO> categoryDTOS = FXCollections.observableArrayList(categoryManager.getCategoriesWithBasicDetails());
        categoriesList.setCellFactory(new CategoryListCellFactory());
        categoriesList.setItems(categoryDTOS);
        categoriesList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            updateMediumList(newValue);
        });
    }

    private void updateMediumList(CategoryDTO newValue) {
        Collection<MediumDTO> mediumDTOS = mediumManager.findMediumByCategory(newValue);
        List<MediumViewModel> mediumViewModels = mediumDTOS.stream().map(MediumViewModel::new).collect(Collectors.toList());
        mediumsTable.getItems().clear();
        mediumsTable.setItems(FXCollections.observableList(mediumViewModels));

        int index = 0;
        mediumsTable.getColumns().clear();
        for (ColumnDTO columnDTO : newValue.getColumns()) {
            TableColumn<MediumViewModel, String> tableColumn = new TableColumn<>(columnDTO.getName());
            tableColumn.setCellValueFactory(new MediumTableCellValueFactory(index++));
            mediumsTable.getColumns().add(tableColumn);
        }
    }


    public void openMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Open Menu Item Performed");
        alert.showAndWait();
    }

    public void saveMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Menu Item Performed");
        alert.showAndWait();

    }

    public void closeMenuItemAction() {
        Platform.exit();
    }

    public void aboutMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Menu Item Performed");
        alert.showAndWait();
    }

    public void createCategoryButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create category button clicked");
        alert.showAndWait();

    }

    public void createMediumButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create medium button clicked");
        alert.showAndWait();

    }

    public void searchMediumButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search medium button clicked");
        alert.setHeaderText("Searched text: " + searchTextField.getText());
        alert.showAndWait();
    }

}
