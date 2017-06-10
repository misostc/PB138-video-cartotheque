package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.MediaNotAvailableException;
import cz.muni.fi.pb138.exceptions.MediumNotPersistedException;
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

    private DocumentProvider documentProvider;

    public MediumManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addMedium(MediumDTO m) {
        Node nodeCategory;
        try {
            int id = getNewMediumId(m);
            nodeCategory = locateCategoryNode(m);
            m.setId(id);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        //table-row
        Node nodeNewRow = documentProvider.getDocument().createElement("table:table-row");

        //table-cell value strings
        for (int i = 0; i < m.getValues().size(); i++) {
            Node nodeNewValue = documentProvider.getDocument().createElement("table:table-cell");
            ((Element) nodeNewValue).setAttribute("table:style-name", "ce3");
            ((Element) nodeNewValue).setAttribute("office:value-type", "string");
            ((Element) nodeNewValue).setAttribute("calcext:value-type", "string");
            Node newValueText = documentProvider.getDocument().createElement("text:p");
            newValueText.setNodeValue(m.getValues().get(i));
            nodeNewValue.appendChild(newValueText);
            nodeNewRow.appendChild(nodeNewValue);
        }

        Node firstChild;
        try {
            firstChild = findFirstMediumInCategory(m.getCategory());
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
        Node node = ODSXpathUtils.evaluateXpathNode(
                documentProvider,
                String.format("//table:table[%d]/table:table-row[.//text:p]", categoryDTO.getId() + 1)
        );
        return node;
    }


    @Override
    public void removeMedium(MediumDTO m) {
        Node node = null;
        try {
            node = ODSXpathUtils.evaluateXpathNode(
                    documentProvider,
                    String.format("//table:table[%d]/table:table-row[%d]", m.getCategory().getId() + 1, m.getId() + 1)
            );
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        node.getParentNode().removeChild(node);
    }

    @Override
    public Collection<MediumDTO> findMediumByCategory(CategoryDTO category) {
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
    public Collection<MediumDTO> findMediumByValue(String value) {
        List<MediumDTO> collection = new ArrayList<>();
        NodeList nodeList = null;
        try {
            nodeList = ODSXpathUtils.evaluateXpathNodeList(
                    documentProvider,
                    String.format("//text:p[text()[contains(.,'%s')]]/../..",value)
            );
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        //iterate through media
        for (int i = 0; i < nodeList.getLength(); i++) {
            MediumDTO mediumDTO = new MediumDTO();
            //fill medium properties
            for (int j = 1; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                mediumDTO.getValues().add(nodeList.item(i).getChildNodes().item(j).getTextContent());
            }

            mediumDTO.setId(Integer.parseInt(nodeList.item(i).getFirstChild().getTextContent()));
            //----====PRIDAT KATEGORIU====----
            mediumDTO.setCategory(null);
        }
        return collection;
    }

    private int getNewMediumId(MediumDTO m) throws XPathExpressionException {
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
