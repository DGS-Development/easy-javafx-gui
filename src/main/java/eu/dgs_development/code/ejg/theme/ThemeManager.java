package eu.dgs_development.code.ejg.theme;

import eu.dgs_development.code.ejg.theme.colors.ThemeColor;
import eu.dgs_development.code.ejg.theme.colors.ThemeColorSource;
import eu.dgs_development.code.ejg.theme.colors.ThemeColorsProvider;
import eu.dgs_development.code.ejg.util.ColorToHexStringUtil;
import eu.dgs_development.code.ejg.util.SingleStylesheetManager;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class ThemeManager<ColorsProvider extends ThemeColorsProvider> {
    /**
     * A functional interface to indicate that a theme-change occurred and a new {@link ColorsProvider} instance was
     * set.
     * @param <ColorsProvider> The {@link ColorsProvider} type.
     */
    public interface ThemeChangeListener<ColorsProvider extends ThemeColorsProvider> {
        /**
         * Gets executed when a new {@link ColorsProvider} instance was set, because a theme-change occurred.
         * @param colorsProvider The new {@link ColorsProvider} instance.
         */
        void onThemeChange(ColorsProvider colorsProvider);
    }

    /**
     * A functional interface to indicate that a managed {@link Parent} instance was added or removed.
     */
    public interface ParentChangeListener {
        /**
         * Gets executed when a managed {@link Parent} instance was added or removed.
         * @param addedParent The affected {@link Parent} instance.
         * @param changedParent True, if the {@link Parent} instance was added. False, if the {@link Parent} instance
         *                      was removed.
         */
        void onParentChange(boolean addedParent, Parent changedParent);
    }

    /**
     * A functional interface to indicate that a managed {@link ImageView} instance was added or removed.
     */
    public interface ImageViewChangeListener {
        /**
         * Gets executed when a managed {@link ImageView} instance was added or removed.
         * @param addedImageView The affected {@link ImageView} instance.
         * @param changedImageView True, if the {@link ImageView} instance was added. False, if the {@link ImageView}
         *                         instance was removed.
         */
        void onImageViewChange(boolean addedImageView, ImageView changedImageView);
    }

    /**
     * A functional interface to indicate that a managed {@link Shape} instance was added or removed.
     */
    public interface ShapeChangeListener {
        /**
         * Gets executed when a managed {@link Shape} instance was added or removed.
         * @param addedShape The affected {@link Shape} instance.
         * @param changedShape True, if the {@link Shape} instance was added. False, if the {@link Shape} instance was
         *                     removed.
         */
        void onShapeChange(boolean addedShape, Shape changedShape);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeManager.class);

    //Variables of the set theme-properties.

    private String stylesheetCss;

    private ColorsProvider colorsProvider;

    private ThemeColorSource themeColorSource;

    //Variables containing all managed object-instances.

    private final Map<String, String> sourceThemeColorNameHexColorMap = new HashMap<>();

    private final Set<ThemeChangeListener<ColorsProvider>> themeChangeListeners = new HashSet<>();

    private final Set<ParentChangeListener> parentChangeListeners = new HashSet<>();

    private final Set<ImageViewChangeListener> imageViewChangeListeners = new HashSet<>();

    private final Set<ShapeChangeListener> shapeChangeListeners = new HashSet<>();

    //private final Set<ImageView> colorizedImageViews = new HashSet<>();

    private final Map<ImageView, ThemeColor> colorizationImageViewThemeColorMap = new HashMap<>();

    private final Map<Shape, ThemeColor> colorizationShapeTheneColorMap = new HashMap<>();

    private final SingleStylesheetManager sharedBaseSingleStylesheetManager = new SingleStylesheetManager();

    private Effect colorizedImageEffect;

    /**
     * The internal change listener updates all managed object-instances, if a theme-change occurred and a new
     * ColorsProvider was set.
     */
    private final ThemeChangeListener<ColorsProvider> internalThemeChangeListener = colorsProvider -> {
        //Update all colorization images.

        colorizationImageViewThemeColorMap.forEach(this::colorizeImageView);

        //Update all colorization shapes.

        colorizationShapeTheneColorMap.forEach(this::colorizeShape);

        //Update all parents and the attached stylesheets.

        Map<String, String> colorNameHexColorMap = new HashMap<>();

        for(ThemeColor themeColor : colorsProvider.getAvailableThemeColors()) {
            //Add theme colors of the new ColorsProvider.

            colorNameHexColorMap.put(themeColor.getColorTitle(),
                    ColorToHexStringUtil.colorToHexColorString(themeColor.getDefaultColor()));
        }

        colorNameHexColorMap.putAll(sourceThemeColorNameHexColorMap); //Add colors from color source.

        String newStylesheetString = replaceCssColorVariables(stylesheetCss, colorNameHexColorMap);

        byte[] cssStringBytes = newStylesheetString.getBytes(StandardCharsets.UTF_8);

        String encodedStylesheetData = "data:text/css;base64," + Base64.getEncoder().encodeToString(cssStringBytes);

        sharedBaseSingleStylesheetManager.updateStylesheet(encodedStylesheetData);
    };

    private void colorizeImageView(ImageView imageView, ThemeColor themeColor) {
        Color tmpColorizationColor = getColor(themeColor);

        Effect tmpColorizedImageEffect = calculateColorizedImageEffect(tmpColorizationColor);

        imageView.setEffect(tmpColorizedImageEffect);
    }

    private void colorizeShape(Shape shape, ThemeColor themeColor) {
        Color tmpColorizationColor = getColor(themeColor);

        shape.setFill(tmpColorizationColor);
    }

    /**
     * Creates a new {@link ThemeManager} with the given initial colors-provider.
     * @param initialColorsProvider The initial colors-provider to use.
     */
    public ThemeManager(ColorsProvider initialColorsProvider) {
        this("", initialColorsProvider);
    }

    /**
     * Creates a new {@link ThemeManager} with the given CSS-string and the initial colors-provider.
     * @param stylesheetCss The initial CSS-string to set (e.g. the content of a CSS file).
     * @param initialColorsProvider The initial colors-provider to use.
     */
    public ThemeManager(String stylesheetCss, ColorsProvider initialColorsProvider) {
        this.stylesheetCss = stylesheetCss;

        //Set the default colors provider.
        setColorsProvider(initialColorsProvider);
    }

    private void setColorsProvider(ColorsProvider colorsProvider) {
        if(colorsProvider == this.colorsProvider)
            return;

        this.colorsProvider = colorsProvider;

        notifyThemeChangeListeners(colorsProvider);
    }

    /**
     * Tries to load all colors of the set {@link ThemeColorsProvider} from a {@link ThemeColorSource}.
     * If there is no entry for a certain {@link ThemeColor} name, the default color of the {@link ThemeColor} is
     * used.
     * @param themeColorSource The {@link ThemeColorSource} to load the theme colors from.
     */
    public void setThemeColorSource(ThemeColorSource themeColorSource) {
        this.themeColorSource = themeColorSource;

        if(!sourceThemeColorNameHexColorMap.isEmpty())
            sourceThemeColorNameHexColorMap.clear();

        colorsProvider.getAvailableThemeColors().forEach(tmpColor -> {
            Color loadedColor = themeColorSource.getThemeColorNameColorMap().get(tmpColor.getColorTitle());

            String hexColorString;

            if(loadedColor == null) {
                hexColorString = ColorToHexStringUtil.colorToHexColorString(tmpColor.getDefaultColor());

                LOGGER.warn("Unable to find hex color string in for color name \"" +
                        tmpColor.getColorTitle() + "\". The default color is used (" + loadedColor + ").");
            }
            else {
                hexColorString = ColorToHexStringUtil.colorToHexColorString(loadedColor);
            }

            sourceThemeColorNameHexColorMap.put(tmpColor.getColorTitle(), hexColorString);
        });

        notifyThemeChangeListeners(colorsProvider);
    }

    /**
     * Removes the current set {@link ThemeColorSource} and all overwritten theme-colors.
     * @return The removed {@link ThemeColorSource} or null, if no color-source was removed.
     */
    public ThemeColorSource removeCurrentThemeColorSource() {
        if(themeColorSource != null) {
            ThemeColorSource themeColorSourceToRemove = themeColorSource;

            themeColorSource = null;

            sourceThemeColorNameHexColorMap.clear();

            notifyThemeChangeListeners(colorsProvider);

            return themeColorSourceToRemove;
        }

        return null;
    }

    /**
     * Returns the set {@link ColorsProvider}.
     * @return The set colors provider.
     */
    public ColorsProvider getColorsProvider() {
        return colorsProvider;
    }

    /**
     * Returns the set {@link ThemeColorSource} or null, if no color source was set.
     * @return The set {@link ThemeColorSource} or null.
     */
    public ThemeColorSource getThemeColorSourceOrNull() {
        return themeColorSource;
    }

    /**
     * Returns true if colors of a dark theme are set. Otherwise false, if bright theme colors are set.
     * This function first checks if a {@link ThemeColorSource} is set and returns true, if the color-source provides
     * dark colors. If no color-source is set, this function returns true, if the set {@link ThemeColorsProvider}
     * provides dark default-theme-colors.
     * @return True, if colors of a dark theme are set.
     */
    public boolean isDarkThemeColorsActive() {
        if(themeColorSource != null)
            return themeColorSource.isDarkThemeColorsSource();

        return colorsProvider.containsDarkDefaultThemeColors();
    }

    /**
     * Tries to add a {@link ThemeChangeListener} to notify, if the theme changes.
     * @param themeChangeListener The listener to add.
     * @return True if the listener didn't exist in the internal set.
     */
    public boolean addThemeChangeListener(ThemeChangeListener<ColorsProvider> themeChangeListener) {
        boolean newListener = themeChangeListeners.add(themeChangeListener);

        //Notify the listener about the initial theme.
        if(newListener)
            themeChangeListener.onThemeChange(colorsProvider);

        return newListener;
    }

    /**
     * Tries to remove a {@link ThemeChangeListener} from the internal set of listeners.
     * @param themeChangeListener The listener to remove.
     * @return True if the listener was removed from the internal set.
     */
    public boolean removeThemeChangeListener(ThemeChangeListener<ColorsProvider> themeChangeListener) {
        return themeChangeListeners.remove(themeChangeListener);
    }

    /**
     * Tries to add a {@link ImageViewChangeListener} to notify if a {@link ImageView} is added or removed.
     * @param imageViewChangeListener The listener to add.
     * @return True if the listener didn't exist in the internal set.
     */
    public boolean addImageViewChangeListener(ImageViewChangeListener imageViewChangeListener) {
        return imageViewChangeListeners.add(imageViewChangeListener);
    }

    /**
     * Tries to remove a {@link ImageViewChangeListener} from the internal set of listeners.
     * @param imageViewChangeListener The listener to remove.
     * @return True if the listener was removed from the internal set.
     */
    public boolean removeImageViewChangeListener(ImageViewChangeListener imageViewChangeListener) {
        return imageViewChangeListeners.remove(imageViewChangeListener);
    }

    /**
     * Tries to add a {@link ShapeChangeListener} to notify if a {@link Shape} is added or removed.
     * @param shapeChangeListener The listener to add.
     * @return True if the listener didn't exist in the internal set.
     */
    public boolean addShapeChangeListener(ShapeChangeListener shapeChangeListener) {
        return shapeChangeListeners.add(shapeChangeListener);
    }

    /**
     * Tries to remove a {@link ShapeChangeListener} from the internal set of listeners.
     * @param shapeChangeListener The listener to remove.
     * @return True if the listener was removed from the internal set.
     */
    public boolean removeShapeChangeListener(ShapeChangeListener shapeChangeListener) {
        return shapeChangeListeners.remove(shapeChangeListener);
    }

    /**
     * Tries to add a {@link ParentChangeListener} to notify if a {@link Parent} is added or removed.
     * @param parentChangeListener The listener to add.
     * @return True if the listener didn't exist in the internal set.
     */
    public boolean addParentChangeListener(ParentChangeListener parentChangeListener) {
        return parentChangeListeners.add(parentChangeListener);
    }

    /**
     * Tries to remove a {@link ParentChangeListener} from the internal set of listeners.
     * @param parentChangeListener The listener to remove.
     * @return True if the listener was removed from the internal set.
     */
    public boolean removeParentChangeListener(ParentChangeListener parentChangeListener) {
        return parentChangeListeners.remove(parentChangeListener);
    }

    /**
     * Returns the {@link Color} instance for a certain {@link ThemeColor}, contained inside the given
     * {@link ColorsProvider}. If there is no overwrite the default color is returned.
     * @param colorsProviderThemeColorFunction The function providing the {@link ThemeColorsProvider} to return a
     *                                         certain {@link ThemeColor}.
     * @return The found {@link Color} instance.
     */
    public Color getColor(Function<ColorsProvider, ThemeColor> colorsProviderThemeColorFunction) {
        ThemeColor themeColor = colorsProviderThemeColorFunction.apply(getColorsProvider());

        return getColor(themeColor);
    }

    private Color getColor(ThemeColor themeColor) {
        String hexColor = sourceThemeColorNameHexColorMap.get(themeColor.getColorTitle());

        if(hexColor != null) {
            return Color.web(hexColor);
        }
        else {
            return themeColor.getDefaultColor();
        }
    }

    /**
     * Returns the hex color for a certain {@link ThemeColor}. If there is no overwrite the default color is returned.
     * @param colorsProviderThemeColorFunction The function providing the {@link ThemeColorsProvider} to return a
     *                                         certain {@link ThemeColor}.
     * @return The found hex color.
     */
    public String getHexColor(Function<ColorsProvider, ThemeColor> colorsProviderThemeColorFunction) {
        ThemeColor themeColor = colorsProviderThemeColorFunction.apply(colorsProvider);

        return getHexColor(themeColor);
    }

    private String getHexColor(ThemeColor themeColor) {
        String hexColor = sourceThemeColorNameHexColorMap.get(themeColor.getColorTitle());

        if(hexColor == null)
            hexColor = ColorToHexStringUtil.colorToHexColorString(themeColor.getDefaultColor());

        return hexColor;
    }

    /**
     * Adds an {@link ImageView} to colorize according to the theme-colorization-color.
     * @param imageView The {@link ImageView} to colorize.
     * @return True if a new {@link ImageView} was added.
     */
    public boolean addImageViewToColorize(ImageView imageView) {
        return addImageViewToColorize(imageView, ColorsProvider::getColorizationColor);
    }

    /**
     * Adds an {@link ImageView} to colorize according to the given {@link ThemeColor}.
     * @param imageView The {@link ImageView} to colorize.
     * @param colorsProviderThemeColorFunction The function providing the {@link ThemeColorsProvider} to return a
     *                                         certain {@link ThemeColor}.
     * @return True if a new {@link ImageView} was added.
     */
    public boolean addImageViewToColorize(ImageView imageView,
                                          Function<ColorsProvider, ThemeColor> colorsProviderThemeColorFunction) {
        ThemeColor themeColorToSet = colorsProviderThemeColorFunction.apply(colorsProvider);

        ThemeColor previousThemeColor = colorizationImageViewThemeColorMap.put(imageView, themeColorToSet);

        boolean newImageView = previousThemeColor == null;

        if(newImageView) {
            colorizeImageView(imageView, themeColorToSet);

            imageViewChangeListeners.forEach(tmpListener ->
                    tmpListener.onImageViewChange(true, imageView));
        }

        return newImageView;
    }

    /**
     * Tries to remove a colorized {@link ImageView}.
     * @param imageView The {@link ImageView} to remove.
     * @return True if the {@link ImageView} was removed.
     */
    public boolean removeImageViewToColorize(ImageView imageView) {
        ThemeColor themeColor = colorizationImageViewThemeColorMap.remove(imageView);

        boolean removedImageView = themeColor != null;

        if(removedImageView) {
            imageView.setEffect(null);

            imageViewChangeListeners.forEach(tmpListener ->
                    tmpListener.onImageViewChange(false, imageView));
        }

        return removedImageView;
    }

    /**
     * Adds a {@link Shape} to colorize, according to the theme-colorization-color.
     * @param shape The {@link Shape} to colorize.
     * @return True if a new {@link Shape} was added.
     */
    public boolean addShapeToColorize(Shape shape) {
        return addShapeToColorize(shape, ColorsProvider::getColorizationColor);
    }

    /**
     * Adds a {@link Shape} to colorize, according to the given {@link ThemeColor}.
     * @param shape The {@link Shape} to colorize.
     * @param colorsProviderThemeColorFunction The function providing the {@link ThemeColorsProvider} to return a
     *                                         certain {@link ThemeColor}.
     * @return True if a new {@link Shape} was added.
     */
    public boolean addShapeToColorize(Shape shape,
                                      Function<ColorsProvider, ThemeColor> colorsProviderThemeColorFunction) {
        ThemeColor themeColorToSet = colorsProviderThemeColorFunction.apply(colorsProvider);

        ThemeColor previousThemeColor = colorizationShapeTheneColorMap.put(shape, themeColorToSet);

        boolean newShape = previousThemeColor == null;

        if(newShape) {
            colorizeShape(shape, themeColorToSet);

            shapeChangeListeners.forEach(tmpListener -> tmpListener.onShapeChange(true, shape));
        }

        return newShape;
    }

    /**
     * Tries to remove a colorized {@link Shape}.
     * @param shape The {@link Shape} to colorize.
     * @return True if the {@link Shape} was removed.
     */
    public boolean removeShapeToColorize(Shape shape) {
        ThemeColor themeColor = colorizationShapeTheneColorMap.remove(shape);

        boolean removedShape = themeColor != null;

        if(removedShape)
            shapeChangeListeners.forEach(tmpListener -> tmpListener.onShapeChange(false, shape));

        return removedShape;
    }

    /**
     * Adds a {@link Parent} to style with a stylesheet, based on the current theme.
     * @param parent The {@link Parent} to style.
     * @return True if a new {@link Parent} was added.
     */
    public boolean addParent(Parent parent) {
        if(sharedBaseSingleStylesheetManager.containsStylesheetParent(parent))
            return false;

        //Add all foreign properties to the parent first (this may happen if a listener modifies the parent).
        parentChangeListeners.forEach(tmpListener -> tmpListener.onParentChange(true, parent));

        //Add the most important style at the end.
        sharedBaseSingleStylesheetManager.addStylesheetParent(parent);

        return true;
    }

    /**
     * Tries to remove a stylesheet styled {@link Parent}.
     * @param parent The parent styled with a stylesheet based on the current theme.
     * @return True if the {@link Parent} was removed.
     */
    public boolean removeParent(Parent parent) {
        boolean removedParent = sharedBaseSingleStylesheetManager.removeStylesheetParent(parent);

        if(removedParent)
            parentChangeListeners.forEach(tmpListener -> tmpListener.onParentChange(false, parent));

        return removedParent;
    }

    private void notifyThemeChangeListeners(ColorsProvider newColorsProvider) {
        //Notify internal listener first.
        internalThemeChangeListener.onThemeChange(newColorsProvider);

        //Notify foreign listeners.
        themeChangeListeners.forEach(tmpThemeChangeListener -> tmpThemeChangeListener.onThemeChange(newColorsProvider));
    }

    private Effect calculateColorizedImageEffect(Color colorizationColor) {
        Lighting lighting = new Lighting(new Light.Distant(45, 90, colorizationColor));

        ColorAdjust colorAdjust = new ColorAdjust(0, 1, 1, 1);

        lighting.setContentInput(colorAdjust);

        lighting.setSurfaceScale(0.0);

        return lighting;
    }

    private String replaceCssColorVariables(String cssTemplateString, Map<String, String> colorNameValueMap) {
        final String[] finalString = { cssTemplateString };

        colorNameValueMap.forEach((tmpKey, tmpValue) ->
                finalString[0] = finalString[0].replaceAll("\\{\\{" + tmpKey + "}}", tmpValue));

        return finalString[0];
    }
}
