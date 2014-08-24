package com.sixbuilder.actionqueue;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class representing a queued action
 * 
 * @author George
 *
 */
@JsonIgnoreProperties({"id", "revision"})
@JsonInclude(Include.NON_NULL) 
public class QueueItem {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static QueueItem fromJson(String jsonString) throws Exception {
		return mapper.readValue(jsonString, QueueItem.class);
	}
	
	public String toJson() throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
		
	/*
	 * primary id for this item 
	 */
	@JsonProperty("_id")
	private String id;
	
	/*
	 * document version
	 */
	@JsonProperty("_rev")
	private String revision;
	
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

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
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
