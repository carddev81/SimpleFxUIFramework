/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.omo.free.util.AppUtil;

/**
 * The ANTProcess class once instantiated is used to run an ANT process.  The {@code ANTProcess} class implements Runnable and
 * can be executed by a {@code Thread}.
 *
 * <p><b>What Is ANT?</b></p>
 * <p>ANT is known as a Java-based build tool which can be used for more than just building applications. Instead of a model where it is extended
 * with shell-based commands, Ant is extended using Java classes.  Instead of writing shell commands, the configuration files are XML-based,
 * calling out a target tree where various tasks get executed. Each task is run by an object that implements a particular Task interface.</p>
 *
 * <p><b>Example ANT File (XML)</b></p>
 * <p>The following example shows a simple ANT script.</p>
 * <pre><code>
{@literal <project name="template" default="start">}

    {@literal    <!-- Adds task definitions to this project (ie. if, for, throw, ..., ect.) -->}
    {@literal    <taskdef resource="net/sf/antcontrib/antlib.xml"/>}

    {@literal    <target name="start">}
        {@literal           <echo>This is just placeholder text and should be changed.</echo>}
    {@literal    </target>}

{@literal </project>}
 * </code></pre>
 *
 * <p><b>Example</b></p>
 * <p>The following shows an example of creating an instance of the {@code ANTProcess} class and then starts the {@code ANTProcess}.</p>
 *
 * <pre><code>
ANTProcess process = null;
try{
    process = new ANTProcess(antFile);
    process.setBaseDirectory(new File("C:/AntScriptBase"));
    process.setTargetNameToRun("start");
    process.run();

    if(process.isGood()){
        fetchProperties = process.getFetchProperties();
    }else{
        throw new Exception(process.getErrorMessage());
    }//end if
}finally{
    if(process!=null){
        process.close();
    }//end if
}//end try...finally
 * </code></pre>
 *
 * <p>Note that you must call the {@link #close} method once the {@code ANTProcess} instance is finished processing.</p>
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class ANTProcess implements Runnable{

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.ANTProcess";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private Project antProject;
    private String targetNameToRun;

    private File baseDirectory;
    private File antScript;
    private String processName;
    private Map<String, String> processProperties;
    private Map<String, String> fetchProperties;

    private boolean isGood;
    private String errorMessage;
    private String infoMessage;

    /**
     * Creates an ANTProcess instance with the given ANT {@code File}.
     *
     * <p>Note that the default target name is set to "run"</p>
     * @param antFile the ant file containing the scripting language to be executed
     */
    public ANTProcess(File antFile) {
        myLogger.entering(MY_CLASS_NAME, "ANTProcess()", antFile);
        if(!antFile.exists()){
            throw new IllegalArgumentException("The ant file " + antFile.getAbsolutePath() + " does not exist!");
        }//end if
        if(antFile.isDirectory()){
            throw new IllegalArgumentException("The ant file " + antFile.getAbsolutePath() + " is a directory and not a file!");
        }
        this.antScript = antFile;
        this.isGood = true;
        this.targetNameToRun = "run";//default target
        myLogger.exiting(MY_CLASS_NAME, "ANTProcess()");
    }//end constructor

    /**
     * Creates an {@code ANTProcess} instance with the given ANT {@code File} and ANT properties
     * <code>{@literal Map<String, String>}</code> to be set upon running the ANT script.
     *
     * <p>Note that the default target name is set to "run"</p>
     *
     * @param antFile the ant file containing the scripting language to be executed
     * @param runProperties key and value properties to be set before executing the ANT script.
     */
    public ANTProcess(File antFile, Map<String, String> runProperties){
        this(antFile);
        myLogger.entering(MY_CLASS_NAME, "ANTProcess()", new Object[]{antFile, runProperties});
        this.processProperties = runProperties;
        myLogger.exiting(MY_CLASS_NAME, "ANTProcess()");
    }//end constructor

    /**
     * Creates an {@code ANTProcess} instance with the given ANT {@code File} and an array of property names to be fetched
     * once the ANTProcess completes.
     *
     * <p>The fetch property names are the names of properties that were created by the script during its execution.
     * The user may need to use some of the property values that were set during the ANTProcess.  To access these you
     * must call the {@link ANTProcess#getFetchProperties()} method.</p>
     *
     * <p>Note that the default target name is set to "run"</p>
     *
     * @param antFile the ant file containing the scripting language to be executed
     * @param fetchProperties array of property names that are to be retrieved once the script has completed.
     */
    public ANTProcess(File antFile, String... fetchProperties){
        this(antFile);
        myLogger.entering(MY_CLASS_NAME, "ANTProcess()", new Object[]{antFile, fetchProperties});
        this.setFetchProperties(fetchProperties);
        myLogger.exiting(MY_CLASS_NAME, "ANTProcess()");
    }//end constructor

    /**
     * Creates an {@code ANTProcess} instance with the given ANT {@code File}, ANT properties <code>{@literal Map<String, String>}</code>
     * to be set upon running the ANT script, and an array of property names to be fetched once the ANTProcess completes.
     *
     * <p>The fetch property names are the names of properties that were created by the script during its execution.
     * The user may need to use some of the property values that were set during the ANTProcess.  To access these you
     * must call the {@link ANTProcess#getFetchProperties()} method.</p>
     *
     * <p>Note that the default target name is set to "run"</p>
     *
     * @param antFile the ant file containing the scripting language to be executed
     * @param runProperties key and value properties to be set before executing the ANT script.
     * @param fetchProperties array of property names that are to be retrieved once the script has completed.
     */
    public ANTProcess(File antFile, Map<String, String> runProperties, String... fetchProperties){
        this(antFile, runProperties);
        myLogger.entering(MY_CLASS_NAME, "ANTProcess", new Object[]{antFile, runProperties, fetchProperties});
        this.setFetchProperties(fetchProperties);
        myLogger.exiting(MY_CLASS_NAME, "ANTProcess");
    }//end constructor

    /**
     * This method will execute the {@code ANTProcess} instance.
     *
     * <p>When an object implementing interface Runnable is used to create a thread, starting the thread causes the object's
     * run method to be called in that separately executing thread.  General contract of the method run is that it may take any
     * action whatsoever.</p>
     */
    @Override public void run() {
        myLogger.entering(MY_CLASS_NAME, "run");
        initializeAntProject();
        if(isGood){
            try{
                /* Adding comments for debugging purposes RTS000IS */
                myLogger.info("ANT Process within " + antScript.getPath() + " file is starting.");
                long start = System.currentTimeMillis();
                preProcess();

                myLogger.info("Setting Build Listener if one exists.");
                BuildListener listener = getBuildListener();
                if(listener!=null){
                    antProject.addBuildListener(listener);
                }//end if

                myLogger.info("Firing build started...");
                antProject.fireBuildStarted();

                myLogger.info("init() method called...");
                antProject.init();

                myLogger.info("Getting ProjectHelper...");
                ProjectHelper projectHelper = ProjectHelper.getProjectHelper();

                myLogger.info("Adding ProjectHelper to ant project...");
                antProject.addReference("ant.projectHelper", projectHelper);

                myLogger.info("Parsing ant script...");
                projectHelper.parse(antProject, antScript);

                myLogger.info("Executing target " + String.valueOf(targetNameToRun) + "...");
                antProject.executeTarget(String.valueOf(targetNameToRun));

                myLogger.info("Retrieving properties from ant project...");
                retrievePropertiesFromAntProject();
                infoMessage = AppUtil.getTimeTookInSecMinHours(start);

                myLogger.info("Execute Post process...");
                postProcess();
            }catch(Exception e){
                isGood = false;
                errorMessage = "Exception occurred during the ant process.  " + e.getMessage();
                myLogger.log(Level.SEVERE, errorMessage, e);
            }//end if
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "run");
    }//end method

    /**
     * This method is used to retrieve fetchProperties that were set by the completed ANT process.
     *
     * <p>Note that the property values will be placed within a <code>Map{@literal <String, String>}</code> named {@code fetchProperties}.</p>
     *
     * @return the key and value pairs as a <code>Map{@literal<String, String>}</code>
     */
    private Map<String, String> retrievePropertiesFromAntProject() {
        myLogger.entering(MY_CLASS_NAME, "retrievePropertiesFromAntProject()");
        if(fetchProperties==null || fetchProperties.isEmpty()){
            return Collections.emptyMap();
        }//end if

        Iterator<String> it = fetchProperties.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            fetchProperties.put(key, String.valueOf(antProject.getProperty(key)));
        }//end while
        myLogger.exiting(MY_CLASS_NAME, "retrievePropertiesFromAntProject()");
        return fetchProperties;
    }//end method

    /**
     * This method will create a {@code org.apache.tools.ant.Project} instance and initialize it.
     */
    private void initializeAntProject() {
        myLogger.entering(MY_CLASS_NAME, "initializeAntProject()");
        antProject = new Project();
        try{
            antProject.setUserProperty("ant.file", antScript.getCanonicalPath());
            if(baseDirectory != null){
                antProject.setBaseDir(baseDirectory.getCanonicalFile());
            }//end if
            myLogger.fine("adding the runtime properties processProperties to the ant project before executing the process!");
            if(processProperties != null && !processProperties.isEmpty()){
                Iterator<Entry<String, String>> it = processProperties.entrySet().iterator();
                while(it.hasNext()){
                    Entry<String, String> entry = it.next();
                    antProject.setProperty(entry.getKey(), entry.getValue());
                }//end while
            }//end if
        }catch(Exception e){
            errorMessage = "Exception while trying to initialize the ant script file and the base directory. Error message is: " + e.getMessage();
            myLogger.log(Level.SEVERE, errorMessage, e);
            isGood = false;
        }//end try...catc
        myLogger.exiting(MY_CLASS_NAME, "initializeAntProject()");
    }//end if

    /**
     * This method initializes the {@code fetchProperties} <code>Map{@literal <String, String>}</code> with the given array of property names.
     * @param properties the array of property names to be used for initializing the {@code fetchProperties } <code>Map{@literal <String, String>}</code>
     */
    public void setFetchProperties(String... properties) {
        myLogger.entering(MY_CLASS_NAME, "setFetchProperties()", properties);
        if(properties != null){
            fetchProperties = null;//seting to null to build new process properties
            fetchProperties = new HashMap<>();
            for(int i = 0, j = properties.length; i < j; i++){
                fetchProperties.put(properties[i], null);
            }//end for
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "setFetchProperties()");
    }//end method

    /**
     * This method is used for setting the base directory which is where the script should act like it is being ran from.
     * @param baseDirectory the baseDirectory of the ant file
     */
    public void setBaseDirectory(File baseDirectory) {
        myLogger.entering(MY_CLASS_NAME, "setBaseDirectory()", baseDirectory);
        if(!baseDirectory.exists()){
            myLogger.log(Level.SEVERE, "The base directory " + baseDirectory.getAbsolutePath() + " does not exist!  You must either create this directory or choose a directory that exists!");
            throw new IllegalArgumentException("The base directory " + baseDirectory.getAbsolutePath() + " does not exist!  You must either create this directory or choose a directory that exists!");
        }//end if
        if(baseDirectory.isFile()){
            myLogger.log(Level.SEVERE, "The base directory " + baseDirectory.getAbsolutePath() + " is a file!  The base directory must be a directory!");
            throw new IllegalArgumentException("The base directory " + baseDirectory.getAbsolutePath() + " is a file!  The base directory must be a directory!");
        }//end if
        this.baseDirectory = baseDirectory;
        myLogger.exiting(MY_CLASS_NAME, "setBaseDirectory()");
    }//end method

    /**
     * This method must be called to close any open resources that the ANTProcess may have opened up during its execution.
     */
    public void close() {
        myLogger.entering(MY_CLASS_NAME, "close()");
        try{
            myLogger.info("Firing off Build finished for the " + antScript.getName() + " script.");
            antProject.fireBuildFinished(null);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred while trying to fire the build finished command.");
        }//end try...catch
        myLogger.exiting(MY_CLASS_NAME, "close()");
    }//end method

    /**
     * @param processProperties the processProperties to set
     */
    public void setProcessProperties(Map<String, String> processProperties) {
        this.processProperties = processProperties;
    }//end method

    /**
     * @return the fetchProperties
     */
    public Map<String, String> getFetchProperties() {
        return fetchProperties;
    }//end method

    /**
     * @return the processName
     */
    public String getProcessName() {
        return processName;
    }//end method

    /**
     * @param processName the processName to set
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * @param targetNameToRun the targetNameToRun to set
     */
    public void setTargetNameToRun(String targetNameToRun) {
        this.targetNameToRun = targetNameToRun;
    }

    /**
     * @return the isGood
     */
    public boolean isGood() {
        return isGood;
    }//end method

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }//end method

    /**
     * @return the infoMessage
     */
    public String getInfoMessage() {
        return infoMessage;
    }//end method

    /**
     * This method will return the project name, if one has been set.
     *
     * @return the project name, or null if it hasn't been set.
     */
    public String getProjectName(){
        return antProject == null ? "" : antProject.getName();
    }//end method

    /**
     * This method is meant to be overridden and will execute logic before the ant process is executed.
     */
    protected void preProcess(){
        myLogger.info("no post process will occur as it was not implemented.");
    }//end method

    /**
     * This method is meant to be overridden and will execute logic after the ant process finished.
     */
    protected void postProcess(){
        myLogger.info("no post process will occur as it was not implemented.");
    }//end method

    /**
     * This method is meant to be overridden and will allow the user to create an {@code BuildListener} implementation so that it can be registered to be notified when things happened during a build.
     *
     * @return a {@code BuildListener} instance
     */
    protected BuildListener getBuildListener(){
        return null;
    }//end method

}//end class
