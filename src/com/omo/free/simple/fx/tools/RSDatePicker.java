/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.util.AppUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/**
 * The RSDatePicker class extends the functionality of the {@code DatePicker} (Refer to the JavaFX API for documentation on the {@code DatePicker}
 * class) class with added features.
 *
 * <p>Currently there are 2 features added.</p>
 *
 * <ol>
 * <li>The {@code RSDatePicker} class gives you the option to create an instance of the {@code RSDatePicker} class with a date pattern.</li>
 * <li>The {@code RSDatePicker} class has a built in {@code ChangeListener} that will be set so that when the user types the date value within
 * the {@code TextBox}, it will be validated against the pattern and also the {@code valueProperty} of the {@code DatePicker} will be set on focus lost.</li>
 *  </ol>
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class RSDatePicker extends DatePicker {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.RSDatePicker";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private String pattern = "MM/dd/yyyy";
    private ChangeListener<Boolean> startUpListener;

    /**
     * Creates a {@code RSDatePicker} with the given date pattern.
     *
     * <p>Refer to the {@code SimpleDateFormat} class for date pattern usage.</p>
     *
     * @param datePattern the date pattern to use
     */
    public RSDatePicker(String datePattern) {
        super();
        myLogger.entering(MY_CLASS_NAME, "RSDatePicker()", datePattern);
        this.pattern = datePattern;
        init();
        myLogger.exiting(MY_CLASS_NAME, "RSDatePicker()");
    }//end constructor

    /**
     * Creates a {@code RSDatePicker} with the given date and date pattern.
     *
     * <p>Refer to the {@code SimpleDateFormat} class for date pattern usage.</p>
     *
     * @param date the initial date to use for displaying to the user within the {@code TextBox}
     * @param datePattern the date pattern to use
     */
    public RSDatePicker(LocalDate date, String datePattern) {
        super(date);
        myLogger.entering(MY_CLASS_NAME, "RSDatePicker", new Object[]{date, datePattern});
        this.pattern = datePattern;
        init();
        myLogger.exiting(MY_CLASS_NAME, "RSDatePicker");
    }//end constructor

    /**
    * Creates a {@code RSDatePicker} with a default date pattern of <b>MM/dd/yyyy</b>
    */
    public RSDatePicker() {
        super();
        myLogger.entering(MY_CLASS_NAME, "RSDatePicker");
        init();
        myLogger.exiting(MY_CLASS_NAME, "RSDatePicker");
    }//end constructor

    /**
     * This method will initialize the {@code RSDatePicker} with a custom {@link LocalDateStringConverter} and a Listener.
     */
    private void init(){
        myLogger.entering(MY_CLASS_NAME, "init");
        setConverter(new LocalDateStringConverter());
        //setting initial
        startUpListener = (observ, oldN, newN) -> attachRSFocusListener();
        focusedProperty().addListener(startUpListener);
        myLogger.exiting(MY_CLASS_NAME, "init");
    }//end init()

    /**
     * This method will be called once the RSDatePicker is first accessed to attach a {@code focusOwnerProperty()} listener to the {@code RSDatePicker}.
     */
    private void attachRSFocusListener() {
        myLogger.entering(MY_CLASS_NAME, "attachRSFocusListener");
        getScene().focusOwnerProperty().addListener(this::focusChanged);
        focusedProperty().removeListener(startUpListener);
        startUpListener = null; //nulling out for gc
        myLogger.exiting(MY_CLASS_NAME, "attachRSFocusListener");
    }//end method

    /**
     * This method is called upon when focus is removed from the {@code RSDatePicker} for validating the input within the {@code TextBox}.
     * @param observable the node that is being changed
     * @param oldNode the previous node that had focus
     * @param newNode the new node that gained focus
     */
    private void focusChanged(ObservableValue<? extends Node> observable, Node oldNode, Node newNode){
        myLogger.entering(MY_CLASS_NAME, "focusChanged()", new Object[]{observable, oldNode, newNode});
        if(oldNode == this){
            handleTextDateChanged();
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "focusChanged()");
    }//end method

    /**
     * This method validates the date within the {@code TextBox} of the DatePicker with the date {@code pattern} that is set within the {@code RSDatePicker} instance.
     */
    private void handleTextDateChanged() {
        myLogger.entering(MY_CLASS_NAME, "handleTextDateChanged()");
        String dateStr = getEditor().getText();
        if(AppUtil.isNullOrEmpty(dateStr)){
            getEditor().clear();
        }else{
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate dateDt = LocalDate.parse(dateStr, formatter);
                setValue(dateDt);
            }catch(DateTimeParseException e){
                myLogger.log(Level.WARNING, "DateTimeParseException occurred while trying to parse the date " + String.valueOf(dateStr) + ". Error message is: " + e.getMessage(), e);
                getEditor().clear();
            }//end try...catch
        }//end if...else
        myLogger.exiting(MY_CLASS_NAME, "handleTextDateChanged()");
    }//end method


    /**
     * The LocalDateStringConverter class is created by the {@link RSDatePicker} class for making sure that
     * the date pattern coincides with the string value within the {@code TextBox}.
     *
     * @author Richard Salas
     * @version 1.0
     */
    class LocalDateStringConverter extends StringConverter<LocalDate> {

        private static final String MY_INNER_CLASS_NAME = "com.omo.free.simple.fx.tools.RSDatePicker.LocalDateStringConverter";
        private Logger myInnerLogger = Logger.getLogger(MY_CLASS_NAME);

        private DateTimeFormatter dtFormatter;

        /**
         * Creates an instance of the {@code LocalDateStringConverter}.
         * <p>The pattern of the {@code DateTimeFormatter} class is set based on the {@code RSDatePicker}'s
         * date {@code pattern}</p>
         */
        public LocalDateStringConverter() {
            super();
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "LocalDateStringConverter()");
            dtFormatter = DateTimeFormatter.ofPattern(pattern);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "LocalDateStringConverter()");
        }// end constructor

        /**
         * Converts the string provided into an object defined by the specific converter.
         * Format of the string and type of the resulting object is defined by the specific converter.
         * @return an object representation of the string passed in.
         */
        @Override public LocalDate fromString(String text) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "fromString()", text);
            LocalDate date = null;
            if(text != null && !text.trim().isEmpty()){
                date = LocalDate.parse(text, dtFormatter);
            }//end if
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "fromString()", date);
            return date;
        }

        /**
         * Converts the object provided into its string form.
         * Format of the returned string is defined by the specific converter.
         * @return a string representation of the object passed in.
         */
        @Override public String toString(LocalDate date) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "toString()", date);
            String text = null;
            if(date != null){
                text = dtFormatter.format(date);
            }//end if
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "toString()", text);
            return text;
        }//end method
    }//end inner class

}//end class
