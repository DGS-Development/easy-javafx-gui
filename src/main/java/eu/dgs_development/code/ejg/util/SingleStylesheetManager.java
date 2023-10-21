package eu.dgs_development.code.ejg.util;

import javafx.scene.Parent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to assign a stylesheet to an arbitrary amount of {@link Parent} elements. After the assignment it is
 * possible to update the stylesheet (the old stylesheet is replaced with the new stylesheet).
 */
public class SingleStylesheetManager {
    private final Set<Parent> parents = new HashSet<>();

    private String lastStylesheetString = null;

    private String stylesheetString = null;

    /**
     * Creates a new {@link SingleStylesheetManager} without an initial stylesheet.
     */
    public SingleStylesheetManager() {
        //Ignore...
    }

    /**
     * Creates a new {@link SingleStylesheetManager} with an initial stylesheet, which is assigned to all parent
     * elements.
     * @param stylesheetString The initial stylesheet to assign to all {@link Parent} elements.
     */
    public SingleStylesheetManager(String stylesheetString) {
        this.stylesheetString = stylesheetString;
    }

    /**
     * Removes the old stylesheet from all parent elements and adds the new stylesheet as a replacement.
     * @param stylesheetString The new stylesheet to assign to all {@link Parent} elements.
     */
    public void updateStylesheet(String stylesheetString) {
        this.stylesheetString = stylesheetString;

        for(Parent tmpParent : parents) {
            if(lastStylesheetString != null)
                tmpParent.getStylesheets().remove(lastStylesheetString);

            tmpParent.getStylesheets().add(stylesheetString);
        }

        lastStylesheetString = stylesheetString;
    }

    /**
     * Adds a {@link Parent} element to the internal list of parent elements to style. The set stylesheet is added to
     * the new {@link Parent} element.
     * @param parent The {@link Parent} element to style.
     * @return True if the {@link Parent} element wasn't already included in the internal list of parent elements.
     */
    public boolean addStylesheetParent(Parent parent) {
        boolean newParent = parents.add(parent);

        if(newParent && stylesheetString != null)
            parent.getStylesheets().add(stylesheetString);

        return newParent;
    }

    /**
     * Removes a {@link Parent} element from the internal list of parent elements to style, including the set
     * stylesheet.
     * @param parent The {@link Parent} element to remove.
     * @return True if the {@link Parent} element was included in the internal list of parent elements.
     */
    public boolean removeStylesheetParent(Parent parent) {
        boolean removedParent = parents.remove(parent);

        if(removedParent && stylesheetString != null)
            parent.getStylesheets().remove(stylesheetString);

        return removedParent;
    }

    /**
     * Returns an unmodifiable {@link Set} of all styled {@link Parent} elements.
     * @return All styled {@link Parent} elements.
     */
    public Set<Parent> getStylesheetParents() {
        return Collections.unmodifiableSet(parents);
    }

    /**
     * Returns true if the given {@link Parent} element is already included in the internal list of parent elements.
     * @param parent The {@link Parent} element to check.
     * @return True if the given {@link Parent} element is included in the internal list of parent elements.
     */
    public boolean containsStylesheetParent(Parent parent) {
        return parents.contains(parent);
    }

    /**
     * Returns the currently set stylesheet string.
     * @return The current stylesheet string.
     */
    public String getStylesheetString() {
        return stylesheetString;
    }

    /**
     * Returns the previously set stylesheet string or null.
     * @return The previous stylesheet string.
     */
    public String getLastStylesheetString() {
        return lastStylesheetString;
    }
}
