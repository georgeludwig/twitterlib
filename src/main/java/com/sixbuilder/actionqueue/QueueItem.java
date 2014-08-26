package com.sixbuilder.actionqueue;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class representing a queued action
 * 
 * @author George
 *
 */
public class QueueItem extends CouchDbDocument {
	
	private static final long serialVersionUID = 1L;

	private static ObjectMapper mapper = new ObjectMapper();
	
	public static QueueItem fromJson(String jsonString) throws Exception {
		return mapper.readValue(jsonString, QueueItem.class);
	}
	
	public String toJson() throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
	
	/*
	 * which queue is this, i.e. curation queue
	 */
	private QueueId queueId;
	
	/*
	 * id of the user
	 */
	private String userId;
	
	/**
	 * the tweetId of the tweet in pendingTweetFileUtil
	 */
	private String tweetId;
	
	/*
	 * the date that this queue item was created
	 */
	private long dateCreated;
	
	/*
	 * the exact time at which this action should take place
	 */
	private long targetDate;
	
	/*
	 * the status of this queue item
	 */
	private QueueItemStatus status;

	
	public QueueId getQueueId() {
		return queueId;
	}

	public void setQueueId(QueueId queueId) {
		this.queueId = queueId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}
	
	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(long targetDate) {
		this.targetDate = targetDate;
	}

	public QueueItemStatus getStatus() {
		return status;
	}

	public void setStatus(QueueItemStatus status) {
		this.status = status;
	}
	
}
