/**
 *
 */
package com.omo.free.simple.fx.managers;

import static com.omo.free.simple.fx.util.FileUtility.loadExternalPropertiesFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This {@code UIPropertiesMgr} class is used for managing GUI properties.  The properties managed by this class are settings that are used for
 * initializing default settings within the JavaFX GUI.
 *
 * <p>Three required properties are used during the initializing process of the JavaFX GUI window.
 * The three properties are shown and explained in the table below.</p>
 *
 * <p><b>Required Properties</b></p>
 * <table border="1" width="75%">
 * <tr><th>Property Name</th><th>Description</th></tr>
 * <tr><td>application.name</td><td>The name of the GUI application. <b>The value of this property will be displayed within the title bar of the GUI window</b></td></tr>
 * <tr><td>window.location.x</td><td>Horizontal position of the GUI window on the screen.</td></tr>
 * <tr><td>window.location.y</td><td>Vertical position of the GUI window on the screen.</td></tr>
 * <caption>Required Properties</caption>
 * </table>
 *
 * <p>The {@code UIPropertiesMgr} class allows the developer/user to save new properties.  The properties are saved everytime the JavaFX
 * GUI is closed.  The saved properties will be available for use upon the start up of the JavaFX application.</p>
 *
 * <p><b>Saving GUI Properties Example</b></p>
 * <p>It should be noted that usually on initial startup the scenario would be that the property that you want to save does not exist so therefore
 * the developer should take the initiative to add a null check before using the value as shown in the below code.</p>
 *
 * <pre><code>
    .
    .
    .
    public ExamplePane() {
        String isMailSelected = UIPropertiesMgr.getInstance().getProperties().getProperty("send.mail.selected");
        if(isMailSelected == null){
            //here is where you would put logic for when the property does not exist.
        }//end if
    }//end constructor

    public void close() {
        UIPropertiesMgr.getInstance().getProperties().put("send.mail.selected", sendMailCheckBox.isSelected());
    }//end method
    .
    .
    .
 * </code></pre>
 *
 * <p>Note that if the application.properties file does not exist on the classpath the SimpleFX Framework
 * will create the application.properties for you and also place it onto your classpath.</p>
 *
 * @author Richard Salas JCCC
 * @version 1.0
 *
 */
public class UIPropertiesMgr extends AbstractPropertiesMgr{

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.managers.UIPropertiesMgr";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static UIPropertiesMgr uiPropertiesMgr;

    /**
     * Creates an instance of UIPropertiesMgr.
     *
     * @param uiPropertiesFile gui properties to load
     * @throws Exception if problem occurs during loading
     */
    private UIPropertiesMgr(File uiPropertiesFile) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "PropertiesMgr", uiPropertiesFile);
        this.propertiesFile = uiPropertiesFile;
        loadProperties();
        myLogger.exiting(MY_CLASS_NAME, "PropertiesMgr");
    }

    /**
     * This method returns the {@code UIPropertiesMgr} instance.
     *
     * @return uiPropertiesMgr A static instance of this class.
     */
    public static UIPropertiesMgr getInstance() {
        myLogger.entering(MY_CLASS_NAME, "getInstance");
        myLogger.exiting(MY_CLASS_NAME, "getInstance", uiPropertiesMgr);
        return uiPropertiesMgr;
    }

    /**
     * This method is the initial method to call for initializing gui properties used by the SimpleFX Framework.
     *
     * @param uiPropertiesFile gui properties to load
     * @return uiPropertiesMgr A static instance of this class.
     * @throws Exception thrown when a problem occurs
     */
    public static UIPropertiesMgr getInstance(File uiPropertiesFile) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "getInstance", uiPropertiesFile);
        if(uiPropertiesMgr == null){
            uiPropertiesMgr = new UIPropertiesMgr(uiPropertiesFile);
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "getInstance", uiPropertiesMgr);
        return uiPropertiesMgr;
    }//end method

    /**
     * Method to load properties specific to this application. Look for external properties files.
     *
     * @throws Exception thrown when problems occur
     */
    private void loadProperties() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "loadProperties");
        try{
            properties = loadExternalPropertiesFile(propertiesFile.getParent(), propertiesFile.getName());
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "Unable to load properties. Exception: " + e.getMessage(), e);
            throw e;
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "loadProperties");
    }//end method

    /**
     * Method to validate that required properties exist.
     *
     * @return boolean Whether the required properties have values.
     */
    @Override public boolean validateRequiredPropertiesExist() {
        myLogger.entering(MY_CLASS_NAME, "propertyCheck");
        boolean checkPassed = true;
        if(properties.getProperty("application.name") == null){
            appendMissingPropertyMsg("Missing 'application.name' in properties file.");
            checkPassed = false;
        }// end if
        if(properties.getProperty("window.location.x") == null){
            appendMissingPropertyMsg("Missing 'window.location.x' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("window.location.y") == null){
            appendMissingPropertyMsg("Missing 'window.location.y' in properties file.");
            checkPassed = false;
        } // end if
        if(!checkPassed){
            myLogger.severe("Missing one or more properties. Would receive unpredicted results, check logs!");
            myLogger.setLevel(Level.CONFIG);
            propertiesFile.delete();
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "propertyCheck", checkPassed);
        return checkPassed;
    }//end method

    //TODO add exception handling and logging for saving properties in the methods below rts000is
    /**
     * This method will save all current and new properties to the simple.gui.properties file.
     * @throws Exception if properties cannot be saved
     */
    public void save() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "save");
        savePropertyState();
        saveNewProperties();
        myLogger.exiting(MY_CLASS_NAME, "save");
    }//end method

    /**
     * This method will save all the properties that currently exist within the simple.gui.properties file with new values if they exist.
     *
     * <p>The existing properties file is read and rewritten with new property values if they exist.</p>
     *
     * @throws Exception if properties file cannot be read or the properties file cannot be written
     */
    private void savePropertyState() throws Exception{
        myLogger.entering(MY_CLASS_NAME, "savePropertyState");
        BufferedReader br = null;
        BufferedWriter bw = null;
        File tmpFile = null;
        try{
            myLogger.info("Saving gui properties.");
            //create tmp file
            tmpFile = File.createTempFile("gui", "properties", propertiesFile.getParentFile());
            br = new BufferedReader(new FileReader(propertiesFile));
            bw = new BufferedWriter(new FileWriter(tmpFile));
            String line = br.readLine();
            String name = null;
            while(line!=null){
                line = line.trim();
                if(line.isEmpty()){//property = value
                    //empty line
                    bw.newLine();
                    line = br.readLine();
                    continue;
                }else if(line.startsWith("#")){
                    //comment line
                    bw.append(line);
                }else{
                    // this must be a property
                    //get property name associated with this line and then
                    // XXX - mlp000is
                    // found instance when this returns null
                    // happens when the key has been removed from the properties object! 
                    // then retrievePropertyName returns null because it cant find the key created
                    // by splitting this line since the key no longer exists
                    name = retrievePropertyName(line);
                    // if the key for this property has been removed lets not write it to the file..
                    if(null != name){
                        bw.append(String.valueOf(name)).append("=").append(properties.getProperty(String.valueOf(name)));
                    }// end if
                }//end if
                line = br.readLine();
                if(line!=null){//write new line if line is not equal to null
                    bw.newLine();
                }//end if
            }//end while
        }finally{
            //closing resources...
            if(br != null){
                br.close();
            }//end if
            if(bw != null){
                bw.close();
            }//end if
            if(tmpFile != null){
                propertiesFile.delete();
                tmpFile.renameTo(propertiesFile);
            }//end if
        }//end try...finally
        myLogger.exiting(MY_CLASS_NAME, "savePropertyState");
    }//end method

    /**
     * This method will determine if new properties exist and if new properties do exist they will be saved to the simple.gui.properties file.
     *
     * @throws Exception if properties cannot be written to the simple.gui.properties file
     */
    private void saveNewProperties() throws Exception{
        myLogger.entering(MY_CLASS_NAME, "saveNewProperties");
        // load new property file then create array.
        Properties propertiesToCompare =  loadExternalPropertiesFile(propertiesFile.getParent(), propertiesFile.getName());
        List<Object> keys = new ArrayList<Object>();
        Iterator<Object> it = properties.keySet().iterator();
        while(it.hasNext()){
            Object key = it.next();
            if(!propertiesToCompare.containsKey(key)){
                keys.add(key);
            }//end if
        }//end while

        if(keys.isEmpty()){
            return;
        }//end if

        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter(propertiesFile, true));
            bw.newLine();
            Iterator<Object> keysIt = keys.iterator();
            while(keysIt.hasNext()){
                Object name = keysIt.next();
                bw.append(String.valueOf(name)).append("=").append(properties.getProperty(String.valueOf(name)));
                bw.newLine();
            }//end while
        }finally{
            if(bw != null){
                bw.close();
            }//end if
        }//end try...finally
        myLogger.exiting(MY_CLASS_NAME, "saveNewProperties");
    }//end method

    /**
     * This method parse the given line and returns the name of a property.
     * @param line the value that contains a name of a property
     * @return the name of a property
     */
    private String retrievePropertyName(String line) {
        myLogger.entering(MY_CLASS_NAME, "retrievePropertyName", line);
        String returnName = null;
        if(line.contains("=")){//just to make sure....
            String[] array = line.split("=");
            Iterator<Object> it = properties.keySet().iterator();
            while(it.hasNext()){
                Object name = it.next();
                if(String.valueOf(name).equals(array[0].trim())){
                    returnName = String.valueOf(name);
                    break;
                }//end method
            }//end while
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "retrievePropertyName", returnName);
        return returnName;
    }//end method

}//end class
