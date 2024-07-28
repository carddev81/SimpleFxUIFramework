/**
 *
 */
package com.omo.free.simple.fx.application;

/**
 * The <code>Refactorable</code> interface is used by the simple fx framework to allow developers to rename their application jar files if needed.  This interface will allow the application to update itself no matter what the name is.
 *
 * <b>I created this interface as just a test for an easy way of renaming an application and to not lose its update ability due to this.  USE with caution meaning to test the functionality thoroughly</b>
 *
 * @author Richard Salas
 * @version 1.0.0
 */
public interface Refactorable {

    /**
     * This method must return the future jar name of you application.
     *
     * <p>Example:  SetJavaUIv1.0</p>
     *
     * @return future jar name
     */
    public String futureJarName();

    /**
     * This method must return the current jar name of you application.
     *
     * <p>Example:  SetJavav1.0</p>
     *
     * @return current jar name
     */
    public String currentJarName();

}//end interface
