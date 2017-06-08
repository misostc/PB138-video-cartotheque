package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;

import java.util.Collection;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class CategoryManagerImpl implements CategoryManager {

    private DocumentProvider documentProvider;

    public CategoryManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addCategory(CategoryDTO c) {

    }

    @Override
    public void removeCategory(CategoryDTO c) {

    }

    @Override
    public Collection<CategoryDTO> getCategories() {
        return null;
    }

}
