package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.exceptions.CategoryNotPersistedException;
import cz.muni.fi.pb138.exceptions.CategoryNotRemovedException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class CategoryManagerImpl implements CategoryManager {

    private final DocumentProvider documentProvider;

    public CategoryManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addCategory(CategoryDTO category) throws CategoryNotPersistedException {
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() != null) {
            throw new IllegalArgumentException("category already has id");
        }

        try {
            Node node = createNodeFromCategory(category);

            Node firstTable = ODSXpathUtils.evaluateXpathNode(documentProvider, "//table:table[1]");
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
        Element tableName = documentProvider.getDocument().createElementNS(ODSXpathUtils.TABLE_NAMESPACE, "table:table");
        Element row = documentProvider.getDocument().createElementNS(ODSXpathUtils.TABLE_NAMESPACE, "table:table-row");

        List<String> columns = c.getColumns();
        for (String column : columns) {
            Element cell = documentProvider.getDocument().createElementNS(ODSXpathUtils.TABLE_NAMESPACE, "table:table-cell");
            assignAttributeNS(cell, "calcext:value-type", ODSXpathUtils.CALCEXT_NAMESPACE, "string");
            assignAttributeNS(cell, "office:value-type", ODSXpathUtils.OFFICE_NAMESPACE, "string");
            Element p = documentProvider.getDocument().createElementNS(ODSXpathUtils.TEXT_NAMESPACE, "text:p");
            p.setTextContent(column);
            cell.appendChild(p);
            row.appendChild(cell);
        }
        tableName.appendChild(row);

        assignAttributeNS(tableName, "table:name", ODSXpathUtils.TABLE_NAMESPACE, c.getName());

        return tableName;
    }

    private void assignAttributeNS(Element element, String attributeName, String namespaceURI, String value) {
        Attr attribute = documentProvider.getDocument().createAttributeNS(namespaceURI, attributeName);
        attribute.setValue(value);
        element.setAttributeNode(attribute);
    }

    @Override
    public void removeCategory(CategoryDTO category) throws CategoryNotRemovedException {
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() == null) {
            throw new IllegalArgumentException("category does not have id");
        }

        try {
            Node node = findCategoryInDocument(category);
            node.getParentNode().removeChild(node);
        } catch (XPathExpressionException e) {
            throw new CategoryNotRemovedException(e);
        }
    }

    private Node findCategoryInDocument(CategoryDTO category) throws XPathExpressionException {
        Integer id = category.getId();

        return ODSXpathUtils.evaluateXpathNode(documentProvider, String.format("//table:table[%d]", id + 1));
    }

    @Override
    public Collection<CategoryDTO> getCategories() throws CategoriesNotAvailableException {
        try {
            NodeList nodes = getCategoriesNodeList();
            return convertNodeListToCategories(nodes);

        } catch (XPathExpressionException e) {
            throw new CategoriesNotAvailableException(e);
        }
    }

    private NodeList getCategoriesNodeList() throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathNodeList(documentProvider, "//table:table[.//text:p]");
    }

    private Collection<CategoryDTO> convertNodeListToCategories(NodeList nodeList) {
        List<CategoryDTO> result = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node item = nodeList.item(i);
            CategoryDTO category = NodeUtils.convertNodeToCategory(item);
            category.setId(i);
            result.add(category);
        }

        return result;
    }

    private Node getCategoriesParent() throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathNode(documentProvider, "//office:spreadsheet");
    }


}
