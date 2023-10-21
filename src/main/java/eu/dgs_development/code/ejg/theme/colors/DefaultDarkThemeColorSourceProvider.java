package eu.dgs_development.code.ejg.theme.colors;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Default provider of the {@link ThemeColorSource} for dark theme-colors.
 */
public class DefaultDarkThemeColorSourceProvider {
    private static final ThemeColorSource THEME_COLOR_SOURCE;

    static {
        DefaultThemeColorsProvider defaultThemeColorsProvider = new DefaultThemeColorsProvider();

        Map<ThemeColor, Color> themeColorColorMap = new HashMap<>();

        themeColorColorMap.put(defaultThemeColorsProvider.getBackgroundColor(), Color.web("#1f1f1f"));

        themeColorColorMap.put(defaultThemeColorsProvider.getBackgroundDarkAccentColor(),
                Color.web("#181818"));

        themeColorColorMap.put(defaultThemeColorsProvider.getBackgroundBrightAccentColor(),
                Color.web("#2c2c2c"));

        themeColorColorMap.put(defaultThemeColorsProvider.getTextColor(), Color.web("#ffffff"));

        themeColorColorMap.put(defaultThemeColorsProvider.getColorizationColor(), Color.web("#3ca7ff"));

        themeColorColorMap.put(defaultThemeColorsProvider.getInfoColor(), Color.web("#17a2b8"));

        themeColorColorMap.put(defaultThemeColorsProvider.getSuccessColor(), Color.web("#28a745"));

        themeColorColorMap.put(defaultThemeColorsProvider.getDangerColor(), Color.web("#dc3545"));

        themeColorColorMap.put(defaultThemeColorsProvider.getWarningColor(), Color.web("#ffc107"));

        THEME_COLOR_SOURCE = ThemeColorSource.fromThemeColorColorMap(themeColorColorMap, true);
    }

    /**
     * Returns the default dark {@link ThemeColorSource}.
     * @return The default dark {@link ThemeColorSource}.
     */
    public static ThemeColorSource getThemeColorSource() {
        return THEME_COLOR_SOURCE;
    }
}
