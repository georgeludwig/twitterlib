package com.sixbuilder.twitterlib.services;

import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;

public interface QueueSettingsDAO {

	public void delete(QueueSettings queueSettings);
	
	public QueueSettings getQueueSettings(String documentId);
	
	public QueueSettings getQueueSettings(QueueType queueType,String userId);
	
	public QueueSettings getCurationSettings(String userId);
	
	public QueueSettings getEngagementSettings(String userId);
	
	public QueueSettings getTestSettings(String userId);
	
	public void update(QueueSettings queueSettings);
	
	public void add(QueueSettings queueSettings);

	public QueueSettingsRepository getRepo();
	
}
