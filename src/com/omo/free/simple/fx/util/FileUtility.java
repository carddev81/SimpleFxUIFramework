package com.omo.free.simple.fx.util;

import static com.omo.free.simple.fx.util.Constants.SECRET_PASSWORD;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import com.omo.free.simple.fx.managers.LoggingMgr;
import com.omo.free.util.AppUtil;

/**
 * FileUtility class - Contains methods common for file manipulation.
 *
 * @author Richard Salas JCCC
 * @author Joe
 * @author Modification Author:<strong>Gary Campbell</strong> JCCC<br>
 *         Date: 12-8-2016 Added deleteDirectory method to class.
 */
public class FileUtility {
    // class variables
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.util.FileUtility";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Constructor is private so that this class can not be initialized from a client.
     */
    private FileUtility() {
        // defualt constructor will not let client instantiate an instance of this class.
    }// end constructor

    /**
     * This method looks for the external properties file. If it exists it reads the value for display.
     *
     * @param parentDirectory
     *        The parent directory of the class
     * @param propertiesFile
     *        The name of the properties file
     * @return properties The external properties file
     */
    public static Properties loadExternalPropertiesFile(String parentDirectory, String propertiesFile) {
        myLogger.entering(MY_CLASS_NAME, "loadExternalPropertiesFile", new Object[]{parentDirectory, propertiesFile});
        Properties properties = new Properties();
        String externalFilePath = "";
        FileInputStream fis = null;
        try{
            externalFilePath = parentDirectory.endsWith("\\") || parentDirectory.endsWith("/") ? parentDirectory + propertiesFile : parentDirectory + "\\" + propertiesFile;
            myLogger.log(Level.FINE, "Path to " + propertiesFile + " for the application: " + externalFilePath);
            fis = new FileInputStream(new File(externalFilePath));
            properties.load(fis);
            fis.close();
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "FileNotFoundException loading properties file: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException loading " + propertiesFile + ". Message is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception loading " + propertiesFile + ". Message is: " + e.getMessage(), e);
        }finally{
            try{
                if(fis != null){
                    fis.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException closing the file input stream for the following file: " + propertiesFile + ". Message is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "loadExternalPropertiesFile", properties);
        return properties;
    }// end loadExternalPropertiesFile

    /**
     * This method looks for the external properties file. If it exists it reads the value for display.
     *
     * @param parentDirectory
     *        The parent directory of the class
     * @param propertiesFile
     *        The name of the properties file
     * @return properties The external properties file
     */

    public static Properties loadExternalPropertiesFileWithEncryptedValues(String parentDirectory, String propertiesFile) {
        myLogger.entering(MY_CLASS_NAME, "loadExternalPropertiesFile", new Object[]{parentDirectory, propertiesFile});
        Properties properties = null;
        if(!"".equals(SECRET_PASSWORD)){
            StandardPBEStringEncryptor propertiesEncryptor = new StandardPBEStringEncryptor();
            propertiesEncryptor.setPassword(SECRET_PASSWORD); // this is the encryption / decryption password key
            properties = new EncryptableProperties(propertiesEncryptor);
        }else{
            properties = new Properties();
        }// end if
        String externalFilePath = "";
        FileInputStream fis = null;
        try{
            externalFilePath = parentDirectory.endsWith("\\") || parentDirectory.endsWith("/") ? parentDirectory + propertiesFile : parentDirectory + "\\" + propertiesFile;
            myLogger.log(Level.FINE, "Path to " + propertiesFile + " for Application: " + externalFilePath);
            fis = new FileInputStream(new File(externalFilePath));
            properties.load(fis);
            fis.close();
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "FileNotFoundException loading properties file: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException loading " + propertiesFile + ". Message is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occrured while trying to decrypt values. Error is: " + e.getMessage());
        }finally{
            try{
                if(fis != null){
                    fis.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException closing the file input stream for the following file: " + propertiesFile + ". Message is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "loadExternalPropertiesFile", properties);
        return properties;
    }// end method

    /**
     * This method will load an internal properties file on the classpath using the given root class and properties file.
     *
     * @param rootClazzPath
     *        the class to obtain the root classpath from
     * @param propertiesFile
     *        the properties file
     * @return properties that were loaded
     */
    public static Properties loadInternalPropertiesFile(Class<?> rootClazzPath, String propertiesFile) {
        myLogger.entering(MY_CLASS_NAME, "loadInternalPropertiesFile", new Object[]{rootClazzPath, propertiesFile});
        boolean isJar = AppUtil.isJar(rootClazzPath);
        String fileLocation = null;
        InputStream fis = null;
        Properties properties = new Properties();
        try{
            fileLocation = URLDecoder.decode(rootClazzPath.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        }catch(UnsupportedEncodingException e1){
            fileLocation = rootClazzPath.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
            myLogger.warning("Could not decode the url that was passed into this method from the classpath. Error is: " + e1.getMessage());
        }// end try...catch
        myLogger.info("checking to see if this a jar file and if it happens to be then we will extract a file from within the jar to a location.");
        if(isJar){
            myLogger.info("this is a jar file");
            JarFile jarFile = null;
            JarEntry jarEntry = null;
            try{
                jarFile = new JarFile(fileLocation);
                Enumeration<JarEntry> entries = jarFile.entries();
                while(entries.hasMoreElements()){
                    jarEntry = entries.nextElement();
                    if(jarEntry.getName().contains(propertiesFile)){
                        myLogger.info("found the properties file" + propertiesFile + " which will be loaded.");
                        break;// we have the correct one so lets break;
                    }// end if
                }// end while
                fis = jarFile.getInputStream(jarEntry);
                properties.load(fis);
                fis.close();
                jarFile.close();
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while attempting to load internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception while attempting to load internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
            }finally{
                try{
                    if(jarFile != null){
                        jarFile.close();
                    }// end if

                    if(fis != null){
                        fis.close();
                    }// end if
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException while attempting to close resources from the process of loading the internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
                }// end try...catch
            }// end try...catch
        }else{
            myLogger.info("this is not a jar file so the " + propertiesFile + " will be loaded differently than if it were in a jar.");
            File rootDirectory = new File(fileLocation.replaceAll("file:", ""));
            String[] pathToInternalFile = new String[1];
            findFile(rootDirectory, propertiesFile, pathToInternalFile);
            try{
                if(pathToInternalFile[0] == null){
                    throw new FileNotFoundException("File " + propertiesFile + " not found");
                }// end if
                 // have the file here load it into memory
                fis = new FileInputStream(new File(pathToInternalFile[0]));
                properties.load(fis);
                fis.close();
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while attempting to load internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception while attempting to load internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
            }finally{
                if(fis != null){
                    try{
                        fis.close();
                    }catch(IOException e){
                        myLogger.log(Level.SEVERE, "IOException while attempting to close resources from the process of loading the internal properties file " + propertiesFile + " file. Error is: " + e.getMessage(), e);
                    }// end try...catch
                }// end if
            }// end try...catch
        }// end if
        return properties;
    }// end method

    /**
     * Method to write a properties file in the location where the jar is.
     *
     * @param bytes
     *        The byte[] of data to write
     * @param parentDirectory
     *        The parent directory of the class
     * @param theFile
     *        The name of the properties file
     */
    public static void writeFile(byte[] bytes, String parentDirectory, String theFile) {
        myLogger.entering(MY_CLASS_NAME, "writeFile", new Object[]{bytes, parentDirectory, theFile});
        FileOutputStream fos = null;
        try{
            String pathToFile = parentDirectory.endsWith("/") || parentDirectory.endsWith("\\") ? parentDirectory + theFile : parentDirectory + "\\" + theFile;
            File file = new File(pathToFile);
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException writing " + parentDirectory + theFile + ". Cause is: " + e.getMessage(), e);
        }finally{
            try{
                if(fos != null){
                    fos.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException closing fileoutput stream." + " Cause is: " + e.getMessage(), e);
            }// end try...catch
        }// end try....catch
        myLogger.exiting(MY_CLASS_NAME, "writeFile");
    }// end writeFile

    /**
     * Method to verify that all directories in the path exist. If they do not then make them.
     *
     * @param filePath
     *        The file path to check
     */
    public static void checkDirectories(String filePath) {
        myLogger.entering(MY_CLASS_NAME, "checkDirectories", filePath);
        String[] fileNames = filePath.split("(/|\\\\)");
        String path = fileNames[0];
        for(int i = 1;i < fileNames.length;i++){
            myLogger.log(Level.FINER, "File path: " + path);
            path += File.separator + fileNames[i];
            File f = new File(path);
            if(!path.contains(".properties") && !path.contains(".log") && !path.contains(".xml") && !path.contains(".java") && !path.contains(".script")){
                if(!f.exists()){
                    f.mkdir();
                } // end if
            } // end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, "checkDirectories");
    }// end checkDirectories

    /**
     * This method will extract file from within itself as a jar file.
     *
     * @param destination
     *        - where files will go to
     * @param nameOfFile
     *        - location of the jar file
     * @throws IOException
     *         exception could be thrown during the extraction of a file from within a jar
     */
    public static void extractFileFromJar(String destination, String nameOfFile) throws IOException {
        myLogger.entering(MY_CLASS_NAME, "extractFileFromJar", new Object[]{destination, nameOfFile});

        JarFile jarFile = new JarFile(Constants.FRAMEWORK_FILE_LOCATION);
        Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry jarEntry = null;
        boolean found = false;
        while(entries.hasMoreElements()){
            jarEntry = entries.nextElement();
            if((jarEntry.getName().contains("gov/") || jarEntry.getName().contains("splash/")) && jarEntry.getName().endsWith(nameOfFile)){
                myLogger.info("found the " + nameOfFile + " within standard isu location which will be extracted...");
                found = true;
                break;// we have the correct one so lets break;
            }// end if
        }// end while

        if(!found){//this was added due to user that does not know that standard location of resource files
            entries = jarFile.entries();
            while(entries.hasMoreElements()){
                jarEntry = entries.nextElement();
                if(jarEntry.getName().endsWith(nameOfFile)){
                  myLogger.info("found the " + nameOfFile + " not within the standard isu location which will be extracted...");
                  found= true;
                  break;// we have the correct one so lets break;
                }// end if
            }//end while
        }//end if

        if(!found){//if still not found then just throw exception!
            if(jarFile != null){
                jarFile.close();
            }// end if
            throw new FileNotFoundException("The file with the name of \"" + String.valueOf(nameOfFile) + "\" could not be found.");
        }//end if

        String pathToFile = destination.endsWith("/") || destination.endsWith("\\") ? destination + nameOfFile : destination + "\\" + nameOfFile;

        File destinationFile = new File(pathToFile);
        destinationFile.createNewFile();

        InputStream in = null;
        OutputStream out = null;

        try{
            in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
            out = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buffer = new byte[2048];
            int i = 0;

            while((i = in.read(buffer)) != -1){
                out.write(buffer, 0, i);
            } // end while

            out.flush();
            out.close();
            in.close();
        }finally{
            if(in != null){
                in.close();
            } // end if
            if(out != null){
                out.close();
            } // end if
            if(jarFile != null){
                jarFile.close();
            }// end if
        } // end finally
        myLogger.exiting(MY_CLASS_NAME, "extractFileFromJar");
    } // end method

    /**
     * This method will extract file from within itself as a jar file.
     *
     * @param destination
     *        - where files will go to
     * @param nameOfFile
     *        - location of the jar file
     * @param theFileRenamed the file name that will be used for the final destination name
     * @throws IOException
     *         exception could be thrown during the extraction of a file from within a jar
     */
    public static void extractFileFromJar(String destination, String nameOfFile, String theFileRenamed) throws IOException {
        myLogger.entering(MY_CLASS_NAME, "extractFileFromJar", new Object[]{destination, nameOfFile});

        JarFile jarFile = new JarFile(Constants.FRAMEWORK_FILE_LOCATION);
        Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry jarEntry = null;
        while(entries.hasMoreElements()){
            jarEntry = entries.nextElement();
            if((jarEntry.getName().contains("gov/") || jarEntry.getName().contains("splash/")) && jarEntry.getName().contains(nameOfFile)){
                myLogger.info("found the " + nameOfFile + " which will be extracted...");
                break;// we have the correct one so lets break;
            }// end if
        }// end while

        String pathToFile = destination.endsWith("/") || destination.endsWith("\\") ? destination + theFileRenamed : destination + "\\" + theFileRenamed;

        File destinationFile = new File(pathToFile);
        destinationFile.createNewFile();

        InputStream in = null;
        OutputStream out = null;

        try{
            in = new BufferedInputStream(jarFile.getInputStream(jarEntry));
            out = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buffer = new byte[2048];
            int i = 0;

            while((i = in.read(buffer)) != -1){
                out.write(buffer, 0, i);
            } // end while

            out.flush();
            out.close();
            in.close();
        }finally{
            if(in != null){
                in.close();
            } // end if
            if(out != null){
                out.close();
            } // end if
            if(jarFile != null){
                jarFile.close();
            }// end if
        } // end finally
        myLogger.exiting(MY_CLASS_NAME, "extractFileFromJar");
    } // end method

    /**
     * This method returns a file as an array of bytes based upon the passed in parameter InputStream which should contain a file.
     *
     * @param is
     *        - the input stream object that contains the file to convert to bytes.
     * @param length
     *        the file length
     * @return byte array of file
     */
    public static byte[] getFileInBytes(InputStream is, long length) {
        myLogger.entering(MY_CLASS_NAME, "getFileInBytes(...) method - returns the byte array of a file", new Object[]{is, length});
        if(is == null){
            return null;
        }// end if

        byte[] bytes = new byte[Long.valueOf(length).intValue()]; // set the initial size of byte array to a default limit (10000) here

        int offset = 0; // set offset variable to 0
        int numRead; // declare numRead variable for checking the number of bytes read in.

        try{
            // first initial read of bytes
            numRead = is.read(bytes, offset, bytes.length - offset);
            while(offset < bytes.length && numRead >= 0){
                // set offset for getting the index of bytes to the end position of array.
                offset += numRead;
                // see if there are more bytes to read
                numRead = is.read(bytes, offset, bytes.length - offset);
            }// end while
            is.close(); // close the stream.
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException converting file in bytes: " + e.getMessage(), e);
            bytes = new byte[0];
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception converting file into bytes: " + e.getMessage(), e);
            bytes = new byte[0];
        }finally{
            try{
                if(is != null){
                    is.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "Exception converting file into bytes: " + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "getFileInBytes(...) method", bytes);
        return bytes;
    }// end getFileInBytes

    /**
     * Checks to see if the path passed in exists
     *
     * @param pathToDirectory
     *        - the path to directory to check for
     * @return true / false
     */
    public static boolean directoryExists(String pathToDirectory) {
        myLogger.entering(MY_CLASS_NAME, "directoryExists(...) method - returns the byte array of a file", pathToDirectory);
        myLogger.exiting(MY_CLASS_NAME, "directoryExists(...) method");
        return new File(pathToDirectory).exists();
    }// end directoryExists

    /**
     * This method will copy a file to a directory.
     *
     * @param fileToCopy
     *        the file to copy
     * @param copyToDirectory
     *        directory to copy the file to
     */
    public static void copyFileToDir(File fileToCopy, String copyToDirectory) {
        myLogger.entering(MY_CLASS_NAME, "copyFileToDir()", new Object[]{fileToCopy, copyToDirectory});

        byte[] bytes = new byte[0];
        myLogger.log(Level.FINE, "Path to " + fileToCopy.getName() + " file for copying to the following location: " + copyToDirectory);
        String location = fileToCopy.getAbsolutePath();
        File fileCopy = new File(location.replaceAll("%20", " ").replaceAll("%23", "#"));
        InputStream is = null;
        try{
            is = new FileInputStream(fileCopy);

            long length = fileCopy.length();
            if(length > Integer.MAX_VALUE){
                throw new Exception();
            } // end if
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = is.read(bytes, offset, bytes.length - offset);
            while(offset < bytes.length && numRead >= 0){
                offset += numRead;
                numRead = is.read(bytes, offset, bytes.length - offset);
            }// end while
            String fileName = fileToCopy.getName();
            writeFile(bytes, copyToDirectory, fileName);
            myLogger.exiting(MY_CLASS_NAME, "copyFileToDirectory");
        }catch(FileNotFoundException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "FileNotFoundException while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToDirectory + " . Error is: " + e.getMessage(), e);
        }catch(IOException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "IOException while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToDirectory + " . Error is: " + e.getMessage(), e);
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "Exception while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToDirectory + " . Error is: ", e);
        }finally{
            try{
                // check to see if the input stream is null before trying to close it.
                if(is != null){
                    is.close();
                }// end if
            }catch(IOException e){
                LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
                myLogger.log(Level.SEVERE, "IOException while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToDirectory + " . Error is: " + e.getMessage(), e);
            }// end if
        }// end try...catch
    }// end copyJarToTransferDir

    /**
     * This method will check to see if internal files binary data is equal to the external files binary data. value returned is true or false.
     *
     * @param externalFilePaths
     *        the list of external file paths
     * @param internalJarEntryNames
     *        the list of internal jar entry names
     * @return true or false value (boolean)
     */
    public static boolean binaryInternalJarFilesAreEqual(List<String> externalFilePaths, List<String> internalJarEntryNames) {
        myLogger.entering(MY_CLASS_NAME, "binaryInternalJarFilesEqual(...)", new Object[]{externalFilePaths, internalJarEntryNames});

        myLogger.finest("initialize local variables to null ");
        File external = null;
        String internalName = null;
        JarFile jar = null;
        InputStream in1 = null;
        InputStream in2 = null;

        boolean areEqual = true;

        try{
            myLogger.finest("create instance of the jar file");
            jar = new JarFile(Constants.APP_FILE_LOCATION);

            myLogger.finest("naming this loop outer so that i can break out of it when need be (ie within the inner loop below.)");
            outer:for(int i = 0, j = externalFilePaths.size();i < j;i++){
                myLogger.finest("making sure that streams are closed here.");
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if

                external = new File(externalFilePaths.get(i));
                internalName = internalJarEntryNames.get(i);

                if(!external.getName().equals(internalName.substring(internalName.lastIndexOf("/") + 1))){
                    areEqual = false;
                    break;
                }// end if

                myLogger.finest("create input stream instances one from the external file and one from the internal file");
                in1 = new BufferedInputStream(jar.getInputStream(jar.getEntry(internalName)));
                in2 = new BufferedInputStream(new FileInputStream(external));

                myLogger.finest("start comparing.");
                int expectedByte = in1.read();
                while(expectedByte != -1){
                    if(expectedByte != in2.read()){
                        areEqual = false;
                        break outer;// breaks out of all loops i named one 'outer'
                    }// end if
                    expectedByte = in1.read();
                }// end while
                if(in2.read() != -1){
                    areEqual = false;
                    break;
                }// end if
            }// end outer for
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while comparing internal and external files. e=" + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while comparing internal and external files. e=" + e.getMessage(), e);
        }finally{
            myLogger.finest("closing the streams");
            try{
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while trying to close input streams during the comparing of internal files and external files. e=" + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.info("External scripts and internal scripts are equal: " + areEqual);
        myLogger.exiting(MY_CLASS_NAME, "binaryInternalJarFilesEqual()", areEqual);
        return areEqual;
    }// end

    /**
     * This method will compares the binary data of two files. Simple but probably could be written better. This works for what it was intended for which is just comparing get logs scripts.
     *
     * @param externalFiles
     *        files that external not within the project source.
     * @param internalFiles
     *        (internal to an ide) files that are internal to the project
     * @return true if the content of the files is the same false if not.
     */
    public static boolean binaryInternalAndExternalFilesAreEqual(List<String> externalFiles, List<String> internalFiles) {
        myLogger.entering(MY_CLASS_NAME, "binaryInternalAndExternalFilesAreEqual(...)", new Object[]{externalFiles, internalFiles});
        myLogger.finest("initialize local variables to null ");
        File external = null;
        File internal = null;

        InputStream in1 = null;
        InputStream in2 = null;

        boolean areEqual = true;

        try{
            myLogger.finest("naming this loop outer so that i can break out of it when need be (ie within the inner loop below.)");
            outer:for(int i = 0, j = externalFiles.size();i < j;i++){
                myLogger.finest("making sure that streams are closed here.");
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if

                external = new File(externalFiles.get(i));
                internal = new File(internalFiles.get(i));

                if(external.length() != internal.length() || !external.getName().equals(internal.getName())){
                    areEqual = false;
                    break;
                }// end if

                myLogger.finest("create input stream instances one from the external file and one from the internal file");
                in1 = new BufferedInputStream(new FileInputStream(external));
                in2 = new BufferedInputStream(new FileInputStream(internal));

                myLogger.finest("start comparing.");
                int expectedByte = in1.read();
                while(expectedByte != -1){
                    if(expectedByte != in2.read()){
                        areEqual = false;
                        break outer;// breaks while
                    }// end if
                    expectedByte = in1.read();
                }// end while
                if(in2.read() != -1){
                    areEqual = false;
                    break;
                }// end if
            }// end outer for
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while comparing internal and external files. e=" + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while comparing internal and external files. e=" + e.getMessage(), e);
        }finally{
            myLogger.finest("closing the streams");
            try{
                // close streams
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while closing streams. e=" + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "binaryInternalAndExternalFilesAreEqual()", areEqual);
        return areEqual;
    }// end

    /**
     * This method will copy the internal file that was passed into this method as the nameOfFileToCopy
     *
     * @param externalDestinationPath
     *        the external path that the file will be copied to
     * @param nameOfFileToCopy
     *        name of the file to copy
     */
    public static void copyInternalFileToExternalDestination(String externalDestinationPath, String nameOfFileToCopy) {
        myLogger.entering(MY_CLASS_NAME, "copyInternalFileToExternalDestination()", new Object[]{externalDestinationPath, nameOfFileToCopy});
        try{
            myLogger.info("checking to see if this a jar file and if it happens to be then we will extract a file from within the jar to a location.");
            if(Constants.IS_JAR){
                myLogger.info("this is a jar file");
                extractFileFromJar(externalDestinationPath, nameOfFileToCopy);
            }else{
                myLogger.info("not a jar file so the " + nameOfFileToCopy + " will be exctrated differently than if it were in a jar.");
                File rootDirectory = new File(Constants.APP_FILE_LOCATION.replaceAll("file:", ""));
                String[] pathToInternalFile = new String[1];
                findFile(rootDirectory, nameOfFileToCopy, pathToInternalFile);
                if(pathToInternalFile[0] == null){
                    throw new FileNotFoundException("File " + nameOfFileToCopy + " not found");
                }// end if
                String splitOn = rootDirectory.getPath().substring(rootDirectory.getPath().lastIndexOf("\\") + 1, rootDirectory.getPath().length());
                String filePath = pathToInternalFile[0].split(splitOn)[1];
                byte[] bytes = getFileInBytes(FileUtility.class.getProtectionDomain().getClassLoader().getResourceAsStream(filePath), new File(pathToInternalFile[0]).length());
                writeFile(bytes, externalDestinationPath, nameOfFileToCopy);
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "copyInternalFileToExternalDestination()");
    }// end copyInternalFileToExternalDestination

    /**
     * This method will return a files path based upon the filename.
     *
     * @param rootDirectory
     *        the root directory to start searching
     * @param nameOfFileToCopy
     *        the name of the file
     * @param pathToRequestedFile
     *        the string buffer object to append the value.
     */
    private static void findFile(File rootDirectory, String nameOfFileToCopy, String[] pathToRequestedFile) {
        myLogger.entering(MY_CLASS_NAME, "findFile()", new Object[]{rootDirectory, nameOfFileToCopy, pathToRequestedFile});
        File[] dirlist = rootDirectory.listFiles();
        for(int i = 0, j = dirlist.length;i < j;i++){
            if(pathToRequestedFile[0] != null){
                break;
            }// end if
            if(dirlist[i].isDirectory() && !dirlist[i].getName().contains("CVS")){
                findFile(dirlist[i], nameOfFileToCopy, pathToRequestedFile);
            }else if(dirlist[i].isFile() && dirlist[i].getName().equals(nameOfFileToCopy)){
                pathToRequestedFile[0] = dirlist[i].getPath();
                break;
            }// end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, "copyFileToDirectory");
    }// end findFile

    /**
     * This method will return the internal file path of the file passed into this method.
     *
     * @param fileName
     *        the name of the file that the path is requested
     * @return internalFilePath the interanl file path to the file
     */
    public static String extractFilePathFromJar(String fileName) {
        myLogger.entering(MY_CLASS_NAME, "extractFilePathFromJar()", fileName);
        JarFile jar = null;
        JarEntry entry = null;
        String internalFilePath = "";
        try{
            jar = new JarFile(Constants.FRAMEWORK_FILE_LOCATION);
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()){
                entry = entries.nextElement();
                if(entry.getName().contains(fileName)){
                    internalFilePath = entry.getName();
                    break;
                }// end if
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
        myLogger.exiting(MY_CLASS_NAME, "extractFilePathFromJar()", internalFilePath);
        return internalFilePath;
    }// end extractFilePathFromJar

    /**
     * This method will check to see if an internal files binary data is equal to the external files binary data. Value returned is true or false.
     *
     * @param externalFilePath
     *        path to the external file
     * @param internalFilePath
     *        path to the internal file
     * @return true if binary data is equal false if the binary data is not equal
     */
    public static boolean binaryInternalJarFileIsEqualToExternalFile(String externalFilePath, String internalFilePath) {
        myLogger.entering(MY_CLASS_NAME, "binaryInternalJarFileIsEqualToExternalFile(...)", new Object[]{externalFilePath, internalFilePath});

        myLogger.finest("initialize local variables to null ");
        File external = null;
        JarFile jar = null;
        InputStream in1 = null;
        InputStream in2 = null;

        boolean isEqual = true;

        try{
            myLogger.finest("create instance of the jar file");
            external = new File(externalFilePath);

            if(!external.getName().equals(internalFilePath.substring(internalFilePath.lastIndexOf("/") + 1))){
                isEqual = false;
            }// end if

            if(isEqual){
                myLogger.finest("create input stream instances one from the external file and one from the internal file");
                jar = new JarFile(Constants.APP_FILE_LOCATION);
                in1 = new BufferedInputStream(jar.getInputStream(jar.getEntry(internalFilePath)));
                in2 = new BufferedInputStream(new FileInputStream(external));

                myLogger.finest("start comparing.");
                int expectedByte = in1.read();
                while(expectedByte != -1){
                    if(expectedByte != in2.read()){
                        isEqual = false;
                        break;// breaks out of all loops i named one 'outer'
                    }// end if
                    expectedByte = in1.read();
                }// end while
                if(in2.read() != -1){
                    isEqual = false;
                }// end if
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while comparing internal and external files. e=" + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while comparing internal and external files. e=" + e.getMessage(), e);
        }finally{
            myLogger.finest("closing the streams");
            try{
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while trying to close input streams during the comparing of internal files and external files. e=" + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.info("External file and internal file is equal: " + isEqual);
        myLogger.exiting(MY_CLASS_NAME, "binaryInternalJarFileIsEqualToExternalFile()", isEqual);
        return isEqual;
    }// end binaryInternalJarFileIsEqualToExternalFile

    /**
     * This method will compares the binary data of two files.
     *
     * @param externalFilePath
     *        path to the external file
     * @param internalFilePath
     *        path to the internal file
     * @return true if binary data is equal false if the binary data is not equal
     */
    public static boolean binaryInternalAndExternalFileIsEqual(String externalFilePath, String internalFilePath) {
        myLogger.entering(MY_CLASS_NAME, "binaryInternalAndExternalFileIsEqual(...)", new Object[]{externalFilePath, internalFilePath});
        myLogger.finest("initialize local variables to null ");
        File external = null;
        File internal = null;

        InputStream in1 = null;
        InputStream in2 = null;

        boolean isEqual = true;

        try{
            external = new File(externalFilePath);
            internal = new File(internalFilePath);

            if(external.length() != internal.length() || !external.getName().equals(internal.getName())){
                isEqual = false;
            }// end if

            if(isEqual){
                myLogger.finest("create input stream instances one from the external file and one from the internal file");
                in1 = new BufferedInputStream(new FileInputStream(external));
                in2 = new BufferedInputStream(new FileInputStream(internal));

                myLogger.finest("start comparing.");
                int expectedByte = in1.read();
                while(expectedByte != -1){
                    if(expectedByte != in2.read()){
                        isEqual = false;
                        break;// breaks while
                    }// end if
                    expectedByte = in1.read();
                }// end while
                if(isEqual){// if still equal try reading another byte from internal file
                    if(in2.read() != -1){
                        isEqual = false;
                    }// end if
                }// end if
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while comparing internal and external files. e=" + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while comparing internal and external files. e=" + e.getMessage(), e);
        }finally{
            myLogger.finest("closing the streams");
            try{
                // close streams
                if(in1 != null){
                    in1.close();
                }// end if
                if(in2 != null){
                    in2.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while closing streams. e=" + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "binaryInternalAndExternalFileIsEqual()", isEqual);
        return isEqual;
    }// end binaryInternalAndExternalFileIsEqual

    /**
     * Checks to see if directory exists as well as checking to see if there are any files/directories contained within it.
     *
     * @param directory
     *        the directory to check
     * @return true or false value
     */
    public static boolean hasFilesInDirectory(File directory) {
        myLogger.entering(MY_CLASS_NAME, "hasFilesInDirectory()", directory);
        if(directory == null){
            return false;
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "hasFilesInDirectory()");
        return directory.isDirectory() && directory.exists() && directory.listFiles().length > 0;
    }// end hasFilesInDirectory

    /**
     * This method will copy an internal file to the external destination.
     *
     * @param class1
     *        the calling class (CallingClassName.class)
     * @param externalDestinationPath
     *        the external destination path
     * @param nameOfFileToCopy
     *        the name of the file to copy
     */
    public static void copyInternalFileToExternalDestination(Class<?> class1, String externalDestinationPath, String nameOfFileToCopy) {
        myLogger.entering(MY_CLASS_NAME, "copyInternalFileToExternalDestination()", new Object[]{class1, externalDestinationPath, nameOfFileToCopy});
        String fileLocation = class1.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        boolean isJar = fileLocation.endsWith(".jar") || fileLocation.endsWith(".JAR");

        try{
            myLogger.info("checking to see if this a jar file and if it happens to be then we will extract a file from within the jar to a location.");
            if(isJar){
                myLogger.info("this is a jar file");
                extractFileFromJar(externalDestinationPath, nameOfFileToCopy);
            }else{
                myLogger.info("not a jar file so the " + nameOfFileToCopy + " will be exctrated differently than if it were in a jar.");
                File rootDirectory = new File(fileLocation.replaceAll("file:", ""));
                String[] pathToInternalFile = new String[1];
                findFile(rootDirectory, nameOfFileToCopy, pathToInternalFile);
                if(pathToInternalFile[0] == null){
                    throw new FileNotFoundException("File " + nameOfFileToCopy + " not found");
                }// end if
                String splitOn = rootDirectory.getPath().substring(rootDirectory.getPath().lastIndexOf("\\") + 1, rootDirectory.getPath().length());
                String filePath = pathToInternalFile[0].split(splitOn)[1];
                byte[] bytes = getFileInBytes(FileUtility.class.getProtectionDomain().getClassLoader().getResourceAsStream(filePath), new File(pathToInternalFile[0]).length());
                writeFile(bytes, externalDestinationPath, nameOfFileToCopy);
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }// end try...catch
    }// end copyInternalFileToExternalDestination

    /**
     * This method will copy an internal file to the external destination.
     *
     * @param clazz
     *        the calling class (CallingClassName.class)
     * @param externalDestinationPath
     *        the external destination path
     * @param nameOfFileToCopy
     *        the name of the file to copy
     * @param theFileRenamed the name of the destination file
     */
    public static void copyInternalFileToExternalDestination(Class<?> clazz, String externalDestinationPath, String nameOfFileToCopy, String theFileRenamed) {
        myLogger.entering(MY_CLASS_NAME, "copyInternalFileToExternalDestination()", new Object[]{clazz, externalDestinationPath, nameOfFileToCopy});
        boolean isJar = AppUtil.isJar(clazz);
        String fileLocation = null;
        try{
            fileLocation = URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        }catch(UnsupportedEncodingException e1){
            fileLocation = clazz.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
            myLogger.warning("Could not decode the url that was passed into this method from the classpath. Error is: " + e1.getMessage());
        }// end try...catch
        try{
            myLogger.info("checking to see if this a jar file and if it happens to be then we will extract a file from within the jar to a location.");
            if(isJar){
                myLogger.info("this is a jar file");
                extractFileFromJar(externalDestinationPath, nameOfFileToCopy, theFileRenamed);
            }else{
                myLogger.info("not a jar file so the " + nameOfFileToCopy + " will be extracted differently than if it were in a jar.");
                File rootDirectory = new File(fileLocation.replaceAll("file:", ""));
                String[] pathToInternalFile = new String[1];
                findFile(rootDirectory, nameOfFileToCopy, pathToInternalFile);
                if(pathToInternalFile[0] == null){
                    throw new FileNotFoundException("File " + nameOfFileToCopy + " not found");
                }// end if
                String splitOn = rootDirectory.getPath().substring(rootDirectory.getPath().lastIndexOf("\\") + 1, rootDirectory.getPath().length());
                String filePath = pathToInternalFile[0].split(splitOn)[1];
                byte[] bytes = getFileInBytes(FileUtility.class.getProtectionDomain().getClassLoader().getResourceAsStream(filePath), new File(pathToInternalFile[0]).length());
                writeFile(bytes, externalDestinationPath, theFileRenamed);
            }// end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while attempting to copy internal file " + nameOfFileToCopy + " file to the external location: " + externalDestinationPath + " . Error is: " + e.getMessage(), e);
        }// end try...catch
    }// end copyInternalFileToExternalDestination

    /**
     * This method will copy a file to a directory.
     *
     * @param fileToCopy
     *        the file to copy
     * @param copyToFile
     *        absolute destination path to copy the file to
     */
    public static void copyFile(File fileToCopy, File copyToFile) {
        myLogger.entering(MY_CLASS_NAME, "copyFile()", new Object[]{fileToCopy, copyToFile});

        // do necessar checks before proceeding to copy
        if(fileToCopy == null || copyToFile == null){
            String errorMessage = "Arguments passed into this method must not be null. Arguments are: fileToCopy=" + String.valueOf(fileToCopy) + ", copyToFile=" + String.valueOf(copyToFile);
            myLogger.severe(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }// end if

        if(!fileToCopy.exists()){
            String errorMessage = "The file " + fileToCopy.getAbsolutePath() + " that you want to copy does not exist! You will need to make sure the file exists before attempting to copy";
            myLogger.severe(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }// end if

        if(!copyToFile.getParentFile().exists()){
            String errorMessage = "The directory " + copyToFile.getParentFile().getAbsolutePath() + " that you want to copy the file to does not exist! You will need to make sure the directory exists before attempting to copy";
            myLogger.severe(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }// end if

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try{
            inputStream = new FileInputStream(fileToCopy);
            outputStream = new FileOutputStream(copyToFile);
            inChannel = inputStream.getChannel();
            outChannel = outputStream.getChannel();

            ByteBuffer myBuffer = ByteBuffer.allocate(1024);
            int read = inChannel.read(myBuffer);
            while(read != -1){
                myBuffer.flip();
                outChannel.write(myBuffer);
                myBuffer.clear();
                read = inChannel.read(myBuffer);
            }// end while
        }catch(FileNotFoundException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "FileNotFoundException while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToFile.getAbsolutePath() + " . Error is: " + e.getMessage(), e);
        }catch(IOException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "IOException while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToFile.getAbsolutePath() + " . Error is: " + e.getMessage(), e);
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.ALL);
            myLogger.log(Level.SEVERE, "Exception while attempting to copy " + fileToCopy.getName() + " file to the following location: " + copyToFile.getAbsolutePath() + " . Error is: " + e.getMessage(), e);
        }finally{
            try{
                if(inChannel != null){
                    inChannel.close();
                }// end if
                if(outChannel != null){
                    outChannel.close();
                }// end if
                if(outputStream != null){
                    outputStream.close();
                }// end if
                if(inputStream != null){
                    inputStream.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "Exception while attempting to close streams and channels. . Error is: " + e.getMessage(), e);
            }// end if
        }// end try...catch
    }// end copyJarToTransferDir

    /**
     * This method is a convenience method used to write to an existing file by appending a line to the end of the file.
     *
     * @param path
     *        The <code>String</code> value of the path to the file being written to.
     * @param lineWithToken
     *        The <code>String</code> to append to the end of the file.
     */
    public static void writeFile(String path, String lineWithToken) {
        myLogger.entering(MY_CLASS_NAME, "writeFile");
        File f = new File(path);
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(f, true));
            myLogger.finer("Writing line to file: " + f.getName());
            writer.append(lineWithToken);
            writer.flush(); // flush to write out to file...
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException was caught while trying to write to the file. Message is: " + e.getMessage(), e);
        }finally{
            try{
                myLogger.finer("See if the writer needs to be closed or not...if so close it");
                if(writer != null){
                    writer.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException was caught while trying to close the Buffered Writer. Message is: " + e.getMessage(), e);
            }// end try/catch
        }// end try/catch/finally
        myLogger.exiting(MY_CLASS_NAME, "writeFile");
    }// end writeFile

    /**
     * This method is a convenience method used to read a file and return a <code>List&lt;String&gt;</code>. WARNING: Do not use a reg expression character that must be escaped such as:
     * <p>
     * <code><b>^ [ . $ { * ( \ + ) | ? &lt; &gt;</b></code>
     *
     * @param path
     *        The {@link java.io.File} to read and add to a <code>List&lt;String&gt;</code>.
     * @param token
     *        the token used to split the string into segments
     * @return The file contents in a <code>List&lt;String&gt;</code>.
     */
    public static List<String> readFile(String path, String token) {
        myLogger.entering(MY_CLASS_NAME, "readFile");
        List<String> values = new ArrayList<String>();
        File f = new File(path);
        BufferedReader reader = null;
        try{
            String line = null;
            String[] split = null;
            reader = new BufferedReader(new FileReader(f));
            while((line = reader.readLine()) != null){
                if(!AppUtil.isNullOrEmpty(line)){
                    split = line.substring(0, line.lastIndexOf(token)).split(token);
                    for(int i = 0, j = split.length;i < j;i++){
                        values.add(split[i]);
                    }// end for
                }// end if
            } // end while
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "FileNotFoundException was caught while trying to initialize the Buffered Reader. Message is: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException was caught while trying to read the file. Message is: " + e.getMessage(), e);
        }finally{
            try{
                myLogger.finer("See if the reader needs to be closed or not...if so close it");
                if(reader != null){
                    reader.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException was caught while trying to close the Buffered Reader. Message is: " + e.getMessage(), e);
            }// end try/catch
        }// end try/catch/finally
        myLogger.exiting(MY_CLASS_NAME, "readFile");
        return values;
    }// end readFile

    /**
     * This method will check to see if a file exists on the class path or not.
     *
     * @param clazz
     *        the class used to obtain the root classpath location
     * @param nameOfFile
     *        the name of the file to find.
     * @return {@code true} or {@code false} value on whether or not the file exists
     */
    public static boolean fileExistsOnClasspath(Class<?> clazz, String nameOfFile) {
        boolean exists = false;
        if(!Constants.IS_JAR){
            File rootDir = null;
            try{
                rootDir = new File(URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
                if(rootDir.exists()){
                    String[] pathToInternalFile = new String[1];
                    findFile(rootDir, nameOfFile, pathToInternalFile);
                    if(pathToInternalFile[0] == null){
                        myLogger.warning("Could not find " + String.valueOf(nameOfFile) + ".");
                    }else{
                        exists = true;
                    }// end if
                }// end if
            }catch(UnsupportedEncodingException e){
                myLogger.warning("UnsupportedEncodingException while trying to decode source file location string.");
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to find a file on the classpath.  Error is: " + e.getMessage());
            }// end try...catch
        }//end if
        return exists;
    }// end method

    /**
     * This method will delete a directory from the bottom up.
     *
     * @param rootDirectory
     *        the root directory to start deleting
     */
    public static void deleteDirectory(File rootDirectory) {
        myLogger.entering(MY_CLASS_NAME, "deleteDirectory", rootDirectory);
        try{
            if(rootDirectory != null && rootDirectory.exists()){
                Files.walkFileTree(rootDirectory.toPath(), new DeleteFileVisitor());
                Files.deleteIfExists(rootDirectory.toPath());
            }else{
                myLogger.warning("Directory does not exist therefore will not be attempted to be deleted.  rootDirectory=" + rootDirectory != null ? rootDirectory.getPath() : "null");
            }//end if...else
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException was caught while trying to close the Buffered Reader. Message is: " + e.getMessage(), e);
        }//end try...catch
        myLogger.exiting(MY_CLASS_NAME, "deleteDirectory");
    }// end deleteFile

}// end class
