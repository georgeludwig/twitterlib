package com.sixbuilder.actionqueue;

import java.io.File;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * 
 * @author George
 *
 */
public class QueueItemProcessorDaemon {
	
	public static void main(String[] args) throws Exception {
		long waitInterval=10000L;
		long processorCount=1;
		String accountsRoot="/sixbuildertest/";
		
		HttpClient httpClient = new StdHttpClient.Builder()
				.host(AbstractTest.DBACCOUNT + ".cloudant.com").port(443)
				.username(AbstractTest.DBACCOUNT).password(AbstractTest.DBPWD)
				.enableSSL(true).relaxedSSLSettings(true).build();

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);

		CouchDbConnector db = new StdCouchDbConnector("testdb", dbInstance);

		
		// launch N processors
		for(int i=0;i<processorCount;i++) {
			QueueItemProcessor qip=new QueueItemProcessor(new File(accountsRoot),db);
			new Thread(qip).start();
		}
		
		Integer waitSem=new Integer(0);
		while(true) {
			// get new set of data from queue
			// TODO
			//db.
			// distribute new items to processing threads
			// TODO
			synchronized(waitSem) {
				try {
					waitSem.wait(waitInterval);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
