package com.omo.free.simple.fx.managers;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import com.omo.free.simple.fx.util.Constants;

/**
 * The AbstractPropertiesMgr class defines the methods common to all the {@code XxxMgr} classes within the SimpleFX Framework.
 *
 * @author Richard Salas JCCC
 * @version 1.0
 *
 * @see UIPropertiesMgr
 * @see PropertiesMgr
 * @see LoggingMgr
 */
public abstract class AbstractPropertiesMgr {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.managers.AbstractPropertiesMgr";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    protected Properties properties;
    protected File propertiesFile;
    protected StringBuffer propertyErrorMsgs;

    /**
     * Creates an instance of the AbstractPropertiesMgr class
     */
    public AbstractPropertiesMgr() {

    }//end constructor

    /**
     * This method is used for validating properties that are required by your application..
     *
     * <p>Note that the this method is abstract and must be implemented within your concrete class.</p>
     *
     * <p>
     * Typical usage is:
     * <pre>
     * <code>
     *  {@literal @Override} public boolean validateRequiredPropertiesExist() {
     *      boolean checkPassed = true;
     *      if(properties.getProperty("application.name") == null){
     *          appendMissingPropertyMsg("Missing 'application.name' in properties file.");
     *          checkPassed = false;
     *      }// end if
     *
     *      if(properties.getProperty("window.location.x") == null){
     *          appendMissingPropertyMsg("Missing 'window.location.x' in properties file.");
     *          checkPassed = false;
     *      } // end if
     *
     *      if(properties.getProperty("window.location.y") == null){
     *          appendMissingPropertyMsg("Missing 'window.location.y' in properties file.");
     *          checkPassed = false;
     *      } // end if
     *
     *      if(!checkPassed){
     *          myLogger.severe("Missing one or more properties. Would receive unpredicted results, check logs!");
     *          myLogger.setLevel(Level.CONFIG);
     *          propertiesFile.delete();
     *      } // end if
     *      return checkPassed;
     *  }//end if
     * </code>
     * </pre>
     *
     * @return {@code true} if properties are valid or {@code false} if properties are not valid
     */
    protected abstract boolean validateRequiredPropertiesExist();

    /**
     * This method will append a missing property message to the {@code propertyErrorMsgs} {@code StringBuffer} variable.
     *
     * @param message the message containing a message e.g. "Missing 'window.location.y' in properties file."
     */
    protected void appendMissingPropertyMsg(String message) {
        myLogger.entering(MY_CLASS_NAME, "appendMissingPropertyMsg", message);
        if(propertyErrorMsgs==null){
            propertyErrorMsgs = new StringBuffer(message);
            return;
        }//end if
        propertyErrorMsgs.append(Constants.LINESEPERATOR).append(message);
        myLogger.exiting(MY_CLASS_NAME, "appendMissingPropertyMsg");
    }//end method

    /**
     * This method will return the missing property messages.
     * @return the property error messages
     */
    public String getMissingPropertyMessage(){
        if(propertyErrorMsgs==null){
           return "No Error Messages";
        }//end if
        return propertyErrorMsgs.toString();
    }//end method

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }//end method

}//end class
