package com.sixbuilder.twitterlib.services;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;

public class QueueSettingsDAOImpl implements QueueSettingsDAO {
	
	private final String DB_ACCOUNT=System.getProperty("dbAccountName");
	private final String DB_PASSWORD=System.getProperty("dbPassword");
	
	
	public QueueSettingsDAOImpl() {
		HttpClient httpClient = new StdHttpClient.Builder()
		.host(DB_ACCOUNT + ".cloudant.com").port(443)
		.socketTimeout(120000)
		.connectionTimeout(120000)
		.username(DB_ACCOUNT).password(DB_PASSWORD)
		.enableSSL(true).relaxedSSLSettings(true).build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = new StdCouchDbConnector(QueueSettingsRepository.DBNAME, dbInstance);
		try {
			db.createDatabaseIfNotExists();
		} catch(Exception e) {
			e.printStackTrace();
		}
		repo = new QueueSettingsRepository(db);
	}

	private QueueSettingsRepository repo;
	
	public QueueSettings getQueueSettings(String documentId) {
		return repo.get(documentId);
	}
	
	public void delete(QueueSettings queueSettings) {
		repo.delete(queueSettings);
	}

	public QueueSettings getQueueSettings(QueueType queueType, String userId) {
		return repo.getQueueSettings(queueType, userId);
	}

	public QueueSettings getCurationSettings(String userId) {
		return repo.getCurationSettings(userId);
	}

	public QueueSettings getEngagementSettings(String userId) {
		return repo.getEngagementSettings(userId);
	}

	public QueueSettings getTestSettings(String userId) {
		return repo.getTestSettings(userId);
	}
    
	public void update(QueueSettings queueSettings) {
		repo.update(queueSettings);
	}
	
	public void add(QueueSettings queueSettings) {
		repo.add(queueSettings);
	}
	
	public QueueSettingsRepository getRepo() {
		return repo;
	}
}
