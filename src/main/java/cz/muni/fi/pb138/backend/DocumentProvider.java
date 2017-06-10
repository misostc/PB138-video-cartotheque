package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.DocumentNotSavedException;
import org.w3c.dom.Document;

public interface DocumentProvider {

    /**
     * Retrieves XML document from the source. This will return the
     * internal node, so that the manipulations from outside will reflect
     * inside the document.
     */
    Document getDocument();

    /**
     * Saves changes to DOM to underlying file.
     *
     * @throws DocumentNotSavedException when there was a problem saving Document (i.e. IO error)
     */
    void save() throws DocumentNotSavedException;
}
