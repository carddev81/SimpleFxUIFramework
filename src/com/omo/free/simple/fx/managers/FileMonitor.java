package com.omo.free.simple.fx.managers;

import static com.omo.free.simple.fx.util.Constants.LINESEPERATOR;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * FileMonitor class used to monitor files for changes by timestamp date modified.
 *
 * @author unascribed
 * @author Joseph Burris JCCC
 * @author Richard Salas JCCC
 */
class FileMonitor {
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.managers.FileMonitor";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static FileMonitor instance = new FileMonitor();
    private Timer timer;
    private Hashtable<String, FileMonitorTask> timerEntries;

    /**
     * Method creates an instance of this class.
     *
     * @return FileMonitor A static instance of this class.
     */
    public static FileMonitor getInstance() {
        Logger.getLogger(MY_CLASS_NAME).entering(MY_CLASS_NAME, "getInstance");
        Logger.getLogger(MY_CLASS_NAME).exiting(MY_CLASS_NAME, "getInstance", instance);
        return instance;
    } // end getInstance

    /**
     * Creates an instance of FileMonitor class.
     */
    protected FileMonitor() {
        myLogger.entering(MY_CLASS_NAME, "FileMonitor");
        // Create timer, run timer thread as daemon.
        timer = new Timer(true);
        timerEntries = new Hashtable<String, FileMonitorTask>();
        myLogger.exiting(MY_CLASS_NAME, "FileMonitor");
    } // end Constructor

    /**
     * Method to add a file change listener.
     *
     * @param listener
     *        FileChangeListener
     * @param fileName
     *        String
     * @param period
     *        long
     * @throws FileNotFoundException
     *         FileNotFoundException
     */
    public void addFileChangeListener(FileChangeListener listener, String fileName, long period) throws FileNotFoundException {
        myLogger.entering(MY_CLASS_NAME, "addFileChangeListener", new Object[]{listener, fileName, period});
        removeFileChangeListener(listener, fileName);
        FileMonitorTask task = new FileMonitorTask(listener, fileName);
        timerEntries.put(fileName + listener.hashCode(), task);
        timer.schedule(task, period, period);
        myLogger.exiting(MY_CLASS_NAME, "addfileChangeListener");
    } // end addFileChangeListener

    /**
     * Method to remove a file change listener.
     *
     * @param listener FileChangeListener
     * @param fileName String
     */
    public void removeFileChangeListener(FileChangeListener listener, String fileName) {
        myLogger.entering(MY_CLASS_NAME, "removeFileChangeListener", new Object[]{listener, fileName});
        FileMonitorTask task = (FileMonitorTask) timerEntries.remove(fileName + listener.hashCode());
        if (task != null) {
            task.cancel();
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "removeFileChangeListener");
    } // end removeFileChangeListener

    /**
     * Method to fire a file change event.
     *
     * @param listener FileChangeListener
     * @param fileName String
     */
    protected void fireFileChangeEvent(FileChangeListener listener, String fileName) {
        myLogger.entering(MY_CLASS_NAME, "fireFileChangeEvent", new Object[]{listener, fileName});
        listener.fileChanged(fileName);
        myLogger.exiting(MY_CLASS_NAME, "fireFileChangeEvent");
    } // end fireFileChangeEvent

    /**
     * FileMonitorTask class.
     *
     * @author unascribed
     * @author Joseph Burris JCCC
     * @author Richard Salas JCCC
     */
    class FileMonitorTask extends TimerTask {
        private static final String MY_INNER_CLASS_NAME = "com.omo.free.file.FileMonitor.FileMonitorTask";
        private Logger myLogger = Logger.getLogger(MY_INNER_CLASS_NAME);
        private FileChangeListener listener;
        private String fileName;
        private File monitoredFile;
        private long lastModified;

        /**
         * Constructor
         *
         * @param listener FileChangeListener
         * @param fileName String
         * @throws FileNotFoundException FileNotFoundException
         */
        public FileMonitorTask(FileChangeListener listener, String fileName) throws FileNotFoundException {
            myLogger.entering(MY_INNER_CLASS_NAME, "FileMonitorTask", new Object[]{listener, fileName});
            this.listener = listener;
            this.fileName = fileName;
            this.lastModified = 0;
            monitoredFile = new File(fileName);
            if (!monitoredFile.exists()) { // but is it on CLASSPATH?
                URL fileURL = listener.getClass().getClassLoader().getResource(fileName);
                if (fileURL != null) {
                    monitoredFile = new File(fileURL.getFile());
                } else {
                    FileNotFoundException e = new FileNotFoundException("File Not Found: " + fileName);
                    myLogger.throwing(MY_INNER_CLASS_NAME, "FileMonitorTask", e);
                    throw e;
                } // end if
            } // end if
            this.lastModified = monitoredFile.lastModified();
            myLogger.exiting(MY_INNER_CLASS_NAME, "FileMonitorTask");
        } // end Constructor

        /**
         * {@inheritDoc}
         */
        @Override public void run() {
            myLogger.entering(MY_INNER_CLASS_NAME, "run");
            if (monitoredFile.lastModified() != this.lastModified) {
                fireFileChangeEvent(this.listener, this.fileName);
            } // end if
            myLogger.exiting(MY_INNER_CLASS_NAME, "run");
        } // end run()
    } // end class

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return "myClassName=" + MY_CLASS_NAME + LINESEPERATOR + "timer=" + String.valueOf(timer) + LINESEPERATOR + "timerEntries size=" + String.valueOf(timerEntries) + LINESEPERATOR;
    }//end method

} // end class
