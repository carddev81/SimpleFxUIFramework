package com.omo.free.simple.fx.application;

import static com.omo.free.simple.fx.util.Constants.DEFAULT_SIMPLE_FX_ICON;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The SFXApplicationLauncher class extends the {@code javafx.application.Application} class. The
 * SFXApplicationLauncher class is the main entry point for JavaFX Applications implementing the SimpleFX Framework.
 *
 * <p>The SFXApplicationLauncher class is used to launch all JavaFX Applications.</p>
 *
 * @author Richard Salas JCCC
 * @author Robert Backus
 * @version 1.0
 * @see SFXApplication
 * @see SFXViewBuilder
 * @see com.omo.free.simple.fx.managers.UIPropertiesMgr
 */
public final class SFXApplicationLauncher extends Application {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.SFXApplicationLauncher";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    static Class<?> fxBuilder;
    private Properties uiProps;
    private SFXViewBuilder view;

    /**
     * Constructs a SFXApplicationLauncher instance.
     */
    public SFXApplicationLauncher() {
        super();
    }//end constructor

    /**
     * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>NOTE: This method is called on the JavaFX Application Thread.</p>
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet. Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     * @exception Exception during the creation process of the {@code Stage} and {@code Scene} graph
     */
    @Override public void start(Stage primaryStage) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "start", primaryStage);
        try{
            if(Constants.IS_JAR && SFXApplication.getSFXApplicationClass() instanceof Refactorable){//checking to see if this application needs to be updated
                myLogger.fine("checking to see if this application needs to be updated.");
                Refactorable refactored = (Refactorable) SFXApplication.getSFXApplicationClass();
                new ApplicationRefactor().checkForRefactoredApplication(refactored);
            }//end if

            if(Constants.IS_JAR){
                myLogger.fine("checking to see if this application needs to be updated.");
                new ApplicationUpdate().checkForNewerVersion(this.getClass());
            }// end if

            myLogger.fine("setting the primary stage static field witin the SFXViewBuilder.");
            SFXViewBuilder.setPrimaryStage(primaryStage);

            myLogger.fine("creating an instance of the SFXViewBuilder concrete implementaion.");
            view = (SFXViewBuilder) fxBuilder.newInstance();
            Scene mainScene = new Scene(view.getRootPane());
            primaryStage.setScene(mainScene);

            //attach the user style sheets to the scene
            attachUserStyleSheets(mainScene, view.getStyleSheets());

            Image titleIcon = null;
            try{
                titleIcon = new Image(view.getStageIconPath());
            }catch(Exception e){
                myLogger.log(Level.WARNING, "Your path to an icon is incorrect. An example path is /com/omo/free/simple/resources/icon.png. Current you set your path to " + String.valueOf(view.getStageIconPath()) + ". Error Message is: " + e.getMessage(), e);
                titleIcon = new Image(DEFAULT_SIMPLE_FX_ICON);
            }//end try...catch

            myLogger.log(Level.INFO, "Total number of screens: " + Screen.getScreens().size());

            myLogger.log(Level.INFO, "Searching through available displays to see if the application is inside the visual boundaries.");
            boolean inBoundsCheck = false;
            for(int i=0,j=Screen.getScreens().size();i<j;i++){
                inBoundsCheck = isScreenInBounds(Double.valueOf(uiProps.getProperty("window.location.x")), Double.valueOf(uiProps.getProperty("window.location.y")), Screen.getScreens().get(i).getVisualBounds());
                if(inBoundsCheck){
                    myLogger.log(Level.INFO, "The application position is inside the visual boundaries.");
                    break; // End the for loop early to avoid unnecessary calculations
                } // End If
            } // End For
            if(!inBoundsCheck){
                myLogger.log(Level.INFO, "The application is outside the visual boundaries. Resetting position.");
                uiProps.setProperty("window.location.x", "0"); // Set the min x coordinate to 0
                uiProps.setProperty("window.location.y", "0"); // Set the min y coordinate to 0
            } // End If

            primaryStage.getIcons().add(titleIcon);
            primaryStage.setTitle(String.valueOf(uiProps.getProperty("application.name")));
            primaryStage.setResizable(view.isResizable());
            primaryStage.initStyle(view.getStyle() == null ? StageStyle.DECORATED : view.getStyle());
            primaryStage.setX(Double.valueOf(uiProps.getProperty("window.location.x")));
            primaryStage.setY(Double.valueOf(uiProps.getProperty("window.location.y")));
            primaryStage.show();
            FXUtil.closeSplashScreen();
        }catch(Exception e){
            SFXDialogLauncher.exception = e;
            SFXDialogLauncher.DialogType.FRAMEWORK_EXCEPTION.showPopUp();
        }// end try....catch
        myLogger.exiting(MY_CLASS_NAME, "start");
    }// end method

    /**
     * This method will attach an array of style sheets to the GUI window if the given array is not null.
     *
     * @param mainScene the main scene to attach style sheets to.
     * @param styleSheets array of style sheet paths that are added to the list of style sheets.
     */
    private void attachUserStyleSheets(Scene mainScene, String[] styleSheets) {
        myLogger.entering(MY_CLASS_NAME, "attachUserStyleSheets", new Object[]{mainScene, styleSheets != null ? styleSheets.length : "NULL"});
        if(styleSheets!=null){
            mainScene.getStylesheets().addAll(styleSheets);
        }//end if
        myLogger.exiting(MY_CLASS_NAME, "attachUserStyleSheets");
    }//end method

    /**
     * The application initialization method. This method is called immediately after the Application class is loaded and constructed. An application may
     * override this method to perform initialization prior to the actual starting of the application.
     *
     * <p>The implementation of this method provided by the Application class does nothing.</p>
     *
     * <p>NOTE: This method is not called on the JavaFX Application Thread. An application must not construct a Scene or a Stage in this method.
     * An application may construct other JavaFX objects in this method.</p>
     */
    @Override public void init() {
        myLogger.entering(MY_CLASS_NAME, "init");
        uiProps = UIPropertiesMgr.getInstance().getProperties();
        myLogger.exiting(MY_CLASS_NAME, "init");
    }// end method

    /**
     * This method is called when the application should stop, and provides a convenient place to prepare for application exit and destroy resources.
     *
     * <p>The implementation of this method provided by the Application class saves the x and y locations of the gui window and then
     * makes a call on the {@link SFXViewBuilder}'s close method for saving simple fx gui properties.</p>
     *
     * <p>NOTE: This method is called on the JavaFX Application Thread.</p>
     */
    @Override public void stop() {
        myLogger.entering(MY_CLASS_NAME, "stop");
        try{
            Stage stage = SFXViewBuilder.getPrimaryStage();
            if(stage.isIconified()){
                myLogger.warning("Your application is minimized therefore not going to save its location at this time.");
            }else{
                myLogger.fine("saving window x and y coordinates");
                uiProps.replace("window.location.x", String.valueOf(stage.getX()));
                uiProps.replace("window.location.y", String.valueOf(stage.getY()));
            }//end if
            myLogger.fine("calling the SFXViewBuilders implementation of the close method and then calling the save method within an instance of the UIPropertiesMgr class");
            view.close();
            UIPropertiesMgr.getInstance().save();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred while trying to save gui settings within the simplefx.gui.properties. Error message is: " + e.getMessage(), e);
        }//end try..catch
        System.exit(0);
        myLogger.exiting(MY_CLASS_NAME, "stop");
    }//end method
    
    /**
     * This method is called before the application should open. It evaluates if the position of the app is within the boundaries of the current screen. 
     * This function is called for every display a user has unless the application was found within a display boundary. 
     * <p>The implementation of this method provided by the Application class does nothing. Should the application be determined to be out of bounds...</p>
     *
     * @param screenPosX the X coordinate of the top left most pixel of the ui app.
     * @param screenPosY the X coordinate of the top left most pixel of the ui app.
     * @param theScreen the current screen. This screen is the current viewable area being evaluated. 
     * @return true|false
     * <p>NOTE: This method is called on the JavaFX Application Thread.</p>
     */
    public boolean isScreenInBounds( Double screenPosX, Double screenPosY, Rectangle2D theScreen ) {
        myLogger.entering(MY_CLASS_NAME, "isScreenInBounds", new Object[]{screenPosX, screenPosY, theScreen});
        myLogger.log(Level.INFO, "Window MinX: " + screenPosX);
        myLogger.log(Level.INFO, "Window MinY: " + screenPosY);
        myLogger.log(Level.INFO, "Screen MinX: " + theScreen.getMinX());
        myLogger.log(Level.INFO, "Screen MinY: " + theScreen.getMinY());
        myLogger.log(Level.INFO, "Screen MaxX: " + theScreen.getMaxX());
        myLogger.log(Level.INFO, "Screen MaxY: " + theScreen.getMaxY());

        boolean isInBounds = screenPosX < theScreen.getMinX() || screenPosY < theScreen.getMinY() || screenPosX > theScreen.getMaxX() || screenPosY > theScreen.getMaxY() ? false : true;
        // If the UI app is outside of the screen isInBounds equals false, otherwise isInBounds equals true
        myLogger.exiting(MY_CLASS_NAME, "isScreenInBounds", isInBounds);
        return isInBounds;
    }// End method

}//end class
