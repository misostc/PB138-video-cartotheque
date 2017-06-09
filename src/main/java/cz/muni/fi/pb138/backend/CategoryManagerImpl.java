package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.exceptions.CategoriesNotAvailableException;
import cz.muni.fi.pb138.exceptions.CategoryNotPersistedException;
import cz.muni.fi.pb138.exceptions.CategoryNotRemovedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
            Node parent = getCategoriesParent();
            parent.appendChild(node);
        } catch (XPathExpressionException e) {
            throw new CategoryNotPersistedException(e);
        }
    }

    private Node createNodeFromCategory(CategoryDTO c) {
        /*
        <table:table table:name="categoryName">
            <table:row>
                <table:cell>
                    <text:p>column1</text:p>
                </table:cell>
                <table:cell>
                    <text:p>column1</text:p>
                </table:cell>
                <table:cell>
                    <text:p>column1</text:p>
                </table:cell>

            </table:row>
        </table:table>
         */
        return null;
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
        // najst prvy riadok = table:table-row
        // z neho vybrat vsetky nazvy a dat ich do listu a vratit
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
