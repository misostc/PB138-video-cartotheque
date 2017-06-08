package cz.muni.fi.pb138.entity;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class MediumDTO {
    CategoryDTO category;
    List<String> values = new ArrayList<>();
    Node xmlNode;

    public Node getXmlNode() {
        return xmlNode;
    }

    public void setXmlNode(Node xmlNode) {
        this.xmlNode = xmlNode;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
