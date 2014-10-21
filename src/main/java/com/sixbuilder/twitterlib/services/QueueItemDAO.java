package com.sixbuilder.twitterlib.services;

import java.util.List;

import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueType;

public interface QueueItemDAO {
	
	public List<QueueItem> getAll();
	
	public List<QueueItem> getAllPending();

	public List<QueueItem> getPending(QueueType queueType, String userId);
	
	public List<QueueItem> getComplete(QueueType queueType, String userId);
	
	public void add(QueueItem queueItem);
	
	public void delete(List<QueueItem>queueItemList);
	
	public void update(List<QueueItem>queueItemList);
	
	public void delete(QueueItem queueItem);
	
	public QueueItemRepository getRepo();
	
	public long getLastCompleteDate(QueueType queueType,String userId);
	
}
