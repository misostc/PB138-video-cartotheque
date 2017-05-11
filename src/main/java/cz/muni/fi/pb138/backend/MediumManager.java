package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.*;
import cz.muni.fi.pb138.exceptions.*;

import java.util.Collection;

public interface MediumManager {

    /**
     * Creates new medium.
     * The medium object must have all fields filled in, except id.
     * The id for medium is created automatically.
     * The column values inside medium must adhere to columns specified in the category.
     *
     * @param m medium to be added
     * @throws IllegalArgumentException when id is filled in, or some fields are missing
     * @throws ColumnsDontMatchCategoryException when the values inside medium don't adhere to columns specified in the category
     * @throws MediumNotPersistedException when there was a problem persisting the medium.
     */
    void addMedium(MediumDTO m);

    /**
     * Edits a medium
     * The medium object must have all fields filled in.
     * When the category is not changed, the values are simply updated with the values inside medium object.
     * When the category is changed, the column values inside medium must adhere to columns specified in the category.
     *
     * @param m medium to be edited
     * @throws IllegalArgumentException when some fields are missing
     * @throws ColumnsDontMatchCategoryException when the values inside medium don't adhere to columns specified in the category
     * @throws MediumNotPersistedException when there was a problem persisting the medium.
     */
    void editMedium(MediumDTO m);

    /**
     * Removes a medium.
     *
     * Searches for media according to the id field and removes it.
     *
     * @param m medium object with id field filled in
     * @throws IllegalArgumentException when id field is missing
     * @throws MediumNotFoundException when there is no medium with this id
     * @throws MediumNotRemovedException when there was a problem with removing a medium
     */
    void removeMedium(MediumDTO m);

    /**
     * Searches for media with specified category.
     *
     * @param c category object with id filled in
     * @return all media with specified category.
     * @throws IllegalArgumentException when id field is missing
     * @throws CategoryNotFoundException when there is no category with this id
     * @throws MediaNotAvailableException when there was a problem retrieving media
     *
     */
    Collection<MediumDTO> findMediumByCategory(CategoryDTO c);

    /**
     * Searches for media based on a column value.
     * Retrieves all media, which have a column-value where text contains specified value
     *
     * @param value string to search for
     * @return all media which match search condition
     * @throws IllegalArgumentException when value is null
     * @throws MediaNotAvailableException when there was a problem retrieving media
     *
     */
    Collection<MediumDTO> findMediumByValue(String value);
}
