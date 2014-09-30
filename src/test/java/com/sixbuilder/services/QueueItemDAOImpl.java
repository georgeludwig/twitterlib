package com.sixbuilder.services;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.services.QueueItemDAO;

public class QueueItemDAOImpl implements QueueItemDAO {
	
	public QueueItemDAOImpl() {
		HttpClient httpClient = new StdHttpClient.Builder()
		.host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443)
		.username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD)
		.enableSSL(true).relaxedSSLSettings(true).build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = new StdCouchDbConnector(QueueItemRepository.DBNAME, dbInstance);
		db.createDatabaseIfNotExists();
		repo = new QueueItemRepository(db);
	}

	private QueueItemRepository repo;

	public List<QueueItem> getAll() {
		return repo.getAll();
	}

	public List<QueueItem> getAllPending() {
		return repo.getAllPending();
	}

	public List<QueueItem> getPending(QueueType queueType, String userId) {
		return getPending(queueType, userId);
	}

	public void delete(List<QueueItem> queueItemList) {
		repo.delete(queueItemList);
	}
	
	public void add(QueueItem queueItem) {
		repo.add(queueItem);
	}

	public void update(List<QueueItem> queueItemList) {
		repo.update(queueItemList);
	}

	public void delete(QueueItem queueItem) {
		repo.delete(queueItem);
	}

	public QueueItemRepository getRepo() {
		return repo;
	}

}
