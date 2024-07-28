/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.omo.free.simple.fx.application.SFXViewBuilder;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.util.AppUtil;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The {@code FXAlertOption} makes it easy to pop up a standard {@code Dialog} that prompts users for a value or informs them of something.
 *
 * <p>The uses of the {@code FXAlertOption} class are one-line calls to one of the static {@code showAlert}, {@code showErrorAlertWithEmailButton}
 * and {@code showBoldAlert} methods.</p>
 *
 * <p>All dialogs are modal. Each method blocks the caller until the user's interaction is complete.</p>

 * <p><b>Parameters:</b><br>
 * The parameters to these methods follow consistent patterns and explanation is below.
 * <blockquote>
 *
 * <dl compact>
 * <dt>owner<dd>
 * Defines the {@code Window} that is to be the parent of this dialog pane.  It is used in two ways: the {@code Stage} that contains
 * it is used as the {@code Stage} parent for the dialog pane, and its screen coordinates are used in the placement of the dialog pane.
 * In general, the dialog pane is placed just below the component. This parameter may be <code>null</code>, in which case the dialog will
 * be centered on the screen.
 *
 * <dt>message<dd>
 * A descriptive message to be placed in the dialog pane.  In the most common usage, message is just a {@code String}. Another message type can be
 * a <code>List{@literal<String>}</code> of strings that is used for messages.
 *
 * <dt>title<dd>
 * The title for the dialog pane.
 *
 * <dt>headerText<dd>
 * The header text for the dialog pane.
 *
 * <dt>alertType<dd>
 * Defines the alert types that the Alert class can use to pre-populate various properties. The {@code AlertType}
 * possible values are:
 * <ul>
 * <li><code>CONFIRMATION</code>
 * <li><code>ERROR</code>
 * <li><code>INFORMATION</code>
 * <li><code>NONE</code>
 * <li><code>WARNING</code>
 * </ul>
 * <dt>icon<dd>
 * The decorative icon image to be placed in the title bar of dialog pane. If this value is not passed in to the method then
 * the primary Stage's icon will be used.
 *
 * </dl>
 * </blockquote>
 *
 * <p>
 * <b>Example:</b>
 * <dl>
 * <dt>Show an error alert that displays the message, 'alert':<dd>
 * <code>
 *      FXAlertOption.showAlert(null, "alert", "alert", null, AlertType.ERROR);
 * </code>
 * </dl>
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class FXAlertOption {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.FXAlertOption";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Creates an instance of the FXAlertOption class.
     */
    private FXAlertOption() {}//end constructor

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message the message to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane
     * @param alertType the type of Alert to be displayed: CONFIRMATION, ERROR, INFORMATION, WARNING
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> showAlert(Window owner, String message, String title, String headerText, AlertType alertType) {
        myLogger.entering(MY_CLASS_NAME, "showAlert()", new Object[]{owner, message, title, headerText, alertType});
        Alert alert = new Alert((alertType != null ? alertType : AlertType.NONE));
        alert.initOwner(owner);
        alert.setTitle(String.valueOf(title));
        alert.setHeaderText(headerText);

        //building the content box here
        VBox content = new VBox();
        Label contentLabel = new Label(message);
        content.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(content);

        //setting the icon to use for the alert
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image p = SFXViewBuilder.getPrimaryStage().getIcons().size() > 0 ? SFXViewBuilder.getPrimaryStage().getIcons().get(0) : new Image(Constants.DEFAULT_SIMPLE_FX_ICON);
        stage.getIcons().add(p);
        myLogger.exiting(MY_CLASS_NAME, "showAlert()");
        return alert.showAndWait();
    }// end method

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message the message to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane
     * @param alertType the type of Alert to be displayed: CONFIRMATION, ERROR, INFORMATION, WARNING
     * @param icon decorative icon image to be placed in the title bar of dialog pane
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> showAlert(Window owner, String message, String title, String headerText, AlertType alertType, Image icon) {
        myLogger.entering(MY_CLASS_NAME, "showAlert()", new Object[]{owner, message, title, headerText, alertType, icon});
        Alert alert = new Alert((alertType != null ? alertType : AlertType.NONE));
        alert.initOwner(owner);
        alert.setTitle(String.valueOf(title));
        alert.setHeaderText(headerText);
        VBox content = new VBox();
        Label contentLabel = new Label(message);
        content.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        if(icon != null){
            stage.getIcons().add(icon);
        }else{
            // trying to get the primary window's icon first
            Image p = SFXViewBuilder.getPrimaryStage().getIcons().size() > 0 ? SFXViewBuilder.getPrimaryStage().getIcons().get(0) : new Image(Constants.DEFAULT_SIMPLE_FX_ICON);
            stage.getIcons().add(p);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "showAlert()");
        return alert.showAndWait();
    }// end method

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param messages the list of messages to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane
     * @param alertType the type of Alert to be displayed: CONFIRMATION, ERROR, INFORMATION, WARNING
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> showAlert(Window owner, List<String> messages, String title, String headerText, AlertType alertType) {
        myLogger.entering(MY_CLASS_NAME, "showAlert()", new Object[]{owner, messages, title, headerText, alertType});
        Alert alert = new Alert((alertType != null ? alertType : AlertType.NONE));
        alert.setTitle(String.valueOf(title));
        alert.setHeaderText(headerText);
        alert.initOwner(owner);

        //make sure that the messages are not empty
        if(AppUtil.isEmpty(messages)){
            alert.setContentText("No Messages To Display!");
        }else{
            //loop through and build the message.
            StringBuffer sb = new StringBuffer();
            for(Iterator<String> it = messages.iterator();it.hasNext();){
                sb.append(it.next());
                if(it.hasNext()){
                    sb.append(Constants.LINESEPERATOR);
                }// end if
            }// end for
            VBox content = new VBox();
            Label contentLabel = new Label(sb.toString());
            content.getChildren().add(contentLabel);
            alert.getDialogPane().setContent(content);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "showAlert()");
        return alert.showAndWait();
    }// end method


    /**
     * This method will build and display a custom dialog pane displaying an error alert message along with an email button to utilize.
     * <p>The email button once pressed will send an email to Administrators letting them know of the issue and will also
     * attempt to attach a copy of the JavaFX Application log to the email if it is not to large.</p>
     *
     * <p><b>Note To Developer</b></p>
     * <p>The email list that this Alert window uses for sending emails is located within the application.properties file.
     * The property name is <b>email.bugs.to</b></p>
     *
     * <p>The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).</p>
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message the list of messages to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane [NOTE: this header text will be colored red and in bold]
     */
    public static void showErrorAlertWithEmailButton(Window owner, String message, String title, String headerText) {
        myLogger.entering(MY_CLASS_NAME, "showErrorAlertWithEmailButton()", new Object[]{owner, message, title, headerText});
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        //creating the bug icon here to be used as the graphic
        ImageView image = new ImageView();
        image.setImage(new Image("/com/omo/free/simple/fx/resources/Bugged.png"));

        // Set the icon (must be included in the project).
        dialog.setGraphic(image);

        // Set the button types.
        ButtonType sendEmailButtonType = new ButtonType("Send Email To Developer", ButtonData.OK_DONE);
        ButtonType okButtonButtonType = new ButtonType("OK", ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(okButtonButtonType, sendEmailButtonType);

        Label content = new Label(message);
        content.setFont(Font.font("Tahoma", 12));
        VBox textContent = new VBox();
        if(AppUtil.isNullOrEmpty(headerText)){
            textContent.getChildren().add(content);
        }else{
            Label header = new Label(headerText);
            header.setTextFill(Color.RED);
            header.setFont(Font.font("Tahoma", FontWeight.BOLD, 15));
            textContent.getChildren().addAll(header, content);
        }// end if
         // VBox textContent = new VBox(label);
        dialog.getDialogPane().setContent(textContent);
        Optional<ButtonType> result = dialog.showAndWait();
        if(sendEmailButtonType == result.get()){
            Platform.runLater(() -> sendEmail(owner, headerText, message));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "showErrorAlertWithEmailButton()");
    }// end method

    /**
     * This method will send an email to administrators with the given parameters.
     *
     * <p><b>Note To Developer</b></p>
     * <p>The email list that this Alert window uses for sending email is located within the application.properties file.
     * The property name is <b>email.bugs.to</b></p>
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param headerText the text to display in the header of the dialog pane
     * @param message the list of messages to display
     */
    private static void sendEmail(Window owner, String headerText, String message) {
        String emailTo = PropertiesMgr.getInstance().getProperties().getProperty("email.bugs.to");
        StringBuffer emailMsg = new StringBuffer("<p>Please forward this email to a developer at the JCCC Shop. This error message may help him to fix the problem if it is code related.</p><p>Error is: <p><font color=\"red\">");
        emailMsg.append(headerText);
        emailMsg.append(message.replaceAll(Constants.LINESEPERATOR, "<br/>")).append("</font></p>");
        try{
            String props = LogManager.getLogManager().getProperty("java.util.logging.FileHandler.pattern");
            if(props != null){
                File log = new File(props.replaceAll("(%[a-z]{1})", "0"));
                if(log.length() > 5000000){
                    emailMsg.append("<p>The " + log.getName() + " file containing the exceptions is too large to send through an email.</p><p>Thank you and have a nice day.</p>");
                    SendMail.send(emailTo, emailMsg.toString(), "APPLICATION ERROR", null);
                }else if(log.length() > 0){
                    emailMsg.append("<p>Please check the attached log file " + log.getName() + ", which contains the logged exceptions that the developer may need to fix the issues.</p><p>Thank you and have a nice day.</p>");
                    SendMail.send(emailTo, emailMsg.toString(), "APPLICATION ERROR", new File[]{log});
                }else{
                    emailMsg.append("<p>Could not attach log file which contains logged exceptions. I hope the above error message will be sufficient.</p><p>Thank you and have a nice day.</p>");
                    SendMail.send(emailTo, emailMsg.toString(), "APPLICATION ERROR", null);
                }// end if/else
            }else{
                emailMsg.append("<p>Could not attach log file which contains logged exceptions. I hope the above error message will be sufficient.</p><p>Thank you and have a nice day.</p>");
                SendMail.send(emailTo, emailMsg.toString(), "APPLICATION ERROR", null);
            }// end if
            showAlert(owner, "Successfully Sent Email To " + String.valueOf(emailTo), "Email Sent", null, AlertType.INFORMATION);
        }catch(Exception e){
            // myLogger.log(Level.SEVERE, "EmailException occurred while trying to send developer an email. Error is: " + e.getMessage(), e);
            showAlert(owner, "Error Sending Email To " + String.valueOf(emailTo), "Error Sending Email", null, AlertType.ERROR);
        }// end try...catch
    }

    /**
     * This method will build and display a dialog pane displaying a message in bold with the given parameters. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message the message to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane
     * @param alertType the type of Alert to be displayed: CONFIRMATION, ERROR, INFORMATION, WARNING
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> showBoldAlert(Window owner, String message, String title, String headerText, AlertType alertType) {
        myLogger.entering(MY_CLASS_NAME, "showBoldAlert()", new Object[]{owner, message, title, headerText, alertType});
        Alert alert = new Alert((alertType != null ? alertType : AlertType.NONE));
        alert.initOwner(owner);
        alert.setTitle(String.valueOf(title));
        alert.setHeaderText(headerText);
        VBox content = new VBox();
        Label contentLabel = new Label(message);
        content.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setStyle("-fx-font-weight: bold");
        myLogger.exiting(MY_CLASS_NAME, "showBoldAlert()");
        return alert.showAndWait();
    }// end method

    /**
     * This method will build and display a dialog pane displaying a message in bold with the given parameters. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     *
     * @param owner determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message the message to display
     * @param title the title to display in the title bar
     * @param headerText the text to display in the header of the dialog pane
     * @param alertType the type of Alert to be displayed: CONFIRMATION, ERROR, INFORMATION, WARNING
     * @param icon decorative icon image to be placed in the title bar of dialog pane
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> showBoldAlert(Window owner, String message, String title, String headerText, AlertType alertType, Image icon) {
        myLogger.entering(MY_CLASS_NAME, "showBoldAlert()", new Object[]{owner, message, title, headerText, alertType, icon});
        Alert alert = new Alert((alertType != null ? alertType : AlertType.NONE));
        alert.initOwner(owner);
        alert.setTitle(String.valueOf(title));
        alert.setHeaderText(headerText);
        VBox content = new VBox();
        Label contentLabel = new Label(message);
        content.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        if(icon != null){
            stage.getIcons().add(icon);
        }else{
            // trying to get the primary window's icon first
            Image p = SFXViewBuilder.getPrimaryStage().getIcons().size() > 0 ? SFXViewBuilder.getPrimaryStage().getIcons().get(0) : new Image(Constants.DEFAULT_SIMPLE_FX_ICON);
            stage.getIcons().add(p);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "showBoldAlert()");
        return alert.showAndWait();
    }// end method

}//end class