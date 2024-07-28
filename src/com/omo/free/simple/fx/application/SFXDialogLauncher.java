/**
 *
 */
package com.omo.free.simple.fx.application;



import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.managers.LoggingMgr;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.tools.FXAlertOption;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.util.AppUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SFXDialogLauncher class is for launching JavaFX Dialog popup windows on the JavaFX Application Thread.
 *
 * <p>The SFXDialogLauncher class is currently only for use within the {@link SFXApplication} class used to display
 * custom messages to the developer.</p>
 * @author Richard Salas
 * @see SFXApplication
 */
public final class SFXDialogLauncher extends Application{

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.SFXDialogLauncher";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    static String internalAppPropertiesPath;
    static String internalGUIPropertiesPath;
    static String internalLogPropertiesPath;

    static DialogType type;
    static Exception exception;
    static boolean update;

    /**
     * Constructs a new {@code SFXDialogLauncher} instance.
     */
    public SFXDialogLauncher() {}//end constructor

    /**
     * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>NOTE: This method is called on the JavaFX Application Thread.</p>
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet. Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     * @exception Exception during the creation process of the {@code Stage} and {@code Scene} graph
     */
    @Override public void start(Stage primaryStage) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "start()", primaryStage);
        FXUtil.closeSplashScreen();
        type.showPopUp();
        myLogger.exiting(MY_CLASS_NAME, "start()");
    }//end method

    /**
     * This method is called when the application should stop, and provides a convenient place to prepare for application exit and destroy resources.
     *
     * <p>The implementation of this method provided by the Application class will call Platform.exit() and then System.exit(0).</p>
     *
     * <p>NOTE: This method is called on the JavaFX Application Thread.</p>
     */
    @Override public void stop() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "stop()");
        if(type != DialogType.UPDATE_MESSAGE || type != DialogType.REFACTOR_MESSAGE){
            myLogger.info("Starting to close application");
            Platform.exit();
            System.exit(0);
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "stop()");
    }//end method

    /**
     * A set of values used for displaying a specific popup to the user with custom messages.
     * @author Richard Salas JCCC
     */
    enum DialogType{
        NO_RESOURCES {
            @Override public void showPopUp() {
                StringBuffer message = new StringBuffer("Could not create the following directory:");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("\t").append(SFXApplication.getExternalResourcesDirectory());
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("This directory is used for storing application settings as well as other application metadata.");
                message.append(Constants.LINESEPERATOR);
                message.append("Please inform a developer of this issue.  The application will not start up until this issue is resolved.");
                FXAlertOption.showBoldAlert(null, message.toString(), "Application Error", "Could Not Create Directory", AlertType.ERROR);
            }//end method
        },
        UPDATE_MESSAGE{
            @Override public void showPopUp() {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(ButtonType.CANCEL, ButtonType.OK);
                alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Newer Version Exists");
                alert.setHeaderText("Newer Application Exists");
                StringBuffer message = new StringBuffer("A newer version of this application exists ").append(Constants.LINESEPERATOR).append("within the following shared directory: ").append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR).append(Constants.APPLICATION_SHARED_DIRECTORY).append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR).append("Current Version: ").append(Constants.CURRENT_VERSION_LABEL).append(Constants.CURRENT_VERSION_DATE_LABEL).append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR).append("Newer Version: ").append(Constants.NEWER_VERSION_LABEL).append(Constants.NEWER_VERSION_DATE_LABEL).append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR).append("Do you want to update your existing application?");
                alert.setContentText(message.toString());

                addDefaultIcon(alert);

                Optional<ButtonType> choice = alert.showAndWait();
                if(choice.isPresent() && choice.get().equals(ButtonType.YES)){
                    update = true;
                }//end if
            }//end method
        },
        REFACTOR_MESSAGE{
            @Override public void showPopUp() {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.getButtonTypes().removeAll(ButtonType.CANCEL, ButtonType.OK);
                alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Application Has Been Renamed");
                alert.setHeaderText("Application Has Been Renamed");
                StringBuffer message = new StringBuffer("The application executable file has been renamed ")
                        .append(Constants.LINESEPERATOR)
                        .append("from ").append(Constants.CURRENT_JAR_NAME).append(" to ").append(Constants.FUTURE_JAR_NAME)
                        .append(Constants.LINESEPERATOR)
                        .append(Constants.LINESEPERATOR)
                        .append("Do you want to update your existing application?");
                alert.setContentText(message.toString());
                addDefaultIcon(alert);
                Optional<ButtonType> choice = alert.showAndWait();
                if(choice.isPresent() && choice.get().equals(ButtonType.YES)){
                    update = true;
                }//end if
            }//end method
        },
        BUILD_PATH_ISSUE{
            @Override public void showPopUp() {
                FXAlertOption.showBoldAlert(null, "Your default output folder for your workspace is outputting to something other " + Constants.LINESEPERATOR + "than build/classes, src, or bin.  Please configure your build path to output compiled " + Constants.LINESEPERATOR + "files to the \"src\" directory.", "Project Build Path Issue", "Please Fix Your Build Path", AlertType.ERROR);
            }//end method
        },
        SIMPLE_FX_INIT{
            @Override public void showPopUp() {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("SimpleFXFramework Utilization!");
                alert.setHeaderText("Thank You For Implementing SimpleFX Framework");
                StringBuffer message = new StringBuffer("Thank you for choosing to implement the SimpleFXFramework.  The SimpleFXFramework requires some");
                message.append(Constants.LINESEPERATOR);
                message.append("properties that are necessary for the framework to initialize itself.  Three *.properties files were just");
                message.append(Constants.LINESEPERATOR);
                message.append("generated within your Java Source directory named \"src\".");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("(1)\tThe application.properties file contains application properties that are required by the framework to");
                message.append(Constants.LINESEPERATOR);
                message.append("\trun successfully. You can modify the values of these properties and you can also add new properties ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tto this file as needed for your application.");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("(2)\tThe simplefx.gui.properties file contains GUI (Graphical User Interface) properties that are used by the ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tframework to initialize GUI specific settings (eg. Location (x,y) of GUI on Users Machine). You can also ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tmodify any of these values. Also you can add new properties as needed for your application. Also note ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tthat this file should only contain specific settings to your GUI window(s). You can choose to save GUI ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tsettings at some point during runtime using the UIPropertiesMgr class save() method can be utilized to ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tsave new property values.");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("(3)\tThe myLogging.properties file contains logging properties that are used by the framework to initialize");
                message.append(Constants.LINESEPERATOR);
                message.append("\tloggers within the framework.  You can also modify any of these values and you could also add any new ");
                message.append(Constants.LINESEPERATOR);
                message.append("\tproperties that your application may need.");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("You can place these properties files within a package if you like.  (e.g. com.omo.free.appname.resources)");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Go ahead and open these files up to browse through some of the properties that are used by the framework.");
                message.append(Constants.LINESEPERATOR);
                message.append("You may find that you will want to change some of the default values.  These properties files contain");
                message.append(Constants.LINESEPERATOR);
                message.append("comments to let you know what each property is used for.");

                VBox content = new VBox(10);
                content.setAlignment(Pos.CENTER);

                Label contentLabel = new Label(message.toString());
                List<Hyperlink> hyperLinks = buildHyperLinks();

                content.getChildren().add(contentLabel);
                content.getChildren().addAll(hyperLinks);
                alert.getDialogPane().setContent(content);

                // Add a default icon.
                addDefaultIcon(alert);

                alert.getDialogPane().setStyle("-fx-font-weight: bold");

                alert.showAndWait();
            }//end method
        },
        LOGGING_PROPS{
            @Override public void showPopUp() {
                // start building message here
                StringBuffer message = new StringBuffer("Your application is missing required properties.  Here is a list of the ");
                message.append(Constants.LINESEPERATOR).append("messages recorded by the SimpleFXFramework:");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append(LoggingMgr.getInstance().getMissingPropertyMessage());
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Please make sure these properties are added into the myLogging.properties file.");
                FXAlertOption.showBoldAlert(null, message.toString(), "Missing Logging Properties (myLogging.properties)", "myLogging.properties File Is Missing Required Properties", AlertType.ERROR);
            }//end method
        },
        GUI_PROPS{
            @Override public void showPopUp() {
                // start building message here
                StringBuffer message = new StringBuffer("Your application is missing required properties.  Here is a list of the ");
                message.append(Constants.LINESEPERATOR).append("messages recorded by the SimpleFXFramework:");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append(UIPropertiesMgr.getInstance().getMissingPropertyMessage());
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Please make sure these properties are added into the myLogging.properties file.");
                FXAlertOption.showBoldAlert(null, message.toString(), "Missing SimpleFX GUI Properties (simplefx.gui.properties)", "simplefx.gui.properties File Is Missing Required Properties", AlertType.ERROR);
            }//end method
        },
        APPLICATION_PROPS{
            @Override public void showPopUp() {
                // start building message here
                StringBuffer message = new StringBuffer("Your application is missing required properties.  Here is a list of the ");
                message.append(Constants.LINESEPERATOR).append("messages recorded by the SimpleFXFramework:");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append(PropertiesMgr.getInstance().getMissingPropertyMessage());
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Please make sure these properties are added into the application.properties file.");
                FXAlertOption.showBoldAlert(null, message.toString(), "Missing Application Properties (application.properties)", "application.properties File Is Missing Required Properties", AlertType.ERROR);
            }//end method
        },
        FRAMEWORK_EXCEPTION{
            @Override public void showPopUp() {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("SimpleFX Framework Exception");
                alert.setHeaderText("SimpleFX Framework Exception Occurred");
                StringBuffer message = new StringBuffer("Error message from exception was: ");
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Message: ").append(exception.getMessage());
                message.append(Constants.LINESEPERATOR).append(Constants.LINESEPERATOR);
                message.append("Click the \"Show Details\" link below for more details.");
                alert.setContentText(message.toString());

                //creating the exeception!!!
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                String exceptionText = sw.toString();
                pw.close();
                Label label = new Label("SimpleFX Stacktrace:");

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                // Add a default icon.
                addDefaultIcon(alert);

                // Set expandable Exception into the dialog pane.
                alert.getDialogPane().setExpandableContent(expContent);
                alert.getDialogPane().getExpandableContent().autosize();
                alert.showAndWait();
            }//end method
        };

        /**
         * This method will display a popup window to user with a detailed message.
         */
        public abstract void showPopUp();

        /**
         * This method will add the default SimpleFX Framework icon to the window.
         * @param alert the alert to add window to add default icon to
         */
        void addDefaultIcon(Alert alert) {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Constants.DEFAULT_SIMPLE_FX_ICON));
        }//end method

        /**
         * Helper method used for building the hyperlinks used within the SimpleFX Init Popup box
         * @return list of {@code Hyperlink}s used for opening files
         */
        List<Hyperlink> buildHyperLinks() {
            //checking to make sure that the path is not null
            List<Hyperlink> links = new ArrayList<>();

            if(!AppUtil.isNullOrEmpty(internalAppPropertiesPath)){
                Hyperlink applink = new Hyperlink("Open application.properties");
                applink.setOnAction(e -> {
                    applink.setVisited(false);
                    openPropertiesFile(internalAppPropertiesPath);
                });
                links.add(applink);
            }//end if

            if(!AppUtil.isNullOrEmpty(internalGUIPropertiesPath)){
                Hyperlink guilink = new Hyperlink("Open simplefx.gui.properties");
                guilink.setOnAction(e -> {
                    guilink.setVisited(false);
                    openPropertiesFile(internalGUIPropertiesPath);
                });
                links.add(guilink);
            }//end if

            if(!AppUtil.isNullOrEmpty(internalLogPropertiesPath)){
                Hyperlink loglink = new Hyperlink("Open myLogging.properties");
                loglink.setOnAction(e -> {
                    loglink.setVisited(false);
                    openPropertiesFile(internalLogPropertiesPath);
                });
                links.add(loglink);
            }//end if
            return links;
        }//end method

        /**
         * This method will open the file based on the file path being sent into this method.
         * @param propFilePath the properties file path
         */
        private void openPropertiesFile(String propFilePath) {
            try{
                Desktop.getDesktop().open(new File(propFilePath));
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to open properties file. Error message is: " + e.getMessage(), e);
            }//end try...catch
        }//end method
    }//end enum

}//end class
