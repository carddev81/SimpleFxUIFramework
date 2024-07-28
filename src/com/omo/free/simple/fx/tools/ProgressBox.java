/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.util.logging.Logger;

import com.omo.free.util.AppUtil;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * The ProgressBox class when instantiated will display a custom window with a {@code ProgressBar}
 * which displays the progress of the currently running process.  <b>You must call the {@link ProgressBox#displayProgressBox()} method
 * in order for the progress box to display.</b>
 *
 * <p>The most common use of this class is when you have an application that contains a
 * long-running process in the background and you need to display progress information
 * to user so that they do not think that the application is hung up or in some sort of
 * unknown state.</p>
 *
 * <p>It is a good idea to use the {@link Platform} class method named {@code runLater(Runnable runnable)} that you should use
 *  to display the progress box for a smooth transition.  The issue you may run into is a screen flicker which is caused by the JavaFX Application Thread
 *  trying to do too much work.</p>
 *
 * <p><b>ProgressBox Example</b></p>
 * <pre><code>
 *
.
.
.
ProgressBox progress = new ProgressBox(getPrimaryStage(), "Example Process Running", "Loading the stats...");
Task{@literal <Void>} task = new Task{@literal<Void>}(){
    {@literal @Override} protected Void call() throws Exception {
        updateMessage(progress.progressMessageProperty().get());
        int i = 0;
        while(i &lt; 4){
            try{
                updateMessage("Currently calculating " + messages[i]);
                updateProgress(0.25 * (i + 1), 1.0);
                TimeUnit.SECONDS.sleep(2);
            }catch(InterruptedException e){
                e.printStackTrace();
            }//end...try...catch
            i++;
        }//end while
        return null;
    }//end method
};
task.setOnSucceeded(e -&gt; progress.close());
progress.progressMessageProperty().bind(task.messageProperty());
progress.progressProperty().bind(task.progressProperty());
Platform.runLater(() -&gt; progress.displayProgressBox());
Thread thread = new Thread(task);
thread.start();
.
.
.
 * </code></pre>
 * <p>Note that a {@code ProgressBox}'s progress and message properties are updated through the methods
 * {@link ProgressBox#progressMessageProperty()} and {@link ProgressBox#progressProperty()} and should
 * only be updated on the Java FX Application Thread. Refer to the {@code javafx.concurrent} package
 * documentation that is found in the JavaFX API Documentation.</p>
 *
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class ProgressBox {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.ProgressBox";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private Stage stage;
    private String title;
    private ProgressBar progressBar = new ProgressBar();
    private Label messageLabel;
    private ChangeListener<Number> xListener;
    private ChangeListener<Number> yListener;

    /**
     * Creates a ProgressBox with a given {@code Window} owner, title, and initial progress message.
     * @param owner the parent window that the ProgressBox is a part of
     * @param title the title of the {@code ProgressBox}
     * @param progressMessage the initial message to display within the the {@code ProgressBox}
     */
    public ProgressBox(Window owner, String title, String progressMessage) {
        myLogger.entering(MY_CLASS_NAME, "ProgressBox()", new Object[]{owner, title, progressMessage});
        this.stage = new Stage(StageStyle.UNDECORATED);
        this.stage.initOwner(owner);
        if(AppUtil.isNullOrEmpty(title)){//default text
            this.title = "Currently Processing";
        }else{
            this.title = title;
        }//end if

        if(AppUtil.isNullOrEmpty(progressMessage)){
            messageLabel = new Label("Currently executing tasks ");
        }else{
            messageLabel = new Label(progressMessage);
        }
        myLogger.exiting(MY_CLASS_NAME, "ProgressBox()");
    }//end progress box

    /**
     * This method will attach {@code ChangeListener}s to the x and y properties for keeping the location of the {@code ProgressBox} in the center of it's parent.
     */
    private void attachListeners() {
        myLogger.entering(MY_CLASS_NAME, "attachListeners()");
        centerOnParent();
        Window owner = stage.getOwner();

        xListener = (ob, oldV, newV) -> centerOnParent();
        yListener = (ob, oldV, newV) -> centerOnParent();

        owner.xProperty().addListener(xListener);
        owner.yProperty().addListener(yListener);
        myLogger.exiting(MY_CLASS_NAME, "attachListeners()");
    }//end listener

    /**
     * This method will center the ProgressBox on top of the parent window.
     */
    private void centerOnParent() {
        myLogger.entering(MY_CLASS_NAME, "centerOnParent()");
        Window owner = stage.getOwner();
        double y = owner.getY() + (owner.getHeight() - stage.getHeight()) / 2.0;
        double x = owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0;
        stage.setY(y);
        stage.setX(x);
        stage.toFront();
        myLogger.exiting(MY_CLASS_NAME, "centerOnParent()");
    }//end method

    /**
     * This method will build and layout the ProgressBox nodes and then display to the user.
     */
    private void layoutProgressBox() {
        myLogger.entering(MY_CLASS_NAME, "layoutProgressBox()");
        BorderPane bLayout = new BorderPane();

        HBox topPane = new HBox(20);
        topPane.setPrefSize(500, 50);
        topPane.setStyle("-fx-background-color: BLANCHEDALMOND;");

        Label text = new Label(title);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        text.setEffect(new Reflection());

        ImageView image = new ImageView("/com/omo/free/simple/fx/resources/SimpleFXIconv2(67x41).png");

        topPane.getChildren().addAll(image, text);
        topPane.setAlignment(Pos.CENTER_LEFT);
        VBox progressVBox = new VBox(10);
        progressVBox.setPadding(new Insets(10));
        progressVBox.setAlignment(Pos.CENTER_LEFT);
        progressBar.setMaxWidth(topPane.getPrefWidth());
        progressVBox.getChildren().addAll(messageLabel, progressBar);
        bLayout.setTop(topPane);
        bLayout.setBottom(progressVBox);
        bLayout.setStyle("-fx-border-color: black;");

        Scene scene = new Scene(bLayout);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        myLogger.exiting(MY_CLASS_NAME, "layoutProgressBox()");
    }//end method

    /**
     * This method will display the {@code ProgressBox} to the user on screen and must be called in order to see the progress bar.
     */
    public void displayProgressBox(){
        layoutProgressBox();
        attachListeners();
    }//end method

    /**
     * This method returns the {@code ProgressBar}'s {@code progressProperty} which is used for updating the progress bar.
     *
     * <p>Note that you can use JavaFX binding to update this property</p>
     *
     * @return the javafx javabean progress property
     */
    public DoubleProperty progressProperty(){
        return progressBar.progressProperty();
    }//end method

    /**
     * This method returns the {@code ProgressBox} Label's {@code textProperty} which is used to let user know the
     * progress information.
     *
     * <p>Note that you can use JavaFX binding to update this property</p>
     *
     * @return the javafx javabean text property
     */
    public StringProperty progressMessageProperty(){
        return messageLabel.textProperty();
    }//end method

    /**
     * This method is used to close the ProgressBox.
     */
    public void close(){
        myLogger.entering(MY_CLASS_NAME, "close()");
        Window owner = stage.getOwner();
        owner.xProperty().removeListener(xListener);
        owner.yProperty().removeListener(yListener);
        stage.close();
        myLogger.exiting(MY_CLASS_NAME, "close()");
    }//end method

    /**
     * Sets the value of the property progress.
     *
     * @param value the actual progress of the ProgressIndicator. A negative value for progress
     * indicates that the progress is indeterminate. A positive value between 0 and 1 indicates
     * the percentage of progress where 0 is 0% and 1 is 100%. Any value greater than 1 is interpreted
     * as 100%.
     */
    public void setProgress(double value){
        progressBar.setProgress(value);
    }//end method

}//end class
