package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.coastal.dwds.CoastalIntegration.constant.Global;

public class ETLGeneratedFiles extends TimerTask {
	private static final Logger log = LogManager.getLogger(ETLGeneratedFiles.class);
	
	CopyFilesProcess copyprocess = new CopyFilesProcess();
	ReadExcelProcess excelprocess = new ReadExcelProcess();
	MailExcelFileData fileData = new MailExcelFileData();

	/**
	 * @see java.util.TimerTask#run() method for create or update standard
	 *      folder structure for Engineer updated files and log file generation
	 */
	public void run() {
		System.out.println("creating or updating directories");
		boolean success = false;
		boolean copyResult = false;
		Properties prop = null;
		try {
			prop = fileData.loadPropertiesFile();
			
			File reportEngineLocation = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR);
			File reportEngineProcessFileLocation = new File(reportEngineLocation + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.PROCESS_FOLDER) + Global.FOLDER_SEPARATOR);
			if (!reportEngineProcessFileLocation.exists()) {
				reportEngineProcessFileLocation.mkdirs();
			}
			File flagFile = new File(Global.FLAG_FILE_DIR);
			if (flagFile.exists()) {
				flagFile.delete();
			}
			System.out.println(" Started executing ETL Script to generate DDR Reports...");
			
			copyResult = copyprocess.copyFiles(prop);
			if(copyResult == true) {
				excelprocess.readExcelFile(prop);
			}
			
			if (success) {
				System.out.println(" Finished the process of generating DDR Reports.");
				success = true;
			}
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
			sender.sendErrorLogEmail("Process is Interrupted... ", "Process has been stopped due to " + Global.NEXT_LINE
					+ message, prop);
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}

	
}
