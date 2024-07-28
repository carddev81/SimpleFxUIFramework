package com.omo.free.simple.fx.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FileUtility;
import com.omo.free.util.AppUtil;

/**
 * The FileListFactory class is used for extracting internal files (located within the application either as a jar or an exploded development environment) to an
 * external destination (located outside of the application somewhere usually alongside of the running application).
 *
 * <p>If the internal files have been already extracted they will be synchronized meaning that there will be a binary
 * comparison between the internal and external files and if there are differences found then the external files will
 * be replaced with the internal files.  This is done to ensure that the application always uses the correct files.</p>
 *
 * <p><b>Example</b></p>
 * <p>The following example shows the basic usage of this class.</p>
 * <pre><code>
.
.
.
    //files are named similar to getARBLogs.xml
    File externalDir = new File("C:/TestDir/resources/scripts");
    FilenameFilter filter = new FilenameFilter(){
        {@literal @Override} public boolean accept(File dir, String name){
            return name.endsWith(".xml") &amp;&amp; name.startsWith("get");
        }
    };
    FileListFactory fileListfactory = new FileListFactory("/com/omo/free/getlogs/scripts/", externalDir.getPath(), "get", "Logs", ".xml", GetLogsPresenter.class);
    fileListfactory.synchronize(filter, true);
.
.
.
 * </code></pre>
 *
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class FileListFactory {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.FileListFactory";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private String internalFilesLocation;
    private String externalFilesLocation;
    private FilenameFilter fileNamesFilter;
    private List<String> filteredFilesList;
    private List<String> fileNames;
    private Class<?> clazz;
    private String filePrefix;
    private String fileSuffix;
    private String fileExtension;
    private boolean formatName;

    /**
     * This method will return the list of file names that was created from the list of file file names.
     * @return fileNames list containing strings that are valued as the name of a file
     */
    public List<String> getFileNames() {
        return fileNames;
    }//end method

    /**
     * Creates an instance of the FileListFactory with the give internal directory (located within the classpath) [required], external location [required], file prefix (optional), file suffix (optional), file extension (required), and calling class (required).
     *
     * <p><b>Example Breakdown Of File</b></p>
     * <p>The following example shows the parameter values to use for files named similar to <b>getARBLogs.xml</b> located within the package
     * /src/com/omo/free/getlogs/scripts.</p>
     *
     * <table border="1">
     * <tr><th>Parameter</th><th>Parameter Value</th></tr>
     * <tr><td>internalFileLocation</td><td>"/com/omo/free/getlogs/scripts"</td></tr>
     * <tr><td>externalLocaton</td><td>"C:/Resources/Scripts"</td></tr>
     * <tr><td>filePrefix</td><td>"get"</td></tr>
     * <tr><td>fileSuffix</td><td>"Logs"</td></tr>
     * <tr><td>fileExtension</td><td>".xml"</td></tr>
     * <tr><td>clazz</td><td>Caller.class</td></tr>
     * <caption>Parameter Examples</caption>
     * </table>
     *
     * @param internalFileLocation [REQUIRED] the internal location of the files on classpath, either this application will be ran within an exploded environment or as a jar.
     * @param externalLocaton [REQUIRED] the location that the files will be extracted to
     * @param filePrefix [OPTIONAL] the prefix of the file file
     * @param fileSuffix [OPTIONAL] the suffix of the file file
     * @param fileExtension [REQUIRED] the files file extension
     * @param clazz [REQUIRED] the calling class used for accessing the location of the file files
     */
    public FileListFactory(String internalFileLocation, String externalLocaton, String filePrefix, String fileSuffix, String fileExtension, Class<?> clazz) {
        myLogger.entering(MY_CLASS_NAME, "FileListFactory()", new Object[]{internalFileLocation, externalLocaton, filePrefix, fileSuffix, fileExtension, clazz});
        filteredFilesList = new ArrayList<String>();

        if(!AppUtil.isNullOrEmpty(filePrefix)){
            filteredFilesList.add(filePrefix);
        }//end if

        if(!AppUtil.isNullOrEmpty(fileSuffix)){
            filteredFilesList.add(fileSuffix);
        }//end if

        if(AppUtil.isNullOrEmpty(fileExtension)){
            throw new IllegalArgumentException("File Extension is required!!! Example extensions are: .exe, .xml, .doc");
        }///end if
        this.fileExtension = fileExtension;
        this.internalFilesLocation = ensureFormat(internalFileLocation);
        this.externalFilesLocation = ensureFormat(externalLocaton);
        this.filePrefix = filePrefix == null ? "" : filePrefix;
        this.fileSuffix = fileSuffix == null ? "" : fileSuffix;
        this.clazz = clazz;
        this.fileNames = new ArrayList<String>();
        myLogger.exiting(MY_CLASS_NAME, "FileListFactory()");
    }//end constructor


    /**
     * This method will return a File object based upon the <tt>name</tt> being passed in as a parameter.
     *
     * @param name
     *        the value from within the map to use to associate it with the key
     * @return fileFile file object that will be executed
     */
    public File getFile(String name) {
        myLogger.entering(MY_CLASS_NAME, "getFile(...)", name);
        File file = null;
        String fileName;
        for(int i = 0, j = fileNames.size();i < j;i++){
            fileName = fileNames.get(i);
            if(fileName.equalsIgnoreCase(name)){
                file = formatName ? new File(externalFilesLocation + filePrefix + fileName + fileSuffix + fileExtension) : new File(externalFilesLocation + fileName);
                break;
            }//end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, "getFile(...)", file);
        return file;
    }//end method

    /**
     * This method will ensure that the path is formatted correctly.
     * @param stringToValidate this is the value of the path
     * @return the validated string
     */
    private String ensureFormat(String stringToValidate) {
        myLogger.entering(MY_CLASS_NAME, "ensureFormat", stringToValidate);
        String validated = null;
        validated = stringToValidate.endsWith("/") || stringToValidate.endsWith("\\") ? stringToValidate : stringToValidate + "/";
        myLogger.exiting(MY_CLASS_NAME, "ensureFormat");
        return validated.startsWith("/") || validated.startsWith("\\") ? validated.substring(1, validated.length()) : validated;
    }//end method

    /**
     * This method will prepare the files external directory on the user's machine.
     *
     * <p><b>Example {@code FilenameFilter}</b></p>
     * <p>The following is an example of a FilenameFilter.</p>
     * <pre>
     * <code>
     *     FilenameFilter filter = new FilenameFilter(){
     *         {@literal @Override} public boolean accept(File dir, String name){
     *              return name.endsWith(".xml") &amp;&amp; name.startsWith("get");
     *          }
     *      };
     * </code>
     * </pre>
     *
     * @param filenameFilter the file name filter used to retrieve files
     * @param formatFileName {@code true} or {@code false} value on whether or not to format the file name
     *        (this means that if the name is getARBLogs.xml and this value is {@code true} the name will
     *        be ARB if set to false the name will stay as getARBLogs.xml
     */
    public final void synchronize(FilenameFilter filenameFilter, boolean formatFileName) {
        myLogger.entering(MY_CLASS_NAME, "synchronize", new Object[]{filenameFilter, formatFileName});
        this.formatName = formatFileName;
        this.fileNamesFilter = filenameFilter;
        File dir = new File(externalFilesLocation);
        if(dir.exists()){
            File[] externalFiles = dir.listFiles(fileNamesFilter);
            if(externalFiles.length > 0){
                compareInternalFilesToExternalFiles(externalFiles, dir);
            }else{
                extractInternalFilesToExternalDirectory(dir);
            }// end if
            setFileNames(dir);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "synchronize");
    }//end method

    /**
     * This method will format the file names based upon the file name and initialized filters.
     *
     * <p>Note that this method can be overridden to suit your particular formatting needs.</p>
     * @param fileDir the directory containing the files
     */
    protected void setFileNames(File fileDir) {
        myLogger.entering(MY_CLASS_NAME, "setFileNames", fileDir);
        File[] files = fileDir.listFiles(fileNamesFilter);
        for(int i = 0, j = files.length;i<j;i++){
            String fileName = files[i].getName();
            if(!AppUtil.isNullOrEmpty(filePrefix) && formatName){
                fileName = fileName.replace(filePrefix, "");
            }//end if

            if(!AppUtil.isNullOrEmpty(fileSuffix)  && formatName){
                fileName = fileName.replace(fileSuffix, "");
            }//end if
            fileNames.add(formatName ? fileName.replace(fileExtension, "") : fileName);//extension....
        }//end for
        myLogger.exiting(MY_CLASS_NAME, "setFileNames");
    }//end method

    /**
     * This method will compare the internal ant files to the external files and if any differences are found then the external files will be updated by replacing them with the files that are internal to the application.
     *
     * @param externalFiles external file files
     * @param copyToDirectory this is the directory that files will be copied to if the external files happen to be different than the internal
     */
    private void compareInternalFilesToExternalFiles(File[] externalFiles, File copyToDirectory) {
        myLogger.entering(MY_CLASS_NAME, "compareInternalFilesToExternalFiles(...)", new Object[]{externalFiles, copyToDirectory});
        List<String> internalFiles = new ArrayList<String>();
        boolean notEqual = false;
        // do something if this is not a jar file...
        myLogger.finest("checking to see if this is being ran as a jar file or as an expanded java project within an ide (ie in eclipse)");
        if(Constants.IS_JAR){
            myLogger.fine("This application is being ran as a jar file going to start exacting file names from within this jar");
            internalFiles = extractFileListFromJar();
            if(myLogger.isLoggable(Level.FINER)){
                myLogger.finer("internalFiles=" + String.valueOf(internalFiles));
            }//end if
        }else{
            // this section runs when this is ran as a java project
            myLogger.fine("This application is being ran as an expanded java project (probably from an ide such as eclipse)");
            try{
                File internalDir = new File(clazz.getResource("/" + internalFilesLocation).getPath());// ******may have to add the prefix******
                if(internalDir.exists()){
                    File[] files = internalDir.listFiles(fileNamesFilter);
                    for(int i = 0, j = files.length;i < j;i++){
                        internalFiles.add(files[i].getAbsolutePath());
                    }// end for
                }// end if
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to locate internal files.  Error Message is: " + e.getMessage(), e);
                throw new IllegalArgumentException("Developers error occured due to the internal location " + String.valueOf(internalFilesLocation) + " does not exist. Please fix!");
            }///end try...catch
        }// end if

        try{
            myLogger.finest("file names/file names are gathered by this point now going to compare...not by bytes but just by name for now.");
            if(externalFiles.length == internalFiles.size()){
                List<String> exFiles = new ArrayList<String>();
                for(int i = 0, j = externalFiles.length;i < j;i++){
                    exFiles.add(externalFiles[i].getCanonicalPath());
                }// end for

                // make sure to sort the lists for binary comparison
                Collections.sort(exFiles);
                Collections.sort(internalFiles);
                if(Constants.IS_JAR){
                    if(!FileUtility.binaryInternalJarFilesAreEqual(exFiles, internalFiles)){
                        myLogger.info("internal files to jar are not Equal");
                        notEqual = true;
                    }// end if
                }else{
                    if(!FileUtility.binaryInternalAndExternalFilesAreEqual(exFiles, internalFiles)){
                        notEqual = true;
                    }// end if
                }//if...else
            }else{// checking date of files...if new day then refresh files just in case they need to be.
                myLogger.info("Sizes of the lists are different. externalFiles.length=" + externalFiles.length + " internalFiles.size() = " + internalFiles.size());
                if(myLogger.isLoggable(Level.FINEST)){
                    StringBuilder sb = new StringBuilder("[");
                    for(int i = 0, j = externalFiles.length; i<j; i++){
                        sb.append(externalFiles[i].getName()).append(", ");
                    }
                    sb.append("]");
                    myLogger.finest("external list is: " + sb.toString());
                }//end if
                notEqual = true;
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException probably while trying to get the canonical path.  Exception is: " + e.getMessage(), e);
        }// end try...catch

        myLogger.finest("if external files are not equal then delete them and then extract a copy of the internal files to the external location");
        if(notEqual){
            myLogger.info("files are not equal deleting external ones");
            for(int i = 0, j = externalFiles.length;i < j;i++){
                if(externalFiles[i].getName().contains(fileExtension)){//TODO extension!!!!!
                    externalFiles[i].delete();
                }// end if
            }// end for
             // extract new ones
            extractInternalFilesToExternalDirectory(copyToDirectory);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "compareInternalFilesToExternalFiles(...)");
    }//end method

    /**
     * This method will extract a list of files from within the jar based upon parameters being passed into it.
     *
     * @return fileList a list of files that are contained within the jar
     */
    private List<String> extractFileListFromJar() {
        myLogger.entering(MY_CLASS_NAME, "extractFileListFromJar()");
        JarFile jar = null;
        JarEntry entry = null;
        List<String> fileList = new ArrayList<String>();

        try{
            jar = new JarFile(Constants.APP_FILE_LOCATION);
            Enumeration<JarEntry> entries = jar.entries();
            outer:while(entries.hasMoreElements()){
                entry = entries.nextElement();
                if(entry.getName().contains(internalFilesLocation) && entry.getName().endsWith(fileExtension)){
                    for(int i = 0, j = filteredFilesList.size(); i < j; i++){
                        if(!entry.getName().contains(filteredFilesList.get(i))){
                            myLogger.info("skipping over " + entry.getName());
                            continue outer;
                        }//end if
                    }//end for
                    fileList.add(entry.getName());//add file to the list
                }//end if
            }// end while
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while extracting file list from jar." + e.getMessage(), e);
        }finally{
            try{
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while extracting file list from jar." + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "extractFileListFromJar()", fileList);
        return fileList;
    }// end extractFileListFromJar()

    /**
     * This method will extract all the internal files to an external directory. This will copy internal files depending if this application is being ran from a jar file or as an ide.
     *
     * @param copyToDirectory
     *        the directory to copy internal files to.
     */
    private void extractInternalFilesToExternalDirectory(File copyToDirectory) {
        myLogger.entering(MY_CLASS_NAME, "extractInternalFilesToExternalDirectory(...)", copyToDirectory);
        // do something if this is not a jar file...
        myLogger.finest("checking to see if this is being ran as a jar file or as an expanded java project (ie in eclipse)");
        if(Constants.IS_JAR){
            myLogger.fine("This application is being ran as a jar file going to start exacting file files");
            extractFilesFromJarToDestination();
        }else{
            // this section runs when this is ran as a java project
        	File internalDir = null;
            try{
                myLogger.fine("This application is being ran as an expanded java project (probably from an ide such as eclipse)");
                internalDir = new File(URLDecoder.decode(clazz.getResource("/" + internalFilesLocation).getPath(), "UTF-8")); // might have to add the prefix /
                if(internalDir.exists()){
                    File[] files = internalDir.listFiles(fileNamesFilter);
                    for(int i = 0, j = files.length;i < j;i++){
                        myLogger.fine("Copying " + files[i] + " to destination directory " + copyToDirectory.getPath());
                        byte[] bytes = FileUtility.getFileInBytes(clazz.getClassLoader().getResourceAsStream(internalFilesLocation + files[i].getName()), files[i].length());
                        FileUtility.writeFile(bytes, copyToDirectory.getPath() + "/", files[i].getName());
                    }// end for
                }// end if
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to locate internal files. Values of interest are:  internalDir=" + (internalDir == null ? "null" : internalDir.getPath()) + "; internalFilesLocation=" + String.valueOf(internalFilesLocation) + ".  Error Message is: " + e.getMessage(), e);
                throw new IllegalArgumentException("Developers error occured due to the internal location " + String.valueOf(internalFilesLocation) + " does not exist. Please fix!");
            }//end truy...catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "extractInternalFilesToExternalDirectory(...)");
    }// end extractInternalFilesToExternalDirectory()

    /**
     * This method will extract the files from the within itself as jar file to the destination location set by the developer.
     */
    private void extractFilesFromJarToDestination() {
        myLogger.entering(MY_CLASS_NAME, "extractFilesFromJarToDestination()");

        myLogger.finest("initializing local variables to null here for use in this method.");
        JarFile jar = null;
        JarEntry entry = null;
        InputStream in = null;
        OutputStream out = null;

        try{
            myLogger.finest("create an instance of this jar file");
            jar = new JarFile(Constants.APP_FILE_LOCATION);
            Enumeration<JarEntry> entries = jar.entries();
            String fileName = null;
            File destinationFile = null;
            myLogger.finest("looping through the entries in the jar filtering out the files that are being requested");
            outer:while(entries.hasMoreElements()){
                entry = entries.nextElement();

                if(entry.getName().contains(internalFilesLocation) && entry.getName().endsWith(fileExtension)){
                    Iterator<String> it = filteredFilesList.iterator();
                    while(it.hasNext()){
                        if(!entry.getName().contains(it.next())){
                            continue outer;
                        }//end if
                    }//end while

                    myLogger.finest("make sure to close streams to free up resources before next iteration.");
                    if(in != null){
                        in.close();
                    }// end if
                    if(out != null){
                        out.close();
                    }// end if

                    myLogger.finest("start coping internal file to destination");
                    fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    destinationFile = new File(externalFilesLocation + fileName);
                    destinationFile.createNewFile();

                    in = new BufferedInputStream(jar.getInputStream(entry));
                    out = new BufferedOutputStream(new FileOutputStream(destinationFile));
                    byte[] buffer = new byte[2048];
                    int i = 0;
                    while((i = in.read(buffer)) != -1){
                        out.write(buffer, 0, i);
                    } // end while
                    out.flush();// flush the output.
                }// end if
            }// end while
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while extracting files from jar." + e.getMessage(), e);
        }finally{
            try{
                myLogger.finest("close all the input/output streams.");
                if(in != null){
                    in.close();
                }// end if
                if(out != null){
                    out.close();
                }// end if
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while extracting files from jar." + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "extractFilesFromJarToDestination");
    } // end method

    /**
     * @return the internalFileLocation
     */
    public String getInternalFileLocation() {
        return internalFilesLocation;
    } // end method

    /**
     * @return the externalFileLocation
     */
    public String getExternalFileLocation() {
        return externalFilesLocation;
    } // end method

    public void setFileNameFilter(FilenameFilter filenameFilter){
        this.fileNamesFilter = filenameFilter;
    }//end method

    /**
     * @return the filePrefix
     */
    public String getFilePrefix() {
        return filePrefix;
    } // end method

    /**
     * @return the fileSuffix
     */
    public String getFileSuffix() {
        return fileSuffix;
    } // end method

    /**
     * @return the fileFilter
     */
    public List<String> getFileFilter() {
        return filteredFilesList;
    } // end method

}//end class
