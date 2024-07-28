package com.omo.free.util;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * DateUtil is a utility class used to format dates.
 * 
 * @author Andrew Fagre
 * @author Joseph Burris JCCC - modification author
 */
public class DateUtil implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3084448485470349723L;
    // class variables
    private static final String MY_CLASS_NAME = "com.omo.free.util.DateUtil";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * This method converts a time to a string.
     * 
     * @param sqlTime
     *        The Time to convert.
     * @return string
     */
    public static String getTimeAsString(Time sqlTime) {
        myLogger.entering(MY_CLASS_NAME, "getTimeAsString", sqlTime);
        if(sqlTime != null){
            myLogger.exiting(MY_CLASS_NAME, "getTimeAsString");
            return new SimpleDateFormat("HH:mm").format(sqlTime);
        }else{
            myLogger.exiting(MY_CLASS_NAME, "getTimeAsString");
            return null;
        }//end if
    }

    /**
     * Returns the SQl Time as a hh:mm AM/PM String.
     * 
     * @param sqlTime
     *        The Time to format.
     * @return String The formatted Time.
     */
    public static String getFormatted12HrsTimeAsString(Time sqlTime) {
        myLogger.entering(MY_CLASS_NAME, "getFormatted12HrsTimeAsString", sqlTime);
        if(sqlTime != null){
            myLogger.exiting(MY_CLASS_NAME, "getFormatted12HrsTimeAsString");
            return new SimpleDateFormat("h:mm a").format(sqlTime);
        }else{
            myLogger.exiting(MY_CLASS_NAME, "getFormatted12HrsTimeAsString");
            return null;
        }//end if
    }

    /**
     * Used to get the current date plus the passed in amount of days.
     * 
     * @param days
     *        Days to increase the date by
     * @return java.sql.Date The increased date
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Date getCalculatedDate(int days) {
        myLogger.entering(MY_CLASS_NAME, "getCalculatedDate", days);
        java.sql.Date date = getSQLDate();
        date.setDate(date.getDate() + days);
        myLogger.exiting(MY_CLASS_NAME, "getCalculatedDate");
        return date;
    }

    /**
     * Used to get the system current date.
     * 
     * @return java.sql.Date Today
     */
    public static java.sql.Date getSQLDate() {
        myLogger.entering(MY_CLASS_NAME, "getSQLDate");
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * Used to convert a string date to an sql date.
     * 
     * @param stringDate
     *        The string value of the date to be converted.
     * @return java.sql.Date The date value of the converted string.
     * @throws ParseException
     *         ParseException
     */
    public static long asDate(String stringDate) throws ParseException {
        myLogger.entering(MY_CLASS_NAME, "asDate", stringDate);
        myLogger.log(Level.FINE, "Converting a string date of " + stringDate);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        java.util.Date utilDate;
        if(StringUtils.isNotEmpty(stringDate)){
            utilDate = df.parse(stringDate);
        }else{
            utilDate = df.parse("12/31/7799");
        } // end if
        myLogger.log(Level.FINE, "Returning the converted string date of " + stringDate + " in the form of an sql date equaling " + utilDate);
        myLogger.exiting(MY_CLASS_NAME, "asDate");
        return utilDate.getTime();
    } // end method

    /**
     * Used to return the string value of an sql date.
     * 
     * @param inDate
     *        The date to be converted
     * @return String The string value of the converted date.
     */
    public static String asString(java.sql.Date inDate) {
        myLogger.entering(MY_CLASS_NAME, "asString", inDate);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(inDate == null){
            myLogger.exiting(MY_CLASS_NAME, "asString");
            return "";
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "asString");
        return df.format(inDate);
    }

    /**
     * Used to return the string value of an util date. 
     * in yyyy-mm-dd 
     * 
     * @param inDate
     *        The date to be converted
     * @return String The string value of the converted date.
     */
    public static String asString(java.util.Date inDate) {
        myLogger.entering(MY_CLASS_NAME, "asString", inDate);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(inDate == null){
            myLogger.exiting(MY_CLASS_NAME, "asString");
            return "";
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "asString");
        return df.format(inDate);
    }
    
    /**
     * Used to get the current timestamp from the system.
     * 
     * @return String The formatted value of the current timestamp
     */
    public static String getSystemTimestampString() {
        myLogger.entering(MY_CLASS_NAME, "getSystemTimestampString");
        SimpleDateFormat sd = new SimpleDateFormat("[MM/dd/yyyy hh:mm:ss.SSS]");
        String date = "";
        try{
            Date dt = new Date();
            date = sd.format(dt);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception getting system timestamp as a string: " + e.getMessage());
        }//end try...catch
        myLogger.exiting(MY_CLASS_NAME, "getSystemTimestampString");
        return date;
    }

    /**
     * Utility method to get the standard default timestamp.
     * 
     * @return Timestamp with the value of '7799-12-31 00:00:00.0'
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Timestamp getDefaultTimestamp() {
        myLogger.entering(MY_CLASS_NAME, "getDefaultTimestamp");
        return new java.sql.Timestamp(new java.util.Date("12/31/7799").getTime());
    }

    /**
     * Returns String representation of todays Date in MM-DD-YYYY format.
     * 
     * @return String todays date
     */
    public static String getDateTime() {
        myLogger.entering("getDate", "Entering getDate().");
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }// end getDate()

    /**
     * Returns String representation of todays Date in MM-DD-YYYY format.
     * 
     * @return String todays date
     */
    public static String getDateTimeMinusPunctuation() {
        myLogger.entering("getDate", "Entering getDate().");
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }// end getDate()

}
