package com.omo.free.simple.fx.util;

/**
 * Constants class - Contains constants used by the SimpleFX Framework.
 * 
 * @author Richard Salas
 * @since 1.0
 */
public class Constants {
    /* Required properties files for this application. */
    public static final String SECRET_PASSWORD = "docsecretpassword";
    public static final String LINESEPERATOR = System.getProperty("line.separator");
    public static final String DEFAULT_SIMPLE_FX_ICON = "/com/omo/free/simple/fx/resources/SimpleFXIcon(60x37).png";
    public static boolean IS_JAR;

    /* constants set up at start up of application framework */
    public static String START_IN_DIR_PATH = "";
    public static String APPLICATION_SHARED_DIRECTORY = "";
    public static String APP_FILE_LOCATION = "";
    public static String FRAMEWORK_FILE_LOCATION = "";
    public static String CURRENT_VERSION_LABEL = "";
    public static String CURRENT_VERSION_DATE_LABEL = "";
    public static String NEWER_VERSION_LABEL = "";
    public static String NEWER_VERSION_DATE_LABEL = "";
    public static String CURRENT_JAR_NAME = "";
    public static String FUTURE_JAR_NAME = "";
    // XXX This is the application name constant with a public access modifier that will be supplied to EMU at runtime - jal000is
    public static String APP_NAME = "";

    // TODO remove this from the framework eventually as this is not associated in any way rts000is
    public static final StringBuffer TEXTPAD_HELP_MSG = new StringBuffer("TextPad cannot be used due to it not existing in the value of your Path Environment Variable.").append(LINESEPERATOR).append("Below are the steps needed in adding TextPad to your value of your Path variable.").append(LINESEPERATOR).append(LINESEPERATOR).append("Step 1) WINDOWS KEY + Pause/Break (keyboard shortcut)").append(LINESEPERATOR).append("Step 2) Advanced System Properties (link or tab within window)").append(LINESEPERATOR).append("Step 3) Environment Variables... (button)").append(LINESEPERATOR).append("Step 4) Make sure that the value of the environment variable 'Path' includes the home directory").append(LINESEPERATOR).append("              path to where TextPad.exe is located For example: C:\\Program Files\\TextPad 5.").append(LINESEPERATOR).append(LINESEPERATOR).append("\t\tEXAMPLE: ").append(LINESEPERATOR).append("\t\t\t\tVariable name:  Path").append(LINESEPERATOR)
            .append("\t\t\t\tVariable value:  %JAVA_HOME%\\bin;C:\\Program Files\\Text Pad 5;").append(LINESEPERATOR).append(LINESEPERATOR).append("Step 5) Open Texpad and then do the following: ").append(LINESEPERATOR).append("              from the menu bar select Configure --> Preferences... --> General").append(LINESEPERATOR).append("Step 6) Make sure that \"Allow multiple files on the command line\" checkbox is checked and Click Apply/OK").append(LINESEPERATOR).append("Step 7) Close TextPad.").append(LINESEPERATOR).append("Step 8) Close the Application and start it up again.");

}// end class
