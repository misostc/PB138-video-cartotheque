# CategoryManager

## `void addCategory(CategoryDTO c) throws CategoryNotPersistedException`

Creates a new category. The category object must have all fields filled in, except id. The id for category is created automatically.

 * **Parameters:** `c` — category object to add
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `CategoryNotPersistedException` — when there was a problem with persisting a category

## `void removeCategory(CategoryDTO c) throws CategoryNotRemovedException`

Removes a category.

Searches for categories according to the id field and removes it. This method also removes all media associated with this category.

 * **Parameters:** `c` — category object with id field filled in
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `CategoryNotRemovedException` — when there was a problem with removing a category

## `Collection<CategoryDTO> getCategories() throws CategoriesNotAvailableException`

Returns categories with only id and name filled in

 * **Returns:** Collection of available categories with only id and name fields
 * **Exceptions:** `CategoriesNotAvailableException` — when there was a problem retrieving categories
 
# DocumentProvider

## `Document getDocument()`

Retrieves XML document from the source. This will return the internal node, so that the manipulations from outside will reflect inside the document.

## `void save() throws DocumentNotSavedException`

Saves changes to DOM to underlying file.

* **Exceptions:** `DocumentNotSavedException` — when there was a problem saving Document (i.e. IO error)

# MediumManager

## `void addMedium(MediumDTO m) throws MediumNotPersistedException`

Creates new medium. The medium object must have all fields filled in, id is created and overwritten.

 * **Parameters:** `m` — medium to be added
 * **Exceptions:**
   * `IllegalArgumentException` — when id is filled in, or some fields are missing
   * `MediumNotPersistedException` — when there was a problem persisting the medium.

## `void removeMedium(MediumDTO m) throws MediumNotRemovedException`

Removes a medium. Searches for medium according to the id field and category name and removes it.

 * **Parameters:** `m` — medium object with id field filled in
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `MediumNotRemovedException` — when there was a problem with removing a medium

## `Collection<MediumDTO> findMediumByCategory(CategoryDTO c) throws MediaNotAvailableException`

Searches for media with specified category.

 * **Parameters:** `c` — category object with id filled in
 * **Returns:** all media with specified category.
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `MediaNotAvailableException` — when there was a problem retrieving media

## `Collection<MediumDTO> findMediumByValue(String value) throws MediaNotAvailableException`

Searches for media based on a column value. Retrieves all media, which have any value equal to the input string

 * **Parameters:** `value` — string to search for
 * **Returns:** all media which match search condition
 * **Exceptions:**
   * `IllegalArgumentException` — when value is null
   * `MediaNotAvailableException` — when there was a problem retrieving media