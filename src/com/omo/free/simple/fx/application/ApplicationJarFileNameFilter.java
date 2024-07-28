/**
 *
 */
package com.omo.free.simple.fx.application;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The application jar file filter for finding application jars contained within directories
 *
 * @author Richard Salas
 * @since SimpleUI Framework 1.0.0
 */
class ApplicationJarFileNameFilter implements FilenameFilter {

    private String prefix;

    /**
     * Initializes an instance of the ApplicationJarFileNameFilter
     * @param applicationPrefix the prefix name of the application jar
     */
    public ApplicationJarFileNameFilter(String applicationPrefix) {
        this.prefix = applicationPrefix;
    }

    /**
     * Tests if a specified file should be included in a file list.
     * @param dir the directory from which this filter is being used for
     * @param name the file name
     * @return accept true or false based on whether or not condition within method passes
     */
    @Override public boolean accept(File dir, String name) {
        boolean accept = false;
        if(name.contains(prefix) && name.contains(".jar")) {
            accept = true;
        }//end if
        return accept;
    }//end method

}
