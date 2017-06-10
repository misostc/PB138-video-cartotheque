package cz.muni.fi.pb138.entity;

import java.util.List;

public class CategoryDTO {
    String id;
    String name;
    List<String> columns;

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

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String toString() {
        String output = id + " " + name + " columns:";
        for (String item : columns) {
            output = output + " " + item;
        }
        return output;
    }

    public boolean isValid() {
        return name.trim().length() > 0 && columns.size() > 0;
    }
}
