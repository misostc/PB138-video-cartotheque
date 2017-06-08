package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.exceptions.CategoryNotFoundException;
import cz.muni.fi.pb138.exceptions.CategoryNotPersistedException;
import cz.muni.fi.pb138.exceptions.CategoryNotRemovedException;

import java.util.Collection;

public interface CategoryManager {

    /**
     * Creates a new category.
     * The category object must have all fields filled in, except id.
     * The id for category is created automatically.
     *
     * @param c category object to add
     * @throws IllegalArgumentException      when a field is missing, or the id field is filled in
     * @throws CategoryNotPersistedException when there was a problem with persisting a category
     */
    void addCategory(CategoryDTO c);

    /**
     * Removes a category.
     * <p>
     * Searches for categories according to the id field and removes it.
     * This method also removes all media associated with this category.
     *
     * @param c category object with id field filled in
     * @throws IllegalArgumentException    when id field is missing
     * @throws CategoryNotFoundException   when there is no category with this id
     * @throws CategoryNotRemovedException when there was a problem with removing a category
     */
    void removeCategory(CategoryDTO c);

    /**
     * Returns categories with only id and name filled in
     *
     * @return Collection of available categories with only id and name fields
     * @throws CategoriesNotAvailableException when there was a problem retrieving categories
     */
    Collection<CategoryDTO> getCategories();
}
