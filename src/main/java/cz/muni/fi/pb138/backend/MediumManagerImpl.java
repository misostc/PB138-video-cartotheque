package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class MediumManagerImpl implements MediumManager {

    DocumentProvider documentProvider;

    public MediumManagerImpl(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    @Override
    public void addMedium(MediumDTO m) throws XPathExpressionException {
        Node nodeCategory = evaluateXpathNode("//table:table[@table:name=\""+m.getCategory().getName()+"\"]");

    }

    @Override
    public void editMedium(MediumDTO m) throws XPathExpressionException {
        removeMedium(m);
        addMedium(m);
    }

    @Override
    public void removeMedium(MediumDTO m) throws XPathExpressionException {
        Node node = evaluateXpathNode("//table:table[@table:name=\""
                +m.getCategory().getName()
                +"\"]/table:table-row/table:table-cell[1]/text:p[text()=\""
                +m.getId()+"\"]/../..");

        node.getParentNode().removeChild(node);
    }

    @Override
    public Collection<MediumDTO> findMediumByCategory(CategoryDTO c) throws XPathExpressionException, NumberFormatException {
        if (!c.isValid()) {
            throw new IllegalArgumentException("Invalid category");
        }

        List<MediumDTO> collection = new ArrayList<MediumDTO>();

        //Iterate through media
        for(int i=2;;i++) {
            NodeList nodeList = getCategoryRow(c.getName(),i);
            if (nodeList.item(1).getFirstChild() == null) break;
            MediumDTO mediumDTO = new MediumDTO();
            //Iterate through values of the medium
            for (int j=1;j<nodeList.getLength();j++) {
                mediumDTO.getValues().add(getCellText(nodeList.item(j)));
            }

            mediumDTO.setCategory(c);
            mediumDTO.setId(Integer.parseInt(getCellText(nodeList.item(0))));
        }

        return collection;
    }

    @Override
    public Collection<MediumDTO> findMediumByValue(String value) throws XPathExpressionException {
        List<MediumDTO> collection = new ArrayList<MediumDTO>();
        NodeList nodeList = evaluateXpathNodeList("//text:p[text()[contains(.,'"+value+"')]]/../..");

        //iterate through media
        for (int i=0;i<nodeList.getLength();i++) {
            MediumDTO mediumDTO = new MediumDTO();
            //fill medium properties
            for (int j=1;j<nodeList.getLength();j++) {
                mediumDTO.getValues().add(getCellText(nodeList.item(i).getChildNodes().item(j)));
            }

            mediumDTO.setId(Integer.parseInt(getCellText(nodeList.item(i).getFirstChild())));
        }
        return collection;
    }

    private String getCellText(Node cell) {
        return cell.getFirstChild().getTextContent();
    }

    private boolean strContainsNoCase(String string, String substr) {
        //return string.toLowerCase().matches("(?i).*"+substr.toLowerCase()+".*");
        return string.matches("(?i).*"+substr+".*");
    }

    private NodeList getCategoryRow(String category, int row) throws XPathExpressionException {
        return evaluateXpathNodeList("//table:table[@table:name=\""+category+"\"]/table:table-row["+row+"]/table:table-cell");
    }

    private NodeList evaluateXpathNodeList(String xpathString) throws XPathExpressionException {
        return (NodeList)XPathFactory
                .newInstance()
                .newXPath()
                .compile(xpathString)
                .evaluate(documentProvider.getDocument(),NODESET);
    }
    private Node evaluateXpathNode(String xpathString) throws XPathExpressionException {
        return (Node)XPathFactory
                .newInstance()
                .newXPath()
                .compile(xpathString)
                .evaluate(documentProvider.getDocument(),NODE);
    }
}
