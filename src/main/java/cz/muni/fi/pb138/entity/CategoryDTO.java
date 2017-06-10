package cz.muni.fi.pb138.entity;

import java.util.List;

public class CategoryDTO {
    private Integer id;
    private String name;
    private List<String> columns;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String toString() {
        return getName();
    }

    public boolean isValid() {
        return name.trim().length() > 0 && columns.size() > 0;
    }
}
