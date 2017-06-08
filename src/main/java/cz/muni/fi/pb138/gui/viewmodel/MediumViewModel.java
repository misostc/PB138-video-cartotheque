package cz.muni.fi.pb138.gui.viewmodel;

import cz.muni.fi.pb138.entity.MediumDTO;

import java.util.List;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MediumViewModel {

    private final List<String> values;
    private final MediumDTO original;

    public MediumViewModel(MediumDTO original) {
        this.original = original;
        this.values = original.getValues();
    }

    public List<String> getValues() {
        return values;
    }

    public MediumDTO getOriginal() {
        return original;
    }
}
