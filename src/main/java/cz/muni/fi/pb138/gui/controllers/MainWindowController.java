package cz.muni.fi.pb138.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MainWindowController {

    @FXML
    ListView categoriesList;
    @FXML
    TextField searchTextField;
    @FXML
    TableView mediumsTable;


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
