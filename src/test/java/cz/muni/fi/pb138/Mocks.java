package cz.muni.fi.pb138;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class Mocks {

    public static CategoryDTO getCategory() {
        CategoryDTO result = mock(CategoryDTO.class);
        when(result.getId()).thenReturn(0);
        when(result.getName()).thenReturn("Category" + result.hashCode());
        List<String> list = Arrays.asList("Column1", "Column2", "Column3");
        when(result.getColumns()).thenReturn(list);
        return result;
    }

    public static MediumDTO getMedium() {
        MediumDTO result = mock(MediumDTO.class);
        CategoryDTO category = getCategory();
        when(result.getCategory()).thenReturn(category);
        List<String> list = Arrays.asList("Value1", "Value2", "Value3");
        when(result.getValues()).thenReturn(list);
        return result;
    }
}
