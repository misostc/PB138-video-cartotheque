package cz.muni.fi.pb138.gui.dialogs;

import cz.muni.fi.pb138.entity.MediumDTO;
import javafx.scene.layout.Pane;

import java.util.Collection;

/**
 * Created by micha on 02.06.2017.
 */
public class MediumSearchResultsPane extends Pane {
    private Collection<MediumDTO> mediums;

    public MediumSearchResultsPane(Collection<MediumDTO> mediums) {
        this.mediums = mediums;
    }
}
