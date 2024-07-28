package com.omo.free.simple.fx.managers;

/**
 * FileChangeListener interface.
 *
 * @author unascribed
 */
interface FileChangeListener {

    /**
     * Invoked when a file changes.
     *
     * @param fileName
     *        name of changed file.
     */
    public void fileChanged(String fileName);
}
