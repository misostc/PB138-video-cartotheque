package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.*;

import java.util.Collection;

public interface MediumManager {

    /**
     * Creates new medium.
     * The medium object must have all fields filled in, id is created and overwritten.
     *
     * @param m medium to be added
     * @throws IllegalArgumentException          when id is filled in, or some fields are missing
     * @throws ColumnsDontMatchCategoryException when the values inside medium don't adhere to columns specified in the category
     * @throws MediumNotPersistedException       when there was a problem persisting the medium.
     */
    void addMedium(MediumDTO m);


    /**
     * Moves a medium specified by a category name and medium ID to a different category.
     * @param m medium to be moved
     * @param newCategory new category to be assigned to the medium
     */
    void moveMedium(MediumDTO m, CategoryDTO newCategory);

    /**
     * Edits a medium
     * The medium object must have all fields filled in.
     * Do NOT use to change the category - use moveMedium instead
     *
     * @param m medium to be edited
     * @throws IllegalArgumentException          when some fields are missing
     * @throws ColumnsDontMatchCategoryException when the values inside medium don't adhere to columns specified in the category
     * @throws MediumNotPersistedException       when there was a problem persisting the medium.
     */
    void editMedium(MediumDTO m);

    /**
     * Removes a medium.
     * Searches for medium according to the id field and category name and removes it.
     *
     * @param m medium object with id field filled in
     * @throws IllegalArgumentException  when id field is missing
     * @throws MediumNotFoundException   when there is no medium with this id
     * @throws MediumNotRemovedException when there was a problem with removing a medium
     */
    void removeMedium(MediumDTO m);

    /**
     * Searches for media with specified category.
     *
     * @param c category object with id filled in
     * @return all media with specified category.
     * @throws IllegalArgumentException   when id field is missing
     * @throws CategoryNotFoundException  when there is no category with this id
     * @throws MediaNotAvailableException when there was a problem retrieving media
     */
    Collection<MediumDTO> findMediumByCategory(CategoryDTO c);

    /**
     * Searches for media based on a column value.
     * Retrieves all media, which have any value equal to the input string
     *
     * @param value string to search for
     * @return all media which match search condition
     * @throws IllegalArgumentException   when value is null
     * @throws MediaNotAvailableException when there was a problem retrieving media
     */
    Collection<MediumDTO> findMediumByValue(String value);
}
