package com.omo.free.simple.fx.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FileUtility;
import com.omo.free.util.AppUtil;

/**
 * The LoggingMgr class extends the {@link AbstractPropertiesMgr} class to inherit all common methods used by
 * all XxxMgr classes within the SimpleFX Framework.
 *
 * <p>The LoggingMgr class will validate the logging properties file (myLogging.properties) which is
 * used to configure and start the {@link java.util.logging.LogManager} which is used to maintain a
 * set of shared state about Loggers and log services. </p>
 *
 * <p><b>myLogging.properties File</b></p>
 * <p>The myLogging.properties contains all logging properties that are used by your JavaFX Application.
 * The myLogging.properties file will be automatically placed ont o your applications classpath by the SimpleFX
 * Framework upon implementation.  The main reason for you to modify the myLogging.properties file would be to
 * add additional application loggers within it.</p>
 *
 * <p>Note that if the myLogging.properties file does not exist on the classpath the SimpleFX Framework
 * will create one for you and also place it onto your classpath.</p>
 *
 * @author unascribed
 * @author Richard Salas JCCC
 * @see java.util.logging.LogManager
 */
public class LoggingMgr extends AbstractPropertiesMgr implements FileChangeListener {
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.util.LoggingMgr";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static LoggingMgr loggingMgr;

    //temp variables used for initializing a temporary properties file
    private File tempPropFile;
    private boolean isErrorOnTempProps;

    /**
     * Creates an instance of {@code LoggingMgr}.
     *
     * @param logPropertiesFile the file that contains the properties to be loaded
     * @throws Exception if problem occurs during loading new properties
     */
    private LoggingMgr(File logPropertiesFile) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "LoggingMgr", logPropertiesFile);
        this.propertiesFile = logPropertiesFile;
        loadProperties();
        myLogger.exiting(MY_CLASS_NAME, "LoggingMgr");
    }//end constructor

    /**
     * This method will return a static instance of the LoggingMgr.
     *
     * @return loggingMgr the {@code LoggingMgr} instance
     */
    public synchronized static LoggingMgr getInstance() {
        Logger.getLogger(MY_CLASS_NAME).entering(MY_CLASS_NAME, "getInstance");
        if(loggingMgr==null){
            throw new IllegalStateException("Could not initialize instance of the LoggingMgr because you must first make a call to the method getInstance(File logPropertiesFile) to initialize and start logging!");
        }//end if
        Logger.getLogger(MY_CLASS_NAME).exiting(MY_CLASS_NAME, "getInstance", loggingMgr);
        return loggingMgr;
    }//end method

    /**
     * This method is the initial method to call for initializing logging properties used within the SimpleFX Framework.
     *
     * @param logPropertiesFile the file that contains the properties to be loaded
     * @return loggingMgr the {@code LoggingMgr} instance
     * @throws Exception if problem occurs during loading new properties
     */
    public synchronized static LoggingMgr getInstance(File logPropertiesFile) throws Exception {
        Logger.getLogger(MY_CLASS_NAME).entering(MY_CLASS_NAME, "getInstance", logPropertiesFile);
        if(loggingMgr == null){
            loggingMgr = new LoggingMgr(logPropertiesFile);
        } // end if
        Logger.getLogger(MY_CLASS_NAME).exiting(MY_CLASS_NAME, "getInstance", loggingMgr);
        return loggingMgr;
    }//end method

    /**
     * Method to load properties specific to this application from the internal myLogging.properties file.
     *
     * @throws Exception throw if any problems occur within this method
     */
    public void loadProperties() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "loadProperties");
        // Logging configuration file will exist at this point since that is set up in the main method of Application.java
        try{
            // Load the logging configuration properties file for this application
            myLogger.info("Going to try and load the properties into memory here. Directory of config file is: " + propertiesFile.getParent() + "; and the config file name is: " + propertiesFile.getName());
            properties = FileUtility.loadExternalPropertiesFile(propertiesFile.getParent(), propertiesFile.getName());
        }catch(Exception e){
            if(!myLogger.isLoggable(Level.ALL)){
                myLogger.finest("Exception caught, loading properties for loggingMgr");
            } // end if
            myLogger.log(Level.SEVERE, "Caught an exception while trying to establish a properties object from the applications logging properties file. Message is: " + e.getMessage(), e);
            throw e;
        } // end catch
          // Set a default check time period
        long checkPeriod = 80000L;
        // If a default check time period exists, use it instead
        if(!AppUtil.isNullOrEmpty(properties.getProperty("log.checkPeriod"))){
            checkPeriod = Long.parseLong(properties.getProperty("log.checkPeriod"));
        } // end if
        setCheckPeriod(checkPeriod, propertiesFile.getPath());
        myLogger.exiting(MY_CLASS_NAME, "loadProperties");
    }//end method

    /**
     * This method is used to start outputing logging statements to a file using the myLogging.properties file for configuration.
     *
     * @throws SecurityException if a security violation
     * @throws FileNotFoundException if configuration file was not found
     * @throws IOException if configuration file was corrupted
     */
    public void startLogging() throws SecurityException, FileNotFoundException, IOException{
        FileInputStream fis = null;
        if(isErrorOnTempProps){
            fis = new FileInputStream(propertiesFile);
            LogManager.getLogManager().readConfiguration(fis);
        }else{
            fis = new FileInputStream(tempPropFile);
            LogManager.getLogManager().readConfiguration(fis);
        }//end if

        fis.close();
        if(!isErrorOnTempProps){
            tempPropFile.delete();
        }//end if
    }//end method

    /**
     * Method gets an instance of a file change listener to watch the internal myLogging.properties file for changes.
     *
     * @param monitorTime
     *        The length of time between file checks.
     * @param filePath
     *        The path to the file that is being watched.
     */
    private void setCheckPeriod(long monitorTime, String filePath) {
        myLogger.entering(MY_CLASS_NAME, "setCheckPeriod", new Object[]{monitorTime, filePath});
        try{
            FileMonitor.getInstance().addFileChangeListener(this, filePath, monitorTime);
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "Failed to get FileMonitor Logging properties will not update. Message is " + e.getMessage(), e);
        } // end catch
        myLogger.exiting(MY_CLASS_NAME, "setCheckPeriod");
    }//end method

    /**
     * This method will will get called when a change occurs to the myLogging.properties file. Only levels of logging will be
     * updated during the run of the application.
     *
     * <p>If there has been a change then the logger level will be changed to the new level.</p>
     * @param fileName the file name
     * @see FileMonitor
     */
    @Override public void fileChanged(String fileName) {
        myLogger.entering(MY_CLASS_NAME, "fileChange", fileName);
        try{
            loadProperties();
            String levelSuffix = ".level";
            Iterator<String> it = properties.stringPropertyNames().iterator();
            while(it.hasNext()){
                String name = it.next();
                if(name.endsWith(levelSuffix)){
                    String loggerName = name.substring(0, name.lastIndexOf(levelSuffix));
                    Level level = Level.parse(properties.getProperty(name));
                    Logger logger = Logger.getLogger(loggerName);
                    logger.setLevel(level);
                }//end if
            }//end while
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred while trying to update logging levels. Error message is :" + e.getMessage(), e);
        } // end catch
        myLogger.exiting(MY_CLASS_NAME, "fileChanged");
    }//end method

    /**
     * This method will set all application logger levels with the given {@link Level}.
     *
     * <p>This method will only set application logger {@code Level}s meaning only packages that start with the ITSD
     * standard package name of "gov" will be set.</p>
     * @param level the level that loggers will be set to.
     */
    public void setAllApplicationLoggersForOneCycle(Level level) {
        myLogger.entering(MY_CLASS_NAME, "setAllLoggersForOneCycle", level);
        setAllLoggers(level, properties.getProperty("log.checkPeriod"));
        myLogger.exiting(MY_CLASS_NAME, "setAllLoggersForOneCycle");
    }//end method

    /**
     * This method sets the loggers to the given {@code Level}.
     * @param level
     *        Level the logging level that will be set
     * @param checkPeriod the check period used to check the file for changes.
     */
    private void setAllLoggers(Level level, String checkPeriod) {
        myLogger.entering(MY_CLASS_NAME, "setAllLoggers", new Object[]{level, checkPeriod});
        myLogger.setLevel(level);
        myLogger.severe("Problem encountered, someone asked to max all loggers!");
        if(StringUtils.isNumeric(checkPeriod) && Integer.parseInt(checkPeriod) > 0){
            properties.setProperty("log.checkPeriod", checkPeriod);
        }else{
            myLogger.warning("checkPeriod was passed in as a null or is equal to or less than zero, this will be ignored and the origional value will remain in place. checkPeriod as passed in is " + checkPeriod);
        } // end if
        LogManager manager = LogManager.getLogManager();
        Enumeration<String> loggerNames = manager.getLoggerNames();
        while(loggerNames.hasMoreElements()){
            String name = loggerNames.nextElement();
            if(name.startsWith("gov")){
                myLogger.log(level, "Setting logger " + name + " to Level of " + level.getName());
                Logger.getLogger(name).setLevel(level);
                properties.setProperty(name, level.getName());
            }//end if
        } // end while
        try{
            loadProperties();
            if(this.propertiesFile.exists()){//this will ensure that file gets reset.
                myLogger.info("Application Loggers within the application will reset to original values once cycle completes in " + String.valueOf(checkPeriod));
                this.propertiesFile.setLastModified(System.currentTimeMillis());
            }//end if
        }catch(Exception e){
            myLogger.log(Level.SEVERE, " Exception occurred while trying to load properties. Error is: " + e.getMessage(), e);
        }//end try...catch.
        myLogger.exiting(MY_CLASS_NAME, "setAllLoggers");
    }//end method

    /**
     * This method is used for validating logging properties that are contained within the myLogging.properties file.
     * @return {@code true} if properties are valid; {@code false} if properties are invalid
     */
    @Override public boolean validateRequiredPropertiesExist() {
        myLogger.entering(MY_CLASS_NAME, "validateRequiredPropertiesExist");
        boolean checkPassed = true;
        String logHandlerPath = null;
        if(properties.getProperty("handlers") == null){
            appendMissingPropertyMsg("Missing 'handlers' in properties file.");
            checkPassed = false;
        }// end if
        if(properties.getProperty(".level") == null){
            appendMissingPropertyMsg("Missing '.level' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.ConsoleHandler.level") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.ConsoleHandler.level' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.ConsoleHandler.formatter") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.ConsoleHandler.formatter' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.FileHandler.formatter") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.FileHandler.formatter' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.FileHandler.limit") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.FileHandler.limit' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.FileHandler.count") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.FileHandler.count' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("log.checkPeriod") == null){
            appendMissingPropertyMsg("Missing 'log.checkPeriod' in properties file.");
            checkPassed = false;
        } // end if
        if(properties.getProperty("java.util.logging.FileHandler.pattern") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.FileHandler.pattern' in properties file.");
            checkPassed = false;
        }else if(properties.getProperty("java.util.logging.FileHandler.pattern").startsWith("./")){
            //resetting the property in memory to be the Start in directory as this needed
            logHandlerPath = properties.getProperty("java.util.logging.FileHandler.pattern");
            logHandlerPath = Paths.get(Constants.START_IN_DIR_PATH, logHandlerPath.replace("./", "")).toString();
        }else if(properties.getProperty("java.util.logging.FileHandler.pattern").startsWith(".\\")){
            logHandlerPath = properties.getProperty("java.util.logging.FileHandler.pattern");
            logHandlerPath = Paths.get(Constants.START_IN_DIR_PATH, logHandlerPath.replace(".\\", "")).toString();
        }// end else...if

        if(properties.getProperty("java.util.logging.FileHandler.append") == null){
            appendMissingPropertyMsg("Missing 'java.util.logging.FileHandler.append' in properties file.");
            checkPassed = false;
        } // end if

        if(!checkPassed){
            myLogger.severe("Missing one or more properties. Would receive unpredicted results, check logs!");
            myLogger.setLevel(Level.CONFIG);
            propertiesFile.delete();//deleted the log config file
        }else{
            //added logic for the log handler path per the problem with windows toolbar.  easiest fix for now is below.
            if(logHandlerPath != null){//making sure the logHandler path is not null
                FileUtility.checkDirectories(logHandlerPath);
                properties.setProperty("java.util.logging.FileHandler.pattern", logHandlerPath);

                tempPropFile = new File(propertiesFile.getParentFile().getPath() + "/temp.properties");

                FileOutputStream fos = null;
                try {
                    if(tempPropFile.exists()){///delete the file if it exists
                        tempPropFile.delete();
                    }//end if

                    tempPropFile.createNewFile();//create the file
                    fos = new FileOutputStream(tempPropFile);//initialize for writing output to the file
                    properties.store(fos, "temporary file");//store the properties within it.
                } catch (Exception e) {
                    myLogger.log(Level.SEVERE, "Exception occurred while trying to initialize the temporary properties file.  Error message is:  " + e.getMessage(), e);
                    isErrorOnTempProps = true;
                }finally{
                    if(fos!=null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            isErrorOnTempProps = true;
                        }//end try...catch
                    }//end if
                }//end try...finally
            }else{
                myLogger.warning("the logHandlerPath was null.  Here is what was returned from the the property " + String.valueOf(properties.getProperty("java.util.logging.FileHandler.pattern")));
                isErrorOnTempProps = true;//need to set this
            }//end if
        } // end if
        return checkPassed;
    }///end method

}//end class
