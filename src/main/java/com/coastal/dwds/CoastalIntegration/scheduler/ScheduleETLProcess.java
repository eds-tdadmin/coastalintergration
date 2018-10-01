package com.coastal.dwds.CoastalIntegration.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

import com.coastal.dwds.CoastalIntegration.constant.Global;
import com.coastal.dwds.CoastalIntegration.process.ETLGeneratedFiles;

public class ScheduleETLProcess {
	/**
	 * This is method set scheduler job at every day
	 * 
	 * @param prop
	 */
	public void run(Properties prop) {
		ETLGeneratedFiles generateEtl = new ETLGeneratedFiles();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(prop.getProperty(Global.SCHEDULE_HOUR)));
		calendar.set(Calendar.MINUTE, Integer.valueOf(prop.getProperty(Global.SCHEDULE_MINUTES)));
		calendar.set(Calendar.SECOND, Integer.valueOf(prop.getProperty(Global.SCHEDULE_SECONDS)));
		// if calendar time is > than current time set time for next day
		if (calendar.getTime().before(new Date())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		Date date = calendar.getTime();
		Timer time = new Timer();
		time.schedule(generateEtl, date, Integer.valueOf(prop.getProperty(Global.SCHEDULE_PERIOD)));
	}
}
