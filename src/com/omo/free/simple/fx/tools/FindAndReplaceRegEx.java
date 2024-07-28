/**
 *
 */
package com.omo.free.simple.fx.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will handles finding text within a file and replacing it--By line only. Quickly wrote this class to handle what I needed it to do. This class may be updated occasionally.
 *
 * <p>This class implements the {@code Callable} interface which is designed for classes whose instances are potentially executed by another thread.
 * This does not mean that you have to run this class in a separate thread.</p>
 *
 * <p><b>Example</b></p>
 * <p>The following shows an example of instantiating this class as well as running the find and replace logic.</p>
 * <pre><code>
.
.
.
File theFile = new File(C:/MyTestFiles/FileNeedsValuesReplaced.txt")
FindAndReplaceRegEx regex = new FindAndReplaceRegEx(theFile, "(.*)(placeholder)(.*)(placeholder)(.*)", "ReplacementText", 2, 4);
try{
    if(!regex.call()){
        myLogger.warning("Could not successfully replace values within the " + theFile.getName() + " file.");
    }//end if
}catch(Exception e){
    myLogger.log(Level.SEVERE, "Exception occurred while trying replace values within " + theFile.getName() + " Error is: " + e.getMessage(), e);
}//end try...catch

.
.
.
 * </code></pre>
 * @author Richard Salas JCCC, January 08, 2016
 *
 */
public class FindAndReplaceRegEx implements Callable<Boolean> {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.FindAndReplaceRegEx";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private BufferedReader reader;
    private BufferedWriter writer;
    private Pattern pattern;
    private int[] groupToReplace;
    private String replacementValue;
    private File source;

    /* other possible values */
    private Map<Pattern, String> patternMap;
    private Map<String, Integer> groupMap;
    private boolean multiReplace;

    /**
     * Creates an instance of the FindAndReplaceRegEx class with the give {@code File} to read, regular expression, value which is used for replacement, and also the group numbers or number to be replaced.
     *
     * @param fileToRead the file that is searched through for replacing values
     * @param regex the regular expression to use for finding data within a file to replace.
     * @param valueUsedForReplacement value that is used when replacing values
     * @param groupNumToReplace the group that will be replaced if there is a group
     */
    public FindAndReplaceRegEx(File fileToRead, String regex, String valueUsedForReplacement, int... groupNumToReplace) {
        myLogger.entering(MY_CLASS_NAME, "FindAndReplaceRegEx", new Object[]{fileToRead, regex, valueUsedForReplacement, groupNumToReplace});
        this.source = fileToRead;
        this.pattern = Pattern.compile(regex);
        this.groupToReplace = groupNumToReplace;
        this.replacementValue = valueUsedForReplacement;
        myLogger.exiting(MY_CLASS_NAME, "FindAndReplaceRegEx");
    }// end constructor

    /**
     * Constructor used to be able to pass more than one group and regular expression so that multi find and replaces can be done.
     *
     * @param fileToRead the file that is search through for replacing values
     * @param regexMap the regular expression map along with the replace value | key=regular expression; value=valueToReplace
     * @param groupNumToReplaceMap the regular expression and group number to be replace | key=regular expression; value=groupNumToReplace
     */
    public FindAndReplaceRegEx(File fileToRead, Map<String, String> regexMap, Map<String, Integer> groupNumToReplaceMap) {
        myLogger.entering(MY_CLASS_NAME, "FindAndReplaceRegEx", new Object[]{fileToRead, regexMap, groupNumToReplaceMap});
        this.source = fileToRead;
        this.patternMap = new HashMap<Pattern, String>();
        this.groupMap = groupNumToReplaceMap;
        // entry map
        Iterator<Entry<String, String>> it = regexMap.entrySet().iterator();
        while(it.hasNext()){
            Entry<String, String> entry = it.next();
            this.patternMap.put(Pattern.compile(entry.getKey()), entry.getValue());
        }//end while
        this.multiReplace = true;
        myLogger.exiting(MY_CLASS_NAME, "FindAndReplaceRegEx");
    }// end constructor

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return true | false based upon whether or not the thread failed to do work
     * @throws Exception thrown during the execution of reading and writing to a file
     */
    @Override public Boolean call() throws Exception {
        myLogger.entering(MY_CLASS_NAME, "call");
        boolean success = false;
        if(!source.exists()){
            throw new FileNotFoundException("Could not file the source file " + source.getName());
        }// end if

        try{
            File tmp = File.createTempFile("mig", "tmp", source.getParentFile());
            reader = new BufferedReader(new FileReader(source));
            writer = new BufferedWriter(new FileWriter(tmp));
            String line = reader.readLine();
            while(line != null){
                writer.append(multiReplace ? processMultipleReplaces(line) : process(line));
                line = reader.readLine();
                if(line != null){
                    writer.newLine();
                }// end if
            }// end while
            reader.close();
            writer.close();
            reader = null;
            writer = null;
            // rename the file
            source.delete();
            success = tmp.renameTo(source);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "Exception occurred while trying ");
            success = false;
        }finally{
            if(reader != null){
                reader.close();
            }// end if
            if(writer != null){
                writer.close();
            }// end if
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "call");
        return success;
    }

    /**
     * Method that will process a line of data looking for stuff to change
     *
     * @param line
     *        the string that is checked against the regular expression to see if it matches...if it does it will return a newly formatted line
     * @return the line that may or may not have changed
     */
    private String process(String line) {
        myLogger.entering(MY_CLASS_NAME, "process", line);
        Matcher m = pattern.matcher(line);
        if(m.find()){
            myLogger.info("found a match on line " + String.valueOf(line));
            int groups = m.groupCount();
            if(groups == 0){
                return replacementValue;
            }// end if
            StringBuffer sb = new StringBuffer();
            int replaceTotalCount = groupToReplace.length;
            int count = 0;
            for(int i = 1; i <= groups; i++){
                if(i != groupToReplace[count]){
                    sb.append(m.group(i));
                }else if(i == groupToReplace[count]){
                    sb.append(replacementValue);
                    if(count < replaceTotalCount && (count + 1) != replaceTotalCount){
                        count++;
                    }//end if
                }//end if
            }//end for
            return sb.toString();// new replacement values
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "process", line);
        return line;
    }// end method

    /**
     * This method will loop through the patternMap to check to see if there needs to be multiple replaces.
     * @param line the line to be processed
     * @return the modified line or the orginal line
     */
    private String processMultipleReplaces(String line) {
        myLogger.entering(MY_CLASS_NAME, "processMultipleReplaces", line);
        Iterator<Entry<Pattern, String>> it = patternMap.entrySet().iterator();
        while(it.hasNext()){
            Entry<Pattern, String> entry = it.next();
            Matcher m = entry.getKey().matcher(line);
            if(m.find()){
                myLogger.info("found a match on line " + String.valueOf(line));
                int groups = m.groupCount();
                if(groups == 0){
                    return entry.getValue();
                }// end if
                StringBuffer sb = new StringBuffer();
                int replaceGroup = groupMap.get(entry.getKey().pattern());
                for(int i = 1;i <= groups;i++){
                    if(i != replaceGroup){
                        sb.append(m.group(i));
                    }else{
                        sb.append(entry.getValue());
                    }// end if
                }// end for
                return sb.toString();// new replacement values
            }// end if
        }//end while
        myLogger.exiting(MY_CLASS_NAME, "processMultipleReplaces", line);
        return line;
    }//end method

}// end class
