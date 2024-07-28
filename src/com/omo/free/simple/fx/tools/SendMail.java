package com.omo.free.simple.fx.tools;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.omo.free.simple.fx.exception.EmailException;
import com.omo.free.simple.fx.managers.LoggingMgr;
import com.omo.free.simple.fx.managers.PropertiesMgr;
import com.omo.free.util.AppUtil;

/**
 * The SendMail class is used to send emails.
 *
 * <p>The SendMail class uses pre-configured email properties, loaded from the application.properties file, for sending
 * emails. The properties are listed below.</p>
 *
 * <p><b>Email Properties</b></p>
 * <ul>
 * <li>email.host</li>
 * <li>email.from</li>
 * <li>email.to</li>
 * <li>email.cc</li>
 * <li>email.bcc</li>
 * <li>email.subject</li>
 * <li>email.store.protocol</li>
 * <li>email.transport.protocol</li>
 * </ul>
 *
 * <p>The uses of the {@code SendMail} class are one-line calls to one of the static {@code send}, methods.<p>
 *
 * @author unascribed
 * @author Richard Salas JCCC
 */
public class SendMail {

    //TODO needs more comments and maybe additional methods...
    // class variables
    private static final String MY_CLASS_NAME = "com.omo.free.simple.fx.tools.SendMail";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private static Properties properties;

    static {
        properties = PropertiesMgr.getInstance().getProperties();
    }

    /**
     * This method constructs the body of an email and sends the email.
     *
     * @param body
     *        The message in an email.
     * @param subject
     *        The subject of the email. Used in the subject line.
     * @param files
     *        The files to attach.
     * @throws EmailException
     *         EmailException
     */
    public static void send(String body, String subject, File[] files) throws EmailException {
        myLogger.entering(MY_CLASS_NAME, "send", new Object[]{body, subject, files});
        String placeholder = "<placeholder>";
        if(body == null){
            EmailException e = new EmailException("Body can not be null but blank would be allowed");
            myLogger.log(Level.SEVERE, "Body of email is null and that is not allowed. Throwing new exception", e);
            myLogger.throwing(MY_CLASS_NAME, "send", e);
            throw e;
        }// end if
        String to = properties.getProperty("email.to");
        String from = properties.getProperty("email.from");
        String cc = properties.getProperty("email.cc");
        String bcc = properties.getProperty("email.bcc");
        String customSubject = null;
        if(AppUtil.isNullOrEmpty(subject)){
            customSubject = properties.getProperty("email.subject");
        }else{
            customSubject =  subject;
        }//end if...else
        String host = properties.getProperty("email.host");
        String storeProtocol = properties.getProperty("email.store.protocol");
        String transportProtocol = properties.getProperty("email.transport.protocol");
        try{
            Properties mailProps = new Properties();
            mailProps.setProperty("mail.host", host);
            mailProps.setProperty("mail.store.protocol", storeProtocol);
            mailProps.setProperty("mail.transport.protocol", transportProtocol);

            Session session = Session.getInstance(mailProps);
            MimeMessage msg = new MimeMessage(session);

            if(cc.contains(placeholder)){
                cc = "";
            }//end if
            if(bcc.contains(placeholder)){
                bcc = "";
            }//end if

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
            msg.setFrom(new InternetAddress(from));
            msg.setSubject(customSubject);
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart.setText(body);
            messageBodyPart.setContent(body, "text/html");
            multipart.addBodyPart(messageBodyPart);

            if(files != null){
                myLogger.fine("adding " + files.length + " attachment(s) to the outgoing email");
                MimeBodyPart attachment;
                DataSource source;
                for(int i = 0, j = files.length;i < j;i++){
                    if(files[i] != null){
                        if(myLogger.isLoggable(Level.CONFIG)){
                            myLogger.config("adding a file to the outgoing email");
                        }// end if
                        attachment = new MimeBodyPart();
                        source = new FileDataSource(files[i]);
                        attachment.setDataHandler(new DataHandler(source));
                        attachment.setFileName(files[i].getName());
                        multipart.addBodyPart(attachment);
                    }// end if
                }// end for
            }// end if
            msg.setContent(multipart);
            Transport.send(msg);
            myLogger.exiting(MY_CLASS_NAME, "send");
        }catch(AddressException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "AddressException caught while working with MimeMessage, from=" + AppUtil.isNull(from) + " to=" + AppUtil.isNull(to), e);
            EmailException ex = new EmailException("AddressException caught while working with MimeMessage, from=" + AppUtil.isNull(from) + " to=" + AppUtil.isNull(to), e);
            myLogger.throwing(MY_CLASS_NAME, "send", ex);
            throw ex;
        }catch(MessagingException e){
            LoggingMgr.getInstance().setAllApplicationLoggersForOneCycle(Level.CONFIG);
            myLogger.log(Level.SEVERE, "MessagingException caught while working with MimeMessage, from=" + AppUtil.isNull(from) + " to=" + AppUtil.isNull(to), e);
            EmailException ex = new EmailException("MessagingException caught while working with MimeMessage, from=" + AppUtil.isNull(from) + " to=" + AppUtil.isNull(to), e);
            myLogger.throwing(MY_CLASS_NAME, "send", ex);
            throw ex;
        }// end try/catch
    }// end send

    /**
     * This method constructs the body of an email and sends the email.
     *
     * @param emailTo comma separated address list
     * @param body
     *        The message in an email.
     * @param subject
     *        The subject of the email. Used in the subject line.
     * @param files
     *        The files to attach.
     * @throws EmailException
     *         EmailException
     */
    public static void send(String emailTo, String body, String subject, File[] files) throws EmailException {
        myLogger.entering(MY_CLASS_NAME, "send", new Object[]{body, subject, files});

        properties.put("email.to", String.valueOf(emailTo));//setting email to
        send(body, subject, files);

        myLogger.exiting(MY_CLASS_NAME, "send");
    }// end send

    /**
     * This method constructs the body of an email and sends the email.
     *
     * @param emailTo comma separated address list
     * @param body
     *        The message in an email.
     * @param subject
     *        The subject of the email. Used in the subject line.
     * @param files
     *        The files to attach.
     * @throws EmailException
     *         EmailException
     */
    public static void send(String emailTo, String emailCc, String body, String subject, File[] files) throws EmailException {
        myLogger.entering(MY_CLASS_NAME, "send", new Object[]{body, subject, files});

        properties.put("email.cc", String.valueOf(emailCc));
        properties.put("email.to", String.valueOf(emailTo));//setting email to
        send(body, subject, files);

        myLogger.exiting(MY_CLASS_NAME, "send");
    }// end send
    
    /**
     * This method constructs the body of an email and sends the email.
     *
     * @param body
     *        The message in an email.
     * @param subject
     *        The subject of the email. Used in the subject line.
     * @param files
     *        The files to attach.
     * @param emailCc the email carbon copied addresses
     *        
     * @throws EmailException
     *         EmailException
     */
    public static void send(String body, String subject, File[] files, String emailCc) throws EmailException {
        myLogger.entering(MY_CLASS_NAME, "send", new Object[]{body, subject, files});

        properties.put("email.cc", String.valueOf(emailCc));
        send(body, subject, files);

        myLogger.exiting(MY_CLASS_NAME, "send");
    }// end send
    
}// end class
