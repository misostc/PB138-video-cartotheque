package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.DocumentNotAvailableException;
import cz.muni.fi.pb138.exceptions.DocumentNotSavedException;
import cz.muni.fi.pb138.exceptions.DocumentNotValidException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by DV on 09-Jun-17.
 */
public class ODSDocumentProvider implements DocumentProvider {

    public static final String CONTENT_XML_FILENAME = "content.xml";
    private Document document;
    private String filename;

    /**
     * Creates document provider from ODS file
     *
     * @param filename ods file to locate.
     * @throws DocumentNotValidException when document is not a proper file.
     */
    public ODSDocumentProvider(String filename) {
        // parse the file and save Document instance
        this.filename = filename;
        try {
            ZipFile zipFile = new ZipFile(this.filename);
            ZipEntry entry = zipFile.getEntry(CONTENT_XML_FILENAME);
            InputStream inputStream = zipFile.getInputStream(entry);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(inputStream);
            zipFile.close();
        } catch (Exception e) {
            throw new DocumentNotAvailableException(e);
        }
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void save() {
        //take the document instance and save it to ods file

        try {
            ZipFile originalFile = new ZipFile(this.filename);
            final ZipOutputStream newZipFileOS = new ZipOutputStream(new FileOutputStream(this.filename + ".tmp"));

            for (Enumeration ent = originalFile.entries(); ent.hasMoreElements(); ) {
                ZipEntry entryIn = (ZipEntry) ent.nextElement();
                if (!entryIn.getName().equalsIgnoreCase(CONTENT_XML_FILENAME)) {
                    copyEntryToZip(originalFile, newZipFileOS, entryIn);
                } else {
                    constructContentXMLFile(newZipFileOS);
                }
                newZipFileOS.closeEntry();
            }
            newZipFileOS.close();
            originalFile.close();

            restoreTempFile();
        } catch (Exception e) {
            throw new DocumentNotSavedException(e.getMessage());
        }

    }

    private void restoreTempFile() {
        new File(this.filename).delete();
        new File(this.filename + ".tmp").renameTo(new File(this.filename));
    }

    private void constructContentXMLFile(ZipOutputStream newZipFileOS) throws IOException, TransformerException {
        newZipFileOS.putNextEntry(new ZipEntry(CONTENT_XML_FILENAME));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(newZipFileOS);
        // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    private void copyEntryToZip(ZipFile originalFile, ZipOutputStream newZipFileOS, ZipEntry entryIn) throws IOException {
        newZipFileOS.putNextEntry(entryIn);
        InputStream is = originalFile.getInputStream(entryIn);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            newZipFileOS.write(buf, 0, len);
        }
    }
}
