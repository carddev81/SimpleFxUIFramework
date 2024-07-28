/**
 *
 */
package com.omo.free.simple.fx.tools;

import static com.omo.free.simple.fx.util.Constants.LINESEPERATOR;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCConnection;
import com.ibm.as400.access.AS400JDBCDatabaseMetaData;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.JobLog;
import com.ibm.as400.access.QueuedMessage;
import com.omo.free.simple.fx.managers.LoggingMgr;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.simple.fx.util.Constants;
import com.omo.free.util.AppUtil;
/**
 * The CreateConnection class is used for establishing and destroying a connection to a database.
 *
 * <p>This class currently has 6 implementation methods used for connecting to a specific database. The 6 {@link Database} types
 * are shown within the table below.</p>
 *
 * <table width="75%" border="1">
 *  <tr><th>Database Type</th><th>Driver Jar Name</th><th>Property Names *** </th></tr>
 *  <tr><td>AS400</td><td>jt400.jar</td><td>as400.user.id<br>as400.password<br>as400.jdbc.url</td></tr>
 *  <tr><td>MYSQL</td><td>mysql-connector-java-X.X.X-bin.jar</td><td>mysql.user.id<br>mysql.password<br>mysql.jdbc.url</td></tr>
 *  <tr><td>MSSQL</td><td>sqljdbcX.jar</td><td>mssql.user.id<br>mssql.password<br>mssql.jdbc.url</td></tr>
 *  <tr><td>ORACLE</td><td>ojdbcX.jar</td><td>oracle.user.id<br>oracle.password<br>oracle.jdbc.url</td></tr>
 *  <tr><td>HSQL</td><td>hsqldb-X.X.X.jar</td><td>hsql.user.id<br>hsql.password<br>hsql.jdbc.url</td></tr>
 *  <tr><td>DB2LUW</td><td>db2jccX.jar</td><td>db2luw.user.id<br>db2luw.password<br>db2luw.jdbc.url</td></tr>
 * <caption>Database Types</caption>
 * </table>
 *
 * <p><b>*** The Property Name along with the value must be created within the application.properties file in order
 * to successfully access the Database</b></p>
 *
 * <p>Please note that any {@code Database} Type that you intend on accessing you must add the assoicated properties to the application.properties file.
 * Refer to the table above for the Database Types property names.</p>
 *
 * <p>The {@code CreateConnection} class also provides the ability to create custom connections by calling its {@link CreateConnection#getConnection(String, String, String)}
 * method. First you will need to register the Database Driver by calling the {@link CreateConnection#registerDBDriver(String)} method.</p>
 *
 * @author Richard Salas JCCC
 * @version 1.0
 */
public class CreateConnection {

    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.CreateConnection";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    private CreateConnection(){ }//end constructor

    /**
     * This method will return a connection to an AS400 database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     * <tr><td>jt400.jar</td><td>as400.user.id<br>as400.password<br>as400.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the AS400 database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getAS400Connection() throws SQLException {
        myLogger.entering(MY_CLASS_NAME, "getAS400Connection gets a connection to the AS400 database");
        Database db = Database.AS400;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "AS400 database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getAS400Connection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getAS400Connection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }// end method

    /**
     * This method will return a connection to a MYSQL database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     * <tr><td>mysql-connector-java-X.X.X-bin.jar</td><td>mysql.user.id<br>mysql.password<br>mysql.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the MYSQL database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getMYSQLConnection() throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "getMYSQLConnection gets a connection to the MySQL database");
        Database db = Database.MYSQL;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "MYSQL database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getMYSQLConnection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getMYSQLConnection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }//end method

    /**
     * This method will return a connection to a MSSQL database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     * <tr><td>sqljdbcX.jar</td><td>mssql.user.id<br>mssql.password<br>mssql.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the MSSQL database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getMSSQLConnection() throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "getMSSQLConnection gets a connection to the MSSQL database");
        Database db = Database.MSSQL;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "MSSQL database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getMSSQLConnection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getMSSQLConnection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }//end method

    /**
     * This method will return a connection to a HSQL database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     *  <tr><td>hsqldb-X.X.X.jar</td><td>hsql.user.id<br>hsql.password<br>hsql.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the HSQL database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getHSQLConnection() throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "getHSQLConnection gets a connection to the HSQL database");
        Database db = Database.HSQL;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "HSQL database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getAS400Connection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getHSQLConnection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }//end method

    /**
     * This method will return a connection to a DB2LUW database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     * <tr><td>db2jccX.jar</td><td>db2luw.user.id<br>db2luw.password<br>db2luw.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the DB2LUW database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getDB2LUWConnection() throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "getDB2LUWConnection gets a connection to the DB2LUW database");
        Database db = Database.DB2LUW;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "DB2LUW database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getDB2LUWConnection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getDB2LUWConnection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }//end method

    /**
     * This method will return a connection to a ORACLE database.
     *
     * <p><b>Notes To Developer</b></p>
     * <ol>
     * <li>Make sure to add the Database Driver Jar onto the classpath.</li>
     * <li>Make sure that you add the associated property names/values into the application.properties file supplied by the SimpleFX Framework</li>
     * </ol>
     *
     * <p>Refer to the table below for associated Database driver jar and property names.</p>
     * <table border="1">
     * <tr><th>Driver Jar Name</th><th>Property Names</th></tr>
     * <tr><td>ojdbcX.jar</td><td>oracle.user.id<br>oracle.password<br>oracle.jdbc.url</td></tr>
     * <caption>Driver Properties</caption>
     * </table>
     * @return connection to the ORACLE database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getORACLEConnection() throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "getORACLEConnection gets a connection to the ORACLE database");
        Database db = Database.ORACLE;
        if(db.hasConfigErrors()){
            myLogger.log(Level.SEVERE, "ORACLE database configuration is incomplete. Logged Error messages is as follows: " + db.getRecordedErrorMessage());
            SQLException sqle = new SQLException(db.getRecordedErrorMessage());
            myLogger.throwing(MY_CLASS_NAME, "getORACLEConnection", sqle);
            throw sqle;
        }// end if

        myLogger.exiting(MY_CLASS_NAME, "getORACLEConnection");
        return getConnection(db.getUserId(), db.getPassword(), db.getUrl());
    }//end method


    /**
     * This method will return a {@code Connection} to a database using the given {@code userId}, {@code password}, and {@code url}.
     *
     * <p>This method allows you the ability to connect to any database that you choose.</p>
     *
     * <p><b>Notes To Developer</b></p>
     * <p>Make sure that you register the Database Driver by calling the {@link #registerDBDriver(String)} method. The {@code registerDBDriver(String)}
     * method ONLY needs to be called once within your application.</p>
     *
     * @param userId the user id used for connecting to the database
     * @param password the password used for connecting to the database
     * @param url the jdbc url (possibly appended with system properties) used for connecting to the database
     * @return connection to a database
     * @exception SQLException if cannot get a connection and will provide information from the database access error or other errors.
     */
    public static Connection getConnection(String userId, String password, String url) throws SQLException {
        myLogger.entering(MY_CLASS_NAME, "getConnection", new Object[]{userId, password, url});
        Connection con = null;
        try{
            con = DriverManager.getConnection(url, userId, password);
        }catch(Exception e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "Exception occurred while trying to get a connection to the database using database url=" + String.valueOf(url) + ", userId=" + String.valueOf(userId) + ", password=" + (AppUtil.isNullOrEmpty(password) ? "no value" : "*******"), e);
            SQLException sqle = new SQLException(e.getMessage());
            myLogger.throwing(MY_CLASS_NAME, "registerDBDriver", sqle);
            throw sqle;
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "getAS400Connection", con);
        return con;
    } // end method

    /**
     * This method registers the Database Driver that is used for establishing connections to a database.
     *
     * <p>This method ONLY needs to be called once by your application so that it can load your driver class into the JVM.</p>
     *
     * <p>
     * Typical usage is:
     * <pre>
     * <code>
     *      CreateConnection.registerDBDriver("org.hsqldb.jdbcDriver");
     * </code>
     * </pre>
     *
     * @param fullyQualifiedDatabaseClassName the fully qualified class name of the driver class used for establishing connections to a database.
     * @throws SQLException if cannot find driver class
     */
    public static void registerDBDriver(String fullyQualifiedDatabaseClassName) throws SQLException{
        myLogger.entering(MY_CLASS_NAME, "registerDBDriver()", fullyQualifiedDatabaseClassName);
        myLogger.info("registering the database driver class to the jvm " + String.valueOf(fullyQualifiedDatabaseClassName));
        try{
            if(Driver.HSQL_DRIVER.getDriver().equalsIgnoreCase(fullyQualifiedDatabaseClassName)){//stop the crazy hsql api from taking over the java.util logging.
                System.setProperty("hsqldb.reconfig_logging", "false");//need to make sure to set this to false
            }//end if
            Class.forName(fullyQualifiedDatabaseClassName).newInstance();
        }catch(Exception e){
            String errorMessage = "Unable to register the JDBC Driver. Make sure the classpath is correct. Driver name= " + String.valueOf(fullyQualifiedDatabaseClassName) + " Error Message is: " + e.getMessage();
            myLogger.log(Level.SEVERE, errorMessage, e);
            SQLException sqle = new SQLException(errorMessage);
            myLogger.throwing(MY_CLASS_NAME, "registerDBDriver", sqle);
            throw sqle;
        }// end catch
        myLogger.exiting(MY_CLASS_NAME, "registerDBDriver()");
    }//end method

    /**
     * This method closes the connection, prepared statement and result set.
     *
     * @param conn
     *        The connection used to access the database.
     * @param pstmt
     *        The prepared statement used to query the database.
     * @param rs
     *        The result set returned from the prepared statement.
     */
    public static void destroyObjects(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        myLogger.entering(MY_CLASS_NAME, "destroyObjects", new Object[]{conn, pstmt, rs});
        try {
            if (rs != null) {
                if (rs.getWarnings() != null) {
                    myLogger.info(logWarningsFromResultSet(rs));
                } // end if
                rs.close();
            } // end if
        } catch (SQLException e) {
            myLogger.log(Level.SEVERE, "Exception - Destroy ResultSet - " + e.getMessage() + logSQLException(e), e);
        } // end try/catch

        try {
            if (pstmt != null) {
                if (pstmt.getWarnings() != null) {
                    myLogger.info(logWarningsFromStatement(pstmt));
                } // end if
                pstmt.close();
            } // end if
        } catch (SQLException e) {
            myLogger.log(Level.SEVERE, "Exception - Destroy Statement - " + e.getMessage() + logSQLException(e), e);
        } // end try/catch

        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }// end if
                conn.close();
            } // end if
        } catch (SQLException e) {
            myLogger.log(Level.SEVERE, "Exception - Destroy Connection - " + e.getMessage() + logSQLException(e), e);
        } // end try/catch
        myLogger.exiting(MY_CLASS_NAME, "destroyObjects");
    } // end method

    /**
     * This method used to log any SQL Exceptions.
     *
     * @param ex
     *        The SQLException to be logged.
     * @return string
     */
    public static String logSQLException(SQLException ex) {
        myLogger.entering(MY_CLASS_NAME, "logSQLException", ex);
        SQLException nextException = ex;
        StringBuffer sb = new StringBuffer();
        sb.append(LINESEPERATOR + "---SQLExceptions---" + LINESEPERATOR);
        while (nextException != null) {
            if (nextException instanceof SQLException) {
                sb.append(nextException.toString() + LINESEPERATOR);
                sb.append("SQLState: " + ((SQLException) nextException).getSQLState() + LINESEPERATOR);
                sb.append("Error Code: " + ((SQLException) nextException).getErrorCode() + LINESEPERATOR);
                sb.append("Message: " + nextException.getMessage() + LINESEPERATOR);
                Throwable t = ex.getCause();
                while (t != null) {
                    sb.append("Cause: " + t + LINESEPERATOR);
                    t = t.getCause();
                } // end while
                nextException = nextException.getNextException();
            } // end if
        } // end while
        myLogger.exiting(MY_CLASS_NAME, "logSQLException", sb.toString());
        return sb.toString();
    } // end method

    /**
     * This method used to log any warnings from a result set.
     *
     * @param rs
     *        The ResultSet to be logged.
     * @return string
     * @exception SQLException
     *         SQLException
     */
    public static String logWarningsFromResultSet(ResultSet rs) throws SQLException {
        myLogger.entering(MY_CLASS_NAME, "logWarningsFromResultSet", rs);
        if (rs == null) {
            myLogger.exiting(MY_CLASS_NAME, "logWarningsFromResultSet");
            return "Error from CreateConnection.logWarningsFromResultSet(): ResultSet is null.";
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "logWarningsFromResultSet");
        return logableWarnings(rs.getWarnings());
    }// end method

    /**
     * This method used to log any warnings from a statement.
     *
     * @param stmt
     *        The Statement to be logged.
     * @return string
     * @exception SQLException
     *         SQLException
     */
    public static String logWarningsFromStatement(Statement stmt) throws SQLException {
        myLogger.entering(MY_CLASS_NAME, "logWarningsFromStatement", stmt);
        if (stmt == null) {
            myLogger.exiting(MY_CLASS_NAME, "logWarningsFromStatement");
            return "Error from CreateConnection.logWarningsFromStatement(): Statement is null.";
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "logWarningsFromStatement");
        return logableWarnings(stmt.getWarnings());
    }// end method

    /**
     * This method used to log any warnings.
     *
     * @param warning
     *        The SQLWarning to be logged.
     * @return string
     * @exception SQLException
     *         SQLException
     */
    public static String logableWarnings(SQLWarning warning) throws SQLException {
        myLogger.entering(MY_CLASS_NAME, "logableWarnings", warning);
        if (warning != null) {
            StringBuffer sb = new StringBuffer();
            sb.append(LINESEPERATOR + "---Warning---" + LINESEPERATOR);
            while (warning != null) {
                sb.append("Message: " + warning.getMessage() + LINESEPERATOR);
                sb.append("SQLState: " + warning.getSQLState() + LINESEPERATOR);
                sb.append("Vendor error code: " + warning.getErrorCode() + LINESEPERATOR);
                warning = warning.getNextWarning();
            } // end while
            myLogger.exiting(MY_CLASS_NAME, "logableWarnings", sb.toString());
            return sb.toString();
        } // end if
        myLogger.exiting(MY_CLASS_NAME, "logableWarnings");
        return null;
    } // end method

    /**
     * This method used to print any SQL Exceptions.
     *
     * @param ex
     *        The SQKException to be logged.
     */
    public static void printSQLException(SQLException ex) {
        myLogger.entering(MY_CLASS_NAME, "printSQLException", ex);
        SQLException nextException = ex;
        while (nextException != null) {
            if (nextException instanceof SQLException) {
                nextException.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) nextException).getSQLState());
                System.err.println("Error Code: " + ((SQLException) nextException).getErrorCode());
                System.err.println("Message: " + nextException.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    String errorMsg = "Cause: " + t;
                    myLogger.log(Level.SEVERE, errorMsg, t);
                    t = t.getCause();
                } // end while
                nextException = nextException.getNextException();
            } // end if
        } // end while
        myLogger.exiting(MY_CLASS_NAME, "printSQLException");
    } // end method

    /**
     * This method will attempt to get a job log which contains helpful information to diagnose problems that may occur during the execution of a
     * query. You need to pass in your logger object from the calling class and also the connection object which contains the metadata needed for
     * the job log that was created.
     *
     * @param callingClassLogger the calling classes logger which is used for logging the job log using the caller's logger
     * @param con the database connection
     */
    public static void getAS400JobLog(Logger callingClassLogger, Connection con) {
        myLogger.entering(MY_CLASS_NAME, "Entering getAS400JobLog(...) method", new Object[]{callingClassLogger, con});
        // check to see if connection is null before attempting to retrieve the job log from the as400.
        if (con != null) {
            try {
                myLogger.finest("Attempting to retrieve the Job Log from the as400 system to retrieve verbose logging of why this query failed.");
                // first cast meta data to as400JdbcMetadata then get the as400 connection from within the metadata...
                AS400JDBCDatabaseMetaData meta = (AS400JDBCDatabaseMetaData) con.getMetaData();
                AS400JDBCConnection as400con = (AS400JDBCConnection) meta.getConnection();
                // get the as400 system object for getting the jobs...
                AS400 system = as400con.getSystem();
                // try to get jobs here first from the database service.
                Job[] jobs = system.getJobs(AS400.DATABASE);
                // getting the job identity which should be something like QZDASOINIT QUSER 123457
                // this is used for checking to make sure we are getting the correct job log which
                // we should always be getting but this is just to make sure.
                String jobIdentity = as400con.getServerJobIdentifier();
                myLogger.finest("Job Identity: " + jobIdentity);
                // check to see if jobs is not null and if the job[0] job number exists within the jobIdentity string variable
                if (jobs != null && jobIdentity.contains(jobs[0].getNumber())) {
                    myLogger.finest("Job Number: " + jobs[0].getNumber());
                    // establish a newLine variable for use in the message...
                    String newLine = System.getProperty("line.separator");
                    // should only be one job here for this connection...
                    JobLog jobLog = jobs[0].getJobLog();
                    // get the queued messages array from the job log...i'm passing -1 in as
                    // first parm to get all messages and 0 as this value doesn't matter because -1 was passed as first parm.
                    QueuedMessage[] messages = jobLog.getMessages(-1, 0);
                    // This is where the message is built...
                    if (messages != null && messages.length > 0) {
                        int i = 0;
                        StringBuffer queuedMessages = new StringBuffer();
                        queuedMessages
                                .append("AS400 JOB LOG START")
                                .append(newLine)
                                .append("****************************************************************************************************************")
                                .append(newLine);
                        for (QueuedMessage message : messages) {
                            if (i == 0) {
                                // THIS IS THE HEADER LINE FOR THE JOB NUMBER.
                                queuedMessages.append(message.getText()).append(newLine);
                            }// end if
                            if (message.getSeverity() > 0) {
                                queuedMessages.append("ERROR: ").append(message.getText()).append(newLine)
                                        .append("\t CAUSE/POSSIBLE RECOVERY: ").append(message.getHelp()).append(newLine).append(newLine);
                            }// end if
                            i++;
                        }// end for
                        queuedMessages
                                .append("****************************************************************************************************************")
                                .append(newLine).append("AS400 JOB LOG END");

                        // output the queued messages to the log file.
                        callingClassLogger.severe(queuedMessages.toString());
                    }// end if
                } else {
                    myLogger.severe("Could not retrieve the AS400 Job Log");
                }// end if/else
            } catch (Exception ex) {
                myLogger.severe("Error while trying to retrieve the Job Log from the AS400 ex.getMessage() = " + ex.getMessage());
            }// end try/catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "Exiting DBUtil.getAS400JobLog(...) method.");
    }// end getAS400JobLog


    /**
     * Driver enum used for storing Database Driver fully qualified names and messages used by the {@code Database} types for initializing configuration
     * per Database type.
     *
     * @author Richard Salas JCCC
     */
    enum Driver{
        AS400_DRIVER("com.ibm.as400.access.AS400JDBCDriver", "Driver jar is named similiar to jt400.jar"),
        MYSQL_DRIVER("com.mysql.jdbc.Driver", "Driver jar is named similiar to mysql-connector-java-X.X.X-bin.jar"),
        MSSQL_DRIVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "Driver jar is named similar to sqljdbc4.jar"),
        ORACLE_DRIVER("oracle.jdbc.driver.OracleDriver", "Driver jar is named similar to ojdbc7.jar"),
        HSQL_DRIVER("org.hsqldb.jdbcDriver", "Driver jar is named similar to hsqldb-X.X.X.jar"),
        DB2LUW_DRIVER("com.ibm.db2.jcc.DB2Driver", "Driver jar is named similar to db2jcc4.jar");

        String driver;//driver class
        String jarNameMessage;//only a possible name

        /**
         * Creates a Driver enum
         *
         * @param className the driver fully qualified class names
         * @param jarNameMessage the jar messages
         */
        Driver(String className, String jarNameMessage){
            this.driver = className;
            this.jarNameMessage = jarNameMessage;
        }//end constructor

        /**
         * This method returns the driver class name.
         * @return the driver fully qualified class name
         */
        public String getDriver(){
            return this.driver;
        }//end method

        /**
         * This method returns the driver jar name message.
         * @return the driver message
         */
        public String getJarNameMessage(){
            return this.jarNameMessage;
        }//end method

    }//end enum


    /**
     * The Database enum contains configuration settings specific to each {@code Database} type. A call to a Database type will validate
     * if settings are configured correctly by the user and if not a message will be logged to the user.
     *
     * @author Richard Salas JCCC
     */
    enum Database{

        AS400("as400.user.id", "as400.password", "as400.jdbc.url", Driver.AS400_DRIVER),
        MYSQL("mysql.user.id", "mysql.password", "mysql.jdbc.url", Driver.MYSQL_DRIVER),
        MSSQL("mssql.user.id", "mssql.password", "mssql.jdbc.url", Driver.MSSQL_DRIVER),
        ORACLE("oracle.user.id", "oracle.password", "oracle.jdbc.url", Driver.ORACLE_DRIVER),
        HSQL("hsql.user.id", "hsql.password", "hsql.jdbc.url", Driver.HSQL_DRIVER),
        DB2LUW("db2luw.user.id", "db2luw.password", "db2luw.jdbc.url", Driver.DB2LUW_DRIVER);

        static final String MY_ENUM_NAME = "com.omo.free.simple.fx.tools.CreateConnection.Database";
        private Logger enumLogger = Logger.getLogger(MY_ENUM_NAME);

        String userId;
        String password;
        String url;
        boolean hasConfigErrors;
        StringBuffer errorMessage;

        /**
         * Creates a Database Type using the given user id property name, password property name, url property name and {@code Driver} type.
         *
         * @param userIdPropertyNm the name of the property containing the user id value
         * @param passwordPropertyNm the name of the property containing the password value
         * @param urlPropertyNm the name of the property containing the jdbc url value
         * @param driver the {@code Driver} type associated with the {@code Database}
         */
        Database(String userIdPropertyNm, String passwordPropertyNm, String urlPropertyNm, Driver driver) {
            enumLogger.entering(MY_ENUM_NAME, "Database", new Object[]{userIdPropertyNm, passwordPropertyNm, urlPropertyNm, driver});
            //setting all properties first!!!
            Properties props = PropertiesMgr.getInstance().getProperties();
            this.userId = props.getProperty(userIdPropertyNm);
            this.password = props.getProperty(passwordPropertyNm);
            this.url = props.getProperty(urlPropertyNm);

            boolean checkPassed = true;
            if(this.userId == null){
                appendToErrorMsg("Missing '" + userIdPropertyNm + "' in application.properties file.", true);
                checkPassed = false;
            }// end if
            if(this.password == null){
                appendToErrorMsg("Missing '" + passwordPropertyNm + "' in application.properties file.", true);
                checkPassed = false;
            }// end if
            if(this.url == null){
                appendToErrorMsg("Missing '" + urlPropertyNm + "' in application.properties file.", true);
                checkPassed = false;
            }// end if

            if(!checkPassed){
            	//commented out this logging message so that user does not think he has a bug in his app.
                //myLogger.severe("Missing one or more properties. Would receive unpredicted results, check logs!");
                hasConfigErrors = true;
            }else{
                myLogger.info("registering the database driver class to the jvm " + driver.getDriver());
                try{
                    if(driver == Driver.HSQL_DRIVER){//stop the crazy hsql api from taking over the java.util logging.
                        System.setProperty("hsqldb.reconfig_logging", "false");//need to make sure to set this to false
                    }//end if
                    Class.forName(driver.getDriver()).newInstance();
                }catch(Exception e){
                	//commented out this logging message so that user does not think he has a bug in his app.
                    //myLogger.log(Level.SEVERE, "Unable to register the JDBC Driver. Make sure the classpath is correct. Driver name= " + driver.getDriver() + "; " + driver.getJarNameMessage(), e);
                    appendToErrorMsg("Unable to register the JDBC Driver. Make sure the driver jar is added to the classpath. Driver name= " + driver.getDriver() + "; " + driver.getJarNameMessage(), false);
                    hasConfigErrors = true;
                }// end catch
            }// end if
            enumLogger.exiting(MY_ENUM_NAME, "Database");
        }//end constructor

        /**
         * This method returns the database user id.
         * @return the database user id
         */
        public String getUserId(){
            return this.userId;
        }//end method

        /**
         * This method returns the database password.
         * @return the database password
         */
        public String getPassword(){
            return this.password;
        }//end method

        /**
         * This method returns the database url
         * @return the database url
         */
        public String getUrl(){
            return this.url;
        }//end method

        /**
         * This method returns whether or not the Database type has configuration errors
         * @return {@code true} or {@code false} value on whether or not the Database type has configuration errors
         */
        public boolean hasConfigErrors(){
            return this.hasConfigErrors;
        }//end method

        /**
         * This method will append message to the {@code errorMessage} container using the given {@code message} and {@code isMissingProp} flag.
         * @param message message used for errors
         * @param isMissingProp {@code true} or {@code false} value on whether or not there was a missing property
         */
        private void appendToErrorMsg(String message, boolean isMissingProp) {
            enumLogger.entering(MY_CLASS_NAME, "appendMissingPropertyMsg", message);
            if(errorMessage==null){
                if(isMissingProp){
                    errorMessage = new StringBuffer("Missing REQUIRED PROPERTIES from within the application.properties file.");
                    errorMessage.append(LINESEPERATOR).append("Please open the application.properties file and add the following").append(LINESEPERATOR).append("properties to it:");
                    errorMessage.append(LINESEPERATOR).append(LINESEPERATOR);
                    errorMessage.append(message);
                }else{
                    errorMessage = new StringBuffer(message);
                }//end if
                return;
            }//end if
            errorMessage.append(Constants.LINESEPERATOR).append(message);
            enumLogger.exiting(MY_ENUM_NAME, "appendToErrorMsg");
        }//end method

        /**
         * This method will return the Database's Type recorded error message
         * @return the error message recorded during configuration setup of the Database Type
         */
        public String getRecordedErrorMessage(){
            if(errorMessage==null){
               return "No Error Messages";
            }//end if
            return errorMessage.toString();
        }//end method

    }//end enum

}//end class
