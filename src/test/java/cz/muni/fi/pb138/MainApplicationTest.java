package cz.muni.fi.pb138;

import cz.muni.fi.pb138.backend.CategoryManager;
import cz.muni.fi.pb138.backend.MediumManager;
import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.exceptions.MediaNotAvailableException;
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
        try {
            categoryManager = mock(CategoryManager.class);
            mediumManager = mock(MediumManager.class);
            List<CategoryDTO> list = Arrays.asList(Mocks.getCategory(), Mocks.getCategory());
            when(categoryManager.getCategories()).thenReturn(list);

            Collection<MediumDTO> mediums = Arrays.asList(Mocks.getMedium(), Mocks.getMedium());
            when(mediumManager.findMediumByCategory(any())).thenReturn(mediums);
            List<MediumDTO> searchResults = Arrays.asList(Mocks.getMedium(), Mocks.getMedium(), Mocks.getMedium(), Mocks.getMedium());
            when(mediumManager.findMediumByValue(any())).thenReturn(searchResults);

            MainWindowController.setCategoryManager(categoryManager);
            MainWindowController.setMediumManager(mediumManager);
        } catch (CategoriesNotAvailableException | MediaNotAvailableException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) {
        launch(args);
    }

}