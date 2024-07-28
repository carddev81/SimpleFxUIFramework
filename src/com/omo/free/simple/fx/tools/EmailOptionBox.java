/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.util.Iterator;
import java.util.logging.Logger;

import com.omo.free.simple.fx.application.SFXViewBuilder;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.util.Constants;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The EmailOptionBox class extends HBox and displays to the user a {@code CheckBox} labeled "Send Email" and a {@code Button} labeled "Show". If the {@code Button} labeled "Show" is pressed an EmailStage is displayed which holds a list of email addresses.
 * <p>
 * The below methods are of interest when using this class:
 * </p>
 * <ul>
 * <li>{@link #isSendEmailSelected()}</li>
 * <li>{@link #getEmailList()}</li>
 * <li>{@link #isValidEmailAddresses()}</li>
 * <li>{@link #selectedProperty()}</li>
 * </ul>
 * Modifications:
 * <ul>
 * <li>Added {@link Stage#initOwner(javafx.stage.Window)} method in {@link EmailStage} initialization. mlp000is - 09/2/2020</li>
 * <li>Added {@link Stage#setOnHiding(javafx.event.EventHandler)} handler for {@link SFXViewBuilder#getPrimaryStage()} to call overriden {@link EmailStage#close()}. mlp000is - 09/2/2020</li>
 * </ul>
 *
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class EmailOptionBox extends HBox {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.EmailOptionBox";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private Button showHideButton = new Button("Show");
    private CheckBox sendEmailCheckBox = new CheckBox("Send Email");
    private EmailStage emailStage;

    /**
     * Creates an instance of the {@code EmailOptionBox} with the given boolean value on whether or not the "Send Email" {@code CheckBox} is checked.
     *
     * @param selected
     *        {@code true} or {@code false} value which determines whether or not the email check box is selected
     */
    public EmailOptionBox(boolean selected) {
        this();
        myLogger.entering(MY_CLASS_NAME, "EmailOptionBox()", selected);
        sendEmailCheckBox.setSelected(selected);
        if(!selected){
            showHideButton.setDisable(true);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "EmailOptionBox()", selected);
    }// end constructor

    /**
     * Creates an instance of the {@code EmailOptionBox} with the "Send Email" {@code CheckBox} is enabled by default
     */
    public EmailOptionBox() {
        myLogger.entering(MY_CLASS_NAME, "EmailOptionBox()");
        layoutForm();
        attachListenersAndActions();
        emailStage = new EmailStage();
        bindFieldValues();
        myLogger.exiting(MY_CLASS_NAME, "EmailOptionBox()");
    }// end constructor

    /**
     * This method will layout the email options within the {@code HBox}.
     */
    private void layoutForm() {
        myLogger.entering(MY_CLASS_NAME, "layoutForm()");
        sendEmailCheckBox.setSelected(true);
        sendEmailCheckBox.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        showHideButton.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        // changed the min width on buttons so that they will display on large screen sizes
        showHideButton.setMinWidth(50);
        sendEmailCheckBox.setMinWidth(90);
        getChildren().add(sendEmailCheckBox);
        getChildren().add(showHideButton);
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        myLogger.exiting(MY_CLASS_NAME, "layoutForm()");
    }// end method

    /**
     * This method will initialize listeners/event handlers within the {@code sendEmailCheckBox} and {@code showHideButton}.
     */
    private void attachListenersAndActions() {
        myLogger.entering(MY_CLASS_NAME, "attachListenersAndActions() - initializes listeners on the email checkbox and the show/hide button");
        sendEmailCheckBox.selectedProperty().addListener(e -> showHideButton.setDisable(!sendEmailCheckBox.isSelected()));
        showHideButton.setOnAction(e -> showOrHideEmailStage(e));
        myLogger.exiting(MY_CLASS_NAME, "attachListenersAndActions()");
    }// end method

    /**
     * This method binds the disableProperty of the HBox with the disable property of the email address book.
     */
    private void bindFieldValues() {
        myLogger.entering(MY_CLASS_NAME, "bindFieldValues()");
        disableProperty().bindBidirectional(emailStage.getEmailBox().disableProperty());
        myLogger.exiting(MY_CLASS_NAME, "bindFieldValues()");
    }// end method

    /**
     * This method is used to show and hide the email address window.
     * 
     * @param e
     *        action event representing some type of action
     */
    private void showOrHideEmailStage(ActionEvent e) {
        myLogger.entering(MY_CLASS_NAME, "showOrHideEmailStage - displays/hides the email panel", e);
        if("Show".equals(showHideButton.getText())){
            showHideButton.setText("Hide");
            sendEmailCheckBox.setDisable(true);
            emailStage.getTextArea().requestFocus();
            emailStage.getTextArea().end();
            emailStage.addListener();
            emailStage.show();
        }else{
            showHideButton.setText("Show");
            sendEmailCheckBox.setDisable(false);
            emailStage.removeListener();
            // changed this to hide now since close just saves the properties
            emailStage.hide();
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "showOrHideEmailStage");
    }// end method

    /**
     * This method returns whether or not the send email checkbox is selected.
     *
     * @return {@code true} or {@code false} based on whether or not the send email checkbox is selected
     */
    public boolean isSendEmailSelected() {
        return sendEmailCheckBox.isSelected();
    }// end if

    /**
     * This method will validate the email addresses within the {@link EmailStage} and return a boolean value.
     * 
     * @return {@code true} or {@code false} on whether or not the email addresses are valid
     */
    public boolean isValidEmailAddresses() {
        myLogger.entering(MY_CLASS_NAME, "isValidEmailAddresses()");
        boolean errorsExist = emailStage.validateAllEmailMessages();
        if(errorsExist){
            if("Show".equals(showHideButton.getText())){
                showHideButton.fire();
            }// end if
            emailStage.getTextArea().requestFocus();
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "isValidEmailAddresses()");
        return !errorsExist;
    }// end method

    /**
     * This method will return the email list that exists within the {@link EmailStage}.
     * <p>
     * Note that before calling this method it is suggested that you call the {@link #isValidEmailAddresses()} method before making the call to this method.
     * </p>
     *
     * @return a comma separated list of email addresses
     */
    public String getEmailList() {
        myLogger.entering(MY_CLASS_NAME, "getEmailList()");
        String emailList = emailStage.getAddresses();
        if(emailList.endsWith(",")){
            emailList = emailList.substring(0, emailList.lastIndexOf(","));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "getEmailList()");
        return emailList;
    }// end method

    /**
     * This method will return the email check box {@code selectedProperty}.
     * <p>
     * This will give you the opportunity for property Binding
     * </p>
     * 
     * @return {@code BooleanProperty} is returned which holds either a {@code true} or {@code false} value
     * @see javafx.beans.binding.Binding
     */
    public BooleanProperty selectedProperty() {
        return sendEmailCheckBox.selectedProperty();
    }// end method

    /**
     * This EmailStage class is a custom undecorated window that will contain a list of email addresses and also allows the user to input the addresses.
     *
     * @author Richard Salas JCCC
     * @version 1.0
     */
    class EmailStage extends Stage {

        private static final String MY_INNER_CLASS_NAME = "com.omo.free.simple.fx.tools.EmailOptionBox.EmailStage";
        private Logger myInnerLogger = Logger.getLogger(MY_INNER_CLASS_NAME);

        private static final String EMAIL_TO_PROPERTY_NM = "email.box.addresses";

        Button resetButton = new Button("Reset Email List");
        Button validateButton = new Button("Validate Addresses");
        TextArea textArea = new TextArea();
        VBox emailListBox = new VBox();
        Stage primaryStage = SFXViewBuilder.getPrimaryStage();
        ChangeListener<Number> xPositionListener;
        ChangeListener<Number> yPositionListener;
        ObservableList<CharSequence> paragraphList;

        /**
         * Creates an instance of the EmailStage.
         */
        public EmailStage() {
            super(StageStyle.UNDECORATED);
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "EmailStage - constructor used to initialize an instance of the email window");
            initializeEmailStageFeatures();
            initializeEmailListFromProperties();
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "EmailStage");
        }// end constructor

        /**
         * This method will initialize the {@code EmailStage} features and {@code Node}s.
         */
        private void initializeEmailStageFeatures() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "initializeEmailStageFeatures - initializes the features within the email window such as the TextArea, Buttons, and Styles");
            // adding some positioning/spacing to the vbox
            emailListBox.setAlignment(Pos.CENTER);
            emailListBox.setSpacing(10);

            Font buttonFt = Font.font(null, FontWeight.BOLD, 12);

            // setting the preferred size of the textarea
            textArea.setPrefRowCount(10);
            textArea.setPrefColumnCount(15);
            textArea.setFont(Font.font("Tahoma", 14));
            textArea.setPromptText("Enter email addresses here");
            textArea.setTooltip(new Tooltip("Enter email addresses here"));
            textArea.setOnKeyReleased(this::handleKey);
            textArea.requestFocus();

            paragraphList = textArea.getParagraphs();

            resetButton.setFont(Font.font(null, FontWeight.BOLD, 12));
            resetButton.setTooltip(new Tooltip("Press to reset email list to default list"));
            resetButton.setOnAction(e -> textArea.setText(PropertiesMgr.getInstance().getProperties().getProperty("email.to").replaceAll(",", Constants.LINESEPERATOR)));
            validateButton.setFont(buttonFt);
            validateButton.setDisable(true);
            validateButton.setOnAction(e -> validateAllEmailMessages());
            emailListBox.getChildren().addAll(textArea, resetButton, validateButton);
            // mlp000is - Stage was not closing from an external close request
            // was not calling the overridden close method added the 2 lines below
            initOwner(primaryStage);
            setOnHiding(e -> close());
            // adding some inline styles to the vbox
            emailListBox.setStyle("-fx-padding: 10;-fx-border-style: solid;-fx-border-width: 2;-fx-border-insets: 0;-fx-border-radius: 5;-fx-border-color: black;-fx-background-radius:5;");
            Scene scene = new Scene(emailListBox);
            scene.getStylesheets().add("/com/omo/free/simple/fx/resources/emailoption.css");
            setScene(scene);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "initializeEmailStageFeatures");
        }// end method

        /**
         * This method will initialize the email address list which is extracted from the simplefx.gui.properties if it exists.
         * <p>
         * Note that this if the email.box.addresses property does not exist within the simplefx.gui.properties file then it will be added to the simplefx.gui.properties when the window closes.
         * </p>
         * 
         * @see UIPropertiesMgr
         */
        private void initializeEmailListFromProperties() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "initializeEmailListFromProperties()");
            String emailString = UIPropertiesMgr.getInstance().getProperties().getProperty(EMAIL_TO_PROPERTY_NM);
            if(emailString == null){
                emailString = PropertiesMgr.getInstance().getProperties().getProperty("email.to");
                UIPropertiesMgr.getInstance().getProperties().put(EMAIL_TO_PROPERTY_NM, emailString);
            }// end if
            emailString = emailString.replaceAll(",", Constants.LINESEPERATOR);
            textArea.setText(emailString);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "initializeEmailListFromProperties()");
        }// end method

        /**
         * This method will return all the addresses that exist within the {@code TextArea} of the {@code EmailStage}.
         * 
         * @return comma separated list of email addresses
         */
        public String getAddresses() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getAddresses()");
            StringBuffer commaSeparatedList = new StringBuffer("");
            Iterator<CharSequence> it = paragraphList.iterator();
            while(it.hasNext()){
                commaSeparatedList.append(String.valueOf(it.next()).trim());
                if(it.hasNext()){
                    commaSeparatedList.append(",");
                }// end if
            }// end while
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getAddresses()");
            return commaSeparatedList.toString();
        }// end if

        /**
         * This method will add x and y property listeners to the EmailStage used for positioning the Stage along side of the Parent Stage.
         */
        public void addListener() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "addListener()");
            double x = primaryStage.getX();
            double y = primaryStage.getY();
            double width = primaryStage.getWidth() + 2;
            setX(x + width);
            setY(y);
            // initializing the position listeners here
            xPositionListener = (property, oldV, newV) -> {
                setX(primaryStage.getX() + width + 2);
                toFront();
            };
            yPositionListener = (property, oldV, newV) -> {
                setY(primaryStage.getY());
                toFront();
            };
            primaryStage.xProperty().addListener(xPositionListener);
            primaryStage.yProperty().addListener(yPositionListener);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "addListener()");
        }// end method

        /**
         * This method removes the listeners off of the parent stage to release some resources and not to unnecessarily overwork the JVM.
         */
        public void removeListener() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "removeListener()");
            primaryStage.xProperty().removeListener(xPositionListener);
            primaryStage.yProperty().removeListener(yPositionListener);

            // null out the listeners here
            xPositionListener = null;
            yPositionListener = null;
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "removeListener()");
        }// end method

        /**
         * This method returns the {@code TextArea} which holds lists of email addresses.
         * 
         * @return the {@code TextArea} node
         */
        public TextArea getTextArea() {
            return textArea;
        }// end method

        /**
         * This method returns the emailListBox {@code Node}.
         * 
         * @return the email list box {@code Node}
         */
        public Node getEmailBox() {
            return emailListBox;
        }// end method

        /**
         * This method handles the key event when the space key or enter key is released.
         * 
         * @param e
         *        the key event.
         */
        public void handleKey(KeyEvent e) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "handleKey()", e);
            if(KeyCode.SPACE == e.getCode() || KeyCode.ENTER == e.getCode()){
                boolean errorFlag = false;
                Iterator<CharSequence> it = paragraphList.iterator();
                StringBuffer content = new StringBuffer();
                while(it.hasNext()){
                    String para = it.next().toString();
                    if(para.trim().equals("")){
                        continue;
                    }// end if
                    if(validEmailAddress(para)){
                        content.append(para.replaceAll(" ", "").replaceAll("\t", "")).append(Constants.LINESEPERATOR);
                    }else{
                        errorFlag = true;
                        if(it.hasNext()){
                            content.append(para).append(Constants.LINESEPERATOR);
                        }else{
                            content.append(para);
                        }// end if
                    }// end if
                }// end while
                 // removing or adding the style class error which is contained within the emailoptions.css
                if(errorFlag && !textArea.getStyleClass().contains("error")){
                    textArea.getStyleClass().add("error");
                    validateButton.setDisable(false);
                }else if(!errorFlag && textArea.getStyleClass().contains("error")){
                    textArea.getStyleClass().remove("error");
                    validateButton.setDisable(true);
                }// end if
                textArea.setText(content.toString());
                textArea.end();
            }// end if
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "handleKey()");
        }// end method

        /**
         * This method validates the given email address. This method will determine whether or not the email address is formatted correctly.
         * 
         * @param address
         *        the email address to be validated
         * @return {@code true} or {@code false} value for whether or not the email address is formatted correctly
         */
        private boolean validEmailAddress(String address) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "validEmailAddress()", address);
            boolean valid = false;
            String spacesRemoved = address.replaceAll(" ", "").replaceAll("\t", "");
            if(spacesRemoved.matches("^[\\w][\\w\\.#]*[\\w]@[\\w][\\w\\.]*[\\w]\\.[a-zA-Z][a-zA-Z\\.]{2,6}[a-zA-Z]$") || spacesRemoved.matches("(\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,6})")){
                valid = true;
            }// end if
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "validEmailAddress()", valid);
            return valid;
        }// end method

        /**
         * This method will validate all the email messages within the email list TextArea.
         * 
         * @return {@code true} or {@code false} value for whether or not all email addresses within the {@code TextArea} are validated
         */
        public boolean validateAllEmailMessages() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "validateAllEmailMessages()");
            Iterator<CharSequence> it = paragraphList.iterator();
            StringBuffer errors = new StringBuffer();
            boolean errorFlag = false;
            while(it.hasNext()){
                String para = it.next().toString();
                if(para.trim().equals("")){
                    continue;
                }// end if
                if(!validEmailAddress(para)){
                    errorFlag = true;
                    errors.append(para).append(Constants.LINESEPERATOR);
                }// end if
            }// end while

            if(errorFlag){
                FXAlertOption.showAlert(primaryStage, errors.toString(), "Incorrect Email Address", "Fix Email Addresses Below", AlertType.ERROR);
            }else{
                textArea.getStyleClass().remove("error");
                validateButton.setDisable(true);
            }// end if
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "validateAllEmailMessages()", errorFlag);
            return errorFlag;
        }// end method

        /**
         * This method will close resources that were opened by this window.
         * <p>
         * Note that when this method is called it will make sure to save the property within the simplefx.gui.properties.
         * </p>
         */
        public void close() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "close()");
            UIPropertiesMgr.getInstance().getProperties().replace(EMAIL_TO_PROPERTY_NM, getAddresses());
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "close()");
        }// end if

    }// end inner class

}// end class
