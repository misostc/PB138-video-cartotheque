package cz.muni.fi.pb138.exceptions;

import cz.muni.fi.pb138.backend.DocumentProvider;
import org.w3c.dom.Document;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class DocumentNotValidException implements DocumentProvider {

    /**
     * Creates document provider from ODS file
     *
     * @param filename ods file to locate.
     * @throws DocumentNotValidException when document is not a proper file.
     */
    public DocumentNotValidException(String filename) {

    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void save() {

    }
}
