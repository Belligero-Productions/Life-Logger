package org.belligero.nautilus.life.logger.utils;

import java.util.Calendar;

public class Utils {
	public static final String getTimeString(Calendar cal) {
		return getTimeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}
	
	public static final String getTimeString(int hour, int minute) {
		return hour + ":" + (minute <= 9 ? "0"+minute : minute);
	}
	
	public static final String getDateString(Calendar cal) {
		return getDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public static final String getDateString(int year, int month, int day) {
		return year + "-" + (month < 9 ? "0"+(month+1) : month+1) + "-" + (day <= 9 ? "0"+day : day);
	}
}
