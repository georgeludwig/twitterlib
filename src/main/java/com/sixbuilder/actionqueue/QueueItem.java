package com.sixbuilder.actionqueue;

/**
 * A class representing a queued action
 * 
 * @author George
 *
 */
public class QueueItem {
	
	/*
	 * possible value for queueId
	 */
	public static final String QUEUE_CURATION="QUEUE_CURATION";
	/*
	 * possible value for queueId
	 */
	public static final String QUEUE_ENGAGEMENT="QUEUE_ENGAGEMENT";
	/*
	 * possible value for status 
	 * the queue item has been created, but has not yet been allocated to
	 */
	public static final String STATUS_PENDING="STATUS_PENDING";
	/*
	 * possible value for status 
	 * the queue item has been successfully proccessed
	 * 
	 */
	public static final String STATUS_INPROCESS="STATUS_INPROCESS";
	/*
	 * possible value for status 
	 */
	public static final String STATUS_COMPLETE="STATUS_COMPLETE";
	
	/*
	 * primary id for this item 
	 */
	private String id;
	
	/*
	 * which queue is this, i.e. curation queue
	 */
	private String queueId;
	
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
	 * the status o this queue item
	 */
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueueId() {
		return queueId;
	}

	public void setQueueId(String queueId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
