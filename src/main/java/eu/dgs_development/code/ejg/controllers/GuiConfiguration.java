package eu.dgs_development.code.ejg.controllers;

import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Helper class to configure a {@link GuiControllerManager}-instance.
 */
public class GuiConfiguration {
    private final Stage primaryStage;

    private ResourceBundle resourceBundle = null;

    private String packageScanPath = null;

    /**
     * Creates a new {@link GuiConfiguration}-instance.
     * @param primaryStage The default primary stage to use.
     */
    public GuiConfiguration(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Returns the default primary stage.
     * @return The default primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns the configured {@link ResourceBundle} or null.
     * @return The set {@link ResourceBundle} or null.
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Sets the {@link ResourceBundle} to use. The bundle is passed to the {@link javafx.fxml.FXMLLoader}.
     * @param resourceBundle The {@link ResourceBundle} to use.
     */
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Returns the package-path of the root-package to scan or null if no path was set. A scan also includes all
     * sub-packages.
     * @return The package-path of the root-package to scan, including all sub-packages.
     */
    public String getPackageScanPath() {
        return packageScanPath;
    }

    /**
     * Sets the package-path of the root-package to scan. A scan also includes all sub-packages.
     * @param packageScanPath The package-path of the root-package to scan.
     */
    public void setPackageScanPath(String packageScanPath) {
        this.packageScanPath = packageScanPath;
    }
}
