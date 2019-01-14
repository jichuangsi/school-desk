package com.hanceedu.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class DateUtil {
	private static final String TAG = "HANCE" ; 
	private static final int MS_PER_DAY = 24 * 60 * 60 * 1000 ; 
	private static String[] formats = new String[] {
			"yyyy-MM-dd",
			"MMM d, yyyy",
			"MM-dd",
			"MMM d",
			"M/d"
	};
	
	private static void setDateInfo(DateInfo di, long days) {
		di.days = days ;
		long time = days *MS_PER_DAY ;
		for (int j = 0 ; j < 5 ; j++) {
			String format = formats[j] ;
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
			//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String s = sdf.format(new Date(time)) ;
			//System.out.println(s) ;
			switch (j) {
			case 0 :
				di.yyyymmdd = s ;
				break ;
			case 1 :
				di.mmmdyyyy = s ;
				break ;
			case 2 :
				di.mmdd = s ;
				break ;
			case 3 :
				di.mmmd = s ;
				break ;
			case 4 :
				di.md = s ;
				break ;		
			}
			
		}
	}


	public static DateInfo[] getRecent7Days(long time) {
		DateInfo[] dates = new DateInfo[7] ;
		Date date ;
		if (time == 0) {
			date = new Date() ;
		}else{
			date = new Date(time) ;			
		}
		long ms = date.getTime() ;
		long days = ms / MS_PER_DAY ;
		days -= 6 ;
		for (int i = 0 ; i < 7 ; i++) {
			DateInfo di = new DateInfo() ;
			dates[i] = di ;
			setDateInfo( di, days) ;
			days++ ;
		}
		
		return dates ;

	}


	public static DateInfo[] getRecent77Days(long time) {
		DateInfo[] dates = new DateInfo[7] ;
		Date date ;
		if (time == 0) {
			date = new Date() ;
		}else{
			date = new Date(time) ;			
		}
		long ms = date.getTime() ;
		long days = ms / MS_PER_DAY ;
		days -= 48 ;
		for (int i = 0 ; i < 7 ; i++) {
			DateInfo di = new DateInfo() ;
			dates[i] = di ;
			setDateInfo( di, days) ;
			days += 7 ;
		}

		return dates ;
	}
	
	public static DateInfo[] getRecent7Weeks(long time) {
		DateInfo[] dates = new DateInfo[7] ;
		Date date ;
		if (time == 0) {
			date = new Date() ;
		}else{
			date = new Date(time) ;			
		}
		long ms = date.getTime() ;
		long days = ms / MS_PER_DAY ;
		Calendar cal = Calendar.getInstance() ;
		cal.setTimeInMillis(days * MS_PER_DAY) ;
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) ;
		//System.out.println("dayOfWeek=====" + dayOfWeek) ;
		days -= dayOfWeek ;
		days -= 41 ;
		for (int i = 0 ; i < 7 ; i++) {
			DateInfo di = new DateInfo() ;
			dates[i] = di ;
			setDateInfo( di, days) ;
			days += 7 ;
		}

		return dates ;
	}
	
	public static DateInfo[] getRecent7Months(long time) {
		DateInfo[] dates = new DateInfo[7] ;
		Date date ;
		if (time == 0) {
			date = new Date() ;
		}else{
			date = new Date(time) ;			
		}
		long ms = date.getTime() ;
		long days = ms / MS_PER_DAY ;
		Calendar cal = Calendar.getInstance() ;
		cal.setTimeInMillis(days * MS_PER_DAY) ;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int month = cal.get(Calendar.MONTH) ; //Jan = 0
		//System.out.println("month=====" + month) ;
		month -= 6 ;
		for (int i = 0 ; i < 7 ; i++) {
			cal.set(Calendar.MONTH, month);
			ms = cal.getTimeInMillis() ;
			days = ms / MS_PER_DAY ;
			DateInfo di = new DateInfo() ;
			dates[i] = di ;
			setDateInfo( di, days) ;
			month++ ;
		}

		return dates ;
	}	

	public static int getCurrentMinutesInDay() {

		Date date = new Date() ;
		Calendar cal = Calendar.getInstance() ;
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE) ;

	}
}
