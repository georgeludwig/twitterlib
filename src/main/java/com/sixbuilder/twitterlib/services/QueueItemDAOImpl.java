package com.sixbuilder.twitterlib.services;

import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemComparatorByTargetDate;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.services.QueueItemDAO;

public class QueueItemDAOImpl implements QueueItemDAO {
	
	private final String DB_ACCOUNT=System.getProperty("dbAccountName");
	private final String DB_PASSWORD=System.getProperty("dbPassword");
	
	public QueueItemDAOImpl() {
		HttpClient httpClient = new StdHttpClient.Builder()
		.host(DB_ACCOUNT+".cloudant.com").port(443)
		.socketTimeout(120000)
		.connectionTimeout(120000)
		.username(DB_ACCOUNT).password(DB_PASSWORD)
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
		List<QueueItem>ret=repo.getPending(queueType, userId);
		// sort them by target date
		Collections.sort(ret,new QueueItemComparatorByTargetDate());
		return ret;
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
	
	public long getLastCompleteDate(QueueType queueType,String userId) {
		return repo.getLastCompleteDate(queueType,userId);
	}

	@Override
	public List<QueueItem> getComplete(QueueType queueType, String userId) {
		return repo.getComplete(queueType, userId);
	}

}
