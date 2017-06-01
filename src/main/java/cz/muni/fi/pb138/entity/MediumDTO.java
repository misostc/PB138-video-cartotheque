package cz.muni.fi.pb138.entity;

import java.util.Collection;

public class MediumDTO {
    CategoryDTO category;
    Collection<ColumnValueDTO> values;

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public Collection<ColumnValueDTO> getValues() {
        return values;
    }

    public void setValues(Collection<ColumnValueDTO> values) {
        this.values = values;
    }
}
