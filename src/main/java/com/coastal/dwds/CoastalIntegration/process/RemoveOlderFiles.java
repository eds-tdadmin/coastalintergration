package com.coastal.dwds.CoastalIntegration.process;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.coastal.dwds.CoastalIntegration.constant.Global;

public class RemoveOlderFiles extends TimerTask {
	private static final Logger log = LogManager.getLogger(RemoveOlderFiles.class);
	MailExcelFileData fileData = new MailExcelFileData();

	/**
	 * @see java.util.TimerTask#run() method for create or update standard
	 *      folder structure for Engineer updated files and log file generation
	 */
	@Override
	public void run() {
		System.out.println("Process Started for deleting older files");
		try {
			Properties prop = fileData.loadPropertiesFile();
			File reportEnglineLocation = new File(prop.getProperty(Global.PSC) + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.REPORT_ENGINE_LOCATION) + Global.FOLDER_SEPARATOR);
			File reportEngineFileBkpLoc = new File(reportEnglineLocation + Global.FOLDER_SEPARATOR
					+ prop.getProperty(Global.BACKUP_FOLDER) + Global.FOLDER_SEPARATOR);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(prop.getProperty(Global.REMOVE_SCHEDULE_HOUR)));
			calendar.set(Calendar.MINUTE, Integer.valueOf(prop.getProperty(Global.REMOVE_SCHEDULE_MINUTES)));
			calendar.set(Calendar.SECOND, Integer.valueOf(prop.getProperty(Global.REMOVE_SCHEDULE_SECONDS)));
			calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(prop.getProperty(Global.DURATION_DAYS_DELETE)));

			if (reportEngineFileBkpLoc.exists()) {
				File[] listOfFiles = reportEngineFileBkpLoc.listFiles();
				if (listOfFiles.length > 0) {
					for (File excelFile : listOfFiles) {
						Date fileTime = new Date(excelFile.lastModified());
						if (fileTime.before(calendar.getTime())) {
							excelFile.delete();
						}
					}
				}
			}
			System.out.println("Finished process for deleting older files");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
