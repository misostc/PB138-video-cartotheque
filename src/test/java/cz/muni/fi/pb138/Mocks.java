package cz.muni.fi.pb138;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.ColumnDTO;
import cz.muni.fi.pb138.entity.ColumnValueDTO;
import cz.muni.fi.pb138.entity.MediumDTO;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class Mocks {

    public static ColumnDTO getColumn() {
        ColumnDTO result = mock(ColumnDTO.class);
        when(result.getId()).thenReturn("1");
        when(result.getName()).thenReturn("Column" + result.hashCode());
        return result;
    }

    public static ColumnValueDTO getColumnValue() {
        ColumnValueDTO result = mock(ColumnValueDTO.class);
        ColumnDTO column = getColumn();
        when(result.getColumn()).thenReturn(column);
        when(result.getValue()).thenReturn("Column value" + result.hashCode());
        return result;
    }

    public static CategoryDTO getCategory() {
        CategoryDTO result = mock(CategoryDTO.class);
        when(result.getId()).thenReturn("0");
        when(result.getName()).thenReturn("Category" + result.hashCode());
        List<ColumnDTO> list = Arrays.asList(getColumn(), getColumn(), getColumn());
        when(result.getColumns()).thenReturn(list);
        return result;
    }

    public static MediumDTO getMedium() {
        MediumDTO result = mock(MediumDTO.class);
        CategoryDTO category = getCategory();
        when(result.getCategory()).thenReturn(category);
        List<ColumnValueDTO> list = Arrays.asList(getColumnValue(), getColumnValue(), getColumnValue());
        when(result.getValues()).thenReturn(list);
        return result;
    }
}
