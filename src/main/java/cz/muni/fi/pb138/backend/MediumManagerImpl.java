package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import cz.muni.fi.pb138.entity.MediumDTO;
import cz.muni.fi.pb138.exceptions.MediumNotPersistedException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;
import java.util.*;

import static javax.xml.xpath.XPathConstants.*;

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
        Node nodeCategory = null;
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("cat",m.getCategory().getName());
            nodeCategory = evaluateXpathNode("//table:table[@table:name=$cat]",mapVariableResolver);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("cat",m.getCategory().getName());

            m.setId(
                    evaluateXpathInt("max($doc//table:table[@table:name=$cat]/table:table-row/table:table-cell[1]/text:p/text()))",mapVariableResolver)+1
            );
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        //table-row
        Node nodeNewRow = documentProvider.getDocument().createElement("table:table-row");
        ((Element)nodeNewRow).setAttribute("table:style-name","ro1");
        //table-cell ID
        Node nodeNewID = documentProvider.getDocument().createElement("table:table-cell");
        ((Element)nodeNewID).setAttribute("table:style-name","ce1");
        ((Element)nodeNewID).setAttribute("office:value-type","float");
        ((Element)nodeNewID).setAttribute("office:value",m.getCategory().getId());
        ((Element)nodeNewID).setAttribute("calcext:value-type","float");
        Node nodeNewIDText = documentProvider.getDocument().createElement("text:p");
        nodeNewIDText.setNodeValue(""+m.getId());
        nodeNewID.appendChild(nodeNewIDText);
        nodeNewRow.appendChild(nodeNewID);
        //table-cell value strings
        for(int i=0;i<m.getValues().size();i++) {
            Node nodeNewValue = documentProvider.getDocument().createElement("table:table-cell");
            ((Element)nodeNewValue).setAttribute("table:style-name","ce3");
            ((Element)nodeNewValue).setAttribute("office:value-type","string");
            ((Element)nodeNewValue).setAttribute("calcext:value-type","string");
            Node newValueText = documentProvider.getDocument().createElement("text:p");
            newValueText.setNodeValue(m.getValues().get(i));
            nodeNewValue.appendChild(newValueText);
            nodeNewRow.appendChild(nodeNewValue);
        }
        nodeCategory.appendChild(nodeNewRow);
    }

    @Override
    public void moveMedium(MediumDTO m, CategoryDTO newCategory) {
        Node node = null;
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("cat",m.getCategory().getName());
            mapVariableResolver.setVariable("id",""+m.getId());
            node = evaluateXpathNode(
                    "//table:table[@table:name=$cat]/table:table-row/table:table-cell[1]/text:p[text()=$id]/../..",
                    mapVariableResolver);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        node.getParentNode().removeChild(node);
        Node nodeNewCategory = null;
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("cat",newCategory.getName());
            nodeNewCategory = evaluateXpathNode("//table:table[@table:name=$cat]",mapVariableResolver);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }
        nodeNewCategory.appendChild(node);
    }

    @Override
    public void editMedium(MediumDTO m) {
        removeMedium(m);
        addMedium(m);
    }

    @Override
    public void removeMedium(MediumDTO m) {
        Node node = null;
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("cat",m.getCategory().getName());
            mapVariableResolver.setVariable("id",""+m.getId());
            node = evaluateXpathNode(
                    "//table:table[@table:name=$cat]/table:table-row/table:table-cell[1]/text:p[text()=$id]/../..",
                    mapVariableResolver);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        node.getParentNode().removeChild(node);
    }

    @Override
    public Collection<MediumDTO> findMediumByCategory(CategoryDTO c) {
        if (!c.isValid()) {
            throw new IllegalArgumentException("Invalid category");
        }

        List<MediumDTO> collection = new ArrayList<>();

        //Iterate through media
        for(int i=2;;i++) {
            NodeList nodeList = null;
            try {
                nodeList = getCategoryRow(c.getName(),i);
            } catch (XPathExpressionException e) {
                throw new MediumNotPersistedException(e);
            }
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
    public Collection<MediumDTO> findMediumByValue(String value) {
        List<MediumDTO> collection = new ArrayList<>();
        NodeList nodeList = null;
        try {
            MapVariableResolver mapVariableResolver = new MapVariableResolver();
            mapVariableResolver.setVariable("val",value);
            nodeList = evaluateXpathNodeList("//text:p[text()[contains(.,$val)]]/../..",mapVariableResolver);
        } catch (XPathExpressionException e) {
            throw new MediumNotPersistedException(e);
        }

        //iterate through media
        for (int i=0;i<nodeList.getLength();i++) {
            MediumDTO mediumDTO = new MediumDTO();
            //fill medium properties
            for (int j=1;j<nodeList.item(i).getChildNodes().getLength();j++) {
                mediumDTO.getValues().add(getCellText(nodeList.item(i).getChildNodes().item(j)));
            }

            mediumDTO.setId(Integer.parseInt(getCellText(nodeList.item(i).getFirstChild())));
            //----====PRIDAT KATEGORIU====----
            mediumDTO.setCategory(null);
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
        MapVariableResolver mapVariableResolver = new MapVariableResolver();
        mapVariableResolver.setVariable("cat",category);
        mapVariableResolver.setVariable("row",""+row);
        return evaluateXpathNodeList("//table:table[@table:name=$cat]/table:table-row[$row]/table:table-cell",mapVariableResolver);
    }

    private NodeList evaluateXpathNodeList(String xpathString, MapVariableResolver mapVariableResolver) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setXPathVariableResolver(mapVariableResolver);
        return (NodeList) xPath.compile(xpathString).evaluate(documentProvider.getDocument(),NODESET);
    }
    private Node evaluateXpathNode(String xpathString, MapVariableResolver mapVariableResolver) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setXPathVariableResolver(mapVariableResolver);
        return (Node) xPath.compile(xpathString).evaluate(documentProvider.getDocument(),NODE);
    }
    private int evaluateXpathInt(String xpathString, MapVariableResolver mapVariableResolver) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setXPathVariableResolver(mapVariableResolver);
        return (int) xPath.compile(xpathString).evaluate(documentProvider.getDocument(),NUMBER);
    }

    private class MapVariableResolver implements XPathVariableResolver {
        // local store of variable name -> variable value mappings
        Map<String, String> variableMappings = new HashMap<>();

        // a way of setting new variable mappings
        void setVariable(String key, String value)  {
            variableMappings.put(key, value);
        }

        // override this method in XPathVariableResolver to
        // be used during evaluation of the XPath expression
        @Override
        public Object resolveVariable(QName varName) {
            // if using namespaces, there's more to do here
            String key = varName.getLocalPart();
            return variableMappings.get(key);
        }
    }
}
