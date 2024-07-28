package com.omo.free.simple.fx.exception;

/**
 * EmailException class.
 * 
 * @author unknown
 * @author Joseph Burris JCCC - modification author
 */
public class EmailException extends SimpleFxException {

    /**
     * 
     */
    private static final long serialVersionUID = 8551033605134556096L;

    /**
     * Constructor
     * 
     * @param s
     *        The exception message.
     */
    public EmailException(String s) {
        super(s);
    } // end constructor

    /**
     * Constructor
     * 
     * @param t
     *        The cause of the exception.
     */
    public EmailException(Throwable t) {
        super(t);
    } // end constructor

    /**
     * Constructor
     * 
     * @param s
     *        The exception message.
     * @param t
     *        The cause of the exception.
     */
    public EmailException(String s, Throwable t) {
        super(s, t);
    } // end constructor
} // end class
