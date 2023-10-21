package eu.dgs_development.code.ejg.theme.colors;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class implemented by all classes, which provide available theme-colors. This class ensures that all
 * color-providers always contain certain default-colors.
 */
public abstract class ThemeColorsProvider {
    /**
     * Returns the default background-color to use.
     * @return The default background-color.
     */
    public abstract ThemeColor getBackgroundColor();

    /**
     * Returns an accent-color which is slightly darker than the default background-color.
     * @return The dark accent-color.
     */
    public abstract ThemeColor getBackgroundDarkAccentColor();

    /**
     * Returns an accent-color which is slightly brighter than the default background-color.
     * @return The bright accent-color.
     */
    public abstract ThemeColor getBackgroundBrightAccentColor();

    /**
     * Returns the default text-color to use.
     * @return The default text-color.
     */
    public abstract ThemeColor getTextColor();

    /**
     * Returns the default colorization-color, used to colorize arbitrary GUI elements (e.g. icons).
     * @return The default colorization-color.
     */
    public abstract ThemeColor getColorizationColor();

    /**
     * Returns the default info-color, used to highlight details for the user.
     * @return The default info-color.
     */
    public abstract ThemeColor getInfoColor();

    /**
     * Returns the default success-color, used to highlight successful status-information for the user.
     * @return The default success-color.
     */
    public abstract ThemeColor getSuccessColor();

    /**
     * Returns the default danger-color, used to highlight error status-information for the user.
     * @return The default danger-color.
     */
    public abstract ThemeColor getDangerColor();

    /**
     * Returns the default warning-color, used to highlight warning status-information for the user.
     * @return The default warning-color.
     */
    public abstract ThemeColor getWarningColor();

    /**
     * Returns true, if this {@link ThemeColorsProvider} provides dark fallback theme-colors.
     * @return True if dark default-colors are provided, false otherwise.
     */
    public abstract boolean containsDarkDefaultThemeColors();

    /**
     * Returns all {@link ThemeColor} instances available from this {@link ThemeColorsProvider}.
     * @return All {@link ThemeColor} instances available from this {@link ThemeColorsProvider}.
     */
    public final List<ThemeColor> getAvailableThemeColors() {
        List<ThemeColor> themeColors = new LinkedList<>();

        Method[] colorProviderMethods = this.getClass().getMethods();

        for(Method tmpMethod : colorProviderMethods) {
            boolean isPublic = Modifier.isPublic(tmpMethod.getModifiers());
            boolean isThemeColorProvider = tmpMethod.getReturnType().isAssignableFrom(ThemeColor.class);
            boolean isGetterFunction = tmpMethod.getParameterCount() == 0;

            if(isPublic && isThemeColorProvider && isGetterFunction) {
                try {
                    Object themeColorInstance = tmpMethod.invoke(this);

                    if(themeColorInstance instanceof ThemeColor)
                        themeColors.add((ThemeColor) themeColorInstance);
                }
                catch(Exception exception) {
                    throw new IllegalStateException("Unable to retrieve " + ThemeColor.class.getName() + " instance " +
                            "for public method " + tmpMethod.getName() + " in " + this.getClass().getName() +
                            ". Cause: " + exception);
                }
            }
        }

        return themeColors;
    }
}