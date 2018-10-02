package com.coastal.dwds.CoastalIntegration.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

public class DateUtil {

	/**
	 * Add or Substract days from the param date. Returns Date object.
	 * 
	 * @param date
	 *            date to be added.
	 * @param days
	 *            int.
	 * @return Date date
	 */
	public static Date addDays(final Date date, final int days) {
		MutableDateTime dateTime = new MutableDateTime(date);
		dateTime.addDays(days);
		return dateTime.toDate();
	}

	/**
	 * Add or Substract days from the current date. Returns Date object.
	 * 
	 * @param days
	 *            int.
	 * @return Date date
	 */
	public static Date addDaysToCurrentDate(final int days) {

		MutableDateTime dateTime = new MutableDateTime();
		dateTime.setHourOfDay(0);
		dateTime.setMinuteOfHour(0);
		dateTime.setSecondOfMinute(0);
		dateTime.addDays(days);

		return dateTime.toDate();
	}

	/**
	 * Add or Substract days from the current date. Returns Date object.
	 * 
	 * @param days
	 *            int.
	 * @return Date date
	 */
	public static Date addDaysToCurrentDateMidNight(final int days) {
		MutableDateTime dateTime = new MutableDateTime();
		if (days < 0) {
			dateTime.setMillisOfDay(0);
			dateTime.addMillis(-1);
			dateTime.addDays(days + 1);
		} else {
			dateTime.addDays(days);
			// In case
			dateTime.addMinutes(-2);

		}
		return dateTime.toDate();
	}

	/**
	 * Add or Substract days from the current date. Returns String date.
	 * 
	 * @param days
	 *            int.
	 * @return String date.
	 */
	public static String addDaysToCurrentDateString(final int days) {

		MutableDateTime dateTime = new MutableDateTime();
		dateTime.setHourOfDay(0);
		dateTime.setMinuteOfHour(0);
		dateTime.setSecondOfMinute(0);
		dateTime.addDays(days);
		format.get().applyPattern(NEW_DATE_FORMAT);
		return format.get().format(dateTime.toDate());
	}

	/**
	 * return the date of the months day.
	 * 
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date addMinutesToDate(final Date date, final int minutes) {
		MutableDateTime newDate = new MutableDateTime(date);
		newDate.addMinutes(minutes);
		return newDate.toDate();
	}

	/**
	 * Format: yyyyMMdd.
	 * 
	 * @param date
	 *            Date.
	 * @return value finalDate
	 */
	public static Date changeDateFormat(final Date date) {
		String strDate = null;
		Date finalDate = null;
		try {
			format.get().applyPattern("yyyyMMdd");
			strDate = format.get().format(date);
			finalDate = format.get().parse(strDate);
		} catch (Exception d) {
			log.error(d.getCause());
		}
		return finalDate;
	}

	public static boolean checkForZeros(String date) {
		try {
			int val = Integer.parseInt(date);
			if (val != 0) {
				return false;
			}
		} catch (Exception pe) {
			return false;
		}

		return true;
	}

	/**
	 * Compares given date with current date. If given date is later than today's
	 * date returns negative value, if earlier returns positive value else zero.
	 * 
	 * @param date
	 * @return
	 */
	public static int compareWithTodaysDate(String date) {
		// Todays date
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		Date todaysDate = today.getTime();
		Date tempDate = convertStringToDate(date);

		return todaysDate.compareTo(tempDate);
	}

	/**
	 * format: dd/mm/yyyy.
	 * 
	 * @param strDate
	 *            String.
	 * @param oldFormat
	 *            String.
	 * @param newFormat
	 *            String.
	 * @return strDate String
	 */
	public static String convertDateFormat(final String strDate, final String oldFormat, final String newFormat) {
		String result = null;
		try {
			// parse the string into Date object
			format.get().applyPattern(oldFormat);
			Date date = format.get().parse(strDate);
			// parse the date into another format
			format.get().applyPattern(newFormat);
			result = format.get().format(date);
		} catch (ParseException pe) {
			log.error(pe.getMessage());
		}
		return result;
	}

	public static final java.sql.Date convertDateToSqlDate(Date dateWidthTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateWidthTime);
		cal = normalizeDayCalendar(cal);
		return new java.sql.Date(cal.getTime().getTime());
	}

	// take the date and convert into yyyyMMdd format.
	public static String convertDateToyyyymmddFormat(Date d, String dateformat) {
		String returnString = null;
		format.get().applyPattern(dateformat);
		// SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		if (d != null) {
			returnString = format.get().format(d);
		}
		return returnString;
	}

	// CAMELOT
	/**
	 * Get todays date as YYYYMMDD format.
	 * 
	 * @param dateformat
	 * @return String
	 */
	public final static String convertDateToYYYYMMDDFormat(String dateformat) {
		format.get().applyPattern(dateformat);
		Calendar c1 = Calendar.getInstance(); // today
		return format.get().format(c1.getTime());
	}

	// take the date and convert into yyyyMMdd format.
	public static String convertDateToyyyyMMddHHMMSSFormat(Date d, String dateformat) {
		String returnString = null;
		format.get().applyPattern(dateformat);
		if (d != null) {
			returnString = format.get().format(d);
		}
		return returnString;
	}

	// get todays date as YYYYMMDDHHMMSS format.
	public final static String convertDateToYYYYMMDDHHMMSSFormat(String dateformat) {
		format.get().applyPattern(dateformat);
		// SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		Calendar c1 = Calendar.getInstance(); // today
		return format.get().format(c1.getTime());
	}

	/**
	 * format: dd/mm/yyyy.
	 * 
	 * @param strDate
	 *            String.
	 * @param oldFormat
	 *            String.
	 * @param newFormat
	 *            String.
	 * @return strDate String
	 */
	public static String convertStrDateFormat(final String strDate, final String oldFormat, final String newFormat) {
		String result = null;
		try {
			// parse the string into Date object
			format.get().applyPattern(oldFormat);
			Date date = format.get().parse(strDate);
			// parse the date into another format
			format.get().applyPattern(newFormat);
			result = format.get().format(date);
		} catch (ParseException pe) {
			log.error(pe.getMessage());
		}
		return result;
	}

	/**
	 * @param value
	 *            .
	 * @param format
	 *            .
	 * @return .
	 */
	// public static Date getDate(final String value, final String dateformat) {
	// Date result = null;
	// if (StringUtils.isNotEmpty(value)) {
	// try {
	// format.get().applyPattern(dateformat);
	// // DateFormat formatter = new SimpleDateFormat(dateformat);
	// result = (Date) format.get().parse(value.trim());
	// } catch (Exception e) {
	// log.error(">>>>>>: "+dateformat+" ==> "+ value);
	// log.error(e.getMessage());
	// }
	// }
	//
	// return result;
	// }

	/**
	 * format: MM/dd/yyyy.
	 * 
	 * @param dateString
	 *            String.
	 * @return strDate String
	 */
	public static Date convertStringToDate(final String dateString) {
		Date date = null;

		try {
			format.get().applyPattern(DATE_FORMAT);
			date = format.get().parse(dateString);
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		return date;
	}

	public static Date convertStringToDate(final String dateString, final String dateFormat) {
		Date date = null;
		try {
			format.get().applyPattern(dateFormat);
			date = format.get().parse(dateString);
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		return date;
	}

	/**
	 * comparison of dates. if startDate is greater than endDate then result > 0. if
	 * startDate is less than endDate then result < 0. if startDate is equal to
	 * endDate result = 0.
	 * 
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return date Date.
	 */
	public static int dateComparison(final String startDate, final String endDate) {
		int result = convertStringToDate(startDate).compareTo(convertStringToDate(endDate));
		return result;
	}

	/**
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return date Date.
	 */
	public static int dateComparsion(final Date startDate, final Date endDate) {
		// dd/MM/yyyy String Date formate
		int difInDays = (int) ((endDate.getTime() - startDate.getTime()) / (MILISECOND * MINUTE * SECOND * HOUR));
		log.debug("difInDays-------------------------" + difInDays);
		return difInDays;

	}

	/**
	 * dd/MM/yyyy String Date formate
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int dateTimeComparsion(final Date startDate, final Date endDate) {
		int difInSec = (int) ((endDate.getTime() - startDate.getTime()) / (MILISECOND));
		return difInSec;
	}

	/**
	 * Format: dd/MM/yyyy HH:mm:ss
	 * 
	 * @param date
	 *            Date.
	 * @return value String
	 */
	public static String dateTimeToString(final Date date) {
		if (date != null) {
			format.get().applyPattern(DATE_TIME_FORMAT);
			return format.get().format(date);
		}
		return null;
	}

	/**
	 * Format: dd/MM/yyyy.
	 * 
	 * @param date
	 *            Date.
	 * @return value String
	 */
	public static String dateToString(final Date date) {
		if (date != null) {
			format.get().applyPattern(DATE_FORMAT);
			return format.get().format(date);
		}
		return null;
	}

	/**
	 * Format: yyyyMMdd.
	 * 
	 * @param date
	 *            Date.
	 * @return value String
	 */
	public static String dateToString(final Date date, String dateformat) {
		if (date != null) {
			format.get().applyPattern(dateformat);
			return format.get().format(date);
		}
		return null;
	}

	/**
	 * @param secsIn
	 *            time in Seconds.
	 * @return time in hhmmss.
	 */
	public static String formatIntoHHMMSS(final int secsIn) {
		if (secsIn > 0) {
			try {
				int hours = secsIn / TOTAL_TIME, remainder = secsIn % TOTAL_TIME, minutes = remainder / MINUTE,
						seconds = remainder % MINUTE;

				return ((hours < TEEN ? "0" : "") + hours + (minutes < TEEN ? "0" : "") + minutes
						+ (seconds < TEEN ? "0" : "") + seconds);
			} catch (Exception e) {
				log.error(secsIn);
			}
		}
		return "000000";
	}

	/**
	 * @param secsInString
	 *            time in Seconds.
	 * @return time in hhmmss.
	 */
	public static String formatIntoHHMMSS(final String secsInString) {
		try {
			int secsIn = Integer.parseInt(secsInString);
			int hours = secsIn / TOTAL_TIME, remainder = secsIn % TOTAL_TIME, minutes = remainder / MINUTE,
					seconds = remainder % MINUTE;

			return ((hours < TEEN ? "0" : "") + hours + (minutes < TEEN ? "0" : "") + minutes
					+ (seconds < TEEN ? "0" : "") + seconds);
		} catch (Exception e) {
			log.error(secsInString);
		}
		return "000000";
	}

	public static String formatMonthYearDate(String monthYear) {
		try {
			Date parsedDate = new SimpleDateFormat("MMM-yyyy").parse(monthYear);
			SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
			return sdf.format(parsedDate);
		} catch (Exception e) {
			log.error(e.getCause());
			return null;
		}
	}

	/**
	 * Returns current Date object.
	 * 
	 * @return Date date
	 */
	public static Date getCurrentDate() {
		Date currentDate = Calendar.getInstance().getTime();
		return currentDate;
	}

	public static String getDateAsString(Date date, String dateFormat) {
		format.get().applyPattern(dateFormat);
		String strDate = format.get().format(date);
		return strDate;
	}

	/**
	 * Return Date as String derived from BP day.
	 * 
	 * @param bp
	 *            Billing Period day of month.
	 * @return
	 */
	public static String getDateAsStringFromBPDay(final String bp) {

		// SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, new Integer(bp).intValue());
		format.get().applyPattern("dd/MM/yyyy");
		String bpDate = format.get().format(cal.getTime());

		return bpDate;
	}

	public static String getDateAsStringFromBPDayForICPResponse(final String bp) {

		// SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, new Integer(bp).intValue());
		format.get().applyPattern("yyyyMMdd");
		String bpDate = format.get().format(cal.getTime());

		return bpDate;
	}

	/**
	 * @param bpDay
	 *            generate date based on billingPeriod. if Bpday is less than
	 *            current day then Bp belongs to nextMonth. if Bpday is greater than
	 *            current day then Bp belongs to currentMonth.
	 * @return Calendar.
	 */
	public final static Calendar getDateFromBPDay(final String bpDay) {
		try {
			Calendar cal = new GregorianCalendar();
			int currentDay = cal.get(Calendar.DAY_OF_MONTH);
			int bpday = new Integer(bpDay).intValue();
			// The cutt off day will be at previousday:59:59
			// if (bpday <= currentDay) {
			/**
			 * Camelot Change by Mujoko.
			 */
			if (bpday < currentDay) {
				cal.set(Calendar.DAY_OF_MONTH, new Integer(bpDay).intValue());
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
			} else {
				cal.set(Calendar.DAY_OF_MONTH, new Integer(bpDay).intValue());
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
			}
			return cal;

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * @param bpDay
	 *            generate date based on billingPeriod.
	 * @return Calendar.
	 */
	public final static Calendar getDateFromBPDay(final String bpDay, int threshold) {
		try {
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.DAY_OF_MONTH, new Integer(bpDay).intValue());
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + threshold);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			return cal;

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public final static Calendar getDateFromDayMonthYear(final String Day, final String Month, final String Year) {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, new Integer(Day).intValue());
		cal.set(Calendar.MONTH, new Integer(Month).intValue() - 1);
		cal.set(Calendar.YEAR, new Integer(Year).intValue());
		return cal;
	}

	/**
	 * Returns the Day from the date object.
	 * 
	 * @param date
	 * @return Day in the Date object.
	 */
	public static int getDayFromDate(Date date) {

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		return now.get(Calendar.DAY_OF_MONTH);
	}

	public static Long getDays(Date startDate, Date endDate) {
		long diffDays = 0;
		try {
			// Creates two calendars instances
			long milliseconds1 = startDate.getTime();
			long milliseconds2 = endDate.getTime();
			long diff = milliseconds2 - milliseconds1;
			// long diffSeconds = diff / 1000;
			// long diffMinutes = diff / (60 * 1000);
			// diffHours = diff / (60 * 60 * 1000);
			diffDays = diff / (24 * 60 * 60 * 1000);

		} catch (Exception pe) {
			log.error(pe.getMessage());
		}
		return diffDays;
	}

	public static Date getEndTimeOfADate(Date date) {
		MutableDateTime d = new MutableDateTime(date);
		d.setHourOfDay(23);
		d.setMinuteOfHour(59);
		d.setSecondOfMinute(59);
		return d.toDate();
	}

	public static Long getHours(Date startDate, Date endDate) {
		long diffHours = 0;
		try {
			// Creates two calendars instances
			long milliseconds1 = startDate.getTime();
			long milliseconds2 = endDate.getTime();
			long diff = milliseconds2 - milliseconds1;
			// long diffSeconds = diff / 1000;
			// long diffMinutes = diff / (60 * 1000);
			diffHours = diff / (60 * 60 * 1000);
			// long diffDays = diff / (24 * 60 * 60 * 1000);

		} catch (Exception pe) {
			log.error(pe.getMessage());
		}
		return diffHours;
	}

	/**
	 * Returns the month from the date object.
	 * 
	 * @param date
	 * @return month in the Date object.
	 */
	public static Integer getMonth(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		int month = now.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * Returns the month from the date object.
	 * 
	 * @param date
	 * @return month in the Date object.
	 */
	public static String getMonthFromDate(Date date) {

		String strMonth = null;

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		int month = now.get(Calendar.MONTH) + 1;
		if (month < 10) {
			strMonth = "0" + month;
		} else {
			strMonth = "" + month;
		}

		return strMonth;
	}

	/**
	 * @param fromDate
	 *            Date
	 * @param toDate
	 *            Date
	 * @return int no of months between given dates.
	 */
	public static int getMonthsBetweenDates(Date fromDate, Date toDate) {
		DateTime fDate = new DateTime(fromDate.getTime());
		DateTime tDate = new DateTime(toDate.getTime());

		Months months = Months.monthsBetween(fDate, tDate);

		return months.getMonths();
	}

	/**
	 * return the date of the next day.
	 * 
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getNextDayDate(final Date date) {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTime(date);

		if (tomorrow.get(Calendar.DAY_OF_MONTH) != tomorrow.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
		} else {
			tomorrow.set(Calendar.DAY_OF_MONTH, 1);

			if (tomorrow.get(Calendar.MONTH) != tomorrow.getActualMaximum(Calendar.MONTH)) {
				tomorrow.set(Calendar.MONTH, tomorrow.get(Calendar.MONTH) + 1);
			} else {
				tomorrow.set(Calendar.MONTH, 0);
				tomorrow.set(Calendar.YEAR, tomorrow.get(Calendar.YEAR) + 1);
			}
		}

		return tomorrow.getTime();
	}

	/**
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getPreviousDate(final Date date) {
		MutableDateTime nextDay = new MutableDateTime(date);
		nextDay.addDays(-1);
		return nextDay.toDate();
	}

	/**
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getPreviousDate(final Date date, int days) {
		MutableDateTime nextDay = new MutableDateTime(date);
		nextDay.addDays(days);
		return nextDay.toDate();
	}

	/**
	 * return the date of the previous day.
	 * 
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getPreviousDayDate(final Date date) {
		Calendar previousDay = Calendar.getInstance();
		previousDay.setTime(date);

		if (previousDay.get(Calendar.DAY_OF_MONTH) != 1) {
			previousDay.set(Calendar.DAY_OF_MONTH, previousDay.get(Calendar.DAY_OF_MONTH) - 1);
		} else {
			if (previousDay.get(Calendar.MONTH) != 1) {
				previousDay.set(Calendar.MONTH, previousDay.get(Calendar.MONTH) - 1);
			} else {
				previousDay.set(Calendar.MONTH, 12);
				previousDay.set(Calendar.YEAR, previousDay.get(Calendar.YEAR) - 1);
			}
			previousDay.set(Calendar.DAY_OF_MONTH, previousDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		}

		return previousDay.getTime();
	}

	/**
	 * return the date of the months day.
	 * 
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getPreviousMonthDate(final Date date) {
		MutableDateTime previousMonth = new MutableDateTime(date);
		previousMonth.addMonths(-1);
		return previousMonth.toDate();
	}

	public static java.sql.Date getSqlDate(Date utilDate) {
		if (utilDate != null) {
			return new java.sql.Date(utilDate.getTime());
		} else {
			return null;
		}
	}

	public static Date getStartTimeOfADate(Date date) {
		MutableDateTime d = new MutableDateTime(date);
		d.setHourOfDay(0);
		d.setMinuteOfHour(0);
		d.setSecondOfMinute(0);
		return d.toDate();
	}

	public static String getStringDayFromDate(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
		return (day.length() < 2 ? ("0" + day) : day);
	}

	/**
	 * Returns current date as Date object.
	 * 
	 * @return Date
	 */
	public static Date getTodaysDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * Fomat :-Tue Jun 14 00:00:00 SGT 2011
	 * 
	 * @return Date
	 */
	public static Date getTodaysDateZeroTime() {
		MutableDateTime d = new MutableDateTime(new Date());
		d.setSecondOfDay(0);
		return d.toDate();
	}

	/**
	 * @param date
	 *            date param.
	 * @return {@link Date}
	 */
	public static Date getTomorrowDate(final Date date) {
		MutableDateTime nextDay = new MutableDateTime(date);
		nextDay.addDays(1);
		return nextDay.toDate();
	}

	/**
	 * return the date of the next day from current system time
	 * 
	 * @return
	 */
	public static Date getTomorrowDateFromSystem() {
		Calendar tomorrow = Calendar.getInstance();

		if (tomorrow.get(Calendar.DAY_OF_MONTH) != tomorrow.getActualMaximum(Calendar.DAY_OF_MONTH))
			tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
		else {
			tomorrow.set(Calendar.DAY_OF_MONTH, 1);

			if (tomorrow.get(Calendar.MONTH) != tomorrow.getActualMaximum(Calendar.MONTH))
				tomorrow.set(Calendar.MONTH, tomorrow.get(Calendar.MONTH) + 1);
			else {
				tomorrow.set(Calendar.MONTH, 0);
				tomorrow.set(Calendar.YEAR, tomorrow.get(Calendar.YEAR) + 1);
			}
		}

		return tomorrow.getTime();
	}

	/**
	 * Returns the Day from the date object.
	 * 
	 * @param date
	 * @return Day in the Date object.
	 */
	public static int getYearFromDate(Date date) {

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		return now.get(Calendar.YEAR);
	}

	/**
	 * Validates given date is matching with the given date format.
	 * 
	 * @param date
	 *            date param.
	 * @param dateFormat
	 *            Format to check.
	 * @return .
	 */
	public static boolean isValidDate(final String date, final String dateFormat) {

		try {

			if (checkForZeros(date)) {
				return true;
			} else {
				format.get().applyPattern(dateFormat);
				format.get().parse(date);
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void main(String args[]) {
		/*
		 * Calendar today = Calendar.getInstance(); today.set(Calendar.HOUR, 0);
		 * today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0);
		 * today.set(Calendar.MILLISECOND, 0);
		 * 
		 * Date todaysDate = today.getTime();
		 * 
		 * String dateparam = "18/12/2011";
		 * 
		 * Date date2 = convertStringToDate(dateparam);
		 */

		log.debug(isValidDate("20111215144152", "yyyyMMddHHmmss"));

	}

	public static Date makeMidNight(final Date date) {
		MutableDateTime dateTime = new MutableDateTime(date);
		dateTime.setMillisOfDay(0);
		dateTime.addDays(1);
		dateTime.addMillis(-1);
		return dateTime.toDate();
	}

	/**
	 * Add or Substract days from the param date. Returns Date object.
	 * 
	 * @param date
	 *            date to be added.
	 * @param days
	 *            int.
	 * @return Date date
	 */
	public static Date minusDays(final Date date, final int days) {
		MutableDateTime dateTime = new MutableDateTime(date);
		dateTime.addDays(-days);
		return dateTime.toDate();
	}

	/**
	 * Add or Substract days from the param date. Returns Date object.
	 * 
	 * @param date
	 *            date to be added.
	 * @param months
	 *            int.
	 * @return Date date
	 */
	public static Date minusMonths(final Date date, final int months) {

		MutableDateTime dateTime = new MutableDateTime(date);
		dateTime.addMonths(-months);
		return dateTime.toDate();

	}

	/**
	 * Normalize the calendar to contains Day related value only, i.e. reset the
	 * time portion to zero.
	 *
	 * @author TImothy (18 Nov 2011)
	 */
	public static final Calendar normalizeDayCalendar(Calendar cal) {
		if (cal != null) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal;
	}

	/**
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return date Date.
	 */
	public static int stringDateComparsion(final String startDate, final String endDate) {
		// dd/MM/yyyy String Date formate
		Date d1 = convertStringToDate(startDate);
		Date d2 = convertStringToDate(endDate);
		int difInDays = (int) ((d2.getTime() - d1.getTime()) / (MILISECOND * MINUTE * SECOND * HOUR));
		return difInDays;

	}

	public static final Timestamp timeStampFromDate(Date dateWidthTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateWidthTime);
		cal = normalizeDayCalendar(cal);
		return new Timestamp(cal.getTime().getTime());
	}

	public static final Timestamp timeStampToDate(Date dateWidthTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateWidthTime);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTime().getTime());
	}

	/** construct log object to debug. */
	private static final Logger log = LogManager.getLogger(DateUtil.class);

	/**
	 * dateFormat in dd/MM/yyyy.
	 **/
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * newDateFormat.
	 **/
	private static final String NEW_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * newDateFormat.
	 **/
	private static final int TOTAL_TIME = 3600;

	/**
	 * MINUTES.
	 **/
	private static final int MINUTE = 60;

	/**
	 * SCOND.
	 **/
	private static final int SECOND = 60;

	/**
	 * MILISCOND.
	 **/
	private static final int MILISECOND = 1000;

	/**
	 * HOUR.
	 **/
	private static final int HOUR = 24;

	/**
	 * 10.
	 **/
	private static final int TEEN = 10;

	/**
	 * oldDateFormat.
	 **/
	// private static final String OLD_DATE_FORMAT = "MMM d HH:mm:ss z yyyy";
	/**
	 * date format
	 */
	public static final String DATE_YYYYMMDDHHMMSS_FORMAT = "yyyyMMddHHmmss";

	/**
	 * date format
	 */
	public static final String DATE_YYYYMMDD_FORMAT = "yyyyMMdd";

	/**
	 * date format
	 */
	public static final String DATE_DDMMYYYYHHMMSS_FORMAT = "dd-MM-yyyy HH:mm:ss";

	/**
	 * Thread-safe SimpleDateFormat Use this instead of create new SimpleDateFormat
	 * everytime.
	 */
	private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			df.setLenient(false);
			return df;
		}
	};

	/**
	 * Constructor for utility class.
	 */
	private DateUtil() {
	}

	/**
	 * convert date format
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Date convertTimetoDateFormat(long time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String newDate = sdf.format(new Date(time));
		return sdf.parse(newDate);
	}
}
