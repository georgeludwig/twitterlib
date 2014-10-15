package com.sixbuilder.helpers;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.georgeludwigtech.common.util.Time;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.twitterlib.helpers.Meridiem;
import com.sixbuilder.twitterlib.helpers.TargetTimeCalculator;
import com.sixbuilder.twitterlib.services.QueueSettings;

public class TargetTimeCalclulatorTest {
	
	@Before
	public void setUp() throws Exception {
		System.out.println("/////////////////////// TargetTimeCalclulatorTest ///////////////////////");
	}
	
	@After
	public void tearDown() throws Exception {}
	
	@Test
	public void testApp() throws Exception {
		// test ASAP random
		QueueSettings settings=new QueueSettings();
		settings.setAsap(true);
		settings.setRandom(true);
		settings.setRandomMin(1);
		settings.setRandomMax(1);
		QueueItem item=new QueueItem();
		long now=System.currentTimeMillis();
		// test ASAP random
		TargetTimeCalculator.calcTargetTime(settings, item, null, now, false);
		long diff=item.getTargetDate()-now;
		assertTrue(diff==Time.MIN_MILLIS);	
		// 3am
		DateTimeZone dtz=DateTimeZone.forID(settings.getTimeZoneId());
		DateTime dt=new DateTime(2014,12,1,3,0,0,0,dtz);
		now=dt.getMillis();
		// test start by/random
		settings=new QueueSettings();
		settings.setAsap(false);
		settings.setStartHour(1);
		settings.setStartMinute(0);
		settings.setStartMeridiem(Meridiem.PM);
		settings.setRandom(true);
		settings.setRandomMin(1);
		settings.setRandomMax(1);
		TargetTimeCalculator.calcTargetTime(settings, item, null, now, false);
		diff=item.getTargetDate()-now;
		DateTime startBy=TargetTimeCalculator.getStartDateTime(settings);
		assertTrue(item.getTargetDate()==startBy.getMillis()+Time.MIN_MILLIS);
	}
	

}
