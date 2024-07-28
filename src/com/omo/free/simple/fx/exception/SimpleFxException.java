package com.omo.free.simple.fx.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ApplicationException class
 * 
 */
public class SimpleFxException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2776562489414153794L;
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.exception.AppException";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private Throwable t;

    /**
     * Constructor
     * 
     * @param s
     *        The exception message.
     */
    public SimpleFxException(String s) {
        this(s, null);
    }

    /**
     * Constructor
     * 
     * @param t
     *        The cause of the exception.
     */
    public SimpleFxException(Throwable t) {
        this(null, t);
    }

    /**
     * Constructor
     * 
     * @param s
     *        The exception message.
     * @param t
     *        The cause of the exception.
     */
    public SimpleFxException(String s, Throwable t) {
        super(s);
        this.t = t;
    }

    /**
     * @return Throwable
     */
    public Throwable getException() {
        return t;
    }

    /**
     * Prints the stack trace to a PrintStream object
     * 
     * @param s
     *        PrintStream object transporting the stack trace
     */
    public void printStackTrace(PrintStream s) {
        synchronized (s){
            myLogger.log(Level.SEVERE, "AppException", getMessage());
            s.println(getMessage());
            if(t != null){
                t.printStackTrace(s);
            } // end if
        }
    }

    /**
     * Prints the stack trace to a PrintWriter object
     * 
     * @param s
     *        PrintWriter object transporting the stack trace
     */
    public void printStackTrace(PrintWriter s) {
        synchronized (s){
            myLogger.log(Level.SEVERE, "AppException", getMessage());
            s.println(getMessage());
            if(t != null){
                t.printStackTrace(s);
            } // end if
        }
    }

    /**
     * Returns a short description of this throwable.
     * @return a string representation of this class
     */
    @Override public String toString() {
        return getMessage();
    }
}
