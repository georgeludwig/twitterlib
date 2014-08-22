package com.sixbuilder.actionqueue;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.georgeludwigtech.common.util.Time;

public class QueueItemTest {

	@Before
	public void setUp() throws Exception {
		System.out.println("/////////////////////// QueueItemTest ///////////////////////");
	}
	
	@After
	public void tearDown() throws Exception {}
	
	@Test
	public void testApp() throws Exception {
		// test to/from json string
		long now=System.currentTimeMillis();
		QueueItem qi=new QueueItem();
		qi.setDateCreated(now);
		qi.setQueueId(QueueId.CURATION);
		qi.setStatus(QueueItemStatus.PENDING);
		qi.setTargetDate(now+Time.HOUR_MILLIS);
		qi.setUserId("testUserName");
		String jsonString=qi.toJson();
		QueueItem qi2=QueueItem.fromJson(jsonString);
		assertTrue(qi.getDateCreated()==qi2.getDateCreated());
		assertTrue(qi.getQueueId()==qi2.getQueueId());
		assertTrue(qi.getStatus()==qi2.getStatus());
		assertTrue(qi.getTargetDate()==qi2.getTargetDate());
		assertTrue(qi.getUserId().equals(qi2.getUserId()));
	}
}
