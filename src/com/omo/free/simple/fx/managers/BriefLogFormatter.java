package com.omo.free.simple.fx.managers;

import static com.omo.free.simple.fx.util.Constants.LINESEPERATOR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * BriefLogFormatter class used to format each line of logging before outputting to a file.
 *
 * @author unascribed
 * @author Joseph Burris JCCC
 * @author modified by Richard Salas JCCC
 */
public class BriefLogFormatter extends Formatter {
    private static final String MY_CLASS_NAME = " com.omo.free.simple.fx.managers.BriefLogFormatter";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Format the given log record and return the formatted string.
     *
     * <p>The resulting formatted String will normally include a localized and formated version of the {@code LogRecord}'s message field.</p>
     *
     * <p>The Formatter.formatMessage convenience method can (optionally) be used to localize and format the message field.</p>
     *
     * @param record
     *        the log record to be formatted.
     * @return the formatted log record
     */
    @Override public String format(LogRecord record) {
        myLogger.setLevel(Level.OFF);
        String loggerName = record.getLoggerName();
        if(loggerName == null){
            loggerName = "root";
        } // end if
        StringBuilder output = new StringBuilder().append("[").append(record.getLevel()).append("|").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(record.getMillis()))).append('|').append(Thread.currentThread().getName()).append("|").append("t=").append(record.getThreadID()).append("|");

        String temp = record.getSourceClassName() + '#' + record.getSourceMethodName();
        String temp2 = "";
        if(temp.length() > 50){
            temp2 = temp.substring((temp.length() - 47));
            temp2 = "..." + temp2;
        }else{
            int j = (50 - temp.length());
            for(int i = 0;i < j;i++){
                temp2 = temp2 + ".";
            } // end for
            temp2 = temp + temp2;
        } // end if...else
        output.append(temp + "]: " + formatMessage(record) + ' ');
        if(record.getThrown() != null){
            Throwable thrown = record.getThrown();
            StackTraceElement[] theTrace = thrown.getStackTrace();
            int j = theTrace.length;
            String temp3 = "";
            for(int i = 0;i < j;i++){
                StackTraceElement theElement = theTrace[i];
                temp3 = LINESEPERATOR + "    at " + theElement.getClassName() + '#' + theElement.getMethodName() + "(Line:" + theElement.getLineNumber() + ')';
                output.append(temp3);
            } // end for
            output.append(LINESEPERATOR);
        } // end if
        output.append(LINESEPERATOR);
        return output.toString();
    }//end method

}//end class
