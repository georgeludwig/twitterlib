package com.sixbuilder.services;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;
import com.sixbuilder.twitterlib.services.QueueSettings;
import com.sixbuilder.twitterlib.services.QueueSettingsDAO;

public class QueueSettingsDAOImpl implements QueueSettingsDAO {
	
	public QueueSettingsDAOImpl() {
		HttpClient httpClient = new StdHttpClient.Builder()
		.host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443)
		.username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD)
		.enableSSL(true).relaxedSSLSettings(true).build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = new StdCouchDbConnector(QueueSettingsRepository.DBNAME, dbInstance);
		db.createDatabaseIfNotExists();
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
