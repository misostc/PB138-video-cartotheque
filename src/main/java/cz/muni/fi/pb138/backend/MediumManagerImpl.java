package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.MediaNotAvailableException;
import cz.muni.fi.pb138.exceptions.MediumNotPersistedException;
import cz.muni.fi.pb138.exceptions.MediumNotRemovedException;
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
public class MediumManagerImpl implements MediumManager {

    private final DocumentProvider documentProvider;

    public MediumManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addMedium(MediumDTO medium) throws MediumNotPersistedException {
        if (medium == null) {
            throw new IllegalArgumentException("medium is null");
        }
        if (medium.getId() != null) {
            throw new IllegalArgumentException("medium already has id");
        }

        Node nodeCategory;
        try {
            int id = getNewMediumId(medium);
            nodeCategory = locateCategoryNode(medium);
            medium.setId(id);
        } catch (XPathExpressionException | MediaNotAvailableException e) {
            throw new MediumNotPersistedException(e);
        }

        //table-row
        Node nodeNewRow = documentProvider.getDocument().createElementNS(ODSXpathUtils.TABLE_NAMESPACE, "table:table-row");

        //table-cell value strings
        for (String value : medium.getValues()) {
            Node nodeNewValue = documentProvider.getDocument().createElementNS(ODSXpathUtils.TABLE_NAMESPACE, "table:table-cell");

            if (value != null && !value.trim().isEmpty()) {
                Node newValueText = documentProvider.getDocument().createElementNS(ODSXpathUtils.TEXT_NAMESPACE, "text:p");
                ((Element) nodeNewValue).setAttributeNS(ODSXpathUtils.OFFICE_NAMESPACE, "office:value-type", "string");
                ((Element) nodeNewValue).setAttributeNS(ODSXpathUtils.CALCEXT_NAMESPACE, "calcext:value-type", "string");
                newValueText.setTextContent(value);
                nodeNewValue.appendChild(newValueText);
            }

            nodeNewRow.appendChild(nodeNewValue);
        }

        Node firstChild;
        try {
            firstChild = findFirstMediumInCategory(medium.getCategory());
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }
        if (firstChild != null) {
            nodeCategory.insertBefore(nodeNewRow, firstChild);
        } else {
            nodeCategory.appendChild(nodeNewRow);
        }
    }

    private Node findFirstMediumInCategory(CategoryDTO categoryDTO) throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathNode(
                documentProvider,
                String.format("//table:table[%d]/table:table-row[2]", categoryDTO.getId() + 1)
        );
    }


    @Override
    public void removeMedium(MediumDTO medium) throws MediumNotRemovedException {
        if (medium == null) {
            throw new IllegalArgumentException("medium is null");
        }
        if (medium.getId() == null) {
            throw new IllegalArgumentException("medium does not have id");
        }

        try {
            Node node = ODSXpathUtils.evaluateXpathNode(
                    documentProvider,
                    String.format("//table:table[%d]/table:table-row[%d]", medium.getCategory().getId() + 1, medium.getId() + 1)
            );
            node.getParentNode().removeChild(node);
        } catch (XPathExpressionException e) {
            throw new MediumNotRemovedException(e);
        }

    }

    @Override
    public Collection<MediumDTO> findMediumByCategory(CategoryDTO category) throws MediaNotAvailableException {

        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (category.getId() == null) {
            throw new IllegalArgumentException("category does not have id");
        }
        if (!category.isValid()) {
            throw new IllegalArgumentException("Invalid category");
        }

        List<MediumDTO> collection = new ArrayList<>();

        //Iterate through media
        try {
            int count = getTableRowCountFromCategory(category);
            if (count < 1) {
                throw new MediaNotAvailableException();
            }
            for (int i = 1; i < count; i++) {
                NodeList nodeList = getTableRowFromCategory(category, i);

                MediumDTO mediumDTO = new MediumDTO();
                //Iterate through values of the medium
                for (int j = 0; j < nodeList.getLength(); j++) {
                    String cellText = nodeList.item(j).getTextContent();
                    mediumDTO.getValues().add(cellText);
                }

                mediumDTO.setCategory(category);
                mediumDTO.setId(i);
                collection.add(mediumDTO);
            }
        } catch (XPathExpressionException e) {
            throw new MediaNotAvailableException(e);
        }

        return collection;
    }

    private int getTableRowCountFromCategory(CategoryDTO category) throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathInt(
                documentProvider,
                String.format("count(//table:table[%d]/table:table-row[.//text:p])", category.getId() + 1)
        );
    }

    @Override
    public Collection<MediumDTO> findMediumByValue(String value) throws MediaNotAvailableException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }

        List<MediumDTO> collection = new ArrayList<>();
        NodeList nodeList;
        try {
            nodeList = ODSXpathUtils.evaluateXpathNodeList(
                    documentProvider,
                    String.format("//text:p[text()[contains(.,'%s')]]/../..", value)
            );
        } catch (XPathExpressionException e) {
            throw new MediaNotAvailableException(e);
        }

        //iterate through media
        for (int i = 0; i < nodeList.getLength(); i++) {
            MediumDTO mediumDTO = new MediumDTO();
            //fill medium properties
            Node item = nodeList.item(i);
            for (int j = 0; j < item.getChildNodes().getLength(); j++) {
                mediumDTO.getValues().add(item.getChildNodes().item(j).getTextContent());
            }

            Node table = findTableParent(item);
            if (table != null) {
                mediumDTO.setCategory(CategoryManagerImpl.convertNodeToCategory(table));
            }

            collection.add(mediumDTO);
        }
        return collection;
    }

    private Node findTableParent(Node item) {
        while (item != null && !item.getNodeName().equals("table:table")) {
            item = item.getParentNode();
        }

        if (item == null) {
            return null;
        }

        return item;
    }

    private int getNewMediumId(MediumDTO m) throws MediaNotAvailableException {
        return findMediumByCategory(m.getCategory()).size() + 1;
    }

    private Node locateCategoryNode(MediumDTO m) throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathNode(documentProvider, String.format("//table:table[%d]", m.getCategory().getId() + 1));
    }

    private NodeList getTableRowFromCategory(CategoryDTO category, int rowIndex) throws XPathExpressionException {
        return ODSXpathUtils.evaluateXpathNodeList(
                documentProvider,
                String.format("//table:table[%d]/table:table-row[%d]/table:table-cell", category.getId() + 1, rowIndex + 1)
        );
    }

}
