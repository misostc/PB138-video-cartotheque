package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;

import java.util.Collection;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MediumManagerImpl implements MediumManager {

    DocumentProvider documentProvider;

    public MediumManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addMedium(MediumDTO m) {

    }

    @Override
    public void editMedium(MediumDTO m) {

    }

    @Override
    public void removeMedium(MediumDTO m) {

    }

    @Override
    public Collection<MediumDTO> findMediumByCategory(CategoryDTO c) {
        return null;
    }

    @Override
    public Collection<MediumDTO> findMediumByValue(String value) {
        return null;
    }
}
