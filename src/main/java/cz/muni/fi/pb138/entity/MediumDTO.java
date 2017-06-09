package cz.muni.fi.pb138.entity;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class MediumDTO {
    CategoryDTO category;
    List<String> values = new ArrayList<>();
    int id;

    public int getId() { return id; }

    public void setId(int id) { this.id=id; }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
