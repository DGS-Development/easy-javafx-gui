package eu.dgs_development.code.ejg.util;

import javafx.scene.paint.Color;

/**
 * Helper class to convert a {@link Color} to a CSS hex string representation.
 */
public class ColorToHexStringUtil {
    /**
     * Converts a {@link Color} to a CSS hex string representation.
     * @param color The color to convert.
     * @return The created hex string representation.
     */
    public static String colorToHexColorString(Color color) {
        return "#" + (colorValueToHex(color.getRed()) + colorValueToHex(color.getGreen()) +
                colorValueToHex(color.getBlue()) + colorValueToHex(color.getOpacity())).toUpperCase();
    }

    private static String colorValueToHex(double colorValue) {
        String integerString = Integer.toHexString((int) Math.round(colorValue * 255));

        return integerString.length() == 1 ? "0" + integerString : integerString;
    }
}
