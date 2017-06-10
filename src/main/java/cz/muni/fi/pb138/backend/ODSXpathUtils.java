package cz.muni.fi.pb138.backend;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Iterator;

import static javax.xml.xpath.XPathConstants.*;

/**
 * Class useful for Xpath-related functions on ODS documents, defines namespace prefixes
 * used for querying ODS documents.
 */
class ODSXpathUtils {
    public static final String TABLE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
    public static final String TEXT_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    public static final String CALCEXT_NAMESPACE = "urn:org:documentfoundation:names:experimental:calc:xmlns:calcext:1.0";
    public static final String OFFICE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

    private static XPath getxPath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NamespaceContext nsContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("table".equals(prefix)) {
                    return TABLE_NAMESPACE;
                }
                if ("text".equals(prefix)) {
                    return TEXT_NAMESPACE;
                }
                if ("office".equals(prefix)) {
                    return OFFICE_NAMESPACE;
                }
                if ("calcext".equals(prefix)) {
                    return CALCEXT_NAMESPACE;
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                throw new UnsupportedOperationException();
            }
        };
        xPath.setNamespaceContext(nsContext);
        return xPath;
    }

    static NodeList evaluateXpathNodeList(DocumentProvider documentProvider, String xpathString) throws XPathExpressionException {
        XPath xPath = getxPath();
        return (NodeList) xPath.compile(xpathString).evaluate(documentProvider.getDocument(), NODESET);
    }

    static Node evaluateXpathNode(DocumentProvider documentProvider, String xpathString) throws XPathExpressionException {
        XPath xPath = getxPath();
        return (Node) xPath.compile(xpathString).evaluate(documentProvider.getDocument(), NODE);
    }

    static int evaluateXpathInt(DocumentProvider documentProvider, String xpathString) throws XPathExpressionException {
        XPath xPath = getxPath();
        return ((Double) xPath.compile(xpathString).evaluate(documentProvider.getDocument(), NUMBER)).intValue();
    }
}
