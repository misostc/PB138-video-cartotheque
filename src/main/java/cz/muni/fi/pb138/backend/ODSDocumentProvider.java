package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.DocumentNotSavedException;
import cz.muni.fi.pb138.exceptions.DocumentNotValidException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by DV on 09-Jun-17.
 */
public class ODSDocumentProvider implements DocumentProvider {

    private static final String CONTENT_XML_FILENAME = "content.xml";
    private Document document;
    private final String filename;

    /**
     * Creates document provider from ODS file
     *
     * @param filename ods file to locate.
     * @throws DocumentNotValidException when document is not a proper file.
     */
    public ODSDocumentProvider(String filename) throws DocumentNotValidException {
        // parse the file and save Document instance
        this.filename = filename;
        try {
            ZipFile zipFile = new ZipFile(this.filename);
            ZipEntry entry = zipFile.getEntry(CONTENT_XML_FILENAME);
            InputStream inputStream = zipFile.getInputStream(entry);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(inputStream);
            zipFile.close();
            performInitialCleanup();
        } catch (Exception e) {
            throw new DocumentNotValidException(e);
        }

    }

    private void performInitialCleanup() throws XPathExpressionException {
        removeAllEmptyTableRows();
    }

    private void removeAllEmptyTableRows() throws XPathExpressionException {
        NodeList nodeList = ODSXpathUtils.evaluateXpathNodeList(this, "//table:table-row[not(.//text:p)]");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node row = nodeList.item(i);
            row.getParentNode().removeChild(row);
        }
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void save() throws DocumentNotSavedException {
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
            throw new DocumentNotSavedException(e);
        }

    }

    private void restoreTempFile() throws DocumentNotSavedException {
        boolean deleted = new File(this.filename).delete();
        boolean renamed = deleted && new File(this.filename + ".tmp").renameTo(new File(this.filename));
        if (!renamed) {
            throw new DocumentNotSavedException("Could not access file.");
        }
    }

    private void constructContentXMLFile(ZipOutputStream newZipFileOS) throws IOException, TransformerException {
        ZipEntry entry = new ZipEntry(CONTENT_XML_FILENAME);
        newZipFileOS.putNextEntry(entry);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(newZipFileOS);
        // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    private void copyEntryToZip(ZipFile originalFile, ZipOutputStream newZipFileOS, ZipEntry entryIn) throws IOException {
        ZipEntry newEntry = new ZipEntry(entryIn.getName());
        newZipFileOS.putNextEntry(newEntry);
        InputStream is = originalFile.getInputStream(entryIn);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            newZipFileOS.write(buf, 0, len);
        }
    }
}
