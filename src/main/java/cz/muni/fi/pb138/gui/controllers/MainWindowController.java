package cz.muni.fi.pb138.gui.controllers;

import cz.muni.fi.pb138.backend.*;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.gui.dialogs.CategoryDialog;
import cz.muni.fi.pb138.gui.dialogs.MediumDialog;
import cz.muni.fi.pb138.gui.dialogs.MediumSearchResultsPane;
import cz.muni.fi.pb138.gui.view.CategoryListCellFactory;
import cz.muni.fi.pb138.gui.view.MediumTableCellValueFactory;
import cz.muni.fi.pb138.gui.viewmodel.MediumViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MainWindowController {

    private static DocumentProvider documentProvider;
    private static MediumManager mediumManager;
    private static CategoryManager categoryManager;

    @FXML
    SplitPane mainPane;
    @FXML
    MenuItem openMenuItem;
    @FXML
    MenuItem saveMenuItem;
    @FXML
    ListView<CategoryDTO> categoriesList;
    @FXML
    TextField searchTextField;
    @FXML
    TableView<MediumViewModel> mediumsTable;

    public static void setDocumentProvider(DocumentProvider documentProvider) {
        MainWindowController.documentProvider = documentProvider;
    }

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
            mainPane.setDisable(false);
            saveMenuItem.setDisable(false);
            updateCategoriesList();
        }

        mediumsTable.getColumns().clear();
        mediumsTable.getItems().clear();
    }

    private void updateCategoriesList() {
        ObservableList<CategoryDTO> categoryDTOS = FXCollections.observableArrayList(categoryManager.getCategories());
        categoriesList.setCellFactory(new CategoryListCellFactory());
        categoriesList.getItems().clear();
        categoriesList.setItems(categoryDTOS);
        categoriesList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            updateMediumList(newValue);
        });

        ContextMenu cm = createCategoryContextMenu();
        categoriesList.setContextMenu(cm);
    }

    private ContextMenu createCategoryContextMenu() {
        ContextMenu result = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(this::deleteCategoryMenuAction);

        result.getItems().addAll(deleteMenuItem);
        return result;
    }

    private void deleteCategoryMenuAction(ActionEvent actionEvent) {
        CategoryDTO selectedItem = categoriesList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Do you wish to delete the whole category? Doing so will also remove ALL media under it.");
        Optional<ButtonType> chosenButton = alert.showAndWait();

        if (chosenButton.isPresent() && chosenButton.get().equals(ButtonType.OK)) {
            categoryManager.removeCategory(selectedItem);
            dataUpdated();
        }
    }

    private void updateMediumList(CategoryDTO newValue) {
        if (newValue == null) {
            mediumsTable.getItems().clear();
            return;
        }

        Collection<MediumDTO> mediumDTOS = mediumManager.findMediumByCategory(newValue);
        List<MediumViewModel> mediumViewModels = mediumDTOS.stream().map(MediumViewModel::new).collect(Collectors.toList());
        mediumsTable.getItems().clear();
        mediumsTable.setItems(FXCollections.observableList(mediumViewModels));

        int index = 0;
        mediumsTable.getColumns().clear();
        for (String column : newValue.getColumns()) {
            TableColumn<MediumViewModel, String> tableColumn = new TableColumn<>(column);
            tableColumn.setCellValueFactory(new MediumTableCellValueFactory(index++));
            mediumsTable.getColumns().add(tableColumn);
        }

        ContextMenu cm = createTableContextMenu();
        mediumsTable.setContextMenu(cm);
    }

    private ContextMenu createTableContextMenu() {
        ContextMenu result = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(this::editMediumMenuAction);
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(this::deleteMediumMenuAction);

        result.getItems().addAll(editMenuItem, deleteMenuItem);
        return result;
    }

    private void deleteMediumMenuAction(ActionEvent actionEvent) {
        MediumViewModel selectedItem = mediumsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Do you wish to delete the medium item?");
        Optional<ButtonType> chosenButton = alert.showAndWait();

        if (chosenButton.isPresent() && chosenButton.get().equals(ButtonType.OK)) {
            MediumDTO medium = selectedItem.getOriginal();
            mediumManager.removeMedium(medium);
            dataUpdated();
            selectCategory(medium.getCategory());
        }
    }

    private void selectCategory(CategoryDTO category) {
        categoriesList.getSelectionModel().select(category);
    }

    private void editMediumMenuAction(ActionEvent event) {
        MediumViewModel selectedItem = mediumsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        MediumDTO original = selectedItem.getOriginal();
        MediumDialog dialog = new MediumDialog(categoryManager, original);
        dialog.setTitle("Edit Medium");
        Optional<MediumDTO> mediumOptional = dialog.showAndWait();

        if (mediumOptional.isPresent()) {
            MediumDTO newMedium = mediumOptional.get();

            mediumManager.removeMedium(original);
            mediumManager.addMedium(newMedium);

            dataUpdated();
            selectCategory(newMedium.getCategory());
        }
    }


    public void openMenuItemAction() {
        if (documentProvider != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Open");
            alert.setHeaderText("Opening a new file will discard changes you havent saved. Do you wish to proceed?");
            Optional<ButtonType> chosenButton = alert.showAndWait();

            if (chosenButton.isPresent() && !chosenButton.get().equals(ButtonType.OK)) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ODS file", "*.ods"));
        fileChooser.setTitle("Open ODS file");
        File result = fileChooser.showOpenDialog(new Stage());
        if (result != null) {
            setDocumentProvider(new ODSDocumentProvider(result.getAbsolutePath()));
            setMediumManager(new MediumManagerImpl(documentProvider));
            setCategoryManager(new CategoryManagerImpl(documentProvider));
            dataUpdated();
        }
    }

    public void saveMenuItemAction() {
        if (documentProvider != null) {
            documentProvider.save();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save");
            alert.setHeaderText("Document saved.");
            alert.showAndWait();
        }
    }

    public void closeMenuItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Do you really want to close? You can lose unsaved changes");

        alert.getButtonTypes().setAll(ButtonType.CLOSE, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.CLOSE) {
            Platform.exit();
        }
    }

    public void aboutMenuItemAction() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("About Video Carthotheque");
        alert.setHeaderText("PB138 Video Carthotheque");
        TextArea textArea = new TextArea();

        String result;
        InputStream aboutTextStream = getClass().getResourceAsStream("/text/about.txt");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(aboutTextStream))) {
            result = buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            result = "https://github.com/misostc/PB138-video-cartotheque";
        }

        textArea.setText(result);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    public void createCategoryButtonAction() {
        CategoryDialog dialog = new CategoryDialog(categoryManager, new CategoryDTO());
        Optional<CategoryDTO> categoryOptional = dialog.showAndWait();

        if (categoryOptional.isPresent()) {
            CategoryDTO newCategory = categoryOptional.get();
            categoryManager.addCategory(newCategory);
            dataUpdated();
            selectCategory(categoryManager.getCategories().iterator().next());
        }
    }

    public void createMediumButtonAction() {
        MediumDialog dialog = new MediumDialog(categoryManager, new MediumDTO());
        dialog.setTitle("Create new Medium");
        Optional<MediumDTO> mediumOptional = dialog.showAndWait();

        if (mediumOptional.isPresent()) {
            MediumDTO newMedium = mediumOptional.get();
            mediumManager.addMedium(newMedium);
            dataUpdated();
            selectCategory(newMedium.getCategory());
        }
    }

    public void searchMediumButtonAction() {
        String query = searchTextField.getText().trim();

        if (query.isEmpty()) {
            searchTextField.requestFocus();
            return;
        }

        Collection<MediumDTO> mediums = mediumManager.findMediumByValue(query);
        MediumSearchResultsPane searchResultsPane = new MediumSearchResultsPane(mediums);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search results");
        alert.setHeaderText(String.format("%d results found.", mediums.size()));
        alert.getDialogPane().setContent(searchResultsPane.getNode());
        alert.getDialogPane().setMinWidth(500.0);
        alert.getDialogPane().setMinHeight(150.0);
        alert.setWidth(500.0);
        alert.setHeight(150.0);
        alert.showAndWait();
    }

}
