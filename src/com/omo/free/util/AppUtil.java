package com.omo.free.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.omo.free.simple.fx.util.Constants;

/**
 * AppUtil class - Contains methods common to the application.
 *
 * @author unknown
 * @author updated by Richard Salas JCCC
 * @author modified by Johnnie Stidum JCCC 11/15/2017
 */
public class AppUtil {
    private static final String MY_CLASS_NAME = "com.omo.free.util.AppUtil";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /***
     * This method return true if the given String is null or empty else returns false.
     *
     * @param s
     *        The String to check.
     * @return boolean True if null or empty, false otherwise.
     */
    public static boolean isNullOrEmpty(String s) {
        myLogger.entering(MY_CLASS_NAME, "isNullOrEmpty", s);
        return (s == null || s.trim().equals("")) ? true : false;
    }

    /**
     * This method will let the calling method know if it is running within a jar based upon the class passed in as a parameter.
     * @param clazz class to check for jar path
     * @return true or false value based upon whether or not caller is being ran from a jar
     */
    public static boolean isJar(Class<?> clazz) {
        myLogger.entering(MY_CLASS_NAME, "isNullOrEmpty", clazz);
        return clazz.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(".jar");
    }

    /***
     * This method return true if the given List is null or empty else returns false.
     *
     * @param list
     *        The list to check.
     * @return boolean True if null or empty, false otherwise.
     */
    public static boolean isEmpty(List<?> list) {
        return (list != null && list.size() > 0) ? false : true;
    }

    /**
     * Used to return the word 'null' if the Object passed in equals null. This method called only from overridden toString() in object classes and forms.
     *
     * @param o
     *        The Object to check if is null.
     * @return String The literal 'null' if is null, otherwise the value of the parameter.
     */
    public static String isNull(Object o) {
        myLogger.entering(MY_CLASS_NAME, "isNull", o);
        return o == null ? "null" : String.valueOf(o);
    }

    /**
     * Added this to log the time.  Just a helper method for determining performance problems
     *
     * @param start time
     * @return String of times took.
     */
    public static String getTimeTookInSecMinHours(long start) {
        myLogger.entering(MY_CLASS_NAME, "getTimeTookInSecMinHours", start);
        long finish = System.currentTimeMillis() - start;
        long hours, minutes, seconds;
        seconds = finish / 1000;
        hours = seconds / 3600;
        seconds = seconds - (hours * 3600);
        minutes = seconds / 60;
        seconds = seconds - (minutes * 60);
        myLogger.exiting(MY_CLASS_NAME, "getTimeTookInSecMinHours");
        return "Process took " + hours + " hours " + minutes + " minutes and " + seconds + " seconds";
    }

    /**
     * This method is for checking to see if a value exists within the Path environment variable.
     *
     * @param value
     *        - the value that will be checked for inside of the Path variable
     * @return boolean - true or false based upon whether or not the value was located
     */
    public static boolean valueExistsInPathEnvVariable(String value) {
        myLogger.entering(MY_CLASS_NAME, "valueExistsInPathEnvVariable(...)", value);

        boolean exists = false;

        String path = System.getenv("Path").toLowerCase();

        if(path.contains(value.toLowerCase())){
            exists = true;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "valueExistsInPathEnvVariable(...)", exists);
        return exists;
    }

    /**
     * This method will retrieve the user's id from within the users environment variables.
     * @return user id of the user that ran the job
     */
    public static String getUserIdFromEnvVar(){
        myLogger.entering(MY_CLASS_NAME, "getUserIdFromEnvVar");
        String user = System.getenv("USERNAME");
        if(user != null && user.length() > 0 && !user.contains("SYSTEM")){
            myLogger.exiting(MY_CLASS_NAME, "getUserIdFromEnvVar");
            return user;
        }//end if

        // well it did not meet the above criteria.
        user = System.getenv("USERPROFILE");
        Pattern p = Pattern.compile("^(.*\\\\.*\\\\)(.*)$");
        Matcher m = p.matcher(user);
        boolean isMatch = m.find();
        if(isMatch){
            user = m.group(2);
        }else{
            user = "unknown user";
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "getUserIdFromEnvVar");
        return user;
    }

    /**
     * This method will break up a string from one large line into smaller lines by breaking the line using the {@code maxNumOfCharacters} threshold values.
     *
     * @param stringToBreakUp the string to breakdown into smaller lines
     * @param maxNumOfCharacters the maximum number of characters per line
     * @return the string broken into smaller lines
     */
    public static String breakUpString(String stringToBreakUp, int maxNumOfCharacters) {
        myLogger.entering(MY_CLASS_NAME, "breakUpString", new Object[]{stringToBreakUp, maxNumOfCharacters});
        StringBuffer sb = new StringBuffer();
        if (stringToBreakUp == null){
            myLogger.exiting(MY_CLASS_NAME, "breakUpString");
            return sb.append("NULL!").toString();
        }//end if
        if(stringToBreakUp.length()>maxNumOfCharacters){
            char[] arrayOfCharacters = stringToBreakUp.toCharArray();
            int count = 0;
            for(int i = 0, j = arrayOfCharacters.length; i < j; i++){
                if(count == maxNumOfCharacters){
                    sb.append(Constants.LINESEPERATOR);
                    count = 0;
                }//end if
                sb.append(arrayOfCharacters[i]);
                count++;
            }//end for
        }else{
            sb.append(stringToBreakUp);
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "breakUpString", stringToBreakUp);
        return sb.toString();
    }//end method

    /**
     * This method will break down an error message if it is more than 100 hundred characters long
     * @param stringToBreakUp the string to break up
     * @return string that is broken up.
     */
    public static String breakUpString(String stringToBreakUp){
        return AppUtil.breakUpString(stringToBreakUp, 100);
    }//end method

    /**
     * Method checks to see if the array passed in is empty or not.
     *
     * @param list the array to check
     * @return true or false value on whether array is empty or not
     */
    public static boolean isEmptyArray(Object[] list) {
        return (list != null && list.length > 0) ? false : true;
    }//end method

    /**
     * Runs a system command called whoami to get permissions. This method has just been added for checking permissions for a user.  There is probably a better way and if you know of one please modify this method to it.
     *
     * @return true or false for obtaining the permissions
     * @throws Exception if an error occurs
     */
    public static boolean hasPermission() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "hasPermission() - runs a system command called whoami to get permissions.");
        Process process = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        boolean success = false;
        try{
            process = Runtime.getRuntime().exec("whoami /priv /fo csv");
            myLogger.info("permission check has started!");
            process.waitFor();

            bis = new BufferedInputStream(process.getInputStream());
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int num = bis.read(buffer, 0, buffer.length);
            while(num != -1){
                baos.write(buffer, 0, num);// write the number of bytes that were read
                baos.flush();
                num = bis.read(buffer, 0, buffer.length);// get all bytes into the buffer.
            }// end while
            String message = baos.toString();

            if(!AppUtil.isNullOrEmpty(message) && message.contains("SeTakeOwnershipPrivilege")){
                if(myLogger.isLoggable(Level.FINE)){
                    myLogger.fine("command prompt message returned from input stream is " + String.valueOf(message));
                }// end if
                success = true;
            }else{
                success = false;
                myLogger.warning("User does not have proper permissions.  Message returned by operating system is: " + String.valueOf(message));
            }// end if
        }catch(IOException e1){
            myLogger.log(Level.SEVERE, "Exception occurred while trying to execute the whoami command.  Error message is: " + e1.getMessage(), e1);
        }finally{
            try{
                if(bis != null){
                    bis.close();
                }// end if
                if(baos != null){
                    baos.close();
                }// end if
            }catch(IOException e1){
                myLogger.log(Level.SEVERE, "IOException occurred while trying to close input and output streams within the SetXPromptTask. Error message is: " + e1.getMessage(), e1);
            }// end try...catch
        }// end try...catch...finally
        myLogger.exiting(MY_CLASS_NAME, "hasPermission()", success);
        return success;
    }//end method

}//end class
