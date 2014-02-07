package com.sixbuilder.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SerializableRecordHelper {

	public static final String UNINIT_DATE="uninitDate";
	
	public static final String FIELD_SEPERATOR   = Character.toString((char) 31);
	
	private static final ThreadLocal<DateFormat>dtf = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy.MMM.dd 'at' HH:mm:ss.SSS z");
		}
	};
	
	public static DateFormat getDateTimeFormat() {
		return dtf.get();
	}
	
	public static String getDateTimeString(long date) {
		if(date==0)
			return UNINIT_DATE;
		String s=null;
		try {
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis(date);
			Date d=c.getTime();
			s=SerializableRecordHelper.getDateTimeFormat().format(d);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static Date getDate(String dateTimeString) throws Exception {
		if(dateTimeString.equals(UNINIT_DATE)||dateTimeString.trim().length()==0) {
			Date d=new Date(0);
			return d;
		}
		// get the date
		Date d= SerializableRecordHelper.getDateTimeFormat().parse(dateTimeString);
		return d;
	}
	
	public static long getDateTimeLong(String date) throws Exception {
		if(date.equals(UNINIT_DATE)||date.trim().length()==0)
			return 0;
		Date d=SerializableRecordHelper.getDateTimeFormat().parse(date);
		return d.getTime();
	}
	
	private static final ThreadLocal<DateFormat>sdtf = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy.MMM.dd 'at' HH:mm z");
		}
	};
	
	public static DateFormat getShortDateTimeFormat() {
		return sdtf.get();
	}
	
	public static String getShortDateTimeString(long date) {
		if(date==0)
			return UNINIT_DATE;
		String s=null;
		try {
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis(date);
			Date d=c.getTime();
			s=SerializableRecordHelper.getShortDateTimeFormat().format(d);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static long getShortDateTimeLong(String date) throws Exception {
		if(date.equals(UNINIT_DATE)||date.trim().length()==0)
			return 0;
		Date d=SerializableRecordHelper.getShortDateTimeFormat().parse(date);
		return d.getTime();
	}
	
	private static final ThreadLocal<DateFormat>ssdf = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy_MMM_dd");
		}
	};
	
	public static DateFormat getShortSimpleDateFormat() {
		return ssdf.get();
	}
	
	public static String getShortDateString(long date) {
		if(date==0)
			return UNINIT_DATE;
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(date);
		Date d=c.getTime();
		return SerializableRecordHelper.getShortSimpleDateFormat().format(d);
	}
	
	public static long getShortDateLong(String date) throws Exception {
		if(date.equals(UNINIT_DATE)||date.equals(""))
			return 0;
		Date d=SerializableRecordHelper.getShortSimpleDateFormat().parse(date);
		return d.getTime();
	}
}
