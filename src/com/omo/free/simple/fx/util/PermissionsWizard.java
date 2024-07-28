package com.omo.free.simple.fx.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Class that creates a wizard to guide the user through changing permissions to run Java programs.
 * 
 * @author Ron Skinner JCCC December 21, 2020
 * @version 1.0
 */
public class PermissionsWizard extends BorderPane {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.util.PermissionsWizard";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private int pageNumber = 1;
    private boolean hasPermissions;
    private Button cancelBtn;
    private Button nextBtn;
    private Button backBtn;
    private HBox buttonBar;
    private Label headerLbl;
    private VBox page1;
    private VBox page2;
    private VBox page3;
    private VBox page4;

    /**
     * Constructor used to instantiate the class.
     * 
     * @param hasPermissions
     *        whether the user has administrative authority to run Java programs.
     */
    public PermissionsWizard(boolean hasPermissions) {
        myLogger.entering(MY_CLASS_NAME, "PermissionsWizard", hasPermissions);
        this.hasPermissions = hasPermissions;
        setPrefSize(500, 355);

        getStylesheets().add("/com/omo/free/simple/fx/resources/permissionswizard.css");//rts000is - set the stylesheet

        HBox header = getHeader();
        setTop(header);

        page1 = getSetUpPage();
        setCenter(page1);

        buttonBar = createButtonBar();
        setBottom(buttonBar);

        page2 = getWindowExplorerPage();
        page3 = getTabPage();
        page4 = getAdminPage();

        setHeaderLabel();
        

        myLogger.exiting(MY_CLASS_NAME, "PermissionsWizard");
    }// end constructor

    /**
     * Creates a container used to display a labeled header.
     * 
     * @return HBox a JavaFX layout
     */
    private HBox getHeader() {
        myLogger.entering(MY_CLASS_NAME, "getHeader");
        headerLbl = createLabel("", new Insets(13, 0, 7, 20), false);
        headerLbl.setFont(Font.font("SanSerif", FontWeight.BOLD, 12));

        HBox header = new HBox();
        header.setId("header");
        header.setMinSize(500, 55);
        header.getChildren().addAll(headerLbl);
        myLogger.exiting(MY_CLASS_NAME, "getHeader");
        return header;
    }// end method

    /**
     * Creates a container used to display controls for the first page of the wizard.
     * 
     * @return VBox a JavaFX layout
     */
    private VBox getSetUpPage() {
        myLogger.entering(MY_CLASS_NAME, "getSetUpPage");
        String text = "Setup will guide you through the steps required to change the property required to run Java programs.\n\nClick Next to continue, or Cancel to exit Setup.";
        Label lbl = createLabel(text, new Insets(20, 10, 10, 20), true);

        ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/javalogo.png");

        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(lbl, imgView);
        myLogger.exiting(MY_CLASS_NAME, "getSetUpPage");
        return box;
    }// end method

    /**
     * Creates a container used to display controls for the second page of the wizard.
     * 
     * @return VBox a JavaFX layout
     */
    private VBox getWindowExplorerPage() {
        myLogger.entering(MY_CLASS_NAME, "getWindowExplorerPage");
        Label prefix = createLabel("Open the ", new Insets(21, 0, 5, 20), false);

        Hyperlink hyperLink = new Hyperlink("Explorer Window");
        hyperLink.setPadding(new Insets(20, 0, 5, 0));
        hyperLink.setOnAction((open) -> openExplorerWindow());

        Label suffix = createLabel(" where the file named javaw.exe is located.", new Insets(21, 10, 5, 0), false);
        Label rightClick = createLabel("Right click the file and select Properties.", new Insets(0, 0, 15, 20), false);

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(prefix, hyperLink, suffix, rightClick);

        ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/javawexe.png");
        Label next = createLabel("To continue, click Next.", new Insets(15, 355, 15, 20), false);

        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(flowPane, imgView, next);
        myLogger.exiting(MY_CLASS_NAME, "getWindowExplorerPage");
        return box;
    }// end method

    /**
     * Creates a container used to display controls for the third page of the wizard.
     * 
     * @return VBox a JavaFX layout
     */
    private VBox getTabPage() {
        myLogger.entering(MY_CLASS_NAME, "getTabPage");
        Label lbl = createLabel("Select the Compatability Tab.", new Insets(20, 320, 10, 20), false);

        ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/javawPropertiesTab.png");
        Label next = createLabel("To continue, click Next.", new Insets(5, 355, 0, 20), false);

        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(lbl, imgView, next);
        myLogger.exiting(MY_CLASS_NAME, "getTabPage");
        return box;
    }// end method

    /**
     * Creates a container used to display controls for the fourth page of the wizard.
     * 
     * @return VBox a JavaFX layout
     */
    private VBox getAdminPage() {
        myLogger.entering(MY_CLASS_NAME, "getAdminPage");
        String text = "Select the checkbox labeled \"Run this program as an administrator\".  Click Apply, then click OK.  Close the window and restart the application.";
        Label lbl = createLabel(text, new Insets(20, 0, 15, 20), true);

        ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/javawPropertiesAdmin.png");

        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(lbl, imgView);
        myLogger.exiting(MY_CLASS_NAME, "getAdminPage");
        return box;
    }// end method

    /**
     * Creates a container used to display the UI buttons.
     * 
     * @return HBox a JavaFX layout
     */
    private HBox createButtonBar() {
        myLogger.entering(MY_CLASS_NAME, "createButtonBar");
        backBtn = createButton("< Back", new Insets(10, 0, 10, 0));
        backBtn.setOnAction((evt) -> backBtn_Click());

        nextBtn = createButton("Next >", new Insets(10, 0, 10, 0));
        nextBtn.setOnAction((evt) -> nextBtn_Click());

        cancelBtn = createButton("Cancel", new Insets(10, 10, 10, 0));
        cancelBtn.setOnAction((evt) -> close());

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBar.setId("button-bar");
        buttonBar.setMinSize(500, 45);
        buttonBar.getChildren().addAll(nextBtn, cancelBtn);
        myLogger.exiting(MY_CLASS_NAME, "createButtonBar");
        return buttonBar;
    }// end method

    /**
     * Closes the stage that this layout is bound to.
     */
    private void close() {
        myLogger.entering(MY_CLASS_NAME, "close");
        
        Stage stage = (Stage) getScene().getWindow();
        stage.close();

        myLogger.exiting(MY_CLASS_NAME, "close");
    }//end method

    /**
     * Utility that creates a button and adds insets to position the button within a container.
     * 
     * @param text
     *        the text displayed on the button
     * @param insets
     *        the offsets used to place the button
     * @return Button a JavaFX control
     */
    private Button createButton(String text, Insets insets) {
        myLogger.entering(MY_CLASS_NAME, "createButton", new Object[]{text, insets});
        Button button = new Button(text);
        button.setId("colored-button");
        button.setMinSize(70, 20);
        HBox.setMargin(button, insets);
        myLogger.exiting(MY_CLASS_NAME, "createButton");
        return button;
    }// end method

    /**
     * Utility that creates a label and adds insets to position the label within a container.
     * 
     * @param text
     *        the text displayed on the label
     * @param insets
     *        the offsets used to place the label
     * @param isWrapped
     *        whether the text should the wrap within the container
     * @return Label a JavaFX control
     */
    private Label createLabel(String text, Insets insets, boolean isWrapped) {
        myLogger.entering(MY_CLASS_NAME, "createLabel", new Object[]{text, insets, isWrapped});
        Label label = new Label(text);
        label.setPadding(insets);
        if(isWrapped){
            label.setWrapText(isWrapped);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "createLabel");
        return label;
    }// end method

    /**
     * Utility that creates an ImageView.
     * 
     * @param path
     *        the url to the resource
     * @return ImageView a JavaFX image
     */
    private ImageView createImageView(String path) {
        myLogger.entering(MY_CLASS_NAME, "createImageView", path);
        String url = PermissionsWizard.class.getResource(path).toString();
        ImageView imgView = new ImageView(new Image(url));
        myLogger.exiting(MY_CLASS_NAME, "createImageView");
        return imgView;
    }// end method

    /**
     * Opens the directory where the Java Runtime Environment file is located. Typically, C:/Program Files/Java/jre/bin
     */
    private void openExplorerWindow() {
        myLogger.entering(MY_CLASS_NAME, "openExplorerWindow");
        Desktop desktop = Desktop.getDesktop();
        String jrehome = System.getProperty("java.home", "");
        nextBtn.requestFocus();
        try{
            desktop.open(new File(jrehome + "/bin"));
        }catch(IOException ex){
            // FXAlertOption.showAlert(null, "Error trying to open explorer window. Error message is: " + ex.getMessage(), "Could Not Open Explorer", null, AlertType.ERROR);
        }catch(Exception ex){
            // FXAlertOption.showAlert(null, "Error trying to open explorer window. Error message is: " + ex.getMessage(), "Could Not Open Explorer", null, AlertType.ERROR);
        }// end try/catch
        myLogger.exiting(MY_CLASS_NAME, "openExplorerWindow");
    }// end method

    /**
     * Sets the wizard page in relation to the next button being clicked.
     */
    private void nextBtn_Click() {
        myLogger.entering(MY_CLASS_NAME, "nextBtn_Click");
        ++pageNumber;
        if(pageNumber == 2){
            setCenter(page2);
            // add the back button
            buttonBar.getChildren().add(0, backBtn);
        }else if(pageNumber == 3){
            setCenter(page3);
        }else{
            // remove the next button
            buttonBar.getChildren().remove(nextBtn);
            setCenter(page4);
        }// end if/else
        myLogger.exiting(MY_CLASS_NAME, "nextBtn_Click");
    }// end method

    /**
     * Sets the wizard page in relation to the back button being clicked.
     */
    private void backBtn_Click() {
        myLogger.entering(MY_CLASS_NAME, "backBtn_Click");
        --pageNumber;
        if(pageNumber == 1){
            setCenter(page1);
            // remove the back button
            buttonBar.getChildren().remove(backBtn);
        }else if(pageNumber == 2){
            setCenter(page2);
        }else{
            // add the next button
            buttonBar.getChildren().add(1, nextBtn);
            setCenter(page3);
        }// end if/else
        myLogger.exiting(MY_CLASS_NAME, "backBtn_Click");
    }// end method

    /**
     * Sets the header label that displays whether the user has administrative authority to run Java programs.
     */
    private void setHeaderLabel() {
        myLogger.entering(MY_CLASS_NAME, "setHeaderLabel");
        if(!hasPermissions){
            headerLbl.setTextFill(Color.RED);
            headerLbl.setText("You do not have administrator rights to run this application.");
        }else{
            headerLbl.setText("Apply administrative rights to your Java programs.");
        }// end if/else
        myLogger.entering(MY_CLASS_NAME, "setHeaderLabel");
    }// end method

}// end class
