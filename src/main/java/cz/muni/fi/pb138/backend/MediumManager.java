package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.MediaNotAvailableException;
import cz.muni.fi.pb138.exceptions.MediumNotPersistedException;
import cz.muni.fi.pb138.exceptions.MediumNotRemovedException;

import java.util.Collection;

public interface MediumManager {

    /**
     * Creates new medium.
     * The medium object must have all fields filled in, id is created and overwritten.
     *
     * @param m medium to be added
     * @throws IllegalArgumentException    when id is filled in, or some fields are missing
     * @throws MediumNotPersistedException when there was a problem persisting the medium.
     */
    void addMedium(MediumDTO m) throws MediumNotPersistedException;

    /**
     * Removes a medium.
     * Searches for medium according to the id field and category name and removes it.
     *
     * @param m medium object with id field filled in
     * @throws IllegalArgumentException  when id field is missing
     * @throws MediumNotRemovedException when there was a problem with removing a medium
     */
    void removeMedium(MediumDTO m) throws MediumNotRemovedException;

    /**
     * Searches for media with specified category.
     *
     * @param c category object with id filled in
     * @return all media with specified category.
     * @throws IllegalArgumentException   when id field is missing
     * @throws MediaNotAvailableException when there was a problem retrieving media
     */
    Collection<MediumDTO> findMediumByCategory(CategoryDTO c) throws MediaNotAvailableException;

    /**
     * Searches for media based on a column value.
     * Retrieves all media, which have any value equal to the input string
     *
     * @param value string to search for
     * @return all media which match search condition
     * @throws IllegalArgumentException   when value is null
     * @throws MediaNotAvailableException when there was a problem retrieving media
     */
    Collection<MediumDTO> findMediumByValue(String value) throws MediaNotAvailableException;
}
