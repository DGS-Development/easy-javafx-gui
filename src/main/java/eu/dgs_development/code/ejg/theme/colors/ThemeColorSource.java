package eu.dgs_development.code.ejg.theme.colors;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class provides colors to overwrite the default colors set in a {@link ThemeColorsProvider}.
 * To achieve this a map is used, which maps a color name to a {@link Color} instance.
 * There are multiple ways to create a {@link ThemeColorSource} by using the static functions inside this class.
 */
public final class ThemeColorSource {
    private final Map<String, Color> themeColorNameColorMap;

    private final boolean darkThemeColorsSource;

    private ThemeColorSource(Map<String, Color> themeColorNameColorMap, boolean darkThemeColorsSource) {
        this.darkThemeColorsSource = darkThemeColorsSource;
        this.themeColorNameColorMap = themeColorNameColorMap;
    }

    /**
     * Returns true, if the {@link ThemeColorSource} provides dark theme colors.
     * @return True if the returned colors belong to a dark theme.
     */
    public boolean isDarkThemeColorsSource() {
        return darkThemeColorsSource;
    }

    /**
     * Returns a map with all available colors (color name mapped to the corresponding color instance).
     * @return The map (color name to color instance).
     */
    public Map<String, Color> getThemeColorNameColorMap() {
        return themeColorNameColorMap;
    }

    /**
     * Creates a new {@link ThemeColorSource} based on the default colors in a {@link ThemeColorsProvider}.
     * @param themeColorsProvider The {@link ThemeColorsProvider} with all default colors.
     * @param darkThemeColors True if colors belong to a dark theme.
     * @return The created {@link ThemeColorSource}.
     */
    public static ThemeColorSource fromThemeColorsProvider(ThemeColorsProvider themeColorsProvider,
                                                          boolean darkThemeColors) {
        Map<String, Color> themeColorNameColorMap = new HashMap<>();

        themeColorsProvider.getAvailableThemeColors().forEach(tmpThemeColor ->
                themeColorNameColorMap.put(tmpThemeColor.getColorTitle(), tmpThemeColor.getDefaultColor()));

        return new ThemeColorSource(themeColorNameColorMap, darkThemeColors);
    }

    /**
     * Creates a new {@link ThemeColorSource} based on a map (theme color name to {@link Color}).
     * @param themeColorNameColorMap The theme color name to {@link Color} map.
     * @param darkThemeColors True if map colors belong to a dark theme.
     * @return The created {@link ThemeColorSource}.
     */
    public static ThemeColorSource fromThemeColorNameColorMap(Map<String, Color> themeColorNameColorMap,
                                                              boolean darkThemeColors) {
        return new ThemeColorSource(themeColorNameColorMap, darkThemeColors);
    }

    /**
     * Creates a new {@link ThemeColorSource} based on a map ({@link ThemeColor} to {@link Color}).
     * @param themeColorColorMap The {@link ThemeColor} to {@link Color} map.
     * @param darkThemeColors True if map colors belong to a dark theme.
     * @return The created {@link ThemeColorSource}.
     */
    public static ThemeColorSource fromThemeColorColorMap(Map<ThemeColor, Color> themeColorColorMap,
                                                          boolean darkThemeColors) {
        Map<String, Color> themeColorNameColorMap = new HashMap<>();

        themeColorColorMap.forEach((key, value) -> themeColorNameColorMap.put(key.getColorTitle(), value));

        return new ThemeColorSource(themeColorNameColorMap, darkThemeColors);
    }

    /**
     * Creates a new {@link ThemeColorSource} based on a map (theme color name to hex color code).
     * @param themeColorNameHexColorMap The theme color name to hex color code map.
     * @param darkThemeColors True if map colors belong to a dark theme.
     * @return The created {@link ThemeColorSource}.
     */
    public static ThemeColorSource fromThemeColorNameHexColorMap(Map<String, String> themeColorNameHexColorMap,
                                                                 boolean darkThemeColors) {
        Map<String, Color> themeColorNameColorMap = new HashMap<>();

        themeColorNameHexColorMap.forEach((key, value) -> themeColorNameColorMap.put(key, Color.web(value)));

        return new ThemeColorSource(themeColorNameColorMap, darkThemeColors);
    }

    /**
     * Creates a new {@link ThemeColorSource} based on {@link Properties} (theme color name to hex color code).
     * @param properties The theme color name to hex color code properties.
     * @param darkThemeColors True if map colors belong to a dark theme.
     * @return The created {@link ThemeColorSource}.
     */
    public static ThemeColorSource fromThemeColorNameHexColorProperties(Properties properties,
                                                                        boolean darkThemeColors) {
        Map<String, Color> themeColorNameColorMap = new HashMap<>();

        for(String tmpColorName : properties.stringPropertyNames()) {
            String tmpHexColor = properties.getProperty(tmpColorName);

            themeColorNameColorMap.put(tmpColorName, Color.web(tmpHexColor));
        }

        return new ThemeColorSource(themeColorNameColorMap, darkThemeColors);
    }
}
