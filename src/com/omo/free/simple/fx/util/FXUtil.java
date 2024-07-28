
package com.omo.free.simple.fx.util;

import java.awt.SplashScreen;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.omo.free.simple.fx.application.SFXViewBuilder;
import com.omo.free.simple.fx.tools.FXAlertOption;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class contains Java FX utility methods used within the SimpleFX Framework.
 *
 * @author Richard Salas JCCC
 * @author modified by Johnnie Stidum JCCC 11/15/2017
 * @author modified by Charles Craft JCCC 12/22/2017
 * @version 1.4
 */
public class FXUtil {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.util.FXUtil";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Default constructor
     */
    private FXUtil() {}

    /**
     * This method when called upon will close the splash screen if there is one being used.
     */
    public static void closeSplashScreen() {
        myLogger.entering(MY_CLASS_NAME, "closeSplashScreen");
        SplashScreen splash = SplashScreen.getSplashScreen();
        if(splash != null){
            splash.close();
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "closeSplashScreen");
    }// end method

    /**
     * This method will attach an {@code OnKeyPressed} action to the Button to fire the onAction event.
     * <p>
     * Pass in the {@code Button} to this method so that it can set your Enter Key to fire the button's onAction event.
     * </p>
     * <p>
     * NOTE: I added this method because I came across a problem where the Enter Key was not firing the onAction when pressed.
     * </p>
     *
     * @param theButton
     *        the button to attach the {@code OnKeyPressed} action event to
     */
    public static void makeEnterKeyFireButtonAction(Button theButton) {
        myLogger.entering(MY_CLASS_NAME, "makeEnterKeyFireButtonAction", theButton);
        if(theButton == null){
            myLogger.warning("The button your passed into this method does not exist!!! Can't attach a listener to it!  Try again.");
            return;
        }// end if

        theButton.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    // changed this to fire default ActionEvent...
                    theButton.fireEvent(new ActionEvent());
                    // theButton.arm();
                    // theButton.fire();
                }// end if
            }// end method
        });// end anonymous inner class
        myLogger.exiting(MY_CLASS_NAME, "makeEnterKeyFireButtonAction");
    }// end method

    /**
     * Builds and displays instructions to the user for how to set up his/her permissions.
     */
    public static void buildAdminHelpPage() {
        myLogger.entering(MY_CLASS_NAME, "buildAdminHelpPage");

        PermissionsWizard wizard = new PermissionsWizard(true);

        Scene scene = new Scene(wizard);
        Stage stage = new Stage();
        stage.setTitle("Permissions Setup");
        stage.initOwner(SFXViewBuilder.getPrimaryStage());
        stage.initModality(Modality.APPLICATION_MODAL);
        Image icon = SFXViewBuilder.getPrimaryStage().getIcons().get(0);
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();

        myLogger.exiting(MY_CLASS_NAME, "permissionStart");
    }// end method

    /**
     * This method should only be used in applications that connect to secure websites. This method will try a handshake attempt with the web server and if the handshake attempt fails will allow the user to add the certificate to his or her java keystore.
     *
     * @param webHostName
     *        (REQUIRED) the host name of the website to connect too.
     * @param phrase
     *        (OPTIONAL) the password to use for accessing the users keystore
     * @param autoAdd
     *        (OPTIONAL) whether or not to display popup to user so that he/she may choose
     */
    public static void checkWebServerCertificate(String webHostName, String phrase, boolean autoAdd) {
        myLogger.entering(MY_CLASS_NAME, "checkWebServerCertificate", new Object[]{webHostName, phrase, autoAdd});
        checkWebServerCertificate(webHostName, Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts"), phrase, autoAdd);
        myLogger.exiting(MY_CLASS_NAME, "checkWebServerCertificate");
    }// end method

    /**
     * This method should only be used in applications that connect to secure websites. This method will try a handshake attempt with the web server and if the handshake attempt fails will allow the user to add the certificate to his or her java keystore.
     *
     * @param webHostName
     *        (REQUIRED) the host name of the website to connect too.
     * @param pathToFile
     *        (REQUIRED) the path to the keystore file to use
     * @param phrase
     *        (OPTIONAL) the password to use for accessing the users keystore
     * @param autoAdd
     *        (OPTIONAL) whether or not to display popup to user so that he/she may choose
     */
    public static void checkWebServerCertificate(String webHostName, Path pathToFile, String phrase, boolean autoAdd) {
        myLogger.entering(MY_CLASS_NAME, "checkWebServerCertificate", new Object[]{webHostName, pathToFile, phrase, autoAdd});

        if(webHostName == null || "".equals(webHostName.trim())){
            myLogger.warning("Web host name is null or empty therefore could not execute handshake test with web server.");
            throw new IllegalArgumentException("webwebHostName must not be empty or null!  Developer must fix his/her logic in order to utilize this method.");
        }// end if

        // splitting the host name for gathering parameters used to establish a handshake with server.
        String[] hostArray = webHostName.split(":");
        String host = hostArray[0];
        int port = (hostArray.length == 1) ? 443 : Integer.parseInt(hostArray[1]);
        char[] passphrase = (phrase == null) ? "changeit".toCharArray() : phrase.toCharArray();

        if(Files.exists(pathToFile, LinkOption.NOFOLLOW_LINKS)){
            InputStream in = null;
            KeyStore ks = null;
            SSLContext context = null;
            TrustManagerFactory tmf = null;
            X509TrustManager defaultTrustManager = null;
            SavingTrustManager tm = null;

            SSLSocketFactory factory = null;
            SSLSocket socket = null;

            OutputStream out = null;
            try{
                myLogger.info("Starting to load the cacerts file: " + pathToFile.toString());
                // obtain the file path and then load the KeyStore
                in = new FileInputStream(pathToFile.toFile());
                ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(in, passphrase);// load
                in.close();

                // get ssl context
                context = SSLContext.getInstance("TLS");
                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);

                defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
                tm = new SavingTrustManager(defaultTrustManager);
                context.init(null, new TrustManager[]{tm}, null);
                factory = context.getSocketFactory();

                myLogger.info("Attempting to open a connection to " + String.valueOf(host) + ":" + String.valueOf(port));
                socket = (SSLSocket) factory.createSocket(host, port);
                socket.setSoTimeout(10000);
                boolean isHandshakeError = false;
                try{
                    myLogger.info("Initiating the handshake with server.");
                    socket.startHandshake();
                    socket.close();
                }catch(SSLException e){
                    if("java.lang.UnsupportedOperationException".equals(e.getMessage())){
                        myLogger.info("certificate is already trusted");
                    }else if(e.getMessage().contains("unable to find valid certification path to requested target")){
                        myLogger.log(Level.WARNING, "Handshake error occurred due to the following reason: " + e.getMessage(), e);
                        isHandshakeError = true;
                    }// end if
                }// end try...catch

                if(!isHandshakeError){
                    return;
                }// end if

                Optional<ButtonType> button = null;
                if(!autoAdd){
                    button = FXAlertOption.showBoldAlert(null, "You have asked to connect securely to \n" + String.valueOf(host) + ", but am unable to connect securely until you\nimport this sites certificate into your cacerts file.\n\nDo you wish to add the certificate now?", "Import " + String.valueOf(host).toUpperCase() + " Certificate", "Security Alert", AlertType.CONFIRMATION, new Image("/com/omo/free/simple/fx/resources/certificate.png"));
                }// end if

                if(autoAdd || ButtonType.OK == button.get()){
                    X509Certificate[] chain = tm.chain;
                    if(chain == null){
                        myLogger.info("Could not obtain the servers certificate chain");
                        return;
                    }// end if

                    myLogger.info("Server sent " + chain.length + " certificate(s):");

                    X509Certificate certificate = null;
                    if(autoAdd){
                        myLogger.info("User is going to have the certificate added to his key store automatically");
                        certificate = chain[chain.length - 1];
                    }else{
                        myLogger.info("Pop up window to user to ask which certificate to add to his key store");
                        certificate = popUpSelectionBoxToUser(chain);
                    }// end if...else

                    if(certificate != null){
                        String alias = host + "-ITSD";
                        ks.setCertificateEntry(alias, certificate);

                        out = new FileOutputStream(pathToFile.toFile());
                        ks.store(out, passphrase);
                        out.close();
                        myLogger.info("Successfully saved new " + alias + " certificate to " + pathToFile.toString());
                    }// end if
                }// end if
            }catch(NoSuchAlgorithmException e){
                myLogger.log(Level.SEVERE, "NoSuchAlgorithmException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(FileNotFoundException e){
                myLogger.log(Level.SEVERE, "FileNotFoundException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(KeyStoreException e){
                myLogger.log(Level.SEVERE, "KeyStoreException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(CertificateException e){
                myLogger.log(Level.SEVERE, "CertificateException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(KeyManagementException e){
                myLogger.log(Level.SEVERE, "KeyManagementException occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred during the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
            }finally{
                // close all resources
                try{
                    if(out != null){
                        out.close();
                    }// end if
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException occurred while trying to close the outputstream used in the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
                }// end try...catch

                try{
                    if(in != null){
                        in.close();
                    }// end if
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException occurred while trying to close the inputstream used in the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
                }// end try...catch

                try{
                    if(socket != null){
                        socket.close();
                    }// end if
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "IOException occurred while trying to close the socket used in the checkWebServerCertificate process.  Error message is: " + e.getMessage(), e);
                }// end try...catch
            }// end try...catch...finally
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "checkWebServerCertificate");
    }// end method

    /**
     * This method will is only called from within this class as it is used within the checkWebServerCertificate method to display a popup to the user.
     *
     * @param chain
     *        the certificate chain
     * @return the X509Certificate the user selected
     * @throws Exception
     *         during user interaction with popup
     */
    private static X509Certificate popUpSelectionBoxToUser(X509Certificate[] chain) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "popUpSelectionBoxToUser", chain);

        Stage popUpWindow = new Stage(StageStyle.UTILITY);
        popUpWindow.setTitle("Certificate Selections");

        // set padding and main selectionBox
        VBox selectionBox = new VBox(20);
        selectionBox.setPadding(new Insets(10));

        Image image = new Image("/com/omo/free/simple/fx/resources/certificate.png");
        Label title = new Label("Which certificate do you want to import?");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));

        ToggleGroup group = new ToggleGroup();
        selectionBox.getChildren().add(title);
        for(int i = 0;i < chain.length;i++){
            X509Certificate cert = chain[i];
            String[] subjectNames = cert.getSubjectDN().toString().split(",");
            String[] issuerNames = cert.getIssuerDN().toString().split(",");

            if(subjectNames.length < 2 || issuerNames.length < 2){
                return null;
            }// end if

            HBox radioBox = new HBox(10);
            radioBox.setAlignment(Pos.CENTER_LEFT);

            RadioButton radio = new RadioButton("  CERTIFICATE " + (i + 1) + " \n  Subject " + subjectNames[0].trim() + " | " + subjectNames[1].trim() + " \n  Issuer " + issuerNames[0].trim() + " | " + issuerNames[1].trim());
            radio.setFont(Font.font("Tahoma", 11));
            radio.setToggleGroup(group);
            ImageView view = new ImageView(image);
            radioBox.getChildren().addAll(view, radio);
            selectionBox.getChildren().addAll(radioBox);
        }// end for

        Button selectBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        selectBtn.setPrefWidth(100);
        cancelBtn.setPrefWidth(100);

        selectBtn.setOnAction(e -> {
            Toggle selected = group.getSelectedToggle();
            if(selected == null){
                return;
            }// end if
            popUpWindow.close();
        });

        cancelBtn.setOnAction(e -> {
            Toggle selected = group.getSelectedToggle();
            if(selected != null){
                selected.setSelected(false);
            }// end if
            popUpWindow.close();
        });// end cancelbtn

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(selectBtn, cancelBtn);
        selectionBox.getChildren().add(buttonBox);

        Scene scene = new Scene(selectionBox);
        popUpWindow.sizeToScene();
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();

        X509Certificate certificate = null;
        Toggle selected = group.getSelectedToggle();
        if(selected != null){
            int index = 0;
            for(int i = 0, j = group.getToggles().size();i < j;i++){
                if(selected == group.getToggles().get(i)){
                    index = i;
                    break;
                }// end if
            }// end for
            certificate = chain[index];
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "popUpSelectionBoxToUser", certificate);
        return certificate;
    }// end method

    /**
     * This method will create a local truststore. This method will copy the currently running java Runtime's cacerts file to local destination.
     *
     * @param parentDirectory
     *        the directory that will contain the local trust store
     * @param trustStoreFilename
     *        the name of the truststore
     */
    public static void createLocalTrustStore(Path parentDirectory, String trustStoreFilename) {
        myLogger.entering(MY_CLASS_NAME, "createLocalTrustStore", new Object[]{parentDirectory, trustStoreFilename});
        if(parentDirectory == null || !Files.exists(parentDirectory)){
            myLogger.warning("parentDirectory is null or does not exist therefore could not create the truststore.");
            throw new IllegalArgumentException("parentDirectory must not be null and must exist!  Developer must fix his/her logic in order to utilize this method.");
        }// end if

        /* below logic is used for getting the default truststore cacerts from users java installation directory */
        Path caCertsPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");

        FileInputStream is = null;
        FileOutputStream fos = null;
        KeyStore keystore = null;
        char[] password = null;

        try{
            myLogger.info("loading cacerts file from: " + String.valueOf(caCertsPath.toString()));
            is = new FileInputStream(caCertsPath.toFile());
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            password = "changeit".toCharArray();
            keystore.load(is, password);

            // create file
            Path truststoreFile = Paths.get(parentDirectory.toString(), trustStoreFilename);
            Files.createFile(truststoreFile);

            // copy installations truststore file into local truststore file
            fos = new FileOutputStream(truststoreFile.toFile());
            keystore.store(fos, password);
        }catch(NoSuchAlgorithmException e){
            myLogger.log(Level.SEVERE, "NoSuchAlgorithmException occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "FileNotFoundException occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }catch(KeyStoreException e){
            myLogger.log(Level.SEVERE, "KeyStoreException occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }catch(CertificateException e){
            myLogger.log(Level.SEVERE, "CertificateException occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred during the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
        }finally{
            try{
                if(is != null){
                    is.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException occurred while trying to close the inputstream used in the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
            }// end try...catch

            try{
                if(fos != null){
                    fos.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException occurred while trying to close the outputstream used in the createLocalTrustStore process.  Error message is: " + e.getMessage(), e);
            }// end try...catch
        }// end try...catch...finally
        myLogger.exiting(MY_CLASS_NAME, "createLocalTrustStore");
    }// end method

    /**
     * This method will set the javax.net.ssl.trustStore property. The only requirement is that it needs to exist.
     * <p>
     * This method should only be used if a private truststore is needed.
     *
     * @param trustStorePath
     *        the location to the truststore.
     */
    public static void setTrustStoreLocationProperty(String trustStorePath) {
        myLogger.entering(MY_CLASS_NAME, "setTrustStoreLocationProperty", trustStorePath);

        if(trustStorePath == null || !Files.exists(Paths.get(trustStorePath))){
            myLogger.warning("trustStorePath is null or does not exist therefore the javax.net.ssl.trustStore property will not be set.");
            throw new IllegalArgumentException("parentDirectory must not be null and must exist!  Developer must fix his/her logic in order to utilize this method.");
        }// end if

        try{
            System.setProperty("javax.net.ssl.trustStore", new File(trustStorePath).getCanonicalPath());
        }catch(Exception e){
            throw new RuntimeException("Problem trying to set truststore location using the getCanonicalPath() method.  Error message is: " + e.getMessage());
        }// end try...catch

        myLogger.exiting(MY_CLASS_NAME, "setTrustStoreLocationProperty");
    }// end

    /**
     * This class is used for checking the servers trusted certificates.
     * <p>
     * The primary responsibility of the TrustManager is to determine whether the presented authentication credentials should be trusted. If the credentials are not trusted, then the connection will be terminated.
     * </p>
     */
    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }// end constructor

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }// end method

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }// end method

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }// end method

    }// end class

}// end class
