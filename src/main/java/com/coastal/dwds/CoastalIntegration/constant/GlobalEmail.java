package com.coastal.dwds.CoastalIntegration.constant;

/**
 * @author Madhan
 *
 */
public class GlobalEmail {
	public static final String MAIL_SMTP_HOST = "MAIL_SMTP_HOST";
	public static final String MAIL_SMTP_PORT = "MAIL_SMTP_PORT";
	public static final String MAIL_SMTP_AUTH = "MAIL_SMTP_AUTH";
	public static final String MAIL_SMTP_AUTH_SMTP_STARTTLS_ENABLE = "MAIL_SMTP_AUTH_SMTP_STARTTLS_ENABLE";
	public static final String MAIL_USER = "FROM_MAIL_USER";
	public static final String MAIL_PASSWORD = "FROM_MAIL_PASSWORD";
	public static final String CONTEXT = "text/plain";
	public static final String ERROR_LOG_MAIL_TO_ADDRESS = "ERROR_MAIL_TO_ADDRESS";
	public static final String NOTIFICATION_MAIL_TO_ADDRESS = "NOTIFICATION_MAIL_TO_ADDRESS";
	public static final String DDR_MAIL_TO_ADDRESS = "DDR_MAIL_TO_ADDRESS";
	public static final String MAIL_FROM_ADDRESS_USERNAME = "MAIL_FROM_ADDRESS_USERNAME";// "ETLOutput@petronas.com.my";
	public static final String MAIL_FROM_ADDRESS_PASSWORD = "MAIL_FROM_ADDRESS_PASSWORD";
	public static final String TRUE = "true";
	public static final String SMTP_MAIL_SERVER = "SMTP_MAIL_SERVER";
	public static final String SMTP_PORT = "SMTP_PORT";
	public static final String GENERATE_EXCEL_BODY_MESSAGE = "This output is Generated for ";
	public static final String ERROR_LOG_SUBJECT = "Error Notification - ";
	public static final String ERROR_LOG_BODY_MESSAGE = "There is an error in processing of reading DDR excel report due to "
			+ Global.NEXT_LINE;
	public static final String FOOTER = "This is computer generated message Hence signature is not required";
	public static final String FILE_NOTIFICATION = "Mail Notification - Mandatory fields with null values";
	public static final String FALSE = "false";
	public static final String MAIL_SMTP_SSL_TRUST = "MAIL_SMTP_SSL_TRUST";
}
