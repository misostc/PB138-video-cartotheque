package cz.muni.fi.pb138.entity;

import java.util.Collection;

public class CategoryDTO {
    String id;
    String name;
    Collection<ColumnDTO> columns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<ColumnDTO> getColumns() {
        return columns;
    }

    public void setColumns(Collection<ColumnDTO> columns) {
        this.columns = columns;
    }
}
