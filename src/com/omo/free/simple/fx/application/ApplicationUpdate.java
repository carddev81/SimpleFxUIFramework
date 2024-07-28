/**
 *
 */
package com.omo.free.simple.fx.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.simple.fx.util.FileUtility;
import com.omo.free.util.AppUtil;
import com.omo.free.util.DateUtil;

/**
 * This class handles updating your application.
 *
 * @author Richard Salas JCCC, September 03, 2015
 * @since SimpleUI Framework 1.0.0
 */
class ApplicationUpdate {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.ApplicationUpdate";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Default Constructor
     */
    public ApplicationUpdate() {

    }

    /**
     * This method will check to see if the running application is older than the application that sits within a shared directory on the network. If the running application is older the user is prompted to update the application with the newer version and then a simple update process is kicked off.
     * @param clazz application class
     */
    protected void checkForNewerVersion(Class<?> clazz) {
        myLogger.entering(MY_CLASS_NAME, "versionCheck");
        String jarName = clazz.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        System.out.println("jarName:"+String.valueOf(jarName));
        Pattern p = Pattern.compile("^(.*)(v\\d+)(\\.\\d+)(\\.jar)$");
        Matcher m = p.matcher(jarName.substring(jarName.lastIndexOf("/") + 1));
        String version = getVersionName();
        if(AppUtil.isNullOrEmpty(version)){
            myLogger.warning("Format of the version name within manifest is not incompliance with the simple ui framework jars update process, therefore the update process is being skipped.  Path to Jar Name is: " + String.valueOf(jarName) + ".");
            return;
        }//end if

        if(m.find()){
            jarName = m.group(1) + m.group(2);
        }else{
            myLogger.warning("Format of the jar name is not incompliance with the simple ui framework jars update process, therefore the update process is being skipped.  Path to Jar Name is: " + String.valueOf(jarName) + ".");
            return;
        }

        myLogger.fine("The name of the running jar is " + jarName);
        myLogger.finest("get the shared directory and then getting the shared jar file that is associated with this application.");
        File sharedDir = new File(Constants.APPLICATION_SHARED_DIRECTORY);
        File sharedJarFile = getSharedJarFile(sharedDir, jarName);//passing in ApplicationUIv

        myLogger.fine("checking for newer version of myself in the shared location");
        if(sharedJarFile != null && newerVersionExists(sharedJarFile)){
            myLogger.fine("ask user if he wants the newer version that was found.");
            SFXDialogLauncher.type = SFXDialogLauncher.DialogType.UPDATE_MESSAGE;
            //Application.launch(SFXDialogLauncher.class, "");doing this differently here
            SFXDialogLauncher.type.showPopUp();
            if(SFXDialogLauncher.update){
                // 2 commands are going to be needed.
                FXUtil.closeSplashScreen();//close associated splash screen to this application if it exists
                String arg = Constants.FRAMEWORK_FILE_LOCATION;
                String splashStatusFilePath = null;
                Properties properties = PropertiesMgr.getInstance().getProperties();
                try{
                    splashStatusFilePath = setUpdatingSplashScreenAndRun();
                }catch(Exception e){
                    myLogger.log(Level.WARNING, "Exception thrown while trying to initialize spash screen used as part of the update process. Error is: " + e.getMessage(), e);
                }//end try...catch
                String javaCommand = "java -splash:no -jar " + sharedJarFile.getName() + " \"" + arg + "\" true " + properties.getProperty("debug.isOn") + " \"" + new File(arg).getParent() + "\"";
                if(splashStatusFilePath != null){
                    javaCommand = javaCommand + " \"" + splashStatusFilePath + "\"";
                }//end if

                myLogger.fine("javaCommand=" + javaCommand);
                FileUtility.checkDirectories(properties.getProperty("temp.dir.holder"));// create the temp directory
                FileUtility.copyFileToDir(sharedJarFile, properties.getProperty("temp.dir.holder"));
                try{
                    String canonicalPath = new File(properties.getProperty("temp.dir.holder")).getCanonicalPath();// removes the redundant . or .. from the path...
                    Runtime.getRuntime().exec(javaCommand, null, new File(canonicalPath));
                    System.exit(0);
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException while trying to execute java sub process commands " + javaCommand + "Controller.run(). e= " + e.getMessage(), e);
                }// end try...catch
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "versionCheck");
    }// end versionCheck()

    /**
     * This method will initialize and start the updating splash screen.
     *
     * @return statusPath the path to the status file
     * @throws Exception exception can be thrown while trying to extract and create new files
     */
    private String setUpdatingSplashScreenAndRun() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "setUpdatingSplashScreenAndRun");
        // splash screen resources will be located/extracted to /resources/splash
        File parentDir = new File(SFXApplication.getExternalResourcesDirectory()); // this should be the /resources folder.
        FileUtility.checkDirectories(parentDir.getPath() + "/splash");
        File statusFile = new File(parentDir.getPath() + "/splash" + "/updatestatus.dat");
        //delete all files here and prepare for new ones.
        File[] list = new File(parentDir.getPath() + "/splash").listFiles();
        for(int i = 0, j = list.length; i < j; i++){
            list[i].delete();
        }//end for
        statusFile.createNewFile();
        FileUtility.extractFileFromJar(parentDir.getPath() + "/splash", "AppUpdateSplash.class");
        FileUtility.extractFileFromJar(parentDir.getPath() + "/splash", "updateSplashScreen.png");

        // make sure to run this from within the location of resources
        String appName = UIPropertiesMgr.getInstance().getProperties().getProperty("application.name");
        Runtime.getRuntime().exec("java -splash:splash/updateSplashScreen.png splash/AppUpdateSplash \"" + appName + "\" " +  Constants.NEWER_VERSION_LABEL, null, new File(parentDir.getCanonicalPath()));
        String statusPath = statusFile.getCanonicalPath();
        myLogger.exiting(MY_CLASS_NAME, "setUpdatingSplashScreenAndRun", statusPath);
        return statusPath;
    }

    /**
     * This method will return the jar file if found inside of the shared directory location.
     *
     * @param dir
     *        the directory that contains the shared jar
     * @param appPrefix
     *        the prefix of the jar
     * @return the jar file is returned by this method
     */
    private File getSharedJarFile(File dir, String appPrefix) {
        myLogger.entering(MY_CLASS_NAME, "getSharedJarFile() method", new Object[]{dir, appPrefix});
        File jar = null;
        if(dir.exists()){
            try{
                double sharedJarVerNo = 0.0;
                File[] files = dir.listFiles(new ApplicationJarFileNameFilter(appPrefix));
                if(files.length == 1){
                    jar = files[0];
                }else if(files.length > 1){
                    Pattern jarP = Pattern.compile("^(.*)(v)(\\d+\\.\\d+)(\\.jar)$");
                    Matcher jarM = null;
                    for(int i = 0, j = files.length;i < j;i++){
                        jarM = jarP.matcher(files[i].getName());
                        if(jar==null){
                            jar = files[i];
                            jarM.find();
                            sharedJarVerNo = Double.valueOf(jarM.group(3)).doubleValue();
                        }else{
                            jarM.find();
                            if(sharedJarVerNo < Double.valueOf(jarM.group(3)).doubleValue()){
                                sharedJarVerNo = Double.valueOf(jarM.group(3)).doubleValue();
                                jar = files[i];
                            }//end if
                        }//end if
                    }// end for
                }//end if
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to obtain the shared jar file. Format of the jar name is not incompliance with the simple ui framework jars update process. Prefix name is: " + String.valueOf(appPrefix) + ". Error Message is: " + e.getMessage(), e);
            }//end try...catch
        }else{
            myLogger.warning("Shared directory does not exist!!! Either this directory really does not exist or if it does then you do not have permissions to access it.  The directory is: " + dir.getAbsolutePath());
        }// end if

        if (dir.exists() && jar == null){
            myLogger.warning("No .jar file existed within this shared directory. Update process will not procede.");
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "getSharedJarFile()", jar);
        return jar;
    }

    /**
     * This method will check to see if this application is older than the application of itself which is found within a shared directory.
     *
     * @param jarFile
     *        the jar file that will be version checked.
     * @return boolean value (true|false) depending on whether or not the jar that is found within the shared directory is newer.
     */
    private boolean newerVersionExists(File jarFile) {
        myLogger.entering(MY_CLASS_NAME, "newerVersionExists() method", jarFile);
        boolean isNewer = false;

        JarFile sharedJar = null;
        JarFile runningJar = null;

        myLogger.finest("checking to see if version file exists as this is when the application update process was implemented as in the future this valus found within will be checked upon but for now we just need to make sure that this exists before proceeding");
//        if(versionFileExists(jarFile)){
        try{
            // create the jar file instance of the shared jar file to extract versioning information from manifest
            myLogger.info("Checking jar version jar inside of the shared directory " + jarFile.getAbsolutePath());
            sharedJar = new JarFile(jarFile);

            myLogger.finest("getting the attributes from manifest found within the shared jar file for comparision with running app.");
            Attributes sharedAttr = sharedJar.getManifest().getMainAttributes();
            String sharedVersion = sharedAttr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            String[] sharedSplit = sharedVersion.split(" ");

            myLogger.fine("Checking jar version of " + Constants.FRAMEWORK_FILE_LOCATION);
            runningJar = new JarFile(Constants.FRAMEWORK_FILE_LOCATION);

            Attributes runningAttr = runningJar.getManifest().getMainAttributes();
            String runningVersion = runningAttr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            String[] runningSplit = runningVersion.split(" ");
            if(runningVersion.contains("-") && sharedVersion.contains("-")){
                runningVersion = runningVersion.substring(runningVersion.lastIndexOf("-") + 1, runningVersion.indexOf(" "));
                myLogger.fine("runningVersion=" + runningVersion);

                sharedVersion = sharedVersion.substring(sharedVersion.lastIndexOf("-") + 1, sharedVersion.indexOf(" ")); // need to modify build to be like this -49-201410271123 v2.0 October 27 2014
                myLogger.fine("sharedVersion=" + sharedVersion);

                try{
                    myLogger.finest("checking to see if splitted string contains the version sections...if so then then proceed if not then the updated version will not work");
                    if(sharedSplit[1].matches("^[v]{1}[1-9]{1}\\.[0-9]{1}$") && runningSplit[1].matches("^[v]{1}[1-9]{1}\\.[0-9]{1}$")){
                        double sharedVer = Double.valueOf(sharedSplit[1].substring(1));
                        double runningVer = Double.valueOf(runningSplit[1].substring(1));

                        long sharedVersionMillies = DateUtil.asDate(sharedVersion);
                        long runningVersionMillies = DateUtil.asDate(runningVersion);

                        Date sharedDate = new Date(sharedVersionMillies);
                        Date currentDate = new Date(runningVersionMillies);
                        myLogger.info("sharedVersionMillies=" + sharedVersionMillies);
                        myLogger.info("shared date=" + String.valueOf(sharedDate));

                        myLogger.info("runningVersionMillies=" + runningVersionMillies);
                        myLogger.info("running date=" + String.valueOf(currentDate));

                        if(sharedVersionMillies > runningVersionMillies || sharedVer > runningVer){
                            myLogger.info("Jar file within the shared directory is newer");
                            //set NEWER_VERSION NM and DATE FIELDS
                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
                            String version = sharedAttr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                            String[] versionSplit = version.split(" ");
                            Constants.NEWER_VERSION_LABEL = versionSplit[1] + "." + version.substring(1, version.lastIndexOf("-"));

                            try{
                                Constants.NEWER_VERSION_DATE_LABEL = "  Built on " + format.format(sharedDate);
                                Constants.CURRENT_VERSION_DATE_LABEL = "  Built on " + format.format(currentDate);
                            }catch(Exception e){
                                myLogger.log(Level.SEVERE, "Exception occurred while trying to format the date for being displayed within the gui update popup window");
                            }//end try...catch
                            isNewer = true;
                        }// end if
                    }// end if
                }catch(ParseException e){
                    myLogger.log(Level.SEVERE, "ParseException while trying to parse date string yyyyMMddHHSS." + " Cause is: " + e.getMessage(), e);
                }// end try...catch
            }//end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while trying to gather manifest data out of jar(s)." + " Cause is: " + e.getMessage(), e);
        }finally{
            try{
                if(sharedJar != null){
                    sharedJar.close();
                }// end if
                if(runningJar != null){
                    runningJar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while trying to closing the jar/zip file streams." + " Cause is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
//        }// end if
        myLogger.info("Is the shared jar newer than the currently running version: " + isNewer);
        myLogger.exiting(MY_CLASS_NAME, "newerVersionExists()", isNewer);
        return isNewer;
    }// end newerVersionExists()

    /**
     * This method will return the formatted version name of this application.
     *
     * @return versionName the name of the version of this file
     */
    protected String getVersionName() {
        myLogger.entering(MY_CLASS_NAME, "getVersionName() method");
        // check to see if the version label exists first.
        if(!"".equals(Constants.CURRENT_VERSION_LABEL)){
            return Constants.CURRENT_VERSION_LABEL;
        }// end if

        String versionName = "";
        JarFile jar = null;
        try{
            jar = new JarFile(Constants.FRAMEWORK_FILE_LOCATION);
            Attributes sharedAttr = jar.getManifest().getMainAttributes();
            String version = sharedAttr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            String[] versionSplit = version.split(" ");
            versionName = versionSplit[1] + "." + version.substring(1, version.lastIndexOf("-"));
            Constants.CURRENT_VERSION_LABEL = versionName; // set the label
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while trying to see get version from within manifest." + " Exception is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while trying build the version name." + " Exception is: " + e.getMessage(), e);
        }finally{
            try{
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while trying to close the jar file stream." + " Exception is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...cathc
        myLogger.exiting(MY_CLASS_NAME, "getVersionName() method", versionName);
        return versionName;
    }

    /**
     * This method is used in the update process and is only kicked off when user answers yes to the prompt of whether he/she wants an updated version. Logging has been set up and will only be used if the user wishes to veiw logging details of the update process. The logging is a feature added to this process mainly for the developer to use as a debug tool.
     *
     * @param args
     *        the arguments that are passed into this application.
     */
    protected void updateApplication(String[] args) {
        File newFile = null;
        BufferedWriter bw = null;
        try{// args[0]= jar to delete; args[1]= true/false; args[2]= debug setting true|false; args[3]=debuglog file parent dir; args[4]=(this may not exist)
            if("true".equals(args[2])){// if args[2] is true then debug log should be activated.
                newFile = new File(args[3] + "/debug.log");
                if(!newFile.exists()){
                    newFile.createNewFile();
                }// end if
                bw = new BufferedWriter(new FileWriter(newFile, true));
                bw.write("**************************Start Debug Log*****************************");
                bw.newLine();
                bw.write("Command Line Arguments");
                bw.newLine();
                for(int i = 0, j = args.length;i < j;i++){
                    bw.write("args[" + i + "] = ");
                    bw.write(args[i]);
                    bw.newLine();
                }// end for
                bw.flush();
            }// end if
            if("true".equals(args[1])){ // true if this is update process false if this is copy of transfer to main dir and delete
                File oldApplicationJarFile = new File(args[0]);
                String parent = oldApplicationJarFile.getParent();
                if(oldApplicationJarFile.exists()){
                    if("true".equals(args[2])){// debug log
                        bw.write(oldApplicationJarFile.getAbsolutePath());
                        bw.write(" exists.");
                        bw.newLine();
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times.
                            if(oldApplicationJarFile.exists()){
                                bw.write((i + 1) + " attempts to delete file. Successful: " + oldApplicationJarFile.delete());
                                bw.newLine();
                            }else{
                                break;
                            }// end if
                        }// end for
                        bw.flush();
                    }else{
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times if can;t delete by then no need to try anymore.
                            if(oldApplicationJarFile.exists()){
                                oldApplicationJarFile.delete();
                            }else{
                                break;
                            }// end if
                        }// end for
                    }// end if
                }// end if
                String jarPath = ApplicationUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
                if("true".equals(args[2])){// debug log
                    bw.write("jar to copy Path=");
                    bw.write(jarPath);
                    bw.newLine();
                    bw.write("the path to copy new jar to parent=");
                    bw.write(parent);
                    bw.newLine();
                    bw.flush();
                }// end if
                File jar = new File(jarPath);
                FileUtility.copyFileToDir(jar, parent + "/");// copy file to location
                String command = "java -Xms128m -Xmx1024m -jar " + jar.getName() + " \"" + jarPath + "\" false " + args[2] + " \"" + args[3] + "\"";
                if(args.length == 5){
                    if(splashAttributeExists(jar)){
                        //shutdown the currently updating splash screen.
                        shutdownSplashScreen(args[4]);
                    }else{
                        //new jar does not contain a splash screen attribute so the updating splash will stay.
                        command = command + " \"" + args[4] + "\"";
                    }//end if
                }//end if
                if("true".equals(args[2])){// debug log
                    bw.write("command with arguments to be executed is as follows:");
                    bw.newLine();
                    bw.write(command);
                    bw.newLine();
                    bw.write("**************************End Debug Log*****************************");
                    bw.newLine();
                    bw.flush();
                }// end if
                if(bw != null){
                    bw.close();// make sure to close to free up resources.
                }// end if
                try{
                    Runtime.getRuntime().exec(command, null, new File(parent));
                    System.exit(0);
                }catch(Exception e){
                    if("true".equals(args[2])){
                        bw.write("Exception trying to execute the command: " + command + ". Exception is: " + e.getMessage());
                    }else{
                        System.out.println("Exception trying to execute the command java -jar " + jar.getName() + ". Exception is: " + e.getMessage());
                    }// end if
                }// end try...catch
            }else{// cleaning process
                if("true".equals(args[2])){// debug log
                    bw.write("cleaning up old temp files");
                    bw.newLine();
                }// end if
                File transferJar = new File(args[0]);// delete transfer jar then transfer location and commence application...
                if(transferJar.exists()){
                    if("true".equals(args[2])){
                        bw.write("jar ");
                        bw.write(transferJar.getAbsolutePath());
                        bw.write(" exists.");
                        bw.newLine();
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times.
                            if(transferJar.exists()){
                                bw.write((i + 1) + " attempts to delete transfer jar file. Successful: " + transferJar.delete());
                                bw.newLine();
                            }else{
                                break;
                            }// end if
                        }// end for
                        bw.flush();
                    }else{
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times if can;t delete by then no need to try anymore.
                            if(transferJar.exists()){
                            	transferJar.delete();
                            }else{
                                break;
                            }// end if
                        }// end for
                    }// end if
                }// end if
                File parentDir = new File(transferJar.getParent());
                if(parentDir.exists()){
                    if("true".equals(args[2])){
                        bw.write("directory ");
                        bw.write(parentDir.getAbsolutePath());
                        bw.write(" exists.");
                        bw.newLine();
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times.
                            if(parentDir.exists()){
                                bw.write((i + 1) + " attempts to delete transfer directory. Successful: " + parentDir.delete());
                                bw.newLine();
                            }else{
                                break;
                            }// end if
                        }// end for
                        bw.write("**************************End Debug Log*****************************");
                        bw.flush();
                    }else{
                        for(int i = 0;i < 10000;i++){// attempt to delete the file 10000 times if can;t delete by then no need to try anymore.
                            if(parentDir.exists()){
                            	parentDir.delete();
                            }else{
                                break;
                            }// end if
                        }// end for
                    }// end if
                }// end if
                if(args.length == 5){
                    shutdownSplashScreen(args[4]);
                }//end if
            }// end if
        }catch(IOException e){
            if("true".equals(args[2])){
                try{
                    bw.write("IOException trying to start new virtual process. Exception is " + e.getMessage());
                }catch(IOException e1){
                    System.err.println("IOException trying to start new virtual process. Exception is " + e1.getMessage());
                }// end try...catch
            }else{
                System.err.println("IOException trying to start new virtual process. Exception is " + e.getMessage());
            }// end if
        }catch(Exception e){
            if("true".equals(args[2])){
                try{
                    bw.write("Exception trying to start new virtual process. Exception is " + e.getMessage());
                }catch(IOException e1){
                    System.err.println("IOException trying to write error message. Exception is " + e1.getMessage());
                }// end try...catch
            }else{
                System.out.println("Exception trying to start new virtual process. Exception is " + e.getMessage());
            }// end if
        }finally{
            if(bw != null){
                try{
                    bw.flush();
                    bw.close();
                }catch(IOException e){
                    System.err.println("IOException occurred while trying to close buffered writer. Exception is " + e.getMessage());
                }// end try....catch
            }// end if
        }// end try...catch
    }// end updateApplication()

    /**
     * This method will return the whether or not the jar file being passed into this method contains the SplashScreen-Image attribute.
     * @param jarFile the file that contains the MANIFEST.MF file to search through
     * @return splashAttributeExists true or false on whether or not there is indeed a SplashScreen-Image attribute
     */
    protected boolean splashAttributeExists(File jarFile) {
        myLogger.entering(MY_CLASS_NAME, "splashAttributeExists() method", jarFile);

        JarFile jar = null;
        boolean splashAttributeExists = false;
        try{
            jar = new JarFile(jarFile);
            Attributes sharedAttr = jar.getManifest().getMainAttributes();
            String attribute = sharedAttr.getValue("SplashScreen-Image");
            if(attribute != null){
                splashAttributeExists = true;
            }//end if
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while trying to see verfify whether or not the jar contains the SplashScreen-Image attribute within manifest." + " Exception is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception while trying check whether not the jar contains the SplashScreen-Image attribute within manifest." + " Exception is: " + e.getMessage(), e);
        }finally{
            try{
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while trying to close the jar file stream." + " Exception is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...cathc
        myLogger.exiting(MY_CLASS_NAME, "splashAttributeExists() method");
        return splashAttributeExists;
    }//end method

    /**
     * This method will shutdown the splash screen and is called when update process is done.
     * @param statusFilePath the path to the status file.
     */
    private void shutdownSplashScreen(String statusFilePath) {
        File statusFile = new File(statusFilePath);
        if(statusFile.exists()){
            BufferedWriter bw = null;
            try{
                bw = new BufferedWriter(new FileWriter(statusFile));
                bw.write("FINISHED");
            }catch(IOException e){
                System.err.println("IOException trying to write finished to the following file: " + statusFilePath + ". Error is:" + e.getMessage());
            }finally{
                if(bw != null){
                    try{
                        bw.close();
                    }catch(IOException e){
                        System.err.println("IOException trying to close writer. Error is:" + e.getMessage());
                    }//end...try...catch
                }//end if
            }//end try...catch
        }//end if
    }//end method

}
