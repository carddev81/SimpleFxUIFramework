package com.omo.free.simple.fx.application;

import static com.omo.free.simple.fx.util.Constants.LINESEPERATOR;
import static com.omo.free.simple.fx.util.FileUtility.copyInternalFileToExternalDestination;
import static com.omo.free.simple.fx.util.FileUtility.fileExistsOnClasspath;
import static com.omo.free.simple.fx.util.FileUtility.loadExternalPropertiesFile;
import static com.omo.free.simple.fx.util.FileUtility.loadInternalPropertiesFile;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.omo.free.simple.fx.application.SFXDialogLauncher.DialogType;
import com.omo.free.simple.fx.managers.LoggingMgr;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.tools.FindAndReplaceRegEx;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.simple.fx.util.FileUtility;
import com.omo.free.util.AppUtil;

import javafx.application.Application;

/**
 * This {@code SFXApplication} class provides a default JavaFX implementation for your GUI
 * application to extend which is meant to minimize the development efforts required to configure
 * your application with features such as logging, property management, establishing database
 * connections ect.
 *
 * <p>To implement this SFXApplication class your concrete implementation class must extend the
 * SFXApplication class. The concrete class must contain a {@code main([])} method which
 * is used for starting up the JavaFX application.</p>
 *
 * <p>Note that the {@link #getResourcesParentDirectoryName} method is {@code abstract} and must be overridden.</p>
 *
 * <p>To start the JavaFX Application you first need to create a concrete class that extends the {@link SFXViewBuilder}
 * class.  Second, from within the concrete class that extends this {@code SFXApplication} class you must create an
 * instance of the concrete class using the inherited {@code SFXApplication} constructor {@link #SFXApplication}.
 * This second step must be done within the {@code main([])} method. The JavaFX application is now
 * ready to be started.</p>
 *
 * <p><b>Example</b></p>
 * <p>The following example will show you a simple JavaFX application that utilizes the
 * <b>SimpleFX Framework</b>.</p>
 *
 * <p><b>SFXApplication Implementation Example</b></p>
 * <pre><code>
package com.omo.free.simple.app;

import com.omo.free.simple.fx.application.SFXApplication;
import com.omo.free.simple.fx.application.SFXViewBuilder;

public class ExampleApp extends SFXApplication{

    public ExampleApp(String[] args, Class&lt;? extends SFXViewBuilder&gt; builderClazz) {
        super(args, builderClazz);
    }//end constructor

    public static void main(String[] args) {
        new ExampleApp(args, ExampleBuilder.class);
    }//end main method

    {@literal @Override} public String getResourcesParentDirectoryName() {
        return "ExampleApp";
    }//end method
}//end class
 * </code></pre>
 *
 * <p><b>SFXViewBuilder Implementation Example</b></p>
 * <pre><code>
package com.omo.free.simple.app;

import com.omo.free.simple.fx.application.SFXViewBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ExampleBuilder extends SFXViewBuilder{

    {@literal @Override} protected Parent buildParent() {
        Label label = new Label("Enter Name:");
        TextField textField = new TextField("Placholder");
        Button enter = new Button("Enter");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(5));
        hbox.getChildren().addAll(label, textField, enter);
        return hbox;
    }//end method
}
 * </code></pre>
 * <p>The above example implementation will produce the following:</p>
 * <p><img src="file:////isuwsphere2svr/SharedJar/simplefx/javadoc/simplefx-api/doc-files/ExampleApp.png" alt="example of an application" /></p>
 *
 * <p><b>Initial Development</b></p>
 * <p>This class contains contains logic that will help the developer get started with using the SimpleFX Framework.
 * During the initial start up of the JavaFX application 3 properties files will be created and placed within the java
 * source folder, commonly named "src". A pop up will display to the developer explaining each of these files and their
 * use.  These properties files are listed below with a brief description of their purpose.</p>
 *
 * <table  border="1">
 * <tr><th>File</th><th>Managing Class</th><th>Description</th></tr>
 * <tr><td>application.properties</td><td>{@link PropertiesMgr}</td><td>Contains application runtime properties that are required by the SimpleFX Framework to run successfully. You can modify the values of these properties and you can also add new properties to this file as needed for your application.</td></tr>
 * <tr><td>myLogging.properties</td><td>{@link LoggingMgr}</td><td>Contains logging properties that are used by the SimpleFX Framework to initialize loggers within the SimpleFX Framework as well as your application. You can also modify any of these values and you could also add new properties as needed for your applications logging requirements.</td></tr>
 * <tr><td>simplefx.gui.properties</td><td>{@link UIPropertiesMgr}</td><td>Contains GUI (Graphical User Interface) properties that are used by the SimpleFX Framework to initialize GUI specific settings (eg. Location of GUI on Users Machine). You can also modify any of these values and you could also add new properties as needed for your application. Also note that this file should only contain specific settings to your GUI window(s). If the Developer chooses he/she wants to save GUI settings at some point during runtime the {@link UIPropertiesMgr#save()} method can be utilized to save new property values.</td></tr>
 * <caption>Property Files</caption>
 * </table>
 *
 * <p>These properties files can be placed anywhere within your java source directory of your Java Project. An example location is displayed below.</p>
 * <p><img src="file:////isuwsphere2svr/SharedJar/simplefx/javadoc/simplefx-api/doc-files/PropertiesFiles.png" /></p>
 *
 * <p><b>Logging</b></p>
 * <p>The SimpleFX Framework implements the Java Platforms core logging facilities which is Java Util Logging.</p>
 * @author Richard Salas JCCC March 17, 2015
 * @version 1.0
 */
public abstract class SFXApplication {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.SFXApplication";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static String externalResourcesDirectory;
    private static String[] commandLineArgs;
    private Class<?> subclass;
    private static SFXApplication appClass;


    /**
     * Creates a SFXApplication with the given command line {@code args} and concrete {@code SFXViewBuilder}
     * (refer to the {@link SFXViewBuilder} documentation for how to implement this class).
     *
     * <p>This constructor will fully initialize logging, constants, and properties used by the SimpleFX Framework as well
     * as your application before displaying your gui window. If you application is using the Update Feature of
     * the SimpleFX Framework then, this constructor will check for a newer version of itself and update if
     * the user chooses to.</p>
     *
     * <p>
     * Typical usage is:
     * <pre>
     * <code>
     * public static void main(String[] args) {
     *     new ExampleApp(args, ExampleViewBuilder.class);
     * }
     * </code>
     * </pre>
     *
     * <p>The above logic is all you need within your main method to run your application.</p>
     *
     * @param args          The Array of command line arguments which is used by the application (not required)
     * @param sfxViewClass  The Class object that extends {@link SFXViewBuilder} which contains the view logic
     */
    public SFXApplication(String[] args, Class<? extends SFXViewBuilder> sfxViewClass) {
        updateSplashScreenFunctionality();
        subclass = this.getClass();
        appClass = this;
        setStartInDirectoryPath();

        if(args.length==1 && args[0].equalsIgnoreCase("SPLASH")){//take this out once there are no more swing applications, this should only be temporary
            try{
                Constants.IS_JAR = AppUtil.isJar(subclass);
                Constants.FRAMEWORK_FILE_LOCATION = URLDecoder.decode(SFXApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
                if(Constants.IS_JAR){
                    Path splashPath = Paths.get(Constants.START_IN_DIR_PATH, "splash");
                    FileUtility.checkDirectories(splashPath.toString());
                    FileUtility.extractFileFromJar(splashPath.toString(), "loadingSplashScreen(SimpleFX)v2.png");
                }//end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException trying to extract splash screen.");
            }//end try....catch
            System.exit(0);
        }else if(args.length==1 && args[0].equalsIgnoreCase("PREPARE")){
            System.out.println("User has ran the application only to extract properties files.");
            FXUtil.closeSplashScreen();
            initializeRuntimeConstants();
            initializeFxApplicationResources();
            System.exit(0);
        }else if(this instanceof Headless && args.length > 0 && args[0].equalsIgnoreCase("HEADLESS")){
            System.out.println("User requested application to run HEADLESS which will run processing logic without a GUI.");
            FXUtil.closeSplashScreen();
            initializeRuntimeConstants();
            initializeFxApplicationResources();
            Headless headless = (Headless) this;
            if(headless.validateHeadlessArgs(args)){
                myLogger.info("Starting headless application ... version " + (Constants.IS_JAR ? new ApplicationUpdate().getVersionName() : ""));
                headless.startHeadlessProcess(args);
                myLogger.info("Exiting headless application ...");
                System.exit(0);
            }else{
                myLogger.severe("Headless version of application could not be ran due to invalid arguments passed into the application.");
                System.exit(1);
            }// end if
        }else if(args.length == 3 && args[0].equalsIgnoreCase("DELETE")){
            // args[0]DELETE args[1]FilePath of jar to delete args[2]Folder Name of old version to delete.
            myLogger.info("User has requested the application to delete a previous version be removed due to a major change.");
            File deleteJar = new File(args[1]);
            myLogger.finer("Jar to be deleted: " + deleteJar.getPath());
            File deleteFolders = new File(args[2]);
            myLogger.finer("Folders to be deleted: " + deleteFolders);

            //close simpleguisplash
            closeLegacySplash(new File(deleteFolders.getPath() + "/resources/splash/updatestatus.dat"));

            if(deleteFolders.exists()){
                myLogger.finer("Folders exist to delete.");
                if(myLogger.isLoggable(Level.FINEST)){
                    myLogger.finest("Deleting folders left from old application.");
                }// end if
                for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times if can't delete by then no need to try anymore.
                    if(deleteFolders.exists()){
                        FileUtility.deleteDirectory(deleteFolders);
                    }else{
                        break;
                    }// end if/else
                }// end for
            }// end if
            FXUtil.closeSplashScreen();

            //attempt to delete splash if exists here
            FileUtility.deleteDirectory(Paths.get(Constants.START_IN_DIR_PATH, "splash").toFile());
            initializeRuntimeConstants();
            initializeFxApplicationResources();
            boolean deleted = false;
            if(deleteJar.exists()){
                myLogger.finer("Attempting to delete older version of application located: " + (String.valueOf(args[1]) != null ? String.valueOf(args[1]) : "null"));
                for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times if can't delete by then no need to try anymore.
                    if(deleteJar.exists()){
                        if(myLogger.isLoggable(Level.FINEST)){
                            myLogger.finest("Attempting to delete old jar. Attempt number: " + i);
                        }// end if
                        deleted = deleteJar.delete();
                    }else{
                        deleted = true;
                        myLogger.finer("Older version removed.");
                        break;
                    }// end if/else
                }// end for
            }else{
                myLogger.finer("Older version already removed.");
            }// end if/else
            myLogger.finer("Older version deleted: " + (String.valueOf(deleted) != null ? String.valueOf(deleted) : "null"));
            if(myLogger.isLoggable(Level.FINEST)){
                myLogger.finest("Exiting older version of application using System.exit(0)");
            }// end if
            
            if(Constants.APP_FILE_LOCATION.contains("/transfer/")){
            	String copyToPath = Constants.APP_FILE_LOCATION.substring(1).replace("/transfer", "");
            	try {
					Files.copy(Paths.get(Constants.APP_FILE_LOCATION.substring(1)), Paths.get(copyToPath), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					myLogger.log(Level.SEVERE, "Exception occured while trying to copy the jar file to target location.  Error message is: " + e.getMessage(), e);
				}//end try....catch
            }//end if
        }else if(args.length > 3 && args[0].equalsIgnoreCase("REFACTOR")){
            new ApplicationRefactor().refactorApplication(args);
            //refactor stuff here!!!
        }else if(args.length > 3 && args[0].contains(".jar")){// check to see if application needs to be updated.
            new ApplicationUpdate().updateApplication(args);
        }//end if
        SFXApplication.commandLineArgs = args;
        SFXApplicationLauncher.fxBuilder = sfxViewClass;
        run();
    }// end constructor
    
    /**
     * This method will close the legacy splash screen upon a major update if it is needed.
     *
     * @param splashScreenFile the splash screen file to write to
     */
    private void closeLegacySplash(File splashScreenFile) {
        myLogger.entering(MY_CLASS_NAME, "closeLegacySplash", splashScreenFile);
        BufferedWriter bw = null;
        if(splashScreenFile.exists()){
            for(int i = 0; i < 10000; i++){
                try{
                    bw = new BufferedWriter(new FileWriter(splashScreenFile));
                    bw.write("FINISHED");
                    bw.flush();
                    bw.close();
                    TimeUnit.SECONDS.sleep(2);
                    break;
                }catch(Exception e){
                    myLogger.log(Level.SEVERE, "Exception occurred while trying to write FINISHED to the splash screen file.  Error message is: " + e.getMessage(), e);
                }finally{
                    if(bw != null){
                        try{
                            bw.close();
                        }catch(IOException e){
                            myLogger.log(Level.SEVERE, "IOException occurred while trying to close the buffered writer.  Error message is: " + e.getMessage(), e);
                        }//end try...catch
                    }//end if
                }//end try...catch...finally
            }//end for
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "closeLegacySplash");
    }//end method

    /**
     * This method is used to set the Start In directory path constant {@code Constants.START_IN_DIR_PATH} used by the framework and leaves it available for users to access.  This is obtained by getting the location of the code source.
     *
     * <p>If there is a problem with obtaining the start in directory path it will be </p>
     */
    private void setStartInDirectoryPath() {
        myLogger.entering(MY_CLASS_NAME, "setStartInDirectoryPath");
        try{
            Constants.APP_FILE_LOCATION = URLDecoder.decode(subclass.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
            Constants.START_IN_DIR_PATH = new File(Constants.APP_FILE_LOCATION).getParent().toString();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred while trying to set the START_IN_DIR_PATH constant going to set it to (.).  Error message is:  " + e.getMessage(), e);
            Constants.START_IN_DIR_PATH = ".";
        }//end method
        myLogger.entering(MY_CLASS_NAME, "setStartInDirectoryPath");
    }//end method

    /**
     * This method is intended to be overridden by the subclass to display a message to user on a splash screen.
     * The default functionality of this method will draw a string on top of the splash screen letting the user know that the application is loading.
     * Also note that this method only runs if there is a splash screen displaying to the user. If there is no splash screen being shown then
     * this method just returns back to the caller.
     *
     * <p><b>Developers Note</b></p>
     * <p>If you are wondering how to utilize a splash screen within your application follow the steps below. There are 2 common ways to achieve this.</p>
     *
     * <p><b>Application Running within Developer Workspace</b></p>
     * <ol>
     *  <li>Within your Java Project add a command with the proper path into your run configuration VM Arguments
     *  similar to <b>{@code -splash:src/com/omo/free/appname/mysplash.png}</b></li>
     * </ol>
     *
     * <p><b>Application Running as Jar</b></p>
     * <ol>
     *  <li>The MANIFEST.MF file will need to have the SplashScreen-Image main attribute
     *  added as well as the path value to your splash screen image.
     *  An example is <b>{@code SplashScreen-Image: com/omo/free/appname/resources/splash.jpg}</b>
     *  </li>
     * </ol>
     */
    protected void updateSplashScreenFunctionality() {
        //no logging necessary within this method
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if(splash == null){
            return;
        }//end if

        final Graphics2D graphics = splash.createGraphics();
        if (graphics == null) {
            return;
        }//end if
        //animate the splash screen.
        Thread animate = new Thread(){
            @Override public void run() {
                String loading = "Application is loading ";
                String[] dots = {".", ". .", ". . .", ". . . .", ". . . . .", ". . . . . .", ". . . . . . .", ". . . . . . . .", ". . . . . . . . .", ". . . . . . . . . .", ". . . . . . . . . . .", ". . . . . . . . . . . ." , ". . . . . . . . . . . . .", ". . . . . . . . . . . . . .", ". . . . . . . . . . . . . . ."};
                int pos = 0;
                while(splash.isVisible()){
                    try{
                        splash.update();
                        if(pos == dots.length){
                            pos = 0;
                        }//end if
                        graphics.setComposite(AlphaComposite.Clear);
                        graphics.fillRect(80,392,408,20);//this clears the text everytime.
                        graphics.setPaintMode();
                        graphics.setColor(Color.WHITE);
                        graphics.setFont(new Font("Arial", Font.BOLD, 11));
                        graphics.drawString(loading + dots[pos++], 80, 402);
                        TimeUnit.MILLISECONDS.sleep(150);
                    }catch(InterruptedException e){
                        System.err.println("InterruptedException occurred during the animation of the splash screen. Error message is: " + e.getMessage());
                    }catch(Exception e){
                        System.err.println("Exception occurred during the animation of the splash screen. Error message is: " + e.getMessage());
                    }//end try...catch
                }//end while
            }//end run
        };//end class
        animate.start();
    }//end method

    /**
     * This method will initialize the runtime resources and constants that are used by the SimpleFX Framework.
     *
     * <p><b>Constants</b></p>
     * <p>The following {@link Constants} are set within this method.</p>
     * <ul>
     *  <li>IS_JAR - this lets the developer know whether his application is running as a jar or an exploded application (within an IDE)</li>
     *  <li>APP_FILE_LOCATION - the location of the implementing class (could be helpful value for the developer)</li>
     *  <li>FRAMEWORK_FILE_LOCATION - the location of the framework class (could be helpful value for the developer)</li>
     * </ul>
     *
     * <p><b>Resources</b></p>
     * <p>The External Resources directory for the SimpleFX application is checked to make sure that it exists. If it does not exist then
     * it is created. The value returned by {@link #getResourcesParentDirectoryName} method will determine the parent name of the resources
     * directory.</p>
     */
    private void initializeRuntimeConstants() {
        myLogger.entering(MY_CLASS_NAME, "initializeRuntimeConstants - initializes resources and constants used by the simplefx framework");
        Constants.IS_JAR = AppUtil.isJar(subclass);
        //XXX initializing the application name constant for use by EMU- jal000is 
        Constants.APP_NAME=getResourcesParentDirectoryName();
        //configuring the external resources directory
        externalResourcesDirectory = AppUtil.isNullOrEmpty(getResourcesParentDirectoryName()) ? Paths.get(Constants.START_IN_DIR_PATH, "FxApplication", "resources").toString() : Paths.get(Constants.START_IN_DIR_PATH, getResourcesParentDirectoryName(), "resources").toString();
        File resources = new File(externalResourcesDirectory);
        if(!resources.exists()){
            if(!resources.mkdirs()){
                // resources couldn't be made probably due to permissions issues...
                showDialog(DialogType.NO_RESOURCES);
            }//end if
        }//end if

        try{
            //setting some constant file locations
            Constants.FRAMEWORK_FILE_LOCATION = URLDecoder.decode(SFXApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            myLogger.log(Level.SEVERE, "UnsupportedEncodingException while trying to determine the location of the developers file location and the location of the SimpleFX Framework.  Error Message is: " + e.getMessage(), e);
            SFXDialogLauncher.exception = e;
            showDialog(DialogType.FRAMEWORK_EXCEPTION);
        }//end try...catch
        myLogger.exiting(MY_CLASS_NAME, "initializeRuntimeConstants");
    }//end method

    /**
     * This method will run all the processing logic for configuring and starting up the JavaFX Application.
     *
     * <p><b>Run Process</b></p>
     * <p>The run process will execute in the following order whenever an application is started.</p>
     * <ol>
     *  <li>Initialized runtime {@link Constants} and Internal/External Resources</li>
     *  <li>Initializes and configures application properties and logging</li>
     *  <li>Checks a shared network directory path to see there is a newer version of the currently running application.
     *  If there is a newer version of the application the user will have the option to update their currently running application.</li>
     *  <li>The JavaFX application is started and a GUI displays</li>
     *  </ol>
     */
    private void run() {
        myLogger.entering(MY_CLASS_NAME, "run");
        if(isJavaVersionValid()){
            initializeRuntimeConstants();
            initializeFxApplicationResources();
            myLogger.info("Constants.IS_JAR="+String.valueOf(Constants.IS_JAR));
            myLogger.info("Constants.APP_FILE_LOCATION=" + String.valueOf(Constants.APP_FILE_LOCATION));
            myLogger.info("Constants.FRAMEWORK_FILE_LOCATION=" + String.valueOf(Constants.FRAMEWORK_FILE_LOCATION));
            myLogger.info("Constants.START_IN_DIR_PATH="+String.valueOf(Constants.START_IN_DIR_PATH));
            Application.launch(SFXApplicationLauncher.class, commandLineArgs);//launch the simple application!!!
        }else{
            System.exit(1);
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "run");
    }// end method

    /**
     * This method is called upon setting up SimpleFX Framework to make sure that the java version is valid.
     *
     * <p>This method will make sure the user is running Java version 1.8 update 40 or greater.</p>
     * @return {@code true} or {@code false} value
     */
    private boolean isJavaVersionValid() {
        boolean valid = true;
        String javaVersion = System.getProperty("java.version", "1.5.0_15");
        System.out.println("java.version = " + javaVersion);
        System.out.println("java.runtime.version = " + System.getProperty("java.runtime.version"));
        if(javaVersion.contains("_")){
            int underscoreIndex = javaVersion.lastIndexOf("_") + 1;
            int updateVersion = Integer.valueOf(javaVersion.substring(underscoreIndex, javaVersion.length()));
            if(updateVersion < 40){
                valid = false;//not valid
//                SwingUtilities.invokeLater(new Runnable(){
//                    @Override public void run() {
                        try{
                            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                        }catch(Exception e){
                            System.err.println("An exception occurred while trying to set the look and feel of the swing window.");
                        }//end try....catch!!!!

                        String jdk8Location;
                        if(System.getenv("USERDOMAIN").equals("ISU")){
                            jdk8Location = "file:////isuwsphere2svr/jdk8";
                        }else{
                            jdk8Location = "file:////docsvr.state.mo.us/ITSD-DOC/AppDevTech/Tooling/Java";
                        }// end if...else

                        StringBuilder message = new StringBuilder("The Java Application you have just tried to execute");
                        message.append(LINESEPERATOR);
                        message.append("requires at least version 1.8, update 40. This is due to some");
                        message.append(LINESEPERATOR);
                        message.append("of the features that are being used by the application.");
                        message.append(LINESEPERATOR).append(LINESEPERATOR);
                        message.append("Currently running java version is:  ");
                        message.append(javaVersion);
                        message.append(LINESEPERATOR);
                        message.append(LINESEPERATOR);
                        message.append("Please obtain and install the latest Java ");
                        message.append(LINESEPERATOR);
                        message.append("Runtime and then restart the application.");
                        message.append(LINESEPERATOR);
                        message.append(LINESEPERATOR);

                        message.append("Do you want to browse through the shared directory");
                        message.append(LINESEPERATOR);
                        message.append("\"").append(jdk8Location).append("\"");
                        message.append(LINESEPERATOR);
                        message.append("that may contain the version of java that you need?");
                        System.out.println(String.valueOf(message));
                        int choice = JOptionPane.showConfirmDialog(null, message.toString(), "Newer Java Version Required!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(choice==JOptionPane.YES_OPTION){
                            try{
                                Runtime.getRuntime().exec("explorer \"" + jdk8Location + "\"");
                            }catch(Exception e){
                                JOptionPane.showMessageDialog(null, "Error Occurred trying to open shared directory. Sorry." + LINESEPERATOR + LINESEPERATOR + "The path is: " + String.valueOf(jdk8Location), "Error", JOptionPane.ERROR_MESSAGE);
                            }// end try....catch
                        }//end if
//                    }//end run();
//                });
            }//end if
        }//end if
        return valid;
	}//end method

	/**
     * <p>This method will initialize java util logging, application properties, and SimpleFX gui properties. The properties files that are used
     * by the SimpleFX Framework are illustrated below.</p>
     *
     * <table  border="1">
     * <tr><th>File</th><th>Managing Class</th><th>Description</th></tr>
     * <tr><td>application.properties</td><td>{@link PropertiesMgr}</td><td>Contains application runtime properties that are required by the SimpleFX Framework to run successfully. You can modify the values of these properties and you can also add new properties to this file as needed for your application.</td></tr>
     * <tr><td>myLogging.properties</td><td>{@link LoggingMgr}</td><td>Contains logging properties that are used by the SimpleFX Framework to initialize loggers within the SimpleFX Framework as well as your application. You can also modify any of these values and you could also add new properties as needed for your applications logging requirements.</td></tr>
     * <tr><td>simplefx.gui.properties</td><td>{@link UIPropertiesMgr}</td><td>Contains GUI (Graphical User Interface) properties that are used by the SimpleFX Framework to initialize GUI specific settings (eg. Location of GUI on Users Machine). You can also modify any of these values and you could also add new properties as needed for your application. Also note that this file should only contain specific settings to your GUI window(s). If the Developer chooses he/she wants to save GUI settings at some point during runtime the {@link UIPropertiesMgr#save()} method can be utilized to save new property values.</td></tr>
     * <caption>Property Files</caption>
     * </table>
     *
     * <p><b>Developers Note</b></p>
     * During the initial start up (first time that the JavaFX application has started within the dev environment) if the
     * application.properties, myLogging.properties, or simplefx.gui.properties do not exist within the developers implementing JavaFX project
     * then they will be created for the developer and also placed within the java source (commonly named "src") directory. A popup will be
     * displayed to the developer detailing this process.
     */
    private void initializeFxApplicationResources() {
        myLogger.entering(MY_CLASS_NAME, "initializeFxApplicationResources()");
        //checking to see if the application is packaged as a jar.
        if(Constants.IS_JAR){
            initializeLogging();
            initializeApplicationProperties();
            initializeSimpleFXGUIProperties();
        }else{//application is not packaged as a jar and is in what I call development mode
            myLogger.fine("Developer logic below is used for making sure the proper files are used.");
            //check to see if the SimpleFX properties files exist on class path of where the subclass of this abstract class is located.
            boolean logPropsExist = fileExistsOnClasspath(subclass, "myLogging.properties");
            boolean appPropsExist = fileExistsOnClasspath(subclass, "application.properties");
            boolean guiPropsExist = fileExistsOnClasspath(subclass, "simplefx.gui.properties");

            File applicationClassPathRoot = null;
            if(!appPropsExist || !logPropsExist){
                applicationClassPathRoot = new File(getParsedAppLocationRootPath());
                if(!applicationClassPathRoot.exists()){
                    myLogger.warning("Developer workspace does not contain a src folder, please update your java source directory within your workspace to src");
                    showDialog(DialogType.BUILD_PATH_ISSUE);
                }//end if
            }//end if

            if(!logPropsExist){
                copyInternalFileToExternalDestination(SFXApplication.class, applicationClassPathRoot.getAbsolutePath(), "simpleFxMyLogging.properties", "myLogging.properties");
                SFXDialogLauncher.internalLogPropertiesPath = applicationClassPathRoot.getAbsolutePath() + "\\myLogging.properties";
                FindAndReplaceRegEx regex = new FindAndReplaceRegEx(new File(applicationClassPathRoot.getAbsolutePath() + "/myLogging.properties"), "(.*)(FxApplication)(.*)(FxApplication)(.*)", getResourcesParentDirectoryName(), 2, 4);
                try{
                    if(!regex.call()){
                        throw new Exception("Could not successfully replace values within the myLogging.properties file. Please Inform a lead developer of this issue.");
                    }//end if
                }catch(Exception e){
                    myLogger.log(Level.SEVERE, "Exception occurred while trying to configure myLogging.properties file. Error is: " + e.getMessage(), e);
                    SFXDialogLauncher.exception = e;
                    showDialog(DialogType.FRAMEWORK_EXCEPTION);
                }//end try...catch
            }//end if

            if(!appPropsExist){
                //copying to application.properties to classpath root!!!
                copyInternalFileToExternalDestination(SFXApplication.class, applicationClassPathRoot.getAbsolutePath(), "simpleFxApplication.properties", "application.properties");
                SFXDialogLauncher.internalAppPropertiesPath = applicationClassPathRoot.getAbsolutePath() + "\\application.properties";
            }//end if

            if(!guiPropsExist){
                //copying to simplefx.gui.properties to classpath root!!!
                copyInternalFileToExternalDestination(SFXApplication.class, applicationClassPathRoot.getAbsolutePath(), "simplefx.gui.properties");
                SFXDialogLauncher.internalGUIPropertiesPath = applicationClassPathRoot.getAbsolutePath() + "\\simplefx.gui.properties";
            }//end if

            //checking to see if this is the first time the JavaFX application was ran.
            if(!appPropsExist || !logPropsExist || !guiPropsExist){
                System.out.println("Displaying the initial Thank You For Choosing To Use SimpleFX Framework Message");
                showDialog(DialogType.SIMPLE_FX_INIT);
            }//end if

            //start initializing the logging, application properties, gui properties
            if(appPropsExist && logPropsExist && guiPropsExist){
                initializeLogging();
                initializeApplicationProperties();
                initializeSimpleFXGUIProperties();
            }//end if
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "initializeFxApplicationResources()");
    }// end method

    /**
     * This method will initialize and load the logging properties located within the myLoggings.properties file.
     * Once properties are fully loaded then the logging will start.
     */
    private void initializeLogging() {
        myLogger.entering(MY_CLASS_NAME, "initializeLogging()");
        File myLoggingProperties = new File(externalResourcesDirectory + "/myLogging.properties");
        if(!myLoggingProperties.exists()){
            myLogger.info("Extracting the myLogging.properties file from application.");
            copyInternalFileToExternalDestination(subclass, externalResourcesDirectory, "myLogging.properties");
        }//end if

        try{
            boolean validated = LoggingMgr.getInstance(myLoggingProperties).validateRequiredPropertiesExist();
            if(validated){
                LoggingMgr.getInstance().startLogging();
                myLogger.info("Logging has initialized succefully.");
            }else{
                myLogger.severe("There are missing properties within the " + String.valueOf(myLoggingProperties.getAbsolutePath()) + " therefore the JavaFX application will not start up until this issue is resovled.");
                showDialog(DialogType.LOGGING_PROPS);
            }//end if
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception thrown during the validation and loading the " + String.valueOf(myLoggingProperties.getAbsolutePath()) + " used within the application", e);
            SFXDialogLauncher.exception = e;
            showDialog(DialogType.FRAMEWORK_EXCEPTION);
        }//end try...catch
        myLogger.exiting(MY_CLASS_NAME, "initializeLogging()");
    }// end initialize Logging

    /**
     * This method will initialize and load the SimpleFX GUI properties located within the simplefx.gui.properties file.
     */
    private void initializeSimpleFXGUIProperties() {
        myLogger.entering(MY_CLASS_NAME, "initializeSimpleFXGUIProperties");
        File guiProperties = new File(externalResourcesDirectory + "/simplefx.gui.properties");
        if(!guiProperties.exists()){
            myLogger.info("Extracting the simplefx.gui.properties file from application.");
            copyInternalFileToExternalDestination(subclass, externalResourcesDirectory, "simplefx.gui.properties");
        }//end if

        try{
            boolean validated = UIPropertiesMgr.getInstance(guiProperties).validateRequiredPropertiesExist();
            if(!validated){
                myLogger.severe("There are missing properties within the " + String.valueOf(guiProperties.getAbsolutePath()) + " therefore the JavaFX application will not start up until this issue is resovled.");
                showDialog(DialogType.GUI_PROPS);
            }//end if
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception thrown during the validation and loading the " + String.valueOf(guiProperties.getAbsolutePath()) + " used within the application", e);
            SFXDialogLauncher.exception = e;
            showDialog(DialogType.FRAMEWORK_EXCEPTION);
        }//end try...catch
    }//end method

    /**
     * This method will initialize and load the JavaFX Application properties located within the application.properties file.
     */
    private void initializeApplicationProperties() {
        myLogger.entering(MY_CLASS_NAME, "initializeApplicationProperties() extract and load the application.properties file");
        File appProperties = new File(externalResourcesDirectory + "/application.properties");
        if(!appProperties.exists()){
            myLogger.info("Extracting the application.properties file from application.");
            copyInternalFileToExternalDestination(subclass, externalResourcesDirectory, "application.properties");
        }else{
            // load internal and external then doing a comparison if there are any differences then
            if(propsAreEqual("application.properties")){
                myLogger.info("Properties are equal not going to modify anything here...");
            }else{
                myLogger.info("Application Properties are not equal going to application.properties so that it can be replaced with internal application.properites file");
                appProperties.delete();//delete the properties
                copyInternalFileToExternalDestination(subclass, externalResourcesDirectory, "application.properties");
            }//end if
        }//end if

        try{
            boolean validated = PropertiesMgr.getInstance(appProperties).validateRequiredPropertiesExist();
            if(!validated){
                myLogger.severe("There are missing properties within the " + String.valueOf(appProperties.getAbsolutePath()) + " therefore the JavaFX application will not start up until this issue is resovled.");
                showDialog(DialogType.APPLICATION_PROPS);
            }//end if
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception thrown during the validation and loading the " + String.valueOf(appProperties.getAbsolutePath()) + " used within the application", e);
            SFXDialogLauncher.exception = e;
            showDialog(DialogType.FRAMEWORK_EXCEPTION);
        }//end try...catch

        if(System.getenv("USERDOMAIN").equals("ISU")){
            Constants.APPLICATION_SHARED_DIRECTORY = PropertiesMgr.getInstance().getProperties().getProperty("isu.shared.location");
        }else{
            Constants.APPLICATION_SHARED_DIRECTORY = PropertiesMgr.getInstance().getProperties().getProperty("ads.shared.location");
        }// end if
        myLogger.info("Shared directory that was set based on domain is: " + Constants.APPLICATION_SHARED_DIRECTORY);
        myLogger.exiting(MY_CLASS_NAME, "initializeApplicationProperties");
    }//end method

    /**
     * This method will display a custom dialog determined by the {@code DialogType}.  The {@code DialogType} is set within the {@link SFXDialogLauncher}
     * class and then it is launched from the {@code JavaFX Application Thread}.
     * @param dialogType the {@code DialogType} to display to the user
     */
    private void showDialog(DialogType dialogType) {
        myLogger.entering(MY_CLASS_NAME, "showDialog", dialogType);
        SFXDialogLauncher.type = dialogType;
        Application.launch(SFXDialogLauncher.class, commandLineArgs);
        myLogger.exiting(MY_CLASS_NAME, "showDialog");
    }//end method


    /**
     * This method will return the JavaFX Application java source folder.  Currently this method is only called by the
     * {@link #initializeFxApplicationResources}. This method is only used during Development Mode and is utilized for
     * helping the developer get started with using the SimpleFX Framework.
     * @return the application root file path
     */
    private String getParsedAppLocationRootPath() {
        myLogger.entering(MY_CLASS_NAME, "getParsedAppLocationRootPath()");
        File rootPath = new File(Constants.APP_FILE_LOCATION);
        if(rootPath.getAbsolutePath().endsWith("src")){
            return rootPath.getAbsolutePath();
        }//end if

        String absoluteRoot = rootPath.getAbsolutePath();
        if(absoluteRoot.endsWith("build\\classes")){
            absoluteRoot = absoluteRoot.substring(0, absoluteRoot.lastIndexOf("build\\classes")) + "src";
        }else if(absoluteRoot.endsWith("bin")){
            absoluteRoot = absoluteRoot.substring(0, absoluteRoot.lastIndexOf("bin")) + "src";
        }else{
            System.err.println("Your Default output folder for your workspace is outputting to something other than build/classes, src, or bin...please make sure to point your output folder to src");
            showDialog(DialogType.BUILD_PATH_ISSUE);
            //close application here
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "getParsedAppLocationRootPath()");
        return absoluteRoot;
    }//end method

    /**
     * This method checks to see if external and internal properties files are equal based on the properties file path passed into this method.
     *
     * @param propertiesFile the path to the properties file
     * @return {@code true} if internal and external properties files are equal {@code false} if they are not
     */
    private boolean propsAreEqual(String propertiesFile) {
        myLogger.entering(MY_CLASS_NAME, "propsAreEqual()", propertiesFile);
        boolean isEqual = true;
        Properties externalProps = loadExternalPropertiesFile(externalResourcesDirectory, propertiesFile);
        Properties internalProps = loadInternalPropertiesFile(subclass, propertiesFile);
        Set<Object> xkeys = externalProps.keySet();
        Set<Object> ikeys = internalProps.keySet();
        //for now only worrying about the application properties file but in future enhancement or updates this could be changed
        //1)doing a simple size to size compare on application.properties
        if(xkeys.size() == ikeys.size()){
            //1)simple size to size compare passed so now do a name to name compare on keys within the application.properties
            Iterator<Object> it = ikeys.iterator();
            while(it.hasNext()){
                Object internalKey = it.next();
                if(!externalProps.containsKey(internalKey)){
                    myLogger.warning("external properties file is different than the internal one within the developers application");
                    isEqual = false;
                    break;
                }//end if
            }//end while
        }else{
            isEqual = false;
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "propsAreEqual()");
        return isEqual;
    }//end method

    /**
     * This method will return the application parameters if needed by running javafx application.
     *
     * @return a String array of arguments.
     */
    public static String[] getApplicationParameters(){
        return commandLineArgs;
    }//end method

    /**
     * This method will return the external resources directory path that was created by the JavaFX application.
     * @return the file path to the external resources directory.
     */
    protected static String getExternalResourcesDirectory() {
        return externalResourcesDirectory;
    }// end getExternalResourcesDirectory

    /**
     * This method will return a static instance of the SFXApplication class
     * @return appClass the {@link SFXApplication} instance
     */
    static SFXApplication getSFXApplicationClass(){
        return appClass;
    }//end method

    /**
     * This method will return the parent Resources directory name. This method is used during the creating of the resources directory structure ({@link #initializeFxApplicationResources}) .
     *
     * <p>
     * Typical usage is:
     * <pre>
     * <code>
     * {@literal @Override} public String getResourcesParentDirectoryName() {
     *       return "ExampleApp";
     *   }
     * </code>
     * </pre>
     *
     * <p>Note: If this method returns null or empty string the default resource directory will be <b>FxApplication</b>.</p>
     *
     *
     * @return the parent directory name.
     */
    public abstract String getResourcesParentDirectoryName();

}//end class
