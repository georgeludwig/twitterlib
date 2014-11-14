package com.sixbuilder.twitterlib.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.georgeludwigtech.common.util.Time;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.twitterlib.services.QueueSettings;

public abstract class TargetTimeCalculator {
	
	public static Random random=new Random();

	// returns true if there was a re-calc of target times for existing items
	public static boolean calcTargetTime(QueueSettings queueSettings, QueueItem newItem, List<QueueItem>existingItems, long now, boolean recalcAll) {
		// recalc all would have been set based on origin of call (i.e. when queue settings are changed, vs. when a new item is queued)
		// when a new item is queued, we may need to recalc all in any event based on this logic
		// if startBy & endBy, always recalc
		if(!queueSettings.getAsap()&&!queueSettings.getRandom())
			recalcAll=true;
		// if startBy & random, use recalcAll
		// if asap & endBy, always recalc
		if(queueSettings.getAsap()&&!queueSettings.getRandom())
			recalcAll=true;
		// if asap & random, use recalcAll
		// the above code resolves to if(!queueSettings.getRandom()) then recalcAll=true, but we leave the long form for intelligibility
		
		// make sure critical values are not null
		boolean hasExistingItems=true;
		if(existingItems==null) {
			existingItems=new ArrayList<QueueItem>();
			hasExistingItems=false;
		}
		// get start reference
		long start=-1;
		if(queueSettings.getAsap())
			// if it's asap, start=now
			start=now;
		else {
			// get start datetime
			DateTime dt=getStartDateTime(queueSettings);
			start=dt.getMillis();
			if(start<now)
				start=now;
		}
		// special case: if end is random (ASAP), there's a new item, and !recalc all, just need to calc a random time for the new item 
		if(queueSettings.getRandom()&&newItem!=null&&!recalcAll) {
			long reference=start;
			if(existingItems.size()>0&&!recalcAll) {
				// get the item that is furthest in the future
				QueueItem furthest=existingItems.get(existingItems.size()-1);
				reference=furthest.getTargetDate();
			}
			// add random time to reference
			int intervalInMinutes = random.nextInt((queueSettings.getRandomMax()-queueSettings.getRandomMin()) + 1)+queueSettings.getRandomMin();
			long targetDate=reference+(intervalInMinutes*60000);
			newItem.setTargetDate(targetDate);
		} else {
			// recalc all
			DateTime dt=getEndDateTime(queueSettings);
			long end=dt.getMillis();
			// create unified list of items for convenience
			List<QueueItem>tempList=new ArrayList<QueueItem>();
			tempList.addAll(existingItems);
			if(newItem!=null) // when we update the queue settings, queueitem will be null
				tempList.add(newItem);
			if(tempList.size()>0) {
				// we've got a start time and end time, we just need to decide 
				// if it's a random interval, or a spread interval
				if(queueSettings.getRandom()) {
					long reference=start;
					for(int i=0;i<tempList.size();i++) {
						QueueItem item=tempList.get(i);
						int intervalInMinutes = random.nextInt((queueSettings.getRandomMax()-queueSettings.getRandomMin()) + 1)+queueSettings.getRandomMin();
						long target=reference+((i+1)*intervalInMinutes*Time.MIN_MILLIS);
						item.setTargetDate(target);
						reference=target;
					}
				} else {
					// calc spread interval 
					long interval=(end-start)/tempList.size();
					// iterate through item list, setting new target dates
					for(int i=0;i<tempList.size();i++) {
						QueueItem item=tempList.get(i);
						item.setTargetDate(start+((i+1)*interval));
					}
				}
			}
		}
		if(hasExistingItems&&recalcAll)
			return true;
		return false;
	}
	
	public static DateTime getStartDateTime(QueueSettings queueSettings) {
		// get the start time zone from settings
		DateTimeZone dtz=DateTimeZone.forID(queueSettings.getTimeZoneId());
		// reference
		DateTime ref=new DateTime(dtz);
		int hour=queueSettings.getStartHour();
		// add 12 hours to start hour if it's pm
		if(queueSettings.getStartMeridiem()==Meridiem.PM&&hour<12) {
			hour+=12;
			if(hour==24)
				hour=0;
		}
		DateTime dt=new DateTime(ref.getYear(),ref.getMonthOfYear(),ref.getDayOfMonth(),hour,queueSettings.getStartMinute(),0,0,dtz);
		return dt;
	}
	
	public static DateTime getEndDateTime(QueueSettings queueSettings) {
		// get the end time zone from settings
		DateTimeZone dtz=DateTimeZone.forID(queueSettings.getTimeZoneId());
		// reference
		DateTime ref=new DateTime(dtz);
		int hour=queueSettings.getEndHour();
		// add 12 hours to start hour if it's pm
		if(queueSettings.getEndMeridiem()==Meridiem.PM) {
			hour+=12;
			if(hour==24)
				hour=0;
		}
		DateTime dt=new DateTime(ref.getYear(),ref.getMonthOfYear(),ref.getDayOfMonth(),hour,queueSettings.getEndMinute(),0,0,dtz);
		return dt;
	}
	
	public static String getTimeDisplayString(String timeZoneId, long time) {
		DateTimeZone dtz=DateTimeZone.forID(timeZoneId);
		LocalDateTime ldt=new LocalDateTime(time,dtz);
		Meridiem meridiem=Meridiem.AM;
		int hour=ldt.getHourOfDay();
		if(hour>=12) {
			meridiem=Meridiem.PM;
		}
		if(hour>12)
			hour-=12;
		int minute=ldt.getMinuteOfHour();
		String h=String.valueOf(hour);
		if(h.length()==1)
			h="0"+h;
		String m=String.valueOf(minute);
		if(m.length()==1)
			m="0"+m;
		return h+":"+m+" "+meridiem;
	}
	
}
