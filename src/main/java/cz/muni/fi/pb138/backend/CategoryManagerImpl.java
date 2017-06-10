package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.exceptions.CategoryNotPersistedException;
import cz.muni.fi.pb138.exceptions.CategoryNotRemovedException;
import org.w3c.dom.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.xml.xpath.XPathConstants.NODE;
import static javax.xml.xpath.XPathConstants.NODESET;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class CategoryManagerImpl implements CategoryManager {

    private DocumentProvider documentProvider;

    public CategoryManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addCategory(CategoryDTO c) {
        try {
            Node node = createNodeFromCategory(c);

            Node firstTable = evaluateXpathNode("//*[local-name()='table']");
            if (firstTable != null) {
                firstTable.getParentNode().insertBefore(node, firstTable);
            } else {
                getCategoriesParent().appendChild(node);
            }
        } catch (XPathExpressionException e) {
            throw new CategoryNotPersistedException(e);
        }
    }

    private Node createNodeFromCategory(CategoryDTO c) {
        Element tableName = documentProvider.getDocument().createElement("table:table");
        Element row = documentProvider.getDocument().createElement("table:table-row");

        List<String> columns = c.getColumns();
        for (int i=0;i<columns.size();i++) {
            Element cell = documentProvider.getDocument().createElement("table:table-cell");
            assignAttributeNS(cell, "calcext:value-type", "urn:org:documentfoundation:names:experimental:calc:xmlns:calcext:1.0", "string");
            assignAttributeNS(cell, "office:value-type", "urn:oasis:names:tc:opendocument:xmlns:office:1.0", "string");
            Element p = documentProvider.getDocument().createElement("text:p");
            p.setTextContent(columns.get(i));
            cell.appendChild(p);
            row.appendChild(cell);
        }
        tableName.appendChild(row);

        assignAttribute(tableName, "table:name", c.getName());

        return tableName;
    }

    private void assignAttribute(Element element, String attributeName, String value) {
        Attr attribute = documentProvider.getDocument().createAttribute(attributeName);
        attribute.setValue(value);
        element.setAttributeNode(attribute);
    }
    private void assignAttributeNS(Element element, String attributeName, String namespaceURI, String value) {
        Attr attribute = documentProvider.getDocument().createAttributeNS(namespaceURI, attributeName);
        attribute.setValue(value);
        element.setAttributeNode(attribute);
    }

    @Override
    public void removeCategory(CategoryDTO c) {
        try {
            Node node = findCategoryInDocument(c);
            node.getParentNode().removeChild(node);
        } catch (XPathExpressionException e) {
            throw new CategoryNotRemovedException(e);
        }
    }

    private Node findCategoryInDocument(CategoryDTO c) throws XPathExpressionException {
        Integer id = Integer.valueOf(c.getId());

        Document document = documentProvider.getDocument();
        NodeList nodes = getCategoriesNodeList(document);
        for (int i=0;i<nodes.getLength();i++) {

            Node item = nodes.item(i);
            if (item.hashCode() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Collection<CategoryDTO> getCategories() {
        Document document = documentProvider.getDocument();

        try {
            NodeList nodes = getCategoriesNodeList(document);
            return convertNodeListToCategories(nodes);

        } catch (XPathExpressionException e) {
            throw new CategoriesNotAvailableException(e);
        }
    }

    private NodeList getCategoriesNodeList(Document document) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile("//*[local-name()='table'][.//*[local-name()='p']]");
        return (NodeList) xPathExpression.evaluate(document, NODESET);
    }

    private Collection<CategoryDTO> convertNodeListToCategories(NodeList nodeList) {
        List<CategoryDTO> result = new ArrayList<>();

        for (int i=0;i<nodeList.getLength();i++) {

            Node item = nodeList.item(i);
            result.add(convertNodeToCategory(item));
        }

        return result;
    }

    private CategoryDTO convertNodeToCategory(Node item) {
        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setId(String.valueOf(item.hashCode()));
        categoryDTO.setName(getCategoryNameFromIndex(item));
        categoryDTO.setColumns(getColumnsFromNode(item));

        return categoryDTO;
    }

    private List<String> getColumnsFromNode(Node item) {
        List<String> result = new ArrayList<>();

        NodeList cells = null;

        NodeList itemChildren = item.getChildNodes();
        for (int i=0;i<itemChildren.getLength();i++) {
            //if(itemChildren.item(i).getNodeName() == "table:table-row"){
            if(itemChildren.item(i).getNodeName().equals("table:table-row")){
                cells = itemChildren.item(i).getChildNodes();
                break;
            }
        }

        for (int i=0;i<cells.getLength();i++) {
            if (cells.item(i).hasChildNodes()) {
                result.add(cells.item(i).getFirstChild().getTextContent());
            }
        }

        return result;
    }

    private String getCategoryNameFromIndex(Node item) {
        Node attributeNode = item.getAttributes().getNamedItem("table:name");
        return attributeNode.getNodeValue();
    }

    public Node getCategoriesParent() throws XPathExpressionException {
        return evaluateXpathNode("//*[local-name()='spreadsheet']");
    }


    private Node evaluateXpathNode(String xpathString) throws XPathExpressionException {
        return (Node)XPathFactory
                .newInstance()
                .newXPath()
                .compile(xpathString)
                .evaluate(documentProvider.getDocument(),NODE);
    }
}
