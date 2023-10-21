package eu.dgs_development.code.ejg.util;

import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to comfortably manage the style of a {@link Node} element by using single CSS properties.
 */
public class NodeCssPropertiesStyler {
    private final Node node;

    private final Map<String, String> cssPropertiesMap = new HashMap<>();

    /**
     * Creates a new {@link NodeCssPropertiesStyler} to manage the style of the given {@link Node} element.
     * @param node The {@link Node} element to style.
     */
    public NodeCssPropertiesStyler(Node node) {
        this.node = node;
    }

    /**
     * Sets a CSS property for the set {@link Node} element.
     * @param name The name of the CSS property to set (e. g. "color").
     * @param value The value of the CSS property to set (e. g. "red").
     */
    public void setProperty(String name, String value) {
        cssPropertiesMap.put(name, value);

        updateNodeElementStyle();
    }

    /**
     * Removes a CSS property from the {@link Node} element.
     * @param name The name of the CSS property to remove.
     * @return The value of the removed CSS property or null if no property was removed.
     */
    public String removeProperty(String name) {
        String returnedString = cssPropertiesMap.remove(name);

        updateNodeElementStyle();

        return returnedString;
    }

    /**
     * Returns the {@link Node} element to style.
     * @return The managed {@link Node} element.
     */
    public Node getNode() {
        return node;
    }

    private void updateNodeElementStyle() {
        boolean firstProperty = true;

        StringBuilder cssStringBuilder = new StringBuilder();

        for(Map.Entry<String, String> tmpEntry : cssPropertiesMap.entrySet()) {
            if(firstProperty) {
                firstProperty = false;
            }
            else {
                cssStringBuilder.append(" ");
            }

            cssStringBuilder.append(tmpEntry.getKey().replace(";", "")).append(": ");
            cssStringBuilder.append(tmpEntry.getValue().replace(";", "")).append(";");
        }

        node.setStyle(cssStringBuilder.toString());
    }
}
