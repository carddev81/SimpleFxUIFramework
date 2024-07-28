/**
 *
 */
package splash;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;

/**
 * Class used for Displaying a splash screen to the user upon updating their application.
 * @author Richard Salas
 */
public class AppUpdateSplash {

    private static String DISPLAY_MESSAGE = "";
    private static final String[] ANIMATE = {".", ". .", ". . .", ". . . .", ". . . . .", ". . . . . .", ". . . . . . .", ". . . . . . . .", ". . . . . . . . ."};

    private static String START_IN;
    /**
     * Default constructor
     */
    public AppUpdateSplash() {
    }//end constructor

    /**
     * This method is used to set the Start In directory path.
     */
    private static void setStartInDirectoryPath() {
        try{
            String fileLocation = URLDecoder.decode(AppUpdateSplash.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
            START_IN = new File(fileLocation).toString();
        }catch(Exception e){
            System.err.println("Exception occurred while trying to set the START_IN_DIR_PATH constant going to set it to (.).  Error message is:  " + e.getMessage());
            START_IN = ".";
        }//end method
    }//end method
    
    /**
     * Main method to start UpdateApplicationSplash. This will run during the update process.
     * @param args array of arguments passed into the application at start up.
     */
    public static void main(String[] args) {
        //args[0]=Application Name; args[1]=Newer Version Label
        //threshold is 5 minutes.
        setStartInDirectoryPath();
        long thresholdTime = System.currentTimeMillis() + (60000 * 4);//4 minutes should be plenty of time in case something goes wrong.

        if(args!=null && args.length == 2){
            DISPLAY_MESSAGE = "Updating " + args[0] + " to version " + args[1] + " ";
        }else{
            DISPLAY_MESSAGE = "Updating your application ";
            thresholdTime = System.currentTimeMillis() + (60000);//1 minute should be plenty of time in case something goes wrong.;
        }//end if

        //here lets make sure the display message isn't above 60.
        if(DISPLAY_MESSAGE.length() > 60){
            DISPLAY_MESSAGE = "Updating your application to version " + args[1] + " ";
        }//end if

        SplashScreen updateSplash = SplashScreen.getSplashScreen();
        if(updateSplash == null){
            System.out.println("Splash Screen does not exist.");
            return;
        }//end if

        //getting the graphics from the splash screen.
        Graphics2D uSplashGraphics = updateSplash.createGraphics();
        if (uSplashGraphics == null) {
            System.out.println("uSplashGraphics is null");
            return;
        }//end if

        int animatePos = 0;
        while(true && System.currentTimeMillis() < thresholdTime){//this loop will terminate at 4 minutes no matter what!!!
            if(!updateFinished()){
                animateSplashScreen(uSplashGraphics, animatePos);
                updateSplash.update();
                try{
                    Thread.sleep(150);
                }catch(Exception e){
                    System.err.println("Exception occurred while putting thread to sleep. Error is: " + e.getMessage());
                }//end try...catch
                // the animated periods array need to be set back to zero once threshold is reached
                if(animatePos >= 8){
                    animatePos = 0;
                }else{
                    animatePos++;
                }//end if
            }else{
                break;
            }//end if
        }//end while
        updateSplash.close();//close the resources for the splash.
    }

    /**
     * This will animate and draw the string to the screen for user to see.
     * @param graphics the graphics object used to modify what the user sees.
     * @param animatePos the position of the element in the periods array that will display to the user.
     */
    private static void animateSplashScreen(Graphics2D graphics, int animatePos) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(80,392,408,20);
        graphics.setPaintMode();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.BOLD, 11));
        graphics.drawString(DISPLAY_MESSAGE + ANIMATE[animatePos], 80, 402);
    }

    /**
     * This method will check to see if the updatestatus.dat file contains the FINISHED key word.
     * @return true or false based whether update is finished or not.
     */
    private static boolean updateFinished() {
        String line = null;
        BufferedReader br = null;
        boolean finished = false;
        try{
            br = new BufferedReader(new FileReader(Paths.get(START_IN, "splash/updatestatus.dat").toFile()));
            line = br.readLine();
        }catch(FileNotFoundException e){
            System.err.println("FileNotFoundException occurred while trying to read the updatestatus.dat file. Error is: " + e.getMessage());
        }catch(IOException e){
            System.err.println("IOException occurred while trying to read the updatestatus.dat file. Error is: " + e.getMessage());
        }finally{
            if(br != null){
                try{
                    br.close();
                }catch(IOException e){
                    System.err.println("IOException occurred while trying to close the reader. Error is: " + e.getMessage());
                }//end try...catch
            }//end if
        }//end try...catch

        if(line != null && "FINISHED".equals(line.trim())){
            finished = true;
        }//end if
        return finished;
    }//end method

}//end class
