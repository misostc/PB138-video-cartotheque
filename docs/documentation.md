# CategoryManager

## `void addCategory(CategoryDTO c)`

Creates a new category. The category object must have all fields filled in, except id. The id for category is created automatically.

 * **Parameters:** `c` — category object to add
 * **Exceptions:**
   * `IllegalArgumentException` — when a field is missing, or the id field is filled in
   * `CategoryNotPersistedException` — when there was a problem with persisting a category

## `void removeCategory(CategoryDTO c)`

Removes a category.

Searches for categories according to the id field and removes it. This method also removes all media associated with this category.

 * **Parameters:** `c` — category object with id field filled in
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `CategoryNotFoundException` — when there is no category with this id
   * `CategoryNotRemovedException` — when there was a problem with removing a category

## `Collection<CategoryDTO> getCategoriesWithBasicDetails()`

Returns categories with only id and name filled in

 * **Returns:** Collection of available categories with only id and name fields
 * **Exceptions:** `CategoriesNotAvailableException` — when there was a problem retrieving categories

## `CategoryDTO getCategoryWithFullDetails(CategoryDTO c)`

For category id return its full detail, including column definitions

 * **Parameters:** `c` — category to search
 * **Returns:** category object with full details
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing in category
   * `CategoryNotFoundException` — when there is no category with this id
   * `CategoriesNotAvailableException` — when there was a problem retrieving details

# MediumManager

## `void addMedium(MediumDTO m)`

Creates new medium. The medium object must have all fields filled in, except id. The id for medium is created automatically. The column values inside medium must adhere to columns specified in the category.

 * **Parameters:** `m` — medium to be added
 * **Exceptions:**
   * `IllegalArgumentException` — when id is filled in, or some fields are missing
   * `ColumnsDontMatchCategoryException` — when the values inside medium don't adhere to columns specified in the category
   * `MediumNotPersistedException` — when there was a problem persisting the medium.

## `void editMedium(MediumDTO m)`

Edits a medium The medium object must have all fields filled in. When the category is not changed, the values are simply updated with the values inside medium object. When the category is changed, the column values inside medium must adhere to columns specified in the category.

 * **Parameters:** `m` — medium to be edited
 * **Exceptions:**
   * `IllegalArgumentException` — when some fields are missing
   * `ColumnsDontMatchCategoryException` — when the values inside medium don't adhere to columns specified in the category
   * `MediumNotPersistedException` — when there was a problem persisting the medium.

## `void removeMedium(MediumDTO m)`

Removes a medium.

Searches for media according to the id field and removes it.

 * **Parameters:** `m` — medium object with id field filled in
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `MediumNotFoundException` — when there is no medium with this id
   * `MediumNotRemovedException` — when there was a problem with removing a medium

## `Collection<MediumDTO> findMediumByCategory(CategoryDTO c)`

Searches for media with specified category.

 * **Parameters:** `c` — category object with id filled in
 * **Returns:** all media with specified category.
 * **Exceptions:**
   * `IllegalArgumentException` — when id field is missing
   * `CategoryNotFoundException` — when there is no category with this id
   * `MediaNotAvailableException` — when there was a problem retrieving media

## `Collection<MediumDTO> findMediumByValue(String value)`

Searches for media based on a column value. Retrieves all media, which have a column-value where text contains specified value

 * **Parameters:** `value` — string to search for
 * **Returns:** all media which match search condition
 * **Exceptions:**
   * `IllegalArgumentException` — when value is null
   * `MediaNotAvailableException` — when there was a problem retrieving media

# ODSToXMLConverter

## `void convertToXml(String odsFileName, String xmlFileName)`

Converts ODS input file to XML format. The xml file is created automatically, when such file exists it is replaced.

 * **Parameters:**
   * `odsFileName` — filename to import
   * `xmlFileName` — filename to export
 * **Exceptions:**
   * `IllegalArgumentException` — when the input filename is null, does not exist or cannot be accessed
   * `IllegalArgumentException` — when the output filename is null, or cannot be accessed
   * `InvalidInputFormatException` — when there was a problem parsing input file
   * `ConverterException` — when there was a problem converting between files

## `void convertToODS(String xmlFileName, String odsFileName)`

Converts XML input file to ODS format. The ods file is created automatically, when such file exists it is replaced.

 * **Parameters:**
   * `odsFileName` — filename to import
   * `xmlFileName` — filename to export
 * **Exceptions:**
   * `IllegalArgumentException` — when the input filename is null, does not exist or cannot be accessed
   * `IllegalArgumentException` — when the output filename is null, or cannot be accessed
   * `InvalidInputFormatException` — when there was a problem parsing input file
   * `ConverterException` — when there was a problem converting between files
