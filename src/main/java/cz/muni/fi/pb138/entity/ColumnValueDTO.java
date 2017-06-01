package cz.muni.fi.pb138.entity;

public class ColumnValueDTO {
    ColumnDTO column;
    String value;

    public ColumnDTO getColumn() {
        return column;
    }

    public void setColumn(ColumnDTO column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
