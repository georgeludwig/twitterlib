package com.sixbuilder.actionqueue;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.georgeludwigtech.common.util.Time;

public class QueueItemSerializationTest {

	@Before
	public void setUp() throws Exception {
		System.out
				.println("/////////////////////// QueueItemSerializationTest ///////////////////////");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApp() throws Exception {

		try {

			HttpClient httpClient = new StdHttpClient.Builder()
					.host("6btest.cloudant.com").port(443).username("6btest")
					.password("6btesttacofranchisescope").enableSSL(true)
					.relaxedSSLSettings(true).build();

			CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);

			CouchDbConnector db = new StdCouchDbConnector("testdb", dbInstance);

			db.createDatabaseIfNotExists();

			long now = System.currentTimeMillis();
			QueueItem qi = new QueueItem();
			qi.setDateCreated(now);
			qi.setQueueId(QueueId.CURATION);
			qi.setStatus(QueueItemStatus.PENDING);
			qi.setTargetDate(now + Time.HOUR_MILLIS);
			qi.setUserId("testUserName");

			db.create(qi);

			qi = db.get(QueueItem.class, qi.getId());

			String rev1 = qi.getRevision();

			qi.setStatus(QueueItemStatus.INPROCESS);

			db.update(qi);
			
			qi = db.get(QueueItem.class, qi.getId());

			assertTrue(!qi.getRevision().equals(rev1));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
