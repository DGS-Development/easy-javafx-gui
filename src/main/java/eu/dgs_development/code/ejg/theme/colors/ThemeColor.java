package eu.dgs_development.code.ejg.theme.colors;

import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Data object class representing a theme color.
 */
public class ThemeColor {
    private final String colorTitle;

    private final Color defaultColor;

    /**
     * Creates a new {@link ThemeColor} with a title and a {@link Color}.
     * @param colorTitle The color title.
     * @param defaultColor The default color object.
     */
    public ThemeColor(String colorTitle, Color defaultColor) {
        this.colorTitle = colorTitle;
        this.defaultColor = defaultColor;
    }

    /**
     * Returns the color title.
     * @return The color title.
     */
    public String getColorTitle() {
        return colorTitle;
    }

    /**
     * Returns the default color object.
     * @return The default color object.
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThemeColor that = (ThemeColor) o;
        return Objects.equals(colorTitle, that.colorTitle) && Objects.equals(defaultColor, that.defaultColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colorTitle, defaultColor);
    }

    @Override
    public String toString() {
        return "ThemeColor{" +
                "colorTitle='" + colorTitle + '\'' +
                ", defaultColor=" + defaultColor +
                '}';
    }
}
