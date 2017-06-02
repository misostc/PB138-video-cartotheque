package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.DocumentNotValidException;
import org.w3c.dom.Document;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class ODSDocumentProvider implements DocumentProvider {

    private Document document;
    private String filename;

    /**
     * Creates document provider from ODS file
     * @param filename ods file to locate.
     * @throws DocumentNotValidException when document is not a proper file.
     */
    public ODSDocumentProvider(String filename) {
        // parse the file and save Document instance
        this.filename = filename;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void save() {
        //take the document instance and save it to ods file
    }
}
