package com.example.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	 * Checks whether the dateString is in a valid yyyy-mm-dd format
	 * 
	 * @param dateString
	 * @return true/false
	 */
	public static boolean isValidDateString(String dateString) {
		Date date = DateUtils.parseDate(dateString);
		if (date == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Convert dateString in a valid yyyy-mm-dd format to java.util.Date
	 * 
	 * @param dateString
	 * @return java.util.Date
	 */
	public static Date parseDate(String dateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (dateString != null) {
			try {
				Date date = simpleDateFormat.parse(dateString);
				if (simpleDateFormat.format(date).equals(dateString.trim())) {
					return date;
				}
			} catch (ParseException e) {
			}
		}
		return null;
	}

}
