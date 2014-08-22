package com.sixbuilder.actionqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class representing a queued action
 * 
 * @author George
 *
 */
public class QueueItem {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static QueueItem fromJson(String jsonString) throws Exception {
		return mapper.readValue(jsonString, QueueItem.class);
	}
	
	public String toJson() throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
		
	/*
	 * primary DB id for this item 
	 */
	private String _id;
	
	/*
	 * document version
	 */
	private String _rev;
	
	/*
	 * which queue is this, i.e. curation queue
	 */
	private QueueId queueId;
	
	/*
	 * id of the user
	 */
	private String userId;
	
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

	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

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
