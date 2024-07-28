/**
 *
 */
package com.omo.free.simple.fx.application;

/**
 * The <code>Headless</code> interface is used in the simple ui framework and is used to start a ui application without a user interface.
 *
 * @author Richard Salas
 * @since SimpleUI Framework 1.0.0
 */
public interface Headless {

    /**
     * This method will start the headless version of the application.
     *
     * @param args arguments that are passed into the application at startup
     */
    public void startHeadlessProcess(String[] args);

    /**
     * This method will validate the start up arguments that are passed to the application at start up.  If the arguments are not valid the application will shutdown.
     * @param args arguments that are passed into the application at startup
     * @return true|false
     */
    public boolean validateHeadlessArgs(String[] args);

}
