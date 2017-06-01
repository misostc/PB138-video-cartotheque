package cz.muni.fi.pb138.gui.view;

import cz.muni.fi.pb138.gui.viewmodel.MediumViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MediumTableCellValueFactory implements Callback<TableColumn.CellDataFeatures<MediumViewModel, String>, ObservableValue<String>> {

    int index;

    public MediumTableCellValueFactory(int index) {
        this.index = index;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<MediumViewModel, String> mediumViewModelStringCellDataFeatures) {
        return new SimpleStringProperty(mediumViewModelStringCellDataFeatures.getValue().getValues().get(index));
    }
}
