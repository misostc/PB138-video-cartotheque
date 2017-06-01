package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.DocumentNotAvailableException;
import org.w3c.dom.Document;

public interface DocumentProvider {

    /**
     * Retrieves XML document from the source. This will return the
     * internal node, so that the manipulations from outside will reflect
     * inside the document.
     *
     * @throws DocumentNotAvailableException when there was a problem retrieving Document (i.e. IO error)
     */
    Document getDocument();

}
