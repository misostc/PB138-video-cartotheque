package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.entity.MediumDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by micha on 02.06.2017.
 */
public class MediumSearchResultsPane {
    private TableView<MediumSearchResult> resultTable;
    private Node node;

    public MediumSearchResultsPane(Collection<MediumDTO> mediums) {
        if (mediums.size() > 0) {
            constructResultTable(mediums);
            this.node = resultTable;
        } else {
            this.node = new Pane();
        }
    }

    private void constructResultTable(Collection<MediumDTO> mediums) {
        List<MediumSearchResult> results = mediums.stream().map(MediumSearchResult::new).collect(Collectors.toList());
        int numberOfColumns = results.stream().map(MediumSearchResult::getValues).map(List::size).max(Comparator.naturalOrder()).get();
        numberOfColumns++; // we count category name as well

        resultTable = new TableView<>();
        resultTable.getItems().addAll(results);
        for (int index = 0; index < numberOfColumns; index++) {
            final int id = index;
            TableColumn<MediumSearchResult, String> column = new TableColumn<>();
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCell(id)));
            resultTable.getColumns().add(column);
        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    private class MediumSearchResult {
        private final String categoryName;
        private final List<String> columnNames;
        private final List<String> values;

        public MediumSearchResult(MediumDTO mediumDTO) {
            this.categoryName = mediumDTO.getCategory().getName();
            this.values = mediumDTO.getValues();
            this.columnNames = mediumDTO.getCategory().getColumns();
        }

        public String getCategoryName() {
            return categoryName;
        }

        public List<String> getValues() {
            return values;
        }

        public String getCell(int index) {
            if (index == 0) {
                return getCategoryName();
            } else {
                index--;
                if (index < values.size()) {
                    String value = values.get(index);
                    String columnName = index < columnNames.size() ? columnNames.get(index) : "";
                    return String.format("%s [%s]", value, columnName);
                } else {
                    return "";
                }
            }
        }
    }
}
