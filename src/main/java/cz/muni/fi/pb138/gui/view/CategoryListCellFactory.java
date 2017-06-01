package cz.muni.fi.pb138.gui.view;

import cz.muni.fi.pb138.entity.CategoryDTO;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class CategoryListCellFactory implements Callback<ListView<CategoryDTO>, ListCell<CategoryDTO>> {
    @Override
    public ListCell<CategoryDTO> call(ListView<CategoryDTO> categoryDTOListView) {
        return new ListCell<CategoryDTO>() {
            @Override
            protected void updateItem(CategoryDTO categoryDTO, boolean empty) {
                super.updateItem(categoryDTO, empty);
                textProperty().unbind();
                if (categoryDTO != null) {
                    textProperty().bind(Bindings.format("%s", categoryDTO.getName()));
                }
            }
        };
    }
}
