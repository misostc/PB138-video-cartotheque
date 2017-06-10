package cz.muni.fi.pb138.entity;

import java.util.ArrayList;
import java.util.List;

public class MediumDTO {
    private CategoryDTO category;
    private List<String> values = new ArrayList<>();
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isValid() {
        for (String s : values) {
            if (s != null) {
                return true;
            }
        }
        return false;
    }
}
