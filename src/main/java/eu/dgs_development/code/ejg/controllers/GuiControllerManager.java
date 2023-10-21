package eu.dgs_development.code.ejg.controllers;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The {@link GuiControllerManager} instantiates all {@link GuiController}-classes, found in the scanned packages for
 * the given package-scan-path. The package-scan-path is provided by the set {@link GuiConfiguration}. If no path was
 * set the path "*" is used.
 */
public final class GuiControllerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiControllerManager.class);

    private final Map<Class<? extends GuiController>, GuiController> classGuiControllerCacheMap = new HashMap<>();

    private final Map<Class<? extends GuiController>, Scene> classSceneCacheMap = new HashMap<>();

    private final GuiConfiguration guiConfiguration;

    private final Map<Class<?>, Object> diClassInstanceObjectMap;

    /**
     * Creates a new {@link GuiControllerManager}.
     * @param guiConfiguration The {@link GuiConfiguration} to use.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    public GuiControllerManager(GuiConfiguration guiConfiguration) throws ControllerInitializationException,
            DependencyInjectionException {
        this.guiConfiguration = guiConfiguration;

        //Setup reflection scan-result.

        String packageScanPath = getPackageScanPathOrDefault(guiConfiguration, "*");

        try(ScanResult packageScanResult = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .acceptPackages(packageScanPath)
                .scan()) {
            //Find all methods providing dependencies for dependency-injection.

            ClassInfoList instanceProviderClassesList = packageScanResult
                    .getClassesWithMethodAnnotation(InstanceProvider.class);

            List<Method> instanceProviderMethodList = new ArrayList<>();

            for(ClassInfo tmpClassInfo : instanceProviderClassesList) {
                Class<?> tmpClass = tmpClassInfo.loadClass();

                for(Method tmpMethod : tmpClass.getMethods()) {
                    if(tmpMethod.getAnnotation(InstanceProvider.class) != null)
                        instanceProviderMethodList.add(tmpMethod);
                }
            }

            if(instanceProviderMethodList.isEmpty()) {
                diClassInstanceObjectMap = new HashMap<>();
            }
            else {
                diClassInstanceObjectMap = getAvailableInstanceDependencies(instanceProviderMethodList);
            }

            //Find and instantiate all GuiController classes (we always create instances to check ensure that an
            //instantiation is possible).

            ClassInfoList guiControllerClassesList = packageScanResult.getSubclasses(GuiController.class);

            List<Class<? extends GuiController>> guiControllerClasses = new ArrayList<>();

            for(ClassInfo tmpClassInfo : guiControllerClassesList) {
                guiControllerClasses.add(tmpClassInfo.loadClass(GuiController.class));
            }

            setupGuiControllers(guiControllerClasses);
        }
    }

    private void setupGuiControllers(List<Class<? extends GuiController>> guiControllerClasses) throws ControllerInitializationException {
        //Try to create an instance of every available controller class.

        for(Class<? extends GuiController> tmpGuiControllerClass : guiControllerClasses) {
            //Ignore abstract classes.
            if(Modifier.isAbstract(tmpGuiControllerClass.getModifiers()))
                continue;

            //Try to create a new GUI controller instance.

            GuiController guiController = createGuiControllerInstance(tmpGuiControllerClass, diClassInstanceObjectMap);

            //We only keep cacheable GUI controller instances! All other instances should be created just-in-time.

            if(guiController.isCacheableGuiController())
                classGuiControllerCacheMap.put(tmpGuiControllerClass, guiController);
        }
    }

    private String getPackageScanPathOrDefault(GuiConfiguration guiConfiguration, String defaultValue) {
        //Try to determine the configured package path or the root package.

        if(guiConfiguration.getPackageScanPath() != null) {
            return guiConfiguration.getPackageScanPath();
        }
        else {
            return defaultValue;
        }
    }

    private Map<Class<?>, Object> getAvailableInstanceDependencies(List<Method> providerMethods) throws DependencyInjectionException {
        Map<Class<?>, Object> classInstanceObjectMap = new HashMap<>();

        //We try to execute each valid provider function to get all available instances for dependency injection.

        for(Method tmpMethod : providerMethods) {
            //Execute only public and static provider-methods.

            if(Modifier.isPublic(tmpMethod.getModifiers()) && Modifier.isStatic(tmpMethod.getModifiers())) {
                //Execute only functions which don't require any parameters.

                if(tmpMethod.getParameterCount() == 0) {
                    Class<?> tmpClass = tmpMethod.getReturnType();

                    try {
                        if(!classInstanceObjectMap.containsKey(tmpClass)) {
                            Object instance = tmpMethod.invoke(null);

                            if(instance == null) {
                                LOGGER.warn("Added empty dependency injection instance for function \"{} {}()\" in " +
                                                "class \"{}\".", tmpMethod.getReturnType().getSimpleName(),
                                        tmpMethod.getName(), tmpMethod.getDeclaringClass().getName());
                            }

                            classInstanceObjectMap.put(tmpClass, instance);
                        }
                        else {
                            LOGGER.warn("Unable to add dependency injection instance: There is already an existing " +
                                    "instance for class \"{}\". Providing function is \"{} {}()\" in class \"{}\".",
                                    tmpClass.getName(), tmpMethod.getReturnType().getSimpleName(), tmpMethod.getName(),
                                    tmpMethod.getDeclaringClass().getName());
                        }
                    }
                    catch (Exception exception) {
                        throw new DependencyInjectionException("Unable to create instance object: Unable to perform " +
                                "function call \"" + tmpMethod.getReturnType().getSimpleName() + " " +
                                tmpMethod.getName() + "()" + "\" in class \"" +
                                tmpMethod.getDeclaringClass().getName() + "\".", exception);
                    }
                }
                else {
                    throw new DependencyInjectionException("Unable to create instance object: Unable to perform " +
                            "function call \"" + tmpMethod.getReturnType().getSimpleName() + " " + tmpMethod.getName() +
                            "()\" in class " + "\"" + tmpMethod.getDeclaringClass().getName() + "\", because the " +
                            "function requires arguments (expected 0 arguments).");
                }
            }
            else {
                throw new DependencyInjectionException("Unable to add instance object: Unable to perform function " +
                        "call \"" + tmpMethod.getReturnType().getSimpleName() + " " + tmpMethod.getName() + "()\" in " +
                        "class \"" + tmpMethod.getDeclaringClass().getName() + "\", because the function is not " +
                        "set to \"public\".");
            }
        }

        return classInstanceObjectMap;
    }

    /**
     * Returns the default primary stage.
     * @return The default primary stage.
     */
    public Stage getPrimaryStage() {
        return guiConfiguration.getPrimaryStage();
    }

    /**
     * Creates a new {@link GuiController}-instance for a specific controller-class.
     * @param controllerClass The class of the {@link GuiController}-instance to create.
     * @param <T> The {@link GuiController}-type.
     * @return The created {@link GuiController}-instance.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    public <T extends GuiController> T createGuiControllerInstance(Class<? extends T> controllerClass) throws ControllerInitializationException {
        T createdGuiController = createGuiControllerInstance(controllerClass, diClassInstanceObjectMap);

        boolean dependenciesInitialized = createdGuiController.tryToInitializeAfterDependencies();

        if(!dependenciesInitialized) {
            throw new ControllerInitializationException("Unable to initialize dependencies for GUI controller " +
                    "class \"" + controllerClass.getName() + "\".");
        }

        return createdGuiController;
    }

    /**
     * Convenience function to create a new {@link Scene}-instance for an existing {@link GuiController}-instance.
     * @param guiController The existing {@link GuiController}-instance.
     * @return The created {@link Scene}-instance.
     */
    public Scene createSceneInstance(GuiController guiController) {
        return new Scene(guiController.getRootNode());
    }

    /**
     * Returns an existing (cacheable) {@link GuiController}-instance or returns null if no instance is
     * present. There is no cached instance if the requested {@link GuiController}-class is in a package which wasn't
     * scanned. This happens if the wrong scan-path was set inside the {@link GuiConfiguration}.
     * @param controllerClass The class of the {@link GuiController} containing the corresponding FXML-resource.
     * @param <T> The {@link GuiController}-type.
     * @return The cached {@link GuiController}-instance.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    public <T extends GuiController> T getCachedGuiControllerOrNull(Class<? extends T> controllerClass) throws ControllerInitializationException {
        return getCachedGuiControllerOptional(controllerClass).orElse(null);
    }

    /**
     * Returns an existing (cacheable) {@link GuiController}-instance (if present) or an empty {@link Optional}.
     * @param controllerClass The class of the {@link GuiController} containing the corresponding FXML-resource.
     * @param <T> The {@link GuiController}-type.
     * @return The optional {@link GuiController}-instance.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    @SuppressWarnings("unchecked")
    public <T extends GuiController> Optional<T> getCachedGuiControllerOptional(Class<? extends T> controllerClass) throws ControllerInitializationException {
        if(!classGuiControllerCacheMap.containsKey(controllerClass))
            return Optional.empty();

        T guiController = (T) classGuiControllerCacheMap.get(controllerClass);

        if(!guiController.isInitialized()) {
            boolean dependenciesInitialized = guiController.tryToInitializeAfterDependencies();

            if(!dependenciesInitialized) {
                throw new ControllerInitializationException("Unable to initialize dependencies for GUI controller " +
                        "class \"" + controllerClass.getName() + "\".");
            }
        }

        return Optional.of(guiController);
    }

    /**
     * Returns a {@link Scene} for an existing (cacheable) {@link GuiController}-instance or returns null if
     * no controller instance is present. There is no controller-instance if the requested {@link GuiController}-class
     * is in a package which wasn't scanned. This happens if the wrong scan-path was set inside the
     * {@link GuiConfiguration}.
     * @param controllerClass The {@link GuiController} class.
     * @param <T> The {@link GuiController}-type.
     * @return The cached {@link Scene}-instance.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    public <T extends GuiController> Scene getCachedSceneOrNull(Class<? extends T> controllerClass) throws ControllerInitializationException {
        Optional<Scene> optionalScene = getCachedSceneOptional(controllerClass);

        return optionalScene.orElse(null);
    }

    /**
     * Returns a {@link Scene} for an existing (cacheable) {@link GuiController}-class (if present) or an empty
     * {@link Optional}.
     * @param controllerClass The {@link GuiController}-class.
     * @param <T> The {@link GuiController}-type.
     * @return The optional {@link Scene}-instance.
     * @throws ControllerInitializationException An unexpected {@link ControllerInitializationException}.
     */
    public <T extends GuiController> Optional<Scene> getCachedSceneOptional(Class<? extends T> controllerClass) throws ControllerInitializationException {
        Optional<GuiController> guiController = getCachedGuiControllerOptional(controllerClass);

        if(guiController.isEmpty())
            return Optional.empty();

        Scene scene = classSceneCacheMap.get(controllerClass);

        if(scene == null) {
            scene = new Scene(guiController.get().getRootNode());

            classSceneCacheMap.put(controllerClass, scene);
        }

        return Optional.of(scene);
    }

    private <T extends GuiController> T createGuiControllerInstance(Class<? extends T> controllerClass,
                                                                    Map<Class<?>, Object> classInstanceMap) throws ControllerInitializationException {
        //Try to create a GUI controller instance by using the default constructor.

        T guiController;

        try {
            guiController = controllerClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception exception) {
            throw new ControllerInitializationException("Unable to create instance of GUI controller class \"" +
                    controllerClass.getName() + "\".", exception);
        }

        //Set the GUI controller manager.

        boolean guiControllerManagerSet = guiController.tryToSetGuiControllerManager(this);

        if(!guiControllerManagerSet) {
            throw new ControllerInitializationException("Unable to set GUI controller manager for GUI controller " +
                    "class \"" + controllerClass.getName() + "\".");
        }

        //Load and set the FXML root component.

        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(guiController.getFxmlPath()),
                guiConfiguration.getResourceBundle());

        loader.setController(guiController);

        Parent rootNode;

        try {
            rootNode = loader.load();
        }
        catch (Exception exception) {
            throw new ControllerInitializationException("Unable to load FXML content for GUI controller " +
                    "class \"" + controllerClass.getName() + "\".", exception);
        }

        boolean rootNodeSet = guiController.tryToSetRootNode(rootNode);

        if(!rootNodeSet) {
            throw new ControllerInitializationException("Unable to set root node for GUI controller class \"" +
                    controllerClass.getName() + "\".");
        }

        //Get all fields from the controller class, including all parent super classes.

        Set<Field> availableFields = new HashSet<>();

        Class<?> tmpClass = controllerClass;

        while (tmpClass != Object.class) {
            availableFields.addAll(List.of(tmpClass.getDeclaredFields()));

            tmpClass = tmpClass.getSuperclass();
        }

        //Inject controller dependencies.

        for(Field tmpField : availableFields) {
            if(tmpField.getAnnotation(InstanceConsumer.class) != null) {
                boolean updateModifier = !Modifier.isPublic(tmpField.getModifiers());

                Object dependencyObject = classInstanceMap.get(tmpField.getType());

                if(dependencyObject == null) {
                    throw new ControllerInitializationException("Unable to find dependency for field \"" +
                            tmpField.getName() + "\" in GUI controller class \"" + controllerClass.getName() + "\".");
                }
                else {
                    try {
                        if(updateModifier)
                            tmpField.setAccessible(true);

                        tmpField.set(guiController, dependencyObject);

                        if(updateModifier)
                            tmpField.setAccessible(false);
                    }
                    catch (Exception exception) {
                        throw new ControllerInitializationException("Unable to set dependency for field \"" +
                                tmpField.getName() + "\" in GUI controller class \"" + controllerClass.getName() +
                                "\". Cause: " + exception);
                    }
                }
            }
        }

        return guiController;
    }
}
