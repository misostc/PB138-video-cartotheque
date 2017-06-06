package cz.muni.fi.pb138.gui.controllers;

import cz.muni.fi.pb138.backend.*;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.ColumnDTO;
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
        CategoryDialog dialog = new CategoryDialog();
        Optional<CategoryDTO> categoryDTO = dialog.showAndWait();
        System.out.println(categoryDTO);
        //maybe better to handle using exception?
    if(!categoryDTO.toString().equals("Optional.empty"))
        categoriesList.getItems().add(categoryDTO.get());
    }

    public void createMediumButtonAction() {
        MediumDialog dialog = new MediumDialog();
        Optional<MediumDTO> mediumDTO = dialog.showAndWait();
        System.out.println(mediumDTO);
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
        alert.getDialogPane().setContent(searchResultsPane);
        alert.showAndWait();
    }

}
