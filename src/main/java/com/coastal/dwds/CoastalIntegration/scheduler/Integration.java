package com.coastal.dwds.CoastalIntegration.scheduler;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.process.MailExcelFileData;

public class Integration {
	
	/** construct log object to debug. */
	private static final Logger log = LogManager.getLogger(Integration.class);
	static MailExcelFileData fileData = new MailExcelFileData();

	/**
	 * @param args
	 *            This is the main class to run all scheduler jobs
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		boolean success = false;
		Properties prop = new Properties();
		File currFolder = new File(System.getProperty("user.dir"));
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		try {
			prop = fileData.loadPropertiesFile();
			if (!prop.isEmpty()) {
				
				File file = new File(prop.getProperty(Global.LOG_FILE));
				// this will force a reconfiguration
				context.setConfigLocation(file.toURI());
				
				// To schedule and process the ETL jar file
				System.out.println("ETL Scheduler has been started.....");
				ScheduleETLProcess etlProcess = new ScheduleETLProcess();
				if (!prop.isEmpty()) {
					etlProcess.run(prop);
				}
				
				// To email files
				System.out.println("Scheduler for Sending files has been started.....");
				ScheduleEmailFiles emailProcess = new ScheduleEmailFiles();
				if (!prop.isEmpty()) {
					emailProcess.run(prop);
				}
				
				// To delete the older files
				System.out.println("Scheduler for Remove the older files.....");
				ScheduleRemoveOlderFiles removeFiles = new ScheduleRemoveOlderFiles();
				if (!prop.isEmpty()) {
					removeFiles.run(prop);
				}
				success = true;
			} else {
				log.error("The PSCJava.properties file is empty.. Please add content and restart the process");
				Thread.sleep(60000);
				success = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (!success) {
			log.error("Please copy the PSCJava.properties file into " + currFolder + " directory and restart the process");
			Thread.sleep(60000);
		}
	}
}
