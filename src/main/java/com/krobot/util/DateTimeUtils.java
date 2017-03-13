package com.krobot.util;

import org.joda.time.DateTime;

public class DateTimeUtils {

	public static String getStringCurDate() {
		return new DateTime().toString("yyyy-MM-dd");
	}
	
}
