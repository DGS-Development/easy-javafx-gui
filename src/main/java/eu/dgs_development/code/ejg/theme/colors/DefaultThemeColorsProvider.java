package eu.dgs_development.code.ejg.theme.colors;

import javafx.scene.paint.Color;

/**
 * Default colors-provider with bright colors. The colors get used, if no {@link ThemeColorSource} overwrites them.
 */
public class DefaultThemeColorsProvider extends ThemeColorsProvider {
    @Override
    public ThemeColor getBackgroundColor() {
        return new ThemeColor("backgroundColor", Color.web("#f3f3f3"));
    }

    @Override
    public ThemeColor getBackgroundDarkAccentColor() {
        return new ThemeColor("backgroundDarkAccentColor", Color.web("#e0e0e0"));
    }

    @Override
    public ThemeColor getBackgroundBrightAccentColor() {
        return new ThemeColor("backgroundLightAccentColor", Color.web("#ffffff"));
    }

    @Override
    public ThemeColor getTextColor() {
        return new ThemeColor("textColor", Color.web("#111111"));
    }

    @Override
    public ThemeColor getColorizationColor() {
        return new ThemeColor("colorizationColor", Color.web("#3ca7ff"));
    }

    @Override
    public ThemeColor getInfoColor() {
        return new ThemeColor("infoColor", Color.web("#17a2b8"));
    }

    @Override
    public ThemeColor getSuccessColor() {
        return new ThemeColor("successColor", Color.web("#28a745"));
    }

    @Override
    public ThemeColor getDangerColor() {
        return new ThemeColor("dangerColor", Color.web("#dc3545"));
    }

    @Override
    public ThemeColor getWarningColor() {
        return new ThemeColor("warningColor", Color.web("#ffc107"));
    }

    @Override
    public boolean containsDarkDefaultThemeColors() {
        return false;
    }
}
