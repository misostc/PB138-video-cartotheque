package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.entity.CategoryDTO;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcel on 11.6.2017.
 */
public class NodeUtils {
    public static CategoryDTO convertNodeToCategory(Node item) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(getCategoryNameFromIndex(item));
        categoryDTO.setColumns(getColumnsFromNode(item));

        return categoryDTO;
    }

    private static List<String> getColumnsFromNode(Node item) {
        List<String> result = new ArrayList<>();

        NodeList cells = null;

        NodeList itemChildren = item.getChildNodes();
        for (int i = 0; i < itemChildren.getLength(); i++) {
            if (itemChildren.item(i).getNodeName().equals("table:table-row")) {
                cells = itemChildren.item(i).getChildNodes();
                break;
            }
        }

        if (cells == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < cells.getLength(); i++) {
            String textContent = cells.item(i).getTextContent();
            result.add(textContent == null ? "" : textContent);
        }

        removeEndingWhitespace(result);

        return result;
    }

    private static void removeEndingWhitespace(List<String> result) {
        for (int i = result.size() - 1; i != 0; i--) {
            String value = result.get(i);
            if (value == null || value.trim().isEmpty()) {
                result.remove(i);
            }
        }
    }

    private static String getCategoryNameFromIndex(Node item) {
        Node attributeNode = item.getAttributes().getNamedItem("table:name");
        return attributeNode.getNodeValue();
    }
}
