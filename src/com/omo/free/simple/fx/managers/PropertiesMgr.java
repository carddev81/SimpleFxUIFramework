package com.omo.free.simple.fx.managers;

import static com.omo.free.simple.fx.util.FileUtility.loadExternalPropertiesFileWithEncryptedValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.util.Constants;
import com.omo.free.util.AppUtil;

/**
 * The class is used for loading required and user added application properties used during the runtime of a JavaFX application.
 *
 * <p>The {@code PropertiesMgr} class extends the {@link AbstractPropertiesMgr} class to inherit all common methods used by
 * all XxxMgr classes within the SimpleFX Framework. The {@code PropertiesMgr} class when instantiated will validate all of the
 * required properties found within the application.properties file which are used by the SimpleFX Framework during startup.
 * If these properties are missing an error will be displayed to the user.</p>
 *
 * <p><b>application.properties File</b></p>
 * <p>The application.properties file contains all application required and user added properties that are used by
 * your JavaFX Application. The application.properties file will be automatically placed onto your applications
 * classpath by the SimpleFX Framework, upon implementation.  This file is available to the user to add
 * as many key/value properties as he/she chooses.</p>
 *
 * <p>Note that if the application.properties file does not exist on the classpath the SimpleFX Framework
 * will create the application.properties for you and also place it onto your classpath.</p>
 *
 * @author unascribed
 * @author Joseph Burris JCCC
 * @author Richard Salas modified for simple fx.
 */
public class PropertiesMgr extends AbstractPropertiesMgr implements FileChangeListener {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.managers.PropertiesMgr";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static PropertiesMgr propertiesMgr;

    /**
     * Creates an instance of PropertiesMgr.
     *
     * @param appPropertiesFile application properties to load
     * @throws Exception thrown when a problem occurs during loading
     */
    private PropertiesMgr(File appPropertiesFile) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "PropertiesMgr", appPropertiesFile);
        this.propertiesFile = appPropertiesFile;
        loadProperties();
        myLogger.exiting(MY_CLASS_NAME, "PropertiesMgr");
    }//end constructor

    /**
     * This method returns the {@code PropertiesMgr} instance.
     *
     * @return propertiesMgr A static instance of this class.
     */
    public static PropertiesMgr getInstance() {
        myLogger.entering(MY_CLASS_NAME, "getInstance");
        if(propertiesMgr==null){
            throw new IllegalStateException("Could not initialize instance of the PropertiesMgr because you must first make a call to the method getInstance(File appPropertiesFile) to initialize and start logging!");
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "getInstance", propertiesMgr);
        return propertiesMgr;
    }//end constructor

    /**
     * This method is the initial method to call for initializing application properties used within the SimpleFX Framework.
     *
     * @param appPropertiesFile application properties to load
     * @return propertiesMgr the {@code PropertiesMgr} singleton instance
     * @throws Exception thrown when a problem occurs
     */
    public static PropertiesMgr getInstance(File appPropertiesFile) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "getInstance", appPropertiesFile);
        if(propertiesMgr == null){
            propertiesMgr = new PropertiesMgr(appPropertiesFile);
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "getInstance", propertiesMgr);
        return propertiesMgr;
    }//end method

    /**
     * Method to load properties specific to this application.
     *
     * @throws Exception thrown when problems occur
     */
    private void loadProperties() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "loadProperties");
        try{
            properties = loadExternalPropertiesFileWithEncryptedValues(propertiesFile.getParent(), propertiesFile.getName());
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "Unable to load properties. Exception: " + e.getMessage(), e);
            throw e;
        }// end try...catch

        // Set a default check time period
        long checkPeriod = 80000L;
        // If these properties do not exist, make defaults
        if(properties != null){
            // If a default check time period exists, use it instead
            if(!AppUtil.isNullOrEmpty(properties.getProperty("checkPeriod"))){//currently i did not add this property to the list of required properties...
                checkPeriod = Long.parseLong(properties.getProperty("checkPeriod"));
            } // end if
        } // end if
        setCheckPeriod(checkPeriod, propertiesFile.getPath());
        myLogger.exiting(MY_CLASS_NAME, "loadProperties");
    }

    /**
     * Method gets an instance of a file change listener to watch the internal application.properties file for changes.
     *
     * @param l
     *        The length of time between file checks.
     * @param s
     *        The path to the file that is being watched.
     */
    private void setCheckPeriod(long l, String s) {
        myLogger.entering(MY_CLASS_NAME, "setCheckPeriod", new Object[]{l, s});
        try{
            FileMonitor.getInstance().addFileChangeListener(this, s, l);
        }catch(FileNotFoundException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "Failed to get FileMonitor. Logging properties will not update. e= " + e.getMessage(), e);
        } // end catch
        myLogger.exiting(MY_CLASS_NAME, "setCheckPeriod");
    }

    /**
     * Method to validate that required properties exist.
     *
     * @return boolean Whether the required properties have values.
     */
    @Override public boolean validateRequiredPropertiesExist() {
        myLogger.entering(MY_CLASS_NAME, "propertyCheck");
        boolean checkPassed = true;
        if(properties.getProperty("temp.dir.holder") == null){
            appendMissingPropertyMsg("Missing 'temp.dir.holder' in properties file.");
            checkPassed = false;
        }else if(properties.getProperty("temp.dir.holder").startsWith("./")){
            //resetting the property in memory to be the Start in directory as this needed
            String newDirectory = properties.getProperty("temp.dir.holder");
            properties.setProperty("temp.dir.holder", Paths.get(Constants.START_IN_DIR_PATH, newDirectory.replace("./", "")).toString());
        }else if(properties.getProperty("temp.dir.holder").startsWith(".\\")){
            String newDirectory = properties.getProperty("temp.dir.holder");
            properties.setProperty("temp.dir.holder", Paths.get(Constants.START_IN_DIR_PATH, newDirectory.replace(".\\", "")).toString());
        }// end else...if

        if(properties.getProperty("ads.shared.location") == null){
            appendMissingPropertyMsg("Missing 'ads.shared.location' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("isu.shared.location") == null){
            appendMissingPropertyMsg("Missing 'isu.shared.location' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("debug.isOn") == null){
            appendMissingPropertyMsg("Missing 'debug.isOn' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.host") == null){
            appendMissingPropertyMsg("Missing 'email.host' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.from") == null){
            appendMissingPropertyMsg("Missing 'email.from' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.to") == null){
            appendMissingPropertyMsg("Missing 'email.to' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.bugs.to") == null){
            appendMissingPropertyMsg("Missing 'email.bugs.to' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.subject") == null){
            appendMissingPropertyMsg("Missing 'email.subject' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.cc") == null){
            appendMissingPropertyMsg("Missing 'email.cc' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("email.bcc") == null){
            appendMissingPropertyMsg("Missing 'email.bcc' in properties file.");
            checkPassed = false;
        } // end if
        if(!checkPassed){
            myLogger.severe("Missing one or more properties. Would receive unpredicted results, check logs!");
            myLogger.setLevel(Level.CONFIG);
            propertiesFile.delete();
            dump();
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "propertyCheck", checkPassed);
        return checkPassed;
    }//end method

    /**
     * Method to log all existing properties in internal application.properties file.
     *
     * <p>Map.Entry is a raw type. References to generic type <code>{@literal Map<K,V>.Entry<K,V>}</code> should be parameterized.</p>
     */
    @SuppressWarnings("rawtypes")
    public void dump() {
        myLogger.entering(MY_CLASS_NAME, "dump");
        if(myLogger.isLoggable(Level.CONFIG)){
            Iterator<?> iter = properties.entrySet().iterator();
            Map.Entry entry;
            while(iter.hasNext()){
                entry = (Map.Entry) iter.next();
                myLogger.config("Properties - Key [" + entry.getKey() + "] Value [" + (String.valueOf(entry.getKey()).contains("password") ? "*****" : entry.getValue())  + "]");
            }// end while
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "dump");
    }//end method

    /**
     * Invoked when a file changes.
     *
     * @param fileName
     *        name of changed file.
     */
    @Override public void fileChanged(String fileName) {
        myLogger.entering(MY_CLASS_NAME, "fileChange", fileName);
        try{
            loadProperties();//simply load properties
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "LogManager.readConfiguration() Failed, properties will not update. e= " + e.getMessage(), e);
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "fileChanged");
    }//end method

}//end class
