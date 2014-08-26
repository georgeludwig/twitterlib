package com.sixbuilder.twitterlib.services;

import java.util.TimeZone;

import org.ektorp.support.CouchDbDocument;

public class QueueSettings extends CouchDbDocument {
	
	private static final long serialVersionUID = 1L;
	
	private Long startTime;
	
	private Long endTime;
	
	private TimeZone timeZone;

	private Boolean startAsap;
	
	private Boolean endBy;
	
	private Integer minInterval;
	
	private Integer maxInterval;

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	public Boolean getStartAsap() {
		return startAsap;
	}

	public void setStartAsap(Boolean startAsap) {
		this.startAsap = startAsap;
	}

	public Boolean getEndBy() {
		return endBy;
	}

	public void setEndBy(Boolean endBy) {
		this.endBy = endBy;
	}

	public Integer getMinInterval() {
		return minInterval;
	}

	public void setMinInterval(Integer minInterval) {
		this.minInterval = minInterval;
	}

	public Integer getMaxInterval() {
		return maxInterval;
	}

	public void setMaxInterval(Integer maxInterval) {
		this.maxInterval = maxInterval;
	}
	
}
