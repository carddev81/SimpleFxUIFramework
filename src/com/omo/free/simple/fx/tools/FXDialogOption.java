package com.omo.free.simple.fx.tools;

import java.util.Optional;
import java.util.logging.Logger;

import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.util.AppUtil;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * The {@code FXDialogOption} makes it easy to pop up a standard {@code Dialog} that prompts users for a value or informs them of something.
 * <p>
 * The uses of the {@code FXDialogOption} class are one-line calls to one of the static {@code dialogPaneSingleButton}, {@code dialogPaneMultiButton} methods.
 * <p>
 * All dialogs are modal. Each method blocks the caller until the user's interaction is complete.
 * </p>
 * <table width="75%" border="1">
 * <tr>
 * <td><b>Parameters:</b></td>
 * <td>The parameters to these methods follow consistent patterns and explanation is below.</td>
 * </tr>
 * <tr>
 * <td><b>owner</b></td>
 * <td>Defines the {@code Window} that is to be the parent of this dialog pane. It is used in two ways: the {@code Stage} that contains it is used as the {@code Stage} parent for the dialog pane, and its screen coordinates are used in the placement of the dialog pane. In general, the dialog pane is placed just below the component. This parameter may be <code>null</code>, in which case the dialog will be centered on the screen.</td>
 * </tr>
 * <tr>
 * <td><b>message</b></td>
 * <td>A descriptive message to be placed in the dialog pane. In the most common usage, message is just a {@code String}. Another message type can be a <code>List{@literal<String>}</code> of strings that is used for messages.</td>
 * </tr>
 * <tr>
 * <td><b>messagefontsize</b></td>
 * <td>The font size for the message text.</td>
 * </tr>
 * <tr>
 * <td><b>title</b></td>
 * <td>The title for the dialog pane.</td>
 * </tr>
 * <tr>
 * <td><b>headerText</b></td>
 * <td>The header text for the dialog pane.</td>
 * </tr>
 * <tr>
 * <td><b>headerTextColor</b></td>
 * <td>The Color of the header text.</td>
 * </tr>
 * <tr>
 * <td><b>headerfontweight</b></td>
 * <td>The Fontweight.</td>
 * </tr>
 * <tr>
 * <td><b>headerfontsize</b></td>
 * <td>The int fontsize.</td>
 * </tr>
 * <tr>
 * <td><b>imageUrl</b></td>
 * <td>The url for the image to display(.BMP,.GIF,.JPEG,.PNG).</td>
 * </tr>
 * <caption>descriptions</caption>
 * </table>
 * <h1>Example:</h1>
 * <h1>Show a dialog box that displays image and one button:</h1>
 * 
 * <pre>
 * <code>
 *  FXDialogOption.dialogPaneSingleButton(SFXViewBuilder.getPrimaryStage(), "message", "title", "headerText" ,"/com/omo/free/installer/resources/images/img3.png", "buttonText");
 * </code>
 * </pre>
 *
 * <h1>Example:</h1>
 * <h1>Show a dialog box that displays no image and has two buttons (using Optional&lt;ButtonType&gt; result)</h1>
 * 
 * <pre>
 * <code>
 *  Optional&lt;ButtonType&gt; result = FXDialogOption.dialogPaneMultiButton(SFXViewBuilder.getPrimaryStage(), "message",15, "title", "headerText" ,Color.BLUE,FontWeight.NORMAL,20, "buttonText","button2Text");
 * </code>
 * </pre>
 *
 * <h1>Example:result</h1>
 * <h1>if statement' to catch result if either button was pressed</h1>
 * 
 * <pre>
 * <code>
    if(result.isPresent() &amp;&amp; result.get().getButtonData() == ButtonData.OK_DONE){
        //do something here if firstButton was pressed
    }//end if

    if(result.isPresent() &amp;&amp; result.get().getButtonData() == ButtonData.CANCEL_CLOSE){
        //do something here if secondButton was pressed
    }//end if
 * </code>
 * </pre>
 * 
 * Modifications:
 * <ul>
 * <li>For each {@code ButtonType} added to each {@code DialogPane#getButtonTypes()} called {@link FXUtil#makeEnterKeyFireButtonAction(Button)}. The {@code ButtonType} initialized with {@link ButtonData#CANCEL_CLOSE} was not firing on Enter. - jal000is</li>
 * </ul>
 * 
 * @author Charles Craft
 */
public class FXDialogOption {
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.FXDialogOption";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and no image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param buttonText
     *        the text on the button
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneSingleButton(Window owner, String message, String title, String headerText, String buttonText) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneSingleButton()", new Object[]{owner, message, title, headerText, buttonText});
        // dialog 1 button no image
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
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
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneSingleButton()");
        return dialog.showAndWait();
    }// end dialogPaneSingleButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and no image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).Editable fonts.
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param messagefontsize
     *        the font size for the message text
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param headerTextColor
     *        the Color.color of the header text
     * @param headerfontweight
     *        the Fontweight
     * @param headerfontsize
     *        the int fontsize
     * @param buttonText
     *        the text on the button
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneSingleButton(Window owner, String message, int messagefontsize, String title, String headerText, Color headerTextColor, FontWeight headerfontweight, int headerfontsize, String buttonText) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneSingleButton()", new Object[]{owner, message, messagefontsize, title, headerText, headerTextColor, headerfontweight, headerfontsize, buttonText});
        // dialog 1 button no image and changeable font size
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
        Label content = new Label(message);
        content.setFont(Font.font("Tahoma", messagefontsize));
        VBox textContent = new VBox();
        if(AppUtil.isNullOrEmpty(headerText)){
            textContent.getChildren().add(content);
        }else{
            Label header = new Label(headerText);
            header.setTextFill(headerTextColor);
            header.setFont(Font.font("Tahoma", headerfontweight, headerfontsize));
            textContent.getChildren().addAll(header, content);
        }// end if
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneSingleButton()");
        return dialog.showAndWait();
    }// end dialogPaneSingleButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and an image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param imageUrl
     *        the url for the image to display
     * @param buttonText
     *        the text on the button
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneSingleButton(Window owner, String message, String title, String headerText, String imageUrl, String buttonText) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneSingleButton()", new Object[]{owner, message, title, headerText, imageUrl, buttonText});
        // dialog 1 button with image
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // creating the image here to be used as the graphic
        ImageView image = new ImageView();
        image.setImage(new Image(imageUrl));

        // Set the image (must be included in the project).
        dialog.setGraphic(image);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
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
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneSingleButton()");
        return dialog.showAndWait();
    }// end dialogPaneSingleButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and an image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).Editable fonts.
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param messagefontsize
     *        the font size for the message text
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param headerTextColor
     *        the Color.color of the header text
     * @param headerfontweight
     *        the Fontweight
     * @param headerfontsize
     *        the int fontsize
     * @param imageUrl
     *        the url for the image to display
     * @param buttonText
     *        the text on the button
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneSingleButton(Window owner, String message, int messagefontsize, String title, String headerText, Color headerTextColor, FontWeight headerfontweight, int headerfontsize, String imageUrl, String buttonText) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneSingleButton()", new Object[]{owner, message, messagefontsize, title, headerText, headerTextColor, headerfontweight, headerfontsize, imageUrl, buttonText});
        // dialog 1 button with image and changeable font size
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // creating the image here to be used as the graphic
        ImageView image = new ImageView();
        image.setImage(new Image(imageUrl));

        // Set the image (must be included in the project).
        dialog.setGraphic(image);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
        Label content = new Label(message);
        content.setFont(Font.font("Tahoma", messagefontsize));
        VBox textContent = new VBox();
        if(AppUtil.isNullOrEmpty(headerText)){
            textContent.getChildren().add(content);
        }else{
            Label header = new Label(headerText);
            header.setTextFill(headerTextColor);
            header.setFont(Font.font("Tahoma", headerfontweight, headerfontsize));
            textContent.getChildren().addAll(header, content);
        }// end if
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneSingleButton()");
        return dialog.showAndWait();
    }// end dialogPaneSingleButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and no image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param buttonText
     *        the text on the button
     * @param button2Text
     *        the text on the button2
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneMultiButton(Window owner, String message, String title, String headerText, String buttonText, String button2Text) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneMultiButton()", new Object[]{owner, message, title, headerText, buttonText, button2Text});
        // dialog box 2 buttons no image
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        ButtonType secondButton = new ButtonType(button2Text, ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton, secondButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
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
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneMultiButton()");
        return dialog.showAndWait();
    }// end dialogPaneMultiButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and no image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).Editable fonts.
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param messagefontsize
     *        the font size for the message text
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param headerTextColor
     *        the Color.color of the header text
     * @param headerfontweight
     *        the Fontweight
     * @param headerfontsize
     *        the int fontsize
     * @param buttonText
     *        the text on the button
     * @param button2Text
     *        the text on the button2
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneMultiButton(Window owner, String message, int messagefontsize, String title, String headerText, Color headerTextColor, FontWeight headerfontweight, int headerfontsize, String buttonText, String button2Text) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneMultiButton()", new Object[]{owner, message, messagefontsize, title, headerText, headerTextColor, headerfontweight, headerfontsize, buttonText, button2Text});
        // dialog box 2 buttons NO image changeable font
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        ButtonType secondButton = new ButtonType(button2Text, ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton, secondButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
        Label content = new Label(message);
        content.setFont(Font.font("Tahoma", messagefontsize));
        VBox textContent = new VBox();
        if(AppUtil.isNullOrEmpty(headerText)){
            textContent.getChildren().add(content);
        }else{
            Label header = new Label(headerText);
            header.setTextFill(headerTextColor);
            header.setFont(Font.font("Tahoma", headerfontweight, headerfontsize));
            textContent.getChildren().addAll(header, content);
        }// end if
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneMultiButton()");
        return dialog.showAndWait();
    }// end dialogPaneMultiButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and an image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).Editable fonts.
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param messagefontsize
     *        the font size for the message text
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param headerTextColor
     *        the Color.color of the header text
     * @param headerfontweight
     *        the Fontweight
     * @param headerfontsize
     *        the int fontsize
     * @param imageUrl
     *        the url for the image to display
     * @param buttonText
     *        the text on the button
     * @param button2Text
     *        the text on the button2
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneMultiButton(Window owner, String message, int messagefontsize, String title, String headerText, Color headerTextColor, FontWeight headerfontweight, int headerfontsize, String imageUrl, String buttonText, String button2Text) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneMultiButton()", new Object[]{owner, message, messagefontsize, title, headerText, headerTextColor, headerfontweight, headerfontsize, imageUrl, buttonText, button2Text});
        // dialog box 2 buttons image changeable font
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // creating the image to use on the dialog box
        ImageView image = new ImageView();
        image.setImage(new Image(imageUrl));

        // Set the image (must be included in the project).
        dialog.setGraphic(image);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        ButtonType secondButton = new ButtonType(button2Text, ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton, secondButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
        Label content = new Label(message);
        content.setFont(Font.font("Tahoma", messagefontsize));
        VBox textContent = new VBox();
        if(AppUtil.isNullOrEmpty(headerText)){
            textContent.getChildren().add(content);
        }else{
            Label header = new Label(headerText);
            header.setTextFill(headerTextColor);
            header.setFont(Font.font("Tahoma", headerfontweight, headerfontsize));
            textContent.getChildren().addAll(header, content);
        }// end if
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneMultiButton()");
        return dialog.showAndWait();
    }// end dialogPaneMultiButton

    /**
     * This method will build and display a dialog pane displaying a message with the given parameters and an image. The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * <p>
     * The dialog pane will wait for the user response (in other words, brings up a blocking dialog, with the returned value the users input).
     * </p>
     *
     * @param owner
     *        determines the {@code Stage} in which the dialog is displayed; if null, the dialog will be centered on the screen
     * @param message
     *        the message to display
     * @param title
     *        the title to display in the title bar
     * @param headerText
     *        the text to display in the header of the dialog pane
     * @param imageUrl
     *        the url for the image to display
     * @param buttonText
     *        the text on the button
     * @param button2Text
     *        the text on the button2
     * @return An Optional that contains the result. Refer to the {@code javafx.scene.control.Dialog} class documentation for more detail.
     */
    public static Optional<ButtonType> dialogPaneMultiButton(Window owner, String message, String title, String headerText, String imageUrl, String buttonText, String button2Text) {
        myLogger.entering(MY_CLASS_NAME, "dialogPaneMultiButton()", new Object[]{owner, message, title, headerText, imageUrl, buttonText, button2Text});
        // dialog 2 buttons image
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        // creating the image to use on the dialog box
        ImageView image = new ImageView();
        image.setImage(new Image(imageUrl));

        // Set the image (must be included in the project).
        dialog.setGraphic(image);
        // Set the button types.
        ButtonType firstButton = new ButtonType(buttonText, ButtonData.OK_DONE);
        ButtonType secondButton = new ButtonType(button2Text, ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(firstButton, secondButton);
        enableKeyEventsOnButtonTypes(dialog.getDialogPane());
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
        dialog.getDialogPane().setContent(textContent);
        myLogger.exiting(MY_CLASS_NAME, "dialogPaneMultiButton()");
        return dialog.showAndWait();
    }// end dialogPaneMultiButton

    /**
     * Method for ensuring all DialogPane ButtonType instances work as expected when Enter is used to fire the button.
     * 
     * @param buttons
     *        the ObservableList<ButtonType> returned from {@link DialogPane#getButtonTypes()}.
     */
    private static void enableKeyEventsOnButtonTypes(DialogPane dialogPane) {
        myLogger.entering(MY_CLASS_NAME, "enableKeyEventsOnButtonTypes", dialogPane);
        // mlp000is need to make button fire with key event
        // these ButtonType instances do not have the same behavior as ButtonBase
        Button button;
        if(null != dialogPane && null != dialogPane.getButtonTypes()){
            for(ButtonType type : dialogPane.getButtonTypes()){
                button = (Button) dialogPane.lookupButton(type);
                FXUtil.makeEnterKeyFireButtonAction(button);
            }// end for
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "enableKeyEventsOnButtonTypes()");
    }// end enableKeyEventsOnButtonTypes

}// end class
