package com.coastal.dwds.CoastalIntegration.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.process.MailFilesProcess;

public class ScheduleEmailFiles {
	/**
	 * This method is set for scheduler job at every day
	 * 
	 * @param prop
	 */
	public void run(Properties prop) {
		Timer time = null;
		MailFilesProcess engineerUpdate = new MailFilesProcess();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(prop.getProperty(Global.ENGINEER_SCHEDULE_HOUR)));
		calendar.set(Calendar.MINUTE, Integer.valueOf(prop.getProperty(Global.ENGINEER_SCHEDULE_MINUTES)));
		calendar.set(Calendar.SECOND, Integer.valueOf(prop.getProperty(Global.ENGINEER_SCHEDULE_SECONDS)));
		// if calendar time is > than current time set time for next day
		if (calendar.getTime().before(new Date())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		Date date = calendar.getTime();
		time = new Timer();
		time.schedule(engineerUpdate, date, Integer.valueOf(prop.getProperty(Global.SCHEDULE_PERIOD)));
	}
}
