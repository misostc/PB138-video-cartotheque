package cz.muni.fi.pb138.backend;

import org.w3c.dom.Document;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class ODSDocumentProvider implements DocumentProvider {

    /**
     * Creates document provider from ODS file
     * @param filename ods file to locate.
     */
    public ODSDocumentProvider(String filename) {
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void save() {

    }
}
