package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.constant.GlobalCellsConstants;
import com.coastal.dwds.CoastalIntegration.constant.GlobalEmail;
import com.coastal.dwds.CoastalIntegration.constant.GlobalErrors;

/**
 * @author Madhan
 *
 */
public class EmailSender {
	private static final Logger log = LogManager.getLogger(EmailSender.class);

	/**
	 * @param outputFile
	 * @param name
	 * @param dateFolder
	 * @param reportNo
	 * @param prop
	 * @return
	 * @throws IOException
	 */

	public boolean sendPSCMail(File outputFile, String name, String reportNo, String reportDate, String wellName,
			Properties prop) throws IOException {
		String port = "";
		String mailFrom = "";
		String password = "";
		String host = "";
		String subject;
		String[] attachFiles;
		String message = "";
		boolean success = false;
		try {
			// SMTP info
			host = prop.getProperty(GlobalEmail.SMTP_MAIL_SERVER);
			port = prop.getProperty(GlobalEmail.SMTP_PORT);
			mailFrom = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_USERNAME);
			password = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_PASSWORD);

			System.out.println("Host :: " + host);

			// message info
			String mailProp = prop.getProperty(GlobalEmail.DDR_MAIL_TO_ADDRESS);
			String[] mailTo = mailProp.split(",");
			subject = Global.DRILLING_DATA + Global.FOR + reportDate + name + "; Well:" + wellName + "_"
					+ GlobalCellsConstants.SUBJECT_REPORT_NO + ":" + reportNo;
			message = GlobalEmail.GENERATE_EXCEL_BODY_MESSAGE + name;
			// attachments
			attachFiles = new String[1];
			attachFiles[0] = outputFile.toString();
			success = sendEmailWithAttachments(host, port, mailFrom, password, mailTo, subject, message, attachFiles,
					prop);
		} catch (Exception ex) {
			log.error(GlobalErrors.MAIL_NETWORK_ERROR + prop.getProperty(GlobalEmail.SMTP_PORT) + Global.NEXT_LINE);
			log.error(ex.getMessage() + Global.NEXT_LINE + GlobalErrors.NETWORK_ADMIN);
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return success;
	}

	/**
	 * @param sb
	 * @param prop2
	 * @throws IOException
	 *             mail notification for mandatory fields having null values
	 * @throws MessagingException
	 */
	public boolean sendMailNotification(StringBuffer sb, File fileLocation, Properties prop)
			throws IOException, MessagingException {

		// get SMTP server properties
		Properties properties = new Properties();
		boolean success = false;
		try {

			final String userName = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_USERNAME);
			final String password = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_PASSWORD);
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_HOST),
					prop.getProperty(GlobalEmail.SMTP_MAIL_SERVER));
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_PORT), prop.getProperty(GlobalEmail.SMTP_PORT));
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH_SMTP_STARTTLS_ENABLE), GlobalEmail.FALSE);
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH), GlobalEmail.TRUE);
			properties.put(prop.getProperty(GlobalEmail.MAIL_USER), userName);
			properties.put(prop.getProperty(GlobalEmail.MAIL_PASSWORD), password);

			// session with an authenticator
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			// creates a new e-mail message
			String mailProp = prop.getProperty(GlobalEmail.NOTIFICATION_MAIL_TO_ADDRESS);
			String[] mailTo = mailProp.split(",");
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(userName));
			InternetAddress[] addressTo = new InternetAddress[mailTo.length];
			for (int i = 0; i < mailTo.length; i++) {
				addressTo[i] = new InternetAddress(mailTo[i]);
			}
			msg.setRecipients(RecipientType.TO, addressTo);
			msg.setSubject(GlobalEmail.FILE_NOTIFICATION);
			msg.setSentDate(new Date());

			StringBuffer footer = new StringBuffer();
			footer.append(Global.NEXT_LINE);
			footer.append(Global.NEXT_LINE);

			System.out.println(sb.toString());

			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(sb.toString() + footer + Global.FILE_LOCATION + " "
					+ Inet4Address.getLocalHost().getHostAddress() + Global.NEXT_LINE + fileLocation.toString(),
					GlobalEmail.CONTEXT);

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// sets the multi-part as e-mail's content
			msg.setContent(multipart);

			// sends the e-mail
			Transport.send(msg);
			success = true;
			System.out.println("Notification Email has been sent..");
		} catch (Exception e) {
			log.error(GlobalErrors.MAIL_FAILURE + e.getMessage());
		}
		return success;
	}

	/**
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param toAddress
	 * @param subject
	 * @param message
	 * @param attachFiles
	 * @param prop
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean sendEmailWithAttachments(String host, String port, final String userName, final String password,
			String[] mailTo, String subject, String message, String[] attachFiles, Properties prop)
			throws AddressException, MessagingException, IOException {
		boolean result = false;
		// sets SMTP server properties
		Properties props = new Properties();
		try {

			props.put(prop.getProperty(GlobalEmail.MAIL_SMTP_HOST), host);
			props.put(prop.getProperty(GlobalEmail.MAIL_SMTP_PORT), port);
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			// props.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH_SMTP_STARTTLS_ENABLE),
			// GlobalEmail.FALSE);
			props.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH), GlobalEmail.TRUE);
			props.put(prop.getProperty(GlobalEmail.MAIL_USER), userName);
			props.put(prop.getProperty(GlobalEmail.MAIL_PASSWORD), password);
			// session with an authenticator
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			// creates a new e-mail message
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(userName));
			InternetAddress[] addressTo = new InternetAddress[mailTo.length];
			for (int i = 0; i < mailTo.length; i++) {
				addressTo[i] = new InternetAddress(mailTo[i]);
			}
			msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setSentDate(new Date());

			StringBuffer footer = new StringBuffer();
			footer.append(Global.NEXT_LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(Global.LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(GlobalEmail.FOOTER);

			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message + footer, GlobalEmail.CONTEXT);

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// adds attachments
			if (attachFiles != null && attachFiles.length > 0) {
				for (String filePath : attachFiles) {
					if (filePath != null) {
						MimeBodyPart attachPart = new MimeBodyPart();

						try {
							attachPart.attachFile(filePath);
						} catch (IOException ex) {
							log.error(ex.getMessage());
							ex.printStackTrace();
							sendErrorLogEmail(GlobalEmail.ERROR_LOG_SUBJECT, ex.getMessage(), prop);
						}
						multipart.addBodyPart(attachPart);
					}
				}
			}
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
			System.out.println(GlobalErrors.DISPLAY_SUCCESS);
			result = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param host
	 * @param port
	 * @param subject
	 * @param message
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean sendErrorLogEmail(String subject, String message, Properties prop)
			throws AddressException, MessagingException, IOException {
		boolean success = false;
		// get SMTP server properties
		Properties properties = new Properties();
		try {

			final String userName = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_USERNAME);
			final String password = prop.getProperty(GlobalEmail.MAIL_FROM_ADDRESS_PASSWORD);
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_HOST),
					prop.getProperty(GlobalEmail.SMTP_MAIL_SERVER));
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_PORT), prop.getProperty(GlobalEmail.SMTP_PORT));
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH_SMTP_STARTTLS_ENABLE), GlobalEmail.FALSE);
			properties.put(prop.getProperty(GlobalEmail.MAIL_SMTP_AUTH), GlobalEmail.TRUE);
			properties.put(prop.getProperty(GlobalEmail.MAIL_USER), userName);
			properties.put(prop.getProperty(GlobalEmail.MAIL_PASSWORD), password);
			// session with an authenticator
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			// creates a new e-mail message
			Message msg = new MimeMessage(session);
			String mailProp = prop.getProperty(GlobalEmail.ERROR_LOG_MAIL_TO_ADDRESS);
			String[] mailTo = mailProp.split(",");
			msg.setFrom(new InternetAddress(userName));// userName
			InternetAddress[] addressTo = new InternetAddress[mailTo.length];
			for (int i = 0; i < mailTo.length; i++) {
				addressTo[i] = new InternetAddress(mailTo[i]);
			}
			msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			StringBuffer footer = new StringBuffer();
			footer.append(Global.NEXT_LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(Global.LINE);
			footer.append(Global.NEXT_LINE);
			footer.append(GlobalEmail.FOOTER);
			String bodyMessage = message + Global.NEXT_LINE + footer;
			messageBodyPart.setContent(bodyMessage, GlobalEmail.CONTEXT);
			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
			success = true;
			System.out.println("Process Error Notification has been sent");
		} catch (Exception e) {
			log.error(GlobalErrors.MAIL_FAILURE + "  " + e.getMessage());
		}
		return success;

	}
}
