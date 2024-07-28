/**
 *
 */
package com.omo.free.simple.fx.application;

import static com.omo.free.simple.fx.util.Constants.CURRENT_VERSION_LABEL;
import static com.omo.free.simple.fx.util.Constants.DEFAULT_SIMPLE_FX_ICON;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.tools.CustomColorPicker;
import com.omo.free.simple.fx.tools.SFXUIScraper;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.simple.fx.util.FXUtil;
import com.omo.free.simple.fx.util.PermissionsWizard;
import com.omo.free.util.AppUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * SFXViewBuilder {@code abstract} class that defines the methods common to building the GUI components of JavaFX Applications.
 * <p>
 * The implementation of the SFXViewBuilder class is fairly simple. It only requires that you override a method named {@link #buildParent}.
 * </p>
 * <p>
 * This method needs to return a concrete implementation of a {@code Parent} (Refer to the JavaFX API for further details about the {@code Parent} class).
 * </p>
 * <p>
 * Note that the {@link #buildParent} method is {@code abstract} and must be overridden.
 * </p>
 * <p>
 * The {@link #close} and {@link #getStageIconPath} methods have concrete implementations. The {@code close} method does nothing. The {@code getStageIconPath} method returns a default path that is used for creating the title bar icon.
 * </p>
 * <p>
 * The default implementation features inherited by the {@code SFXViewBuilder} class are
 * </p>
 * <table border="1">
 * <tr>
 * <th>Feature</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>Version Number</td>
 * <td>A version number is displayed on the bottom-right corner of the GUI window. This version number is currently retrieved from the {@code MANIFEST.MF} file that is created by an ANT build.xml when the JavaFX application is packaged as a runnable jar, else the version number is defaulted to "dev1.0.0"</td>
 * </tr>
 * <tr>
 * <td>Standard {@code MenuBar}</td>
 * <td>A default {@code MenuBar} implementation which contains a <b>Help</b> {@code Menu} and an About {@code MenuItem}. The {@code MenuBar} is optional and if needed you must call the {@link #addStandardMenuBar(Credit)} method. You must create an instance of the {@code Credit} class and pass this credit instance into the {@code addStandardMenuBar(Credit)} method.</td>
 * </tr>
 * <tr>
 * <td>Tools {@code Menu}</td>
 * <td>An optional {@code Menu} implementation which contains a <b>Tools</b> {@code Menu} and a Choose Theme {@code MenuItem}. The {@code Menu} is optional and if wanted you must call the {@link #buildToolsMenuItem(MenuBar)} method. You must have created the {@code MenuBar} first. If you used the {@code addStandardMenuBar(Credit)} method to create your {@code MenuBar}, then pass {@code null} as a parameter into the {@code buildToolsMenuItem(MenuBar)} method. Otherwise, pass the {@code MenuBar} you created as a parameter.</td>
 * </tr>
 * <tr>
 * <td>{@link #getNodesFromParent(Predicate)}</td>
 * <td>This method gives the user the ability to retrieve a {@code List<Node>} containing all the children Nodes inside the SFX's primary scene. Those nodes are filtered by the {@link Predicate} that is passed to this method as a parameter.</td>
 * </tr>
 * <caption>Implementation Features</caption>
 * </table>
 * <p>
 * <b>Example</b>
 * </p>
 * <p>
 * The following example will show you a simple implementation of the {@code SFXViewBuilder} class. This example illustrates the use of the {@link UIPropertiesMgr} class which allows the developer to save GUI settings by overriding the {@link #close} method.
 * </p>
 *
 * <pre>
 * <code>
package com.omo.free.simple.app;

import java.util.Properties;

import com.omo.free.com.util.AppUtil;
import com.omo.free.simple.fx.application.Credit;
import com.omo.free.simple.fx.application.SFXViewBuilder;
import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Parent;

public class ExampleBuilder extends SFXViewBuilder{

    Properties settings;
    TextField textField;

    public ExampleBuilder() {
        settings = UIPropertiesMgr.getInstance().getProperties();
    }//end constructor

    {@literal @Override} protected Parent buildParent() {
        String defaultText = settings.getProperty("default.text");
        Label label = new Label("Enter Name:");
        textField = new TextField(AppUtil.isNullOrEmpty(defaultText) ? "Placeholder" : defaultText);
        Button enter = new Button("Enter");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(5));
        hbox.getChildren().addAll(label, textField, enter);

        Credit credit = new Credit();
        credit.addContributor("Don Brown");
        credit.addLeadDeveloper("Richard Salas");
        credit.addDocumentors("Johnny Stidum");
        credit.addProjectManager("Dwayne Walker");
        credit.addDevelopers("Brian Hicks");
        addStandardMenuBar(credit);

        return hbox;
    }//end method

    {@literal @Override} protected String getStageIconPath(){
        return "/com/omo/free/simple/resources/icon.png";
    }//end method

    {@literal @Override} protected void close() {
        settings.put("default.text", textField.getText());
    }//end method
}//end class
 * </code>
 * </pre>
 *
 * @author Richard Salas JCCC
 * @author Modified Johnnie Stidum JCCC on 2017-11-15 to include permissions help.
 * @author Modified Charles Craft JCCC on 2017-12-17 to customize permissions help window.
 * @author Modified Ron Skinner JCCC on 2021-01-07 customize permissions help window and AboutStage.
 * @author Modified Brandon Turner JCCC on 2023-11-28 added color theme functionality.
 * @author Modified Brandon Turner JCCC on 2023-12-04 added functionality to retrieve all nodes as the framework level.
 * @version 1.7
 * @see SFXApplication
 * @see UIPropertiesMgr
 */
public abstract class SFXViewBuilder {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.SFXViewBuilder";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static Stage primaryStage;

    private BorderPane simpleRoot = new BorderPane();
    private HBox versionPane = new HBox();
    private Label versionLabel;
    private Parent developerParent;
    private MenuBar menuBar;
    private String[] styleSheets;
    private boolean resizable;
    private StageStyle stageStyle;

    /**
     * Constructs a new {@code SFXViewBuilder} instance.
     */
    public SFXViewBuilder() {}// end constructor

    /**
     * This method creates and returns the {@code Parent} root {@code Node} which will be used for creating a {@code Scene}.
     * <p>
     * Note that the {@code Parent} class is the base class for all nodes that have children in the scene graph. (Refer to the JavaFX API for further details about the {@code Parent} class).
     * </p>
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     *  {@literal @Override} protected Parent buildParent() {
     *      Label label = new Label("Enter Name:");
     *      TextField textField = new TextField("Placeholder");
     *      Button enter = new Button("Enter");
     *
     *      HBox hbox = new HBox();
     *
     *      hbox.setAlignment(Pos.CENTER);
     *      hbox.setSpacing(5);
     *      hbox.setPadding(new Insets(5, 5, 5, 5));
     *
     *      hbox.getChildren().addAll(label, textField, enter);
     *      return hbox;
     *  }
     * </code>
     * </pre>
     * <p>
     * Note: If this method returns null a popup will be display to the Developer giving him/her a hint on how to implement this method.
     * </p>
     *
     * @return the root node of the scene graph
     */
    protected abstract Parent buildParent();

    /**
     * This method will create and return the root Node for the Scene.
     * <p>
     * <b>Creation Process</b>
     * </p>
     * <p>
     * The creation process of the Root {@code Parent} will execute in the following order whenever {@link SFXApplicationLauncher} launches the JavaFX Application.
     * </p>
     * <ol>
     * <li>Initializes and places the version number in bottom right corner of GUI.</li>
     * <li>Initializes the developers pane which was created by the implemented {@link buildParent} method. This pane will be placed in the center of the GUI.</li>
     * <li>Initializes the standard {@code MenuBar} by placing it at the top of the GUI only if the developer called the {@link #addStandardMenuBar(Credit)} method.</li>
     * </ol>
     *
     * @return the root node used to create the {@code Scene}
     */
    Parent getRootPane() {
        myLogger.entering(MY_CLASS_NAME, "getRootPane");
        initialVersionPane();
        layoutDevelopersPane();

        if(menuBar == null){
            myLogger.fine("Standard MenuBar will not be displayed.");
        }else{
            myLogger.fine("Standard MenuBar will be displayed at top of GUI BorderPane.");
            this.simpleRoot.setTop(menuBar);
        }// end if
        this.simpleRoot.setCenter(developerParent);
        myLogger.exiting(MY_CLASS_NAME, "getRootPane", this.simpleRoot);
        return this.simpleRoot;
    }// end method

    /**
     * This method will call the {@link #buildParent} method implemented by the developer. If this method returns null then a default {@code Parent} node will be created and displayed to the user. This default display will show a helpful hint to the developer for how to implement the {@link #buildParent} method.
     */
    private void layoutDevelopersPane() {
        myLogger.entering(MY_CLASS_NAME, "layoutDevelopersPane()");
        developerParent = buildParent();

        if(developerParent == null){
            myLogger.warning("The buildParent() method was not implemented correctly, a default GUI is being built and will be displayed to the developer.");
            Text heading = new Text("Developers Note");
            heading.setFont(Font.font(null, FontWeight.BOLD, 14));

            Text noParent = new Text("You have not implemented the SFXViewBuilder correctly. The implementation of this class is fairly simple. It only requires that you override a method called buildParent(). This method needs to return a concrete implementation of a Parent (Refer to the JavaFx API for further details about the Parent class). An example implementation is displayed below:");
            noParent.setTextAlignment(TextAlignment.JUSTIFY);
            noParent.setFont(Font.font(null, FontWeight.MEDIUM, 14));

            ImageView imageV = new ImageView("/com/omo/free/simple/fx/resources/buildParentExample.png");
            noParent.setWrappingWidth(imageV.getImage().getWidth());
            VBox vbox = new VBox();
            vbox.getChildren().addAll(heading, noParent, imageV);
            vbox.setSpacing(10);
            vbox.setStyle("-fx-padding: 10;-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-border-radius: 5;-fx-border-color: black;-fx-background-color:white;");
            developerParent = vbox;
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "layoutDevelopersPane()");
    }// end method

    /**
     * This method will create and initialize the version label to be displayed on the bottom right corner of the GUI.
     * <p>
     * This version number is currently retrieved from the {@code MANIFEST.MF} file that is created by an {@code ANT} build.xml when the JavaFX application is packaged as a runnable jar, else the version number is defaulted to "dev1.0.0"
     * </p>
     */
    private void initialVersionPane() {
        myLogger.entering(MY_CLASS_NAME, "initialVersionPane");
        versionLabel = new Label(AppUtil.isNullOrEmpty(CURRENT_VERSION_LABEL) ? "dev1.0.0" : CURRENT_VERSION_LABEL);
        versionLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 10));
        versionLabel.setStyle("-fx-font-size: 7pt;");
        versionPane.getChildren().add(versionLabel);
        versionPane.setAlignment(Pos.BOTTOM_RIGHT);
        this.simpleRoot.setBottom(versionPane);
        myLogger.exiting(MY_CLASS_NAME, "initialVersionPane");
    }// end method

    /**
     * This method when called will create a standard {@code MenuBar} concrete implementation which contains a <b>Help</b> {@code Menu} and an <b>About</b> {@code MenuItem}.
     * <p>
     * The {@code MenuBar} is optional and if it is desired then you must call this method. You must create an instance of the {@code Credit} class and pass this credit instance into this method as a parameter.
     * </p>
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     *      .
     *      .
     *      .
     *      Credit credit = new Credit();
     *      credit.addContributor("Don Brown");
     *      credit.addLeadDeveloper("Richard Salas");
     *      credit.addDocumentors("Johnny Stidum");
     *      credit.addProjectManager("Dwayne Walker");
     *      credit.addDevelopers("Brian Hicks");
     *      <b>addStandardMenuBar(credit);</b>
     *      .
     *      .
     *      .
     * </code>
     * </pre>
     *
     * @param credit
     *        the {@link Credit} instance used to build the Credits section within the {@link AboutStage}.
     * @return the {@code MenuBar} bar instance used for maybe adding more {@code MenuItems} to if needed.
     */
    protected final MenuBar addStandardMenuBar(Credit credit) {
        myLogger.entering(MY_CLASS_NAME, "addStandardMenuBar()", credit);
        if(menuBar == null){
            myLogger.fine("Building the menu bars standard Menu and Menu Item.");
            menuBar = new MenuBar();
            Menu menu = new Menu("_Help");

            // set up the about and permission
            MenuItem about = buildAboutMenuItem(credit);
            MenuItem permission = buildPermissionsItem();

            menu.getItems().addAll(permission, about);
            menuBar.getMenus().add(menu);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "addStandardMenuBar()", menuBar);
        return menuBar;
    }// end method

    /**
     * This method is used to build the standard About {@code MenuItem}.
     * <p>
     * Note: Main reason for this method is for when user uses fxml instead of java for an easy way of adding the Standard About menu item to their root.
     * </p>
     *
     * @param credit
     *        the {@link Credit} instance used to build the Credits section within the {@link AboutStage}.
     * @return the standard menu item used to attach to the users supplied {@code MenuBar}
     * @see #addStandardMenuBar(Credit)
     */
    protected final MenuItem buildAboutMenuItem(Credit credit) {
        myLogger.entering(MY_CLASS_NAME, "buildAboutMenuItem", credit);
        // set up the about
        MenuItem about = new MenuItem("_About");
        myLogger.fine("adding onAction to the About time");
        about.setOnAction(e -> showAbout(credit));
        myLogger.exiting(MY_CLASS_NAME, "buildAboutMenuItem", about);
        return about;
    }// end method

    /**
     * This method is used to build the optional Tools {@code Menu}, which contains a {@link CustomColorPicker}.
     * <p>
     * <b>Developers Notes:</b>
     * <ul>
     * <li>Main reason for this method is to give the users the ability to implement a way to change an applications color theme on demand.</li>
     * <li>If used, this method must be used after the {@code MenuBar} has already been built.</li>
     * </ul>
     * </p>
     *
     * @param theMenuBar
     *        <b>(OPTIONAL)</b> - The {@link MenuBar} instance used add the Tools Menu to.<br>
     *        <b>Note:</b> If the {@link SFXViewBuilder#addStandardMenuBar(Credit) addStandardMenuBar(Credit credit)} method was used to build the MenuBar, then pass {@code null} to this method.
     */
    protected final void buildToolsMenuItem(MenuBar theMenuBar) {
        myLogger.entering(MY_CLASS_NAME, "buildToolsMenuItem");
        // Set up the Tools Menu and the CustomColorPicker.
        String defaultHexValue = UIPropertiesMgr.getInstance().getProperties().getProperty("ui.color.theme");
        // If a default color theme exists, then set it for initial load.
        if(null != defaultHexValue){
            setRootColorTheme(defaultHexValue);
        }// end if
        CustomColorPicker colorPicker = new CustomColorPicker(defaultHexValue);
        Menu tools = new Menu("_Tools");
        Menu theme = new Menu("Choose Theme");
        CustomMenuItem colorMenuItem = new CustomMenuItem(colorPicker);
        colorMenuItem.setHideOnClick(false);

        myLogger.fine("Setting the onAction of the ColorPicker.");
        colorPicker.setOnAction(e -> {
            String colorHexValue = colorPicker.getColorAsHexValue();
            setRootColorTheme(colorHexValue);
            UIPropertiesMgr.getInstance().getProperties().setProperty("ui.color.theme", colorHexValue);
            tools.hide();
        });// end setOnAction

        myLogger.fine("Adding Color MenuItem to the Tools Menu.");
        theme.getItems().add(colorMenuItem);
        tools.getItems().add(theme);

        myLogger.fine("Adding Tools Menu second to last on the MenuBar.");
        if(null != theMenuBar){
            theMenuBar.getMenus().add((theMenuBar.getMenus().size() - 1), tools);
        }else{
            menuBar.getMenus().add((menuBar.getMenus().size() - 1), tools);
        }// end if/else

        myLogger.exiting(MY_CLASS_NAME, "buildToolsMenuItem", tools);
    }// end method

    /**
     * <p>
     * <b>DEVELOPERS NOTE: Main reason for this method is for when user uses fxml or already has an established {@link javafx.scene.control.MenuBar} and does not want to write more verbose code for adding the standard menu items.</b>
     * <p>
     * This method when called will either add the _Help {@code Menu} with standard {@code MenuItem}'s or if Help {@code Menu} exists will add only the standard {@code MenuItem}'s to the existing {@code Menu}.
     * <p>
     * You must create an instance of the {@code Credit} class and pass this credit instance into this method as a parameter.
     * </p>
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     *      .
     *      .
     *      .
     *      Credit credit = new Credit();
     *      credit.addContributor("Don Brown");
     *      credit.addLeadDeveloper("Richard Salas");
     *      credit.addDocumentors("Johnny Stidum");
     *      credit.addProjectManager("Dwayne Walker");
     *      credit.addDevelopers("Brian Hicks");
     *      <b>MenuBar menubar = loader.getController().getMenuBar();</b>//controller associated with the root fxml document
     *      <b>addStandardMenuItemsToMenuBar(menubar, credit);</b>
     *      .
     *      .
     *      .
     * </code>
     * </pre>
     *
     * @param theMenuBar
     *        the {@link MenuBar} instance used add standard menu items to
     * @param credit
     *        the {@link Credit} instance used to build the Credits section within the {@link AboutStage}.
     */
    protected final void addStandardMenuItemsToMenuBar(MenuBar theMenuBar, Credit credit) {
        myLogger.entering(MY_CLASS_NAME, "addStandardMenuItemsToMenuBar", new Object[]{theMenuBar, credit});
        if(theMenuBar == null){
            throw new IllegalArgumentException("MenuBar being passed in cannot be null!");
        }// end if

        Iterator<Menu> it = theMenuBar.getMenus().iterator();
        Menu menu = null;
        boolean isHelpMenuExists = false;
        while(it.hasNext()){
            // check each menu to see if it has a Help menu if so drop the About and Permissions into it
            menu = it.next();
            if("help".equals(menu.getText().replaceAll("_", "").toLowerCase().trim())){
                myLogger.info("Menu Help does exist therefore adding the standard menu items to it.");
                menu.getItems().addAll(buildPermissionsItem(), buildAboutMenuItem(credit));
                isHelpMenuExists = true;
            }// end if
        }// end while

        if(!isHelpMenuExists){
            myLogger.info("Menu _Help does not exist creating the _Help menu and standard items, then addding them to the menubar");
            Menu helpMenu = new Menu("_Help");
            helpMenu.getItems().addAll(buildPermissionsItem(), buildAboutMenuItem(credit));
            theMenuBar.getMenus().add(helpMenu);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "addStandardMenuItemsToMenuBar");
    }// end method

    /**
     * This method is called by the About {@code MenuItem}'s onAction method used to create and display the {@link AboutStage}.
     *
     * @param credit
     *        the {@link Credit} instance used to build the Credits section within the About Window.
     */
    private void showAbout(Credit credit) {
        myLogger.entering(MY_CLASS_NAME, "showAbout()", credit);
        AboutStage aboutStage = new AboutStage(credit);
        aboutStage.show();
        myLogger.exiting(MY_CLASS_NAME, "showAbout()");
    }// end if

    /**
     * This method is used to build the standard Permissions {@code MenuItem}.
     * <p>
     * Note: Main reason for this method is for when user uses fxml instead of java for an easy way of adding the Standard Permissions menu item to their root.
     * </p>
     *
     * @return the standard menu item used to attach to the users supplied {@code MenuBar}
     * @see #addStandardMenuBar(Credit)
     */
    protected final MenuItem buildPermissionsItem() {
        myLogger.entering(MY_CLASS_NAME, "buildPermissionsItem");
        // set up the about
        MenuItem perm = new MenuItem("_Permissions");
        myLogger.fine("adding onAction to the Permission");
        perm.setOnAction(e -> FXUtil.buildAdminHelpPage());
        myLogger.exiting(MY_CLASS_NAME, "buildPermissionsItem", perm);
        return perm;
    }// end method

    /**
     * This is a convenience method that will perform a permissions check, <b>ONLY if application is packaged as a jar (if not packaged as jar this check will ALWAYS return null)</b>, to see if the users machine has administrative permissions or not.
     * <ul>
     * <li>If the user does not have administrative permissions then this method will return a {@link Parent} node containing instructions on how he/she can correct this problem.</li>
     * <li>If the user does have administrative permissions then this method will return a null value.</li>
     * </ul>
     * <p>
     * Typically this method needs to be called within the {@link #buildParent()} method and the return value of the {@code adminCheck} method if it is not null should be sent as the return value of the {@code buildParent} method.
     * </p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     *  {@literal @Override} protected Parent buildParent() {
     *      Label label = new Label("Enter Name:");
     *      TextField textField = new TextField("Placeholder");
     *      Button enter = new Button("Enter");
     *
     *      HBox parent = adminCheck();
     *
     *      if(parent == null){
     *          hbox.setAlignment(Pos.CENTER);
     *          hbox.setSpacing(5);
     *          hbox.setPadding(new Insets(5, 5, 5, 5));
     *
     *          hbox.getChildren().addAll(label, textField, enter);
     *      }//end if
     *      return hbox;
     *  }//end method
     * </code>
     * </pre>
     * 
     * @return box the parent admin rights help box
     */
    protected final Parent adminCheck() {
        myLogger.entering(MY_CLASS_NAME, "adminCheck");
        Parent box = null;
        try{

            if(Constants.IS_JAR && !AppUtil.hasPermission()){

                box = new PermissionsWizard(false);// build the help page for user

            }// end if
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred during the execution of the admin command check.  Error is: " + e.getMessage(), e);
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "adminCheck");
        return box;
    }// end method

    /**
     * This method provides a default implementation for returning the path to the application's title bar icon.
     * <p>
     * Note that this method is usually overridden by a concrete implementation of the SFXViewBuilder class to return a path to an icon that is more suited for the application
     * </p>
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     * &#64;Override
     * protected String getStageIconPath() {
     *     return "/com/omo/free/simple/resources/icon.png";
     * }// end method
     * </code>
     * </pre>
     *
     * @return the path to the stage icon to be shown in the title bar
     */
    protected String getStageIconPath() {
        return DEFAULT_SIMPLE_FX_ICON;
    }// end method

    /**
     * This method is available to the concrete implementation class which sets the value of the property resizable.
     * <p>
     * Defines whether the Stage is resizable or not by the user. Programmatically you may still change the size of the {@code Stage}. This is a hint which allows the implementation to optionally make the Stage resizable by the user.
     * </p>
     *
     * @param resizable
     *        {@code true} or {@code false} on whether or not the user has the ability to resize the window.
     */
    protected void setResizable(boolean resizable) {
        this.resizable = resizable;
    }// end method

    /**
     * This method is available to the concrete implementation class for adding custom style sheets to the main Scene.
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     * addStyleSheets("/com/example/javafx/app/mystyles1.css", "/com/example/javafx/app/mystyles2.css")
     * </code>
     * </pre>
     *
     * @param styleSheets
     *        array of style sheets that will be attached to the main scene within the primary stage
     */
    protected void addStyleSheets(String... styleSheets) {
        this.styleSheets = styleSheets;
    }// end method

    /**
     * This method specifies the style for this stage. This must be done prior to making the stage visible.
     * <p>
     * The style is one of: {@code StageStyle.DECORATED}, {@code StageStyle.UNDECORATED}, {@code StageStyle.TRANSPARENT}, or {@code StageStyle.UTILITY}.
     * </p>
     *
     * @param style
     *        the style for this stage.
     */
    public final void initStyle(StageStyle style) {
        this.stageStyle = style;
    }// end method

    /**
     * This method is called during the shutdown process of the application and provides a convenient place for saving GUI settings, shutting down resources, ect.
     * <p>
     * The implementation of this method provided by the {@code SFXViewBuilder} class does nothing.
     * </p>
     * <p>
     * Typical usage is:
     *
     * <pre>
     * <code>
     *  {@literal @Override} protected void close() {
     *      settings.put("default.text", textField.getText());
     *  }//end method
     * </code>
     * </pre>
     *
     * @see UIPropertiesMgr
     */
    protected void close() {

    }// end method

    /**
     * This method sets the primaryStage {@code static} variable
     *
     * @param parent
     *        the primary stage for this application
     */
    static void setPrimaryStage(Stage parent) {
        primaryStage = parent;
    }// end method

    /**
     * This method will return the primary stage for this application.
     *
     * @return the primary stage for this application.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }// end method

    /**
     * Returns the array of style sheets that have been set by the implementing class.
     *
     * @return array of style sheets
     */
    String[] getStyleSheets() {
        return styleSheets;
    }// end method

    /**
     * This method will return whether or not the view is resizable.
     *
     * @return {@code true} or {@code false} value on whether or not the gui window is resizable
     */
    boolean isResizable() {
        return resizable;
    }// end method

    /**
     * This method will return the {@code StageStyle} that is to be set on the primary stage.
     *
     * @return the {@code StageStyle} to be applied to the primary stage
     */
    StageStyle getStyle() {
        return stageStyle;
    }// end method

    /**
     * This method will set padding on the root {@link BorderPane}.
     *
     * @param padding
     *        the {@link Insets} to use for setting padding
     */
    protected void setRootPadding(Insets padding) {
        myLogger.entering(MY_CLASS_NAME, "setRootPadding()", padding);
        if(padding != null){
            simpleRoot.setPadding(padding);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "setRootPadding()");
    }// end method

    /**
     * This method will set the base color value on the root {@link BorderPane}.
     * 
     * @param color
     *        The color hex value to use for setting base color.
     */
    protected void setRootColorTheme(String color) {
        myLogger.entering(MY_CLASS_NAME, "setRootColorTheme()", color);
        simpleRoot.setStyle("-fx-base:" + color);
        myLogger.exiting(MY_CLASS_NAME, "setRootColorTheme()");
    }// end method
    
    /**
     * This method will return a {@code List<Node>} of all the children inside the SFX Parent Node.
     * 
     * @param filter
     *        The lambda expression used to filter what Nodes to return to the caller.
     * @return {@code List<Node>}
     */
    public static List<Node> getNodesFromParent(Predicate<Node> filter) {
        myLogger.entering(MY_CLASS_NAME, "getNodesFromParent()");
        SFXUIScraper scraper = () -> getPrimaryStage().getScene().getRoot();
        myLogger.exiting(MY_CLASS_NAME, "getNodesFromParent()");
        return scraper.scrape(filter);
    }// end method

}// end class
