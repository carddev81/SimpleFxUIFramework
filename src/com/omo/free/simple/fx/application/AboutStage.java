package com.omo.free.simple.fx.application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.omo.free.simple.fx.managers.UIPropertiesMgr;
import com.omo.free.simple.fx.util.Constants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The AboutStage class extends the {@code javafx.stage.Stage} class used for displaying the details about the JavaFX Application within a custom {@code Window}.
 * <p>
 * The details consists of:
 * </p>
 * <ul>
 * <li>Software info</li>
 * <li>Build info</li>
 * <li>SimpleFX Framework Version</li>
 * <li>Java Info</li>
 * <li>OS info</li>
 * <li>Credits (Listed names of people for the credit of work done for the JavaFX Application)</li>
 * </ul>
 *
 * @author Richard Salas JCCC
 * @author modified by Ron Skinner JCCC 01/05/2021
 * @version 2.0
 */
class AboutStage extends Stage {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.application.AboutStage";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private String applicationName;
    private Credit credits;

    /**
     * Creates an AboutStage with the given {@link Credit} instance.
     * 
     * @param credits
     *        the initial credit instance to use for building the credits section within the {@code AboutStage}
     */
    public AboutStage(Credit credits) {
        super();
        myLogger.entering(MY_CLASS_NAME, "AboutStage", credits);
        applicationName = UIPropertiesMgr.getInstance().getProperties().getProperty("application.name");
        if(credits == null){
            credits = new Credit();
        }// end if
        this.credits = credits;
        Stage parentStage = SFXViewBuilder.getPrimaryStage();
        setTitle("About " + applicationName);
        initOwner(parentStage);
        initModality(Modality.WINDOW_MODAL);
        setMaxWidth(655);
        setMinHeight(340);
        setResizable(false);

        myLogger.fine("Building the AboutPane and then adding it to the Scene");
        AboutPane bp = new AboutPane();
        getIcons().add(parentStage.getIcons().get(0));
        Scene scene = new Scene(bp);
        scene.getStylesheets().add("/com/omo/free/simple/fx/resources/about.css");
        setScene(scene);
        sizeToScene();
        myLogger.exiting(MY_CLASS_NAME, "AboutStage");
    }// end constructor

    /**
     * The AboutPane class extends BorderPane which is used for laying out the system information, application information, and the credits.
     *
     * @author Richard Salas JCCC
     * @author modified by Ron Skinner JCCC 01/05/2021
     */
    class AboutPane extends BorderPane {

        private static final String MY_INNER_CLASS_NAME = "com.omo.free.simple.fx.application.AboutStage.AboutPane";
        private Logger myInnerLogger = Logger.getLogger(MY_INNER_CLASS_NAME);

        private VBox page1;
        private VBox page2;
        private VBox page3;

        /**
         * Creates and instance of the {@code AboutPane}.
         */
        public AboutPane() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "AboutPane");
            setMinSize(640, 340);

            page1 = getAboutPage();
            page2 = getFrameworkPage();
            page3 = getSoftwarePage();

            setCenter(page1);
            setBottom(createHyperlinkBar());
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "AboutPane");
        }// end method

        /**
         * Creates a container used to display the About page.
         * 
         * @return VBox a JavaFX layout
         */
        private VBox getAboutPage() {
            myLogger.entering(MY_CLASS_NAME, "getAboutPage");
            // To be safe, the name of the application should not exceed 32 characters including whitespace
            Text appName = new Text(applicationName);
            appName.setFill(Color.WHITESMOKE);
            appName.setEffect(new Lighting());
            appName.setFont(Font.font(Font.getDefault().getFamily(), 40));

            ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/ISU_JCCC_Dk(153x71).png");
            VBox.setMargin(imgView, new Insets(10, 0, 0, 0));

            Set<String> nameSet = new LinkedHashSet<>();
            nameSet.addAll(credits.getProjectManagers());
            nameSet.addAll(credits.getLeadDevelopers());
            nameSet.addAll(credits.getDevelopers());
            nameSet.addAll(credits.getDocumentors());
            nameSet.addAll(credits.getContributors());

            List<String> nameList = createAboutRoles(nameSet);
            Label namesLabel = createNamesLabel(nameList);

            HBox labelBox = new HBox();
            labelBox.getChildren().add(namesLabel);

            Region spring = new Region();
            VBox.setVgrow(spring, Priority.ALWAYS);

            VBox aboutBox = new VBox();
            aboutBox.setAlignment(Pos.TOP_CENTER);
            aboutBox.setPadding(new Insets(20));
            aboutBox.getChildren().addAll(appName, imgView, spring, labelBox);
            myLogger.exiting(MY_CLASS_NAME, "getAboutPage");
            return aboutBox;
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
            String url = AboutStage.class.getResource(path).toString();
            ImageView imgView = new ImageView(new Image(url));
            myLogger.exiting(MY_CLASS_NAME, "createImageView");
            return imgView;
        }// end method

        /**
         * Creates a List&lt;String&gt; of names and roles of people who were involved in the coding of the application.
         * 
         * @param set
         *        a unique Set&lt;String&gt; of names
         * @return List&lt;String&gt; of names and roles of people who were involved in the application
         */
        private List<String> createAboutRoles(Set<String> set) {
            myLogger.entering(MY_CLASS_NAME, "createAboutRoles", set);
            List<String> nameList = new ArrayList<>();
            StringBuilder creditBuilder = new StringBuilder();
            int nameCount = 0;
            int roleCount = 0;
            int size = set.size();
            myLogger.fine("start building the list of names here");
            String name = null;
            for(Iterator<String> it = set.iterator();it.hasNext();){
                roleCount = 0;
                name = it.next();
                creditBuilder.append(name).append(", ");
                if(credits.isAProjectManager(name)){
                    creditBuilder.append("project manager");
                    roleCount++;
                }// end if
                if(credits.isALeadDeveloper(name)){
                    if(roleCount >= 1){
                        creditBuilder.append(", ");
                    }// end if
                    creditBuilder.append("lead developer");
                    roleCount++;
                }// end if
                if(credits.isADeveloper(name)){
                    if(roleCount >= 1){
                        creditBuilder.append(", ");
                    }// end if
                    creditBuilder.append("developer");
                    roleCount++;
                }// end if
                if(credits.isADocumentor(name)){
                    if(roleCount >= 1){
                        creditBuilder.append(", ");
                    }// end if
                    creditBuilder.append("documenter");
                    roleCount++;
                }// end if
                if(credits.isAContributor(name)){
                    if(roleCount >= 1){
                        creditBuilder.append(", ");
                    }// end if
                    creditBuilder.append("contributor");
                    roleCount++;
                }// end if
                ++nameCount;
                if(nameCount != size){
                    creditBuilder.append(" | ");
                }// end if
                myLogger.fine("add the name and role to the list");
                nameList.add(creditBuilder.toString());
                creditBuilder.delete(0, creditBuilder.length());
            }// end for
            myLogger.exiting(MY_CLASS_NAME, "createAboutRoles");
            return nameList;
        }// end method

        /**
         * Helper method to create a Label used to display credits.
         * 
         * @param nameList
         *        a list of names who contributed to the application's development
         * @return Label the control used to display credits
         */
        private Label createNamesLabel(List<String> nameList) {
            myLogger.entering(MY_CLASS_NAME, "createNamesLabel", nameList);
            String str = "";
            for(int i = 0, j = nameList.size();i < j;i++){
                str += nameList.get(i);
            }// end for
            Label label = new Label(str);
            label.setMaxWidth(570);
            label.setWrapText(true);
            myLogger.exiting(MY_CLASS_NAME, "createNamesLabel");
            return label;
        }// end method

        /**
         * Creates a container used to display the Framework page.
         * 
         * @return VBox a JavaFX layout
         */
        private VBox getFrameworkPage() {
            myLogger.entering(MY_CLASS_NAME, "getFrameworkPage");
            ImageView imgView = createImageView("/com/omo/free/simple/fx/resources/SimpleFXIconv2(267x165).png");
            VBox.setMargin(imgView, new Insets(30, 0, 0, 0));

            Set<String> nameSet = new LinkedHashSet<>();
            nameSet.addAll(credits.getSimpleFXAuthors());
            nameSet.addAll(credits.getGraphicArtists());

            List<String> nameList = createFrameworkRoles(nameSet);
            Label namesLabel = createNamesLabel(nameList);
            VBox.setMargin(namesLabel, new Insets(0, 10, 10, 0));

            VBox labelBox = new VBox();
            labelBox.setPadding(new Insets(0, 0, 10, 25));

            Label frameworkVersion = new Label("This software is powered by Simple FX version " + getSimpleUIFrameworkVersion());
            labelBox.getChildren().addAll(frameworkVersion, namesLabel);

            Region spring = new Region();
            VBox.setVgrow(spring, Priority.ALWAYS);

            VBox frameworkBox = new VBox();
            frameworkBox.setAlignment(Pos.CENTER);
            frameworkBox.getChildren().addAll(imgView, spring, labelBox);
            myLogger.exiting(MY_CLASS_NAME, "getFrameworkPage");
            return frameworkBox;
        }// end method

        /**
         * Creates a List&lt;String&gt; of names and roles of people who were involved in the coding of the framework.
         * 
         * @param set
         *        a unique Set&lt;String&gt; of names
         * @return List&lt;String&gt; of names and roles of people who were involved in the framework
         */
        private List<String> createFrameworkRoles(Set<String> set) {
            myLogger.entering(MY_CLASS_NAME, "createFrameworkRoles", set);
            List<String> nameList = new ArrayList<>();
            StringBuilder creditBuilder = new StringBuilder();
            int nameCount = 0;
            int size = set.size();
            myLogger.fine("start building the list of names here");
            String name = null;
            for(Iterator<String> it = set.iterator();it.hasNext();){
                name = it.next();
                creditBuilder.append(name).append(", ");
                if(credits.getSimpleFXAuthors().contains(name)){
                    creditBuilder.append("Simple FX author");
                }// end if
                if(credits.getGraphicArtists().contains(name)){
                    creditBuilder.append("graphics");
                }// end if
                ++nameCount;
                if(nameCount != size){
                    creditBuilder.append(" | ");
                }// end if
                myLogger.fine("add the name and role to the list");
                nameList.add(creditBuilder.toString());
                creditBuilder.delete(0, creditBuilder.length());
            }// end for
            myLogger.exiting(MY_CLASS_NAME, "createFrameworkRoles");
            return nameList;
        }// end method

        /**
         * This method is used for retrieving the SimpleFX Framework version located within com/omo/free/simple/fx/version/version.properties file.
         * 
         * @return the version of the SimpleFX Framework
         */
        private String getSimpleUIFrameworkVersion() {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getSimpleUIFrameworkVersion");
            URL url = this.getClass().getProtectionDomain().getClassLoader().getResource("com/omo/free/simple/fx/version/version.properties");
            Properties props = null;
            String version = null;
            try{
                props = new Properties();
                props.load(url.openConnection().getInputStream());
                version = props.getProperty("version");
            }catch(Exception e){
                myInnerLogger.log(Level.WARNING, "Exception occurred while trying to retrieve the SimpleFX Framework version information from com/omo/free/simple/fx/version/version.properties.  Error Message is: " + e.getMessage(), e);
                version = "unknown";
            }// end try/catch
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getSimpleUIFrameworkVersion");
            return version;
        }// end if

        /**
         * Creates a container used to display the Software page.
         * 
         * @return VBox a JavaFX layout
         */
        private VBox getSoftwarePage() {
            myLogger.entering(MY_CLASS_NAME, "getSoftwarePage");
            StringBuilder labelBuilder = new StringBuilder();

            // software information
            labelBuilder.append("Software Information: ").append(applicationName);
            Label softwareLabel = createSoftwareLabel(labelBuilder);
            VBox.setMargin(softwareLabel, new Insets(20, 0, 0, 20));

            Attributes mainAttrs = null;
            try{
                mainAttrs = getManifestAttributes();
            }catch(IOException e){
                myInnerLogger.log(Level.SEVERE, "IOException occurred while trying to read attributes from the MANIFEST.MF file.  Error is: " + e.getMessage(), e);
            }catch(Exception e){
                myInnerLogger.log(Level.SEVERE, "Exception occurred while trying to read attributes from the MANIFEST.MF file.  Error is: " + e.getMessage(), e);
            }// end try/catch

            // software description
            Label descriptionLabel = getSoftwareDescription(mainAttrs);
            VBox.setMargin(descriptionLabel, new Insets(0, 0, 0, 20));

            VBox descriptionContainer = new VBox();
            descriptionContainer.getChildren().addAll(softwareLabel, descriptionLabel);

            // build information
            VBox buildBox = getBuildLayout(mainAttrs);
            VBox.setMargin(buildBox, new Insets(0, 0, 0, 20));

            // java version
            labelBuilder.append("Java: ").append(System.getProperty("java.version", ""));
            Label javaLabel = createSoftwareLabel(labelBuilder);
            VBox.setMargin(javaLabel, new Insets(20, 0, 0, 20));
            createSoftwareLabel(labelBuilder);

            // operating system
            labelBuilder.append("Operating System: ").append(System.getProperty("os.name", "")).append(" ").append(System.getProperty("os.arch", "")).append(" ").append(System.getProperty("os.version", ""));
            Label osLabel = createSoftwareLabel(labelBuilder);
            VBox.setMargin(osLabel, new Insets(20, 0, 0, 20));

            VBox softwareContainer = new VBox();
            softwareContainer.getChildren().addAll(descriptionContainer, buildBox, javaLabel, osLabel);

            ImageView appIcon = new ImageView();
            Image icon = SFXViewBuilder.getPrimaryStage().getIcons().get(0);
            appIcon.setImage(icon);
            appIcon.setFitHeight(125);
            appIcon.setPreserveRatio(true);
            appIcon.setSmooth(true);
            appIcon.setCache(true);

            VBox iconContainer = new VBox();
            iconContainer.setAlignment(Pos.CENTER);
            VBox.setMargin(appIcon, new Insets(0, 0, 0, 150));
            iconContainer.getChildren().add(appIcon);

            HBox centerLayout = new HBox();
            centerLayout.getChildren().addAll(softwareContainer, iconContainer);

            VBox masterContainer = new VBox();
            masterContainer.getChildren().addAll(descriptionContainer, centerLayout);

            myLogger.exiting(MY_CLASS_NAME, "getSoftwarePage");
            return masterContainer;
        }// end method

        /**
         * Helper method to create a Label used to display software information.
         * 
         * @param strBuilder
         *        the StringBuilder that contains the information used to build the label
         * @return Label the control used to display software information
         */
        private Label createSoftwareLabel(StringBuilder strBuilder) {
            myLogger.entering(MY_CLASS_NAME, "createSoftwareLabel", strBuilder);
            Label label = new Label(strBuilder.toString());
            strBuilder.delete(0, strBuilder.length());
            myLogger.exiting(MY_CLASS_NAME, "createSoftwareLabel");
            return label;
        }// end method

        /**
         * This method is used for retrieving the software description from the MANIFEST.MF file.
         * 
         * @return Label the control used to display the software description
         */
        private Label getSoftwareDescription(Attributes mainAttrs) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getSoftwareDescription", mainAttrs);
            String description = "Implementation Vendor: MODOC";
            Label descriptionLabel = null;
            if(mainAttrs != null){
                description = mainAttrs.getValue("Software-Description");
                if(description != null || !"".equals(description)){
                    descriptionLabel = new Label(description);
                }// end if
            }else{
                descriptionLabel = new Label(description);
            }// end if/else
            descriptionLabel.setMaxWidth(570);
            descriptionLabel.setWrapText(true);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getSoftwareDescription");
            return descriptionLabel;
        }// end method

        /**
         * Helper method to get the attributes of the manifest file.
         * 
         * @return Attributes the attributes of the manifest file
         * @throws IOException
         *         if an error occurs while attempting to read the MANIFEST.MF file
         */
        private Attributes getManifestAttributes() throws IOException {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getManifestAttributes");
            JarFile jar = null;
            Attributes mainAttrs = null;
            // need to make sure we are running as a jar else this blows up!
            if(Constants.IS_JAR){
                try{
                    jar = new JarFile(new File(Constants.APP_FILE_LOCATION));
                    mainAttrs = jar.getManifest().getMainAttributes();
                }finally{
                    if(jar != null){
                        jar.close();
                    }// end if
                }// end try/catch/finally
            }else{
                myInnerLogger.info("Application is running as an exploded project within an IDE more than likely. Info will be displayed in DEV mode.");
            }// end if/else
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getManifestAttributes");
            return mainAttrs;
        }// end method

        /**
         * Creates a container used to display build information from the the MANIFEST.MF file of a jar.
         * 
         * @param mainAttrs
         *        Attributes used to inspect build information
         * @return VBox container used to display Label controls
         */
        private VBox getBuildLayout(Attributes mainAttrs) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getBuildLayout", mainAttrs);
            List<Label> labels = new ArrayList<>();
            Label buildLabel = new Label("Build Information");
            VBox.setMargin(buildLabel, new Insets(20, 0, 0, 0));
            labels.add(buildLabel);
            List<String> buildInfo = getBuildInfo(mainAttrs);
            Label aLabel = null;
            for(Iterator<String> it = buildInfo.iterator();it.hasNext();){
                aLabel = new Label(it.next());
                labels.add(aLabel);
            }// end for
            VBox buildBox = new VBox();
            buildBox.getChildren().addAll(labels);
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getBuildLayout", mainAttrs);
            return buildBox;
        }// end method

        /**
         * This method is used for retrieving build information from the JavaFX application.
         * <p>
         * Note that if this application does not contain the proper metadata within the MANIFEST.MF file, all values will be unknown
         * </p>
         * 
         * @param mainAttrs
         *        Attributes used to inspect build information
         * @return list of build information to be displayed to user
         */
        private List<String> getBuildInfo(Attributes mainAttrs) {
            myInnerLogger.entering(MY_INNER_CLASS_NAME, "getBuildInfo", mainAttrs);
            List<String> buildInfoList = new ArrayList<>();
            if(Constants.IS_JAR){
                myInnerLogger.info("Application is running as a runnable jar, build information will be retrieved from the MANIFEST.MF");
                try{
                    String buildInfo = mainAttrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                    String[] infoSplit = buildInfo.split(" ");
                    String versionName = infoSplit[1] + "." + buildInfo.substring(1, buildInfo.lastIndexOf("-"));
                    String buildId = buildInfo.substring(buildInfo.lastIndexOf("-") + 1, buildInfo.indexOf(" "));
                    String date = infoSplit[2] + " " + infoSplit[3] + " " + infoSplit[4];
                    buildInfoList.add("Version:  " + String.valueOf(versionName));
                    buildInfoList.add("Date:  " + String.valueOf(date));
                    buildInfoList.add("Build Id:  " + String.valueOf(buildId));
                }catch(Exception e){
                    myInnerLogger.log(Level.SEVERE, "Exception occurred while trying to read build information from within the MANIFEST.MF file.  Error is: " + e.getMessage(), e);
                    buildInfoList.add("Version:  unknown");
                    buildInfoList.add("Date:  unknown");
                    buildInfoList.add("Build Id:  unknown");
                }// end if/else
            }else{
                myInnerLogger.info("Application is running as an exploded project within an IDE more than likely. Build information will display In Development Mode");
                buildInfoList.add("Version:  In DEV Mode");
                buildInfoList.add("Date:  " + LocalDate.now().toString());
                buildInfoList.add("Build Id:  In DEV Mode");
            }// end if/else
            myInnerLogger.exiting(MY_INNER_CLASS_NAME, "getBuildInfo");
            return buildInfoList;
        }// end method

        /**
         * Creates a container used to display the UI Hyperlinks.
         * 
         * @return HBox a JavaFX layout
         */
        private HBox createHyperlinkBar() {
            myLogger.entering(MY_CLASS_NAME, "createHyperlinkBar");

            Hyperlink about = createHyperlink("About", new Insets(15, 85, 15, 0));
            about.setOnAction((evt) -> {
                setCenter(page1);
            });// end lambda

            Hyperlink framework = createHyperlink("Framework", new Insets(15, 85, 15, 0));
            framework.setOnAction((evt) -> {
                setCenter(page2);
            });// end lambda

            Hyperlink build = createHyperlink("Software", new Insets(15, 0, 15, 0));
            build.setOnAction((evt) -> {
                setCenter(page3);
            });// end lambda

            HBox hyperlinks = new HBox();
            hyperlinks.setId("hyperlink-bar");
            hyperlinks.setMinHeight(55);

            Region leftSpring = new Region();
            HBox.setHgrow(leftSpring, Priority.ALWAYS);
            Region rightSpring = new Region();
            HBox.setHgrow(rightSpring, Priority.ALWAYS);
            hyperlinks.getChildren().addAll(leftSpring, about, framework, build, rightSpring);
            myLogger.exiting(MY_CLASS_NAME, "createHyperlinkBar");
            return hyperlinks;
        }// end method

        /**
         * Utility that creates a Hyperlink and adds insets to position the Hyperlink within a container.
         * 
         * @param text
         *        the text displayed on the Hyperlink
         * @param insets
         *        the offsets used to place the Hyperlink
         * @return Hyperlink a JavaFX control
         */
        private Hyperlink createHyperlink(String text, Insets insets) {
            myLogger.entering(MY_CLASS_NAME, "createHyperlink", new Object[]{text, insets});
            Hyperlink link = new Hyperlink(text);
            HBox.setMargin(link, insets);
            myLogger.exiting(MY_CLASS_NAME, "createHyperlink");
            return link;
        }// end method

    }// end inner class

}// end class
