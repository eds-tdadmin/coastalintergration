package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.coastal.dwds.CoastalIntegration.constant.Global;

public class MailFilesProcess extends TimerTask {
	private static final Logger log = LogManager.getLogger(MailFilesProcess.class);
	CompressFileGeneration compProcess = new CompressFileGeneration();
	MailExcelFileData fileData = new MailExcelFileData();

	/**
	 * @see java.util.TimerTask#run() method for create or update standard folder
	 *      structure for Engineer updated files and log file generation
	 */
	@SuppressWarnings("unused")
	@Override
	public void run() {
		System.out.println("checking or creating directories");
		boolean success = false;
		Properties prop = null;
		try {
			prop = fileData.loadPropertiesFile();
			// System.out.println("crating or updating backup and process location");
			File reportEnglineLocation = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR);
			File reportEngineProcessFileLocation = new File(reportEnglineLocation + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.PROCESS_FOLDER) + Global.FOLDER_SEPARATOR);
			if (!reportEngineProcessFileLocation.exists()) {
				reportEngineProcessFileLocation.mkdirs();
			}
			File flagFile = new File(Global.FLAG_FILE_DIR);
			if (flagFile.exists()) {
				flagFile.delete();
			}

			success = compProcess.sendCompressFile(prop);
			System.out.println(" Finished the process of sending DDR Reports.");

		} catch (IOException e) {
			log.error(e.getMessage());
			sendMail(e.getMessage(), prop);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			sendMail(e.getMessage(), prop);
		} catch (MessagingException e) {
			log.error(e.getMessage());
			sendMail(e.getMessage(), prop);
		} catch (NullPointerException e) {
			log.error(e.getMessage());
			sendMail("The data input is " + e.getMessage(), prop);
		} catch (Exception e) {
			log.error(e.getMessage());
			sendMail(e.getMessage(), prop);
		}
	}

	private void sendMail(String message, Properties prop) {
		EmailSender sender = new EmailSender();
		try {
			sender.sendErrorLogEmail("Process is Interrupted... ",
					"Process has been stopped due to " + Global.NEXT_LINE + message, prop);
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}
}
