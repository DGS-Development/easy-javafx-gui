package eu.dgs_development.code.ejg.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * The base-class of all {@link GuiController}-classes. These classes can be accessed with a
 * {@link GuiControllerManager}.
 */
public abstract class GuiController {
    private boolean initialized = false;

    private boolean fxmlInitialized = false;

    private boolean dependenciesInitialized = false;

    private Parent rootNode;

    private GuiControllerManager guiControllerManager;

    protected GuiController() {
        //Ignore...
    }

    /**
     * Tries to set the initial {@link GuiControllerManager}.
     * @param guiControllerManager The initial {@link GuiControllerManager}.
     * @return True if the initial {@link GuiControllerManager} was set, false if a {@link GuiControllerManager} was
     * already set.
     */
    final boolean tryToSetGuiControllerManager(GuiControllerManager guiControllerManager) {
        if(this.guiControllerManager != null)
            return false;

        this.guiControllerManager = guiControllerManager;

        return true;
    }

    /**
     * Tries to set the initial {@link Parent} root-node, if not already set.
     * @param rootNode The initial {@link Parent} root-node.
     * @return True if the initial {@link Parent} root-node was set, false if a root-node was already set.
     */
    final boolean tryToSetRootNode(Parent rootNode) {
        if(rootNode == null)
            return false;

        this.rootNode = rootNode;

        tryToInitialize();

        return true;
    }

    /**
     * Tries to notify the {@link GuiController} that all dependencies were initialized.
     * @return True if the {@link GuiController} was notified.
     */
    final boolean tryToInitializeAfterDependencies() {
        if(dependenciesInitialized)
            return false;

        dependenciesInitialized = true;

        tryToInitialize();

        return true;
    }

    /**
     * Gets notified by JavaFX when the FXML-loading was completed.
     */
    @FXML
    public final void initialize() {
        if(!fxmlInitialized) {
            fxmlInitialized = true;

            //Try to initialize.
            tryToInitialize();
        }
    }

    private void tryToInitialize() {
        //This function checks if all conditions are met, which cause an initialization.
        //We check these conditions with this function, because we don't know the order of events.
        if(!initialized && fxmlInitialized && dependenciesInitialized && rootNode != null) {
            initialized = true;

            onInitialized();
        }
    }

    /**
     * Returns the loaded {@link Parent} root-node.
     * @return Returns the loaded {@link Parent} root-node or null, if it wasn't set yet.
     */
    public Parent getRootNode() {
        return rootNode;
    }

    /**
     * Returns true if the {@link GuiController} was initialized.
     * @return True if the {@link GuiController} is ready to use.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the set {@link GuiControllerManager}.
     * @return The set {@link GuiControllerManager}.
     */
    public GuiControllerManager getGuiControllerManager() {
        return guiControllerManager;
    }

    /**
     * Returns the FXML-path linked to the {@link GuiController}-class.
     * @return The path of the FXML-resource to load.
     */
    public abstract String getFxmlPath();


    /**
     * Returns true, if the {@link GuiController}-instance should be cached. Read about the lifecycle for more
     * information. You have to create instances manually, if "false" is returned.
     * <br><br>
     * If set to "false" an initial controller-instanced will be created to check if instantiation-errors occur.
     * After that no reference is cached, allowing the Garbage Collector to remove the created instance.
     * <br><br>
     * If set to "true" an initial controller instanced will be created to check if instantiation-errors occur.
     * After that a reference is cached.
     * @return True if the {@link GuiController} should be cached.
     */
    public abstract boolean isCacheableGuiController();

    /**
     * Gets executed once, if the {@link GuiController} is ready to use. This function can be compared to the
     * constructor-call of a regular class. For instance, the user can't access fields using the dependency-injection
     * mechanism before this function was called. See the {@link InstanceConsumer}-class for more information.
     */
    public abstract void onInitialized();
}
