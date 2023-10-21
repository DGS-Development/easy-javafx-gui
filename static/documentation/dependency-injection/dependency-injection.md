# Dependency-Injection 🔎

## 1. Provide object-instances

All object-instances, that should be available for dependency-injection, must be provided by using 
instance-provider-functions. The provided instances will be available throughout all GuiController-instances. 

All instance-provider-functions must be static, marked with the "InstanceProvider" annotation and located 
in the configured package-scan-path of the GuiControllerManager (the path can be configured through the GuiConfiguration 
class). Note that it isn't possible to provide multiple object instances of the same type. If multiple 
instance-provider-functions provide the same instance-type, only the first found function is called and all other 
functions will be ignored. Also note that all instance-provider-functions mustn't require any arguments. Otherwise, an 
exception is thrown.

The following example shows how to provide a custom configuration-instance (class "AppConfigurationProperties") to all 
GuiController-instances.

Content of the file "Main.java":

```java
package eu.dgs_development.apps.hjw;

import eu.dgs_development.apps.hjw.controllers.HelloWorldGuiController;
import eu.dgs_development.code.ejg.controllers.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(); //Launch the actual JavaFX application.
    }

    @Override
    public void start(Stage primaryStage) throws DependencyInjectionException, ControllerInitializationException {
        //Configuration of the GuiControllerManager.

        //We create a GuiConfiguration instance with default settings.
        GuiConfiguration guiConfiguration = new GuiConfiguration(primaryStage);

        //It is recommended to set the package-scan-path to avoid unnecessary reflection-lookups.
        //Note that ALL GuiController-related classes must be found in the specified package or a sub-package (this 
        //includes GuiController-implementations and dependency-injection related functions).
        guiConfiguration.setPackageScanPath("eu.dgs_development.apps.hjw");

        GuiControllerManager guiControllerManager = new GuiControllerManager(guiConfiguration);

        //Show the scene of the HelloWorldGuiController-instance.

        //It is possible to read the cached scene of this controller, because "isCacheableGuiController"
        //returns "true".
        Scene helloWorldScene = guiControllerManager.getCachedSceneOrNull(HelloWorldGuiController.class);

        primaryStage.setScene(helloWorldScene);
        primaryStage.show();
    }

    @InstanceProvider
    public static AppConfigurationProperties createAppConfigurationProperties() {
        //This created configuration should be available for dependency-injection, throughout all GuiController
        //instances.
        return new AppConfigurationProperties();
    }
}
```

## 2. Access provided object-instances

Provided object-instances can be accessed throughout all GuiController-instances, by using the 
"InstanceConsumer"-annotation, which can be assigned to fields. All dependencies can be accessed as soon as the function 
"onInitialized" is called.

Content of the file "HelloWorldGuiController.java":

```java
package eu.dgs_development.apps.hjw.controllers;

import eu.dgs_development.apps.hjw.AppConfigurationProperties;
import eu.dgs_development.code.ejg.controllers.GuiController;
import eu.dgs_development.code.ejg.controllers.InstanceConsumer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HelloWorldGuiController extends GuiController {
    @InstanceConsumer
    private AppConfigurationProperties appConfigurationProperties;

    @FXML
    private TextField txtUserName;

    @FXML
    private Button btnSayHello;

    @Override
    public void onInitialized() {
        //This function is called when this GuiController instance is ready to use.
        //You can compare this function call to a constructor call.

        //You should NEVER perform any initialization logic before this function was
        //called.

        //You can access all injectable dependencies here (in this case the provided 
        //"AppConfigurationProperties"-instance).

        String greetingMessage = appConfigurationProperties.getProperty("helloWorldGuiController." +
                "greetingMessage");

        //We implement the interaction logic here, in order to greet the user.
        btnSayHello.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Hello " + txtUserName.getText() + "!");

            alert.setContentText("Nice to meet you " +
                    txtUserName.getText() + "! " + greetingMessage);

            alert.show();
        });
    }

    @Override
    public boolean isCacheableGuiController() {
        //Returns "true", if this GuiController instance should be cached.
        //You have to create instances manually, if "false" is returned.
        //Read about the "lifecycle" of GuiControllers for more information.

        return true;
    }

    @Override
    public String getFxmlPath() {
        //Enter the path to the FXML file here.
        return "/fxml/hello-world.fxml";
    }
}
```