package com.omo.free.simple.fx.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.simple.fx.util.FileUtility;
import com.omo.free.util.AppUtil;

/**
 * This class handles the refactoring of an application based on whether or not the SFXApplication implements the {@link Refactorable}.
 *
 * <b>I created this class as just a test class for an easy way of renaming an application and to not lose its update ability due to this.</b>
 *
 * @author Richard Salas October 23, 2017
 */
public class ApplicationRefactor {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.ApplicationRefactor";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * This method will check to see if the application is ready to be refactored.
     * @param refactoredApp the refactorable application instance
     */
    void checkForRefactoredApplication(Refactorable refactoredApp) {
        myLogger.entering(MY_CLASS_NAME, "checkForRefactoredApplication", refactoredApp);

        boolean proceedToRefactor = validateValues(refactoredApp);
        if(proceedToRefactor){
            myLogger.info("ask user if he wants the refactored version that was found.");
            SFXDialogLauncher.type = SFXDialogLauncher.DialogType.REFACTOR_MESSAGE;
            SFXDialogLauncher.type.showPopUp();
            if(SFXDialogLauncher.update){
                // 2 commands are going to be needed.
                FXUtil.closeSplashScreen();// close associated splash screen to this application if it exists
                String arg = Constants.FRAMEWORK_FILE_LOCATION;//local file
                String splashStatusFilePath = null;
                Properties properties = PropertiesMgr.getInstance().getProperties();
                try{
                    splashStatusFilePath = setUpdatingSplashScreenAndRun();
                }catch(Exception e){
                    myLogger.log(Level.WARNING, "Exception thrown while trying to initialize spash screen used as part of the update process. Error is: " + e.getMessage(), e);
                }// end try...catch

                String extResourceDirPath = null;
                try{
                    extResourceDirPath = new File(SFXApplication.getExternalResourcesDirectory()).getParentFile().getCanonicalPath();
                }catch(IOException e){
                    myLogger.log(Level.WARNING, "IOException thrown while trying to get external resources directory to delete during the refactoring process.  Path trying to get is: " + String.valueOf(SFXApplication.getExternalResourcesDirectory()) + " Error is: " + e.getMessage(), e);
                    extResourceDirPath = "./blah";
                }//end try...catch

                File sharedJarFile = new File(Constants.APPLICATION_SHARED_DIRECTORY + "/" + refactoredApp.futureJarName());
                String javaCommand = "java -splash:no -jar " + sharedJarFile.getName() + " REFACTOR \"" + arg + "\" true " + properties.getProperty("debug.isOn") + " \"" + new File(arg).getParent() + "\" \"" + extResourceDirPath + "\"";
                if(splashStatusFilePath != null){
                    javaCommand = javaCommand + " \"" + splashStatusFilePath + "\"";
                }// end if

                myLogger.info("javaCommand=" + javaCommand);
                FileUtility.checkDirectories(properties.getProperty("temp.dir.holder"));// create the temp directory
                FileUtility.copyFileToDir(sharedJarFile, properties.getProperty("temp.dir.holder"));//copy new shared jar file here!!!
                try{
                    String canonicalPath = new File(properties.getProperty("temp.dir.holder")).getCanonicalPath();// removes the redundant . or .. from the path...
                    Runtime.getRuntime().exec(javaCommand, null, new File(canonicalPath));
                    System.exit(0);
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException while trying to execute java sub process commands " + javaCommand + "Controller.run(). e= " + e.getMessage(), e);
                }// end try...catch
            }// end if
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "versionCheck");
    }//end method

    /**
     * First validate the refactorable values to see if they do in fact exist.
     *
     * @param refactoredApp the refactorable application instance
     * @return true | false
     */
    private boolean validateValues(Refactorable refactoredApp) {
        boolean isValid = true;
        if(AppUtil.isNullOrEmpty(refactoredApp.currentJarName()) || AppUtil.isNullOrEmpty(refactoredApp.futureJarName())){
            isValid = false;
        }//end if

        if(isValid){//local jar file check
            File localJarFile = new File(Constants.FRAMEWORK_FILE_LOCATION);
            isValid = localJarFile.getName().equals(refactoredApp.currentJarName());
            Constants.CURRENT_JAR_NAME = refactoredApp.currentJarName();
        }//end if

        if(isValid){//shared jar file check
            File sharedJarFile = new File(Constants.APPLICATION_SHARED_DIRECTORY + "/" + refactoredApp.futureJarName());
            isValid = sharedJarFile.exists();
            Constants.FUTURE_JAR_NAME = refactoredApp.futureJarName();
        }//end if

        return isValid;
    }//end method

    /**
     * This method will initialize and start the updating splash screen.
     *
     * @return statusPath the path to the status file
     * @throws Exception
     *         exception can be thrown while trying to extract and create new files
     */
    private String setUpdatingSplashScreenAndRun() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "setUpdatingSplashScreenAndRun");
        // splash screen resources will be located/extracted to /resources/splash
        File parentDir = new File(SFXApplication.getExternalResourcesDirectory()); // this should be the /resources folder.
        FileUtility.checkDirectories(parentDir.getPath() + "/splash");
        File statusFile = new File(parentDir.getPath() + "/splash" + "/updatestatus.dat");
        // delete all files here and prepare for new ones.
        File[] list = new File(parentDir.getPath() + "/splash").listFiles();
        for(int i = 0, j = list.length;i < j;i++){
            list[i].delete();
        }// end for
        statusFile.createNewFile();
        FileUtility.extractFileFromJar(parentDir.getPath() + "/splash", "AppUpdateSplash.class");
        FileUtility.extractFileFromJar(parentDir.getPath() + "/splash", "updateSplashScreen.png");

        // make sure to run this from within the location of resources
        Runtime.getRuntime().exec("java -splash:splash/updateSplashScreen.png splash/AppUpdateSplash", null, new File(parentDir.getCanonicalPath()));
        String statusPath = statusFile.getCanonicalPath();
        myLogger.exiting(MY_CLASS_NAME, "setUpdatingSplashScreenAndRun", statusPath);
        return statusPath;
    }//end method

    /**
     * This method is used in the refactor process and is only kicked off when user answers yes to the prompt of whether he/she wants an updated version. Logging has been set up and will only be used if the user wishes to veiw logging details of the update process. The logging is a feature added to this process mainly for the developer to use as a debug tool.
     *
     * @param args
     *        the arguments that are passed into this application.
     */
    protected void refactorApplication(String[] args) {
        File newFile = null;
        BufferedWriter bw = null;
        try{// args[0]=REFACTOR args[1]= jar to delete; args[2]= true/false; args[3]= debug setting true|false; args[4]=debuglog file parent dir; args[5]=(this may not exist)
            if("true".equals(args[3])){// if args[2] is true then debug log should be activated.
                newFile = new File(args[4] + "/debug.log");
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

            if("true".equals(args[2])){ // true if this is update process false if this is copy of transfer to main dir and delete
                File oldApplicationJarFile = new File(args[1]);
                String parent = oldApplicationJarFile.getParent();
                if(oldApplicationJarFile.exists()){
                    if("true".equals(args[3])){// debug log
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
                String jarPath = ApplicationRefactor.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
                if("true".equals(args[3])){// debug log
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
                String command = "java -Xms128m -Xmx1024m -jar " + jar.getName() + " REFACTOR \"" + jarPath + "\" false " + args[3] + " \"" + args[4] + "\" \"" + args[5] + "\"";
                if(args.length == 7){
                    if(splashAttributeExists(jar)){
                        // shutdown the currently updating splash screen.
                        shutdownSplashScreen(args[6]);
                        File oldResources = new File(args[5]);
                        for(int i = 0;i < 20000;i++){// attempt to delete the file 10000 times if can;t delete by then no need to try anymore.
                            if(oldResources.exists()){
                                FileUtility.deleteDirectory(oldResources);
                                if("true".equals(args[3])){
                                    bw.write((i + 1) + " attempts to oldResources dir. Successful: " + !oldResources.exists());
                                    bw.newLine();
                                }//end if
                            }else{
                                break;
                            }// end if
                        }// end for
                    }else{
                        // new jar does not contain a splash screen attribute so the updating splash will stay.
                        command = command + " \"" + args[6] + "\"";
                    }// end if
                }// end if
                if("true".equals(args[3])){// debug log
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
                    if("true".equals(args[3])){
                        bw.write("Exception trying to execute the command: " + command + ". Exception is: " + e.getMessage());
                    }else{
                        System.out.println("Exception trying to execute the command java -jar " + jar.getName() + ". Exception is: " + e.getMessage());
                    }// end if
                }// end try...catch
            }else{// cleaning process
                if("true".equals(args[3])){// debug log
                    bw.write("cleaning up old temp files");
                    bw.newLine();
                }// end if
                File transferJar = new File(args[1]);// delete transfer jar then transfer location and commence application...
                if(transferJar.exists()){
                    if("true".equals(args[3])){
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
                    if("true".equals(args[3])){
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
                if(args.length == 7){
                    shutdownSplashScreen(args[6]);
                    File oldResources = new File(args[5]);
                    for(int i = 0;i < 20000;i++){// attempt to delete the file 10000 times if can;t delete by then no need to try anymore.
                        if(oldResources.exists()){
                            FileUtility.deleteDirectory(oldResources);
                            if("true".equals(args[3])){
                                bw.write((i + 1) + " attempts to oldResources dir. Successful: " + !oldResources.exists());
                                bw.newLine();
                            }//end if
                        }else{
                            break;
                        }// end if
                    }// end for
                }// end if
            }// end if
        }catch(IOException e){
            if("true".equals(args[3])){
                try{
                    bw.write("IOException trying to start new virtual process. Exception is " + e.getMessage());
                }catch(IOException e1){
                    System.err.println("IOException trying to start new virtual process. Exception is " + e1.getMessage());
                }// end try...catch
            }else{
                System.err.println("IOException trying to start new virtual process. Exception is " + e.getMessage());
            }// end if
        }catch(Exception e){
            if("true".equals(args[3])){
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
    }// end method

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

        try{
            TimeUnit.SECONDS.sleep(3);
        }catch(InterruptedException e){
            System.out.println("InterruptedException occurred during sleeping of thread...this is here to give time for the program to shutdown");
        }//end try...catch
    }// end method

}//end class
