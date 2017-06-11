package cz.muni.fi.pb138.gui.controllers;

import cz.muni.fi.pb138.backend.*;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.*;
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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

    private static void setDocumentProvider(DocumentProvider documentProvider) {
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
        try {
            ObservableList<CategoryDTO> categoryDTOS = FXCollections.observableArrayList(categoryManager.getCategories());
            categoriesList.setCellFactory(new CategoryListCellFactory() {
                @Override
                public ListCell<CategoryDTO> call(ListView<CategoryDTO> categoryDTOListView) {
                    final ListCell<CategoryDTO> cell = super.call(categoryDTOListView);
                    cell.setOnDragOver(ev -> {
                        if (cell.getItem() != null && ev.getDragboard().hasString()) {
                            ev.acceptTransferModes(TransferMode.MOVE);
                        }
                        ev.consume();
                    });

                    cell.setOnDragDropped(ev -> {
                        Dragboard db = ev.getDragboard();
                        boolean success = false;
                        if (db.hasString()) {
                            String id = db.getString();
                            if (draggedMedium != null && id.equals(String.valueOf(draggedMedium.getId()))) {
                                CategoryDTO categoryDTO = cell.getItem();
                                MediumDTO movedMedium = draggedMedium;

                                MediumDialog dialog = new MediumDialog(categoryManager, movedMedium, categoryDTO);
                                dialog.setTitle("Move Medium");
                                dialog.setHeaderText("You are moving medium to a new category. Accept these changes?");

                                Optional<MediumDTO> mediumOptional = dialog.showAndWait();

                                if (mediumOptional.isPresent()) {
                                    MediumDTO newMedium = mediumOptional.get();

                                    try {
                                        mediumManager.removeMedium(movedMedium);
                                        mediumManager.addMedium(newMedium);
                                    } catch (MediumNotRemovedException | MediumNotPersistedException e) {
                                        createExceptionAlert(e);
                                    }

                                    dataUpdated();
                                    selectCategory(newMedium.getCategory());
                                }
                                success = true;
                            }
                        }
                        ev.setDropCompleted(success);
                        ev.consume();
                    });

                    return cell;
                }
            });
            categoriesList.getItems().clear();
            categoriesList.setItems(categoryDTOS);
            categoriesList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> updateMediumList(newValue));

            ContextMenu cm = createCategoryContextMenu();
            categoriesList.setContextMenu(cm);

        } catch (CategoriesNotAvailableException e) {
            createExceptionAlert(e);
        }
    }

    private void createExceptionAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An Error occurred.");

        if (e instanceof CategoriesNotAvailableException) {
            alert.setHeaderText("There was a problem retrieving categories.");
        }
        if (e instanceof CategoryNotPersistedException) {
            alert.setHeaderText("There was a problem saving the category.");
        }
        if (e instanceof CategoryNotRemovedException) {
            alert.setHeaderText("There was a problem removing the category.");
        }
        if (e instanceof DocumentNotSavedException) {
            alert.setHeaderText("There was a problem saving the file.");
        }
        if (e instanceof DocumentNotValidException) {
            alert.setHeaderText("There was a problem opening the file.");
        }
        if (e instanceof MediaNotAvailableException) {
            alert.setHeaderText("There was a problem retrieving media.");
        }
        if (e instanceof MediumNotPersistedException) {
            alert.setHeaderText("There was a problem saving the medium.");
        }
        if (e instanceof MediumNotRemovedException) {
            alert.setHeaderText("There was a problem removing the medium.");
        }

        alert.showAndWait();
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
            try {
                categoryManager.removeCategory(selectedItem);
            } catch (CategoryNotRemovedException e) {
                createExceptionAlert(e);
            }
            dataUpdated();
        }
    }

    private static MediumDTO draggedMedium;

    private void updateMediumList(CategoryDTO newValue) {
        if (newValue == null) {
            mediumsTable.getItems().clear();
            return;
        }
        try {
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

            mediumsTable.setRowFactory((TableView<MediumViewModel> cb) -> {
                TableRow<MediumViewModel> row = new TableRow<>();
                row.setOnDragDetected(ev -> {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);

                    ClipboardContent content = new ClipboardContent();
                    MediumDTO original = row.getItem().getOriginal();
                    content.putString(String.valueOf(original.getId()));
                    db.setContent(content);
                    draggedMedium = original;
                    ev.consume();
                });
                return row;
            });

        } catch (MediaNotAvailableException e) {
            createExceptionAlert(e);
        }
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
            try {
                mediumManager.removeMedium(medium);
            } catch (MediumNotRemovedException e) {
                createExceptionAlert(e);
            }
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

            try {
                mediumManager.removeMedium(original);
                mediumManager.addMedium(newMedium);
            } catch (MediumNotRemovedException | MediumNotPersistedException e) {
                createExceptionAlert(e);
            }

            dataUpdated();
            selectCategory(newMedium.getCategory());
        }
    }


    public void openMenuItemAction() {
        if (documentProvider != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Open");
            alert.setHeaderText("Opening a new file will discard changes you haven't saved. Do you wish to proceed?");
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
            try {
                setDocumentProvider(new ODSDocumentProvider(result.getAbsolutePath()));
                setMediumManager(new MediumManagerImpl(documentProvider));
                setCategoryManager(new CategoryManagerImpl(documentProvider));
                dataUpdated();
            } catch (DocumentNotValidException e) {
                createExceptionAlert(e);
            }
        }
    }

    public void saveMenuItemAction() {
        if (documentProvider != null) {
            try {
                documentProvider.save();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save");
                alert.setHeaderText("Document saved.");
                alert.showAndWait();

            } catch (DocumentNotSavedException e) {
                createExceptionAlert(e);
            }
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
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(aboutTextStream, "UTF-8"))) {
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
            try {
                categoryManager.addCategory(newCategory);
                dataUpdated();
                selectCategory(categoryManager.getCategories().iterator().next());

            } catch (CategoryNotPersistedException | CategoriesNotAvailableException e) {
                createExceptionAlert(e);
            }
        }
    }

    public void createMediumButtonAction() {
        MediumDialog dialog = new MediumDialog(categoryManager, new MediumDTO());
        dialog.setTitle("Create new Medium");
        Optional<MediumDTO> mediumOptional = dialog.showAndWait();

        if (mediumOptional.isPresent()) {
            MediumDTO newMedium = mediumOptional.get();
            try {
                mediumManager.addMedium(newMedium);
            } catch (MediumNotPersistedException e) {
                createExceptionAlert(e);
            }
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

        try {
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
        } catch (MediaNotAvailableException e) {
            createExceptionAlert(e);
        }

    }

}
