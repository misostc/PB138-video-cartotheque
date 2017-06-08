package cz.muni.fi.pb138;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.backend.MediumManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.gui.controllers.MainWindowController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MainApplicationTest extends MainApplication {

    private static CategoryManager categoryManager;

    private static MediumManager mediumManager;

    static {
        categoryManager = mock(CategoryManager.class);
        mediumManager = mock(MediumManager.class);
        List<CategoryDTO> list = Arrays.asList(Mocks.getCategory(), Mocks.getCategory());
        when(categoryManager.getCategories()).thenReturn(list);
        Collection<MediumDTO> mediums = Arrays.asList(Mocks.getMedium(), Mocks.getMedium());
        when(mediumManager.findMediumByCategory(any())).thenReturn(mediums);

        MainWindowController.setCategoryManager(categoryManager);
        MainWindowController.setMediumManager(mediumManager);
    }

    public static void main(String... args) {
        launch(args);
    }

}