package com.sixbuilder.twitterlib.services;

import org.ektorp.support.CouchDbDocument;

import com.google.gson.JsonObject;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.helpers.Meridiem;

public class QueueSettings extends CouchDbDocument {
	
	private static final long serialVersionUID = 1L;
	
	public static final String _ID="_id";
	public static final String _REV="_rev";
	public static final String QUEUETYPE="queueType";
	public static final String USERID="userId";
	public static final String START="start";
	public static final String END="end";
	public static final String HOUR="hour";
	public static final String MINUTE="min";
	public static final String MERIDIEM="am";
	public static final String TIMEZONE="timezone";
	public static final String FROM="from";
	public static final String TO="to";
	public static final String ASAP="asap";
	public static final String RANDOM="random";
	public static final String PAUSE="pause";
	
	public QueueSettings() {
		// set defaults
		queueType=QueueType.TEST;
		userId="testUserId";
		startHour=12;
		startMinute=0;
		startMeridiem=Meridiem.PM;
		endHour=12;
		endMinute=30;
		endMeridiem=Meridiem.PM;
		timeZoneId="US/Hawaii";
		asap=true;
		random=true;
		pause=false;
		randomMin=2;
		randomMax=5;
	}

	private QueueType queueType;
	private String userId;
	private Integer startHour;
	private Integer startMinute;
	private Meridiem startMeridiem;
	private Integer endHour;
	private Integer endMinute;
	private Meridiem endMeridiem;
	private String timeZoneId;
	private Boolean asap;
	private Boolean random;
	private Boolean pause;
	private Integer randomMin;
	private Integer randomMax;
	
	public QueueType getQueueType() {
		return queueType;
	}

	public void setQueueType(QueueType queueType) {
		this.queueType = queueType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getStartHour() {
		return startHour;
	}

	public void setStartHour(Integer startHour) {
		this.startHour = startHour;
	}

	public Integer getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(Integer startMinute) {
		this.startMinute = startMinute;
	}

	public Meridiem getStartMeridiem() {
		return startMeridiem;
	}

	public void setStartMeridiem(Meridiem startMeridiem) {
		this.startMeridiem = startMeridiem;
	}

	public Integer getEndHour() {
		return endHour;
	}

	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}

	public Integer getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(Integer endMinute) {
		this.endMinute = endMinute;
	}

	public Meridiem getEndMeridiem() {
		return endMeridiem;
	}

	public void setEndMeridiem(Meridiem endMeridiem) {
		this.endMeridiem = endMeridiem;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Boolean getAsap() {
		return asap;
	}

	public void setAsap(Boolean asap) {
		this.asap = asap;
	}

	public Boolean getRandom() {
		return random;
	}

	public void setRandom(Boolean random) {
		this.random = random;
	}

	public Boolean getPause() {
		return pause;
	}

	public void setPause(Boolean pause) {
		this.pause = pause;
	}

	public Integer getRandomMin() {
		return randomMin;
	}

	public void setRandomMin(Integer randomMin) {
		this.randomMin = randomMin;
	}

	public Integer getRandomMax() {
		return randomMax;
	}

	public void setRandomMax(Integer randomMax) {
		this.randomMax = randomMax;
	}
	
	/**
	 * required by the format used in the javascript
	 * @return
	 */
	public JsonObject toJsonObject() {
		// instantiate return object
		JsonObject jso=new JsonObject();
		// get db control fields
		if(getId()!=null)
			jso.addProperty(_ID, getId());
		if(getRevision()!=null)
			jso.addProperty(_REV, getRevision());
		// queueType
		jso.addProperty(QUEUETYPE,getQueueType().toString());
		// userId
		jso.addProperty(USERID,getUserId());
		// start
		JsonObject start = new JsonObject();
		start.addProperty(HOUR, getStartHour());
		start.addProperty(MINUTE, getStartMinute());
		start.addProperty(MERIDIEM, getStartMeridiem().toString());
		start.addProperty(TIMEZONE, getTimeZoneId());
		jso.add(START, start);
		// end
		JsonObject end = new JsonObject();
		end.addProperty(HOUR, getStartHour());
		end.addProperty(MINUTE, getStartMinute());
		end.addProperty(MERIDIEM, getStartMeridiem().toString());
		end.addProperty(TIMEZONE, getTimeZoneId());
		end.addProperty(FROM, getRandomMin());
		end.addProperty(TO, getRandomMax());
		jso.add(END, end);
		//
		jso.addProperty(ASAP,getAsap());
		//
		jso.addProperty(RANDOM,getRandom());
		//
		jso.addProperty(PAUSE,getPause());
		
		return jso;
	}
	
	public static QueueSettings fromJsonObject(JsonObject jso) {
		QueueSettings ret=new QueueSettings();
		// id
		String val=jso.getAsJsonPrimitive(_ID).getAsString();
		ret.setId(val);
		// rev
		val=jso.getAsJsonPrimitive(_REV).getAsString();
		ret.setRevision(val);
		// userId
		val=jso.getAsJsonPrimitive(USERID).getAsString();
		ret.setUserId(val);
		// queueType
		val=jso.getAsJsonPrimitive(QUEUETYPE).getAsString();
		ret.setQueueType(QueueType.getByString(val));
		// start
		JsonObject start=jso.getAsJsonObject(START);
		Integer v=start.getAsJsonPrimitive(HOUR).getAsInt();
		ret.setStartHour(v);
		v=start.getAsJsonPrimitive(MINUTE).getAsInt();
		ret.setStartMinute(v);
		val=start.getAsJsonPrimitive(MERIDIEM).getAsString();
		ret.setStartMeridiem(Meridiem.valueOf(val));
		val=start.getAsJsonPrimitive(TIMEZONE).getAsString();
		ret.setTimeZoneId(val);
		// end
		JsonObject end=jso.getAsJsonObject(END);
		v=end.getAsJsonPrimitive(HOUR).getAsInt();
		ret.setEndHour(v);
		v=end.getAsJsonPrimitive(MINUTE).getAsInt();
		ret.setEndMinute(v);
		val=end.getAsJsonPrimitive(MERIDIEM).getAsString();
		ret.setEndMeridiem(Meridiem.valueOf(val));
		// we ignore end time zone, assume it is the same as start time zone
//		val=end.getAsJsonPrimitive(TIMEZONE).getAsString();
//		ret.setTimeZone(DateTimeZone.forID(val));
		v=end.getAsJsonPrimitive(FROM).getAsInt();
		ret.setRandomMin(v);
		v=end.getAsJsonPrimitive(TO).getAsInt();
		ret.setRandomMax(v);
		// asap
		Boolean b=jso.getAsJsonPrimitive(ASAP).getAsBoolean();
		ret.setAsap(b);
		// random
		b=jso.getAsJsonPrimitive(RANDOM).getAsBoolean();
		ret.setRandom(b);
		// pause
		b=jso.getAsJsonPrimitive(PAUSE).getAsBoolean();
		ret.setPause(b);
		return ret;
	}
}
