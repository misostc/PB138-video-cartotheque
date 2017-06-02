package cz.muni.fi.pb138.gui.viewmodel;

import cz.muni.fi.pb138.entity.ColumnValueDTO;
import cz.muni.fi.pb138.entity.MediumDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class MediumViewModel {

    public List<String> getValues() {
        return values;
    }

    private final List<String> values;

    public MediumDTO getOriginal() {
        return original;
    }

    private final MediumDTO original;

    public MediumViewModel(MediumDTO original) {
        this.original = original;
        this.values = original.getCategory().getColumns().stream().map(c -> {
          for (ColumnValueDTO valueDTO : original.getValues()) {
              if (valueDTO.getColumn().getId().equals(c.getId())) {
                  return valueDTO.getValue();
              }
          }
          return "";
        }).collect(Collectors.toList());
    }
}
