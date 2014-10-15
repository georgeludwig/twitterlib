package com.sixbuilder.twitterlib.services;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.ektorp.CouchDbConnector;

import com.google.gson.JsonObject;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;

public class QueueManagerImpl implements QueueManager {

  @SuppressWarnings("unused")
  private final static String queueAsString =
		"queueType: 'TEST'," +
		"userId: 'testUserId'," +
        "{start : {hour: 12, min: 00, am: \"PM\", timezone: \"US/Pacific\"}," +
        "   end : {hour: 12, min: 30, am: \"PM\", timezone: \"US/Pacific\", from:2, to:5}," +
        "  asap : true," +
        "random : true}";

    public QueueManagerImpl(CouchDbConnector dbClient) {
        setQueueSettingsRepository(new QueueSettingsRepository(dbClient));
    }

    public JsonObject create(String documentId) throws Exception {
        QueueSettings qs=new QueueSettings();
        qs.setId(documentId);
    	getQueueSettingsRepository().update(qs);
    	JsonObject jso=qs.toJsonObject();
        return jso;
    }

    public void update(String documentId, JsonObject queue) throws Exception {
        QueueSettings qs=QueueSettings.fromJsonObject(queue);
        getQueueSettingsRepository().update(qs);
        queue.addProperty(QueueSettings._REV, qs.getRevision());
    }

    public JsonObject get(String userId) throws Exception {
    	//QueueSettings js=dbClient.get(QueueSettings.class, documentId);
    	QueueSettings qs=getQueueSettingsRepository().getTestSettings(userId);
    	if(qs==null) {
    		qs=new QueueSettings();
    		qs.setUserId(userId);
    		qs.setQueueType(QueueType.TEST);
    		getQueueSettingsRepository().add(qs);
    	}
    	JsonObject jso=qs.toJsonObject();
        return jso;
    }

    public void remove(JsonObject queue) {
    	QueueSettings qs=QueueSettings.fromJsonObject(queue);
    	getQueueSettingsRepository().delete(qs);
    }

    protected Date toEndTime(JsonObject obj) {
        JsonObject end = obj.getAsJsonObject("end");
        if(obj.get("random").getAsBoolean()){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE,
                randomBetween(end.get("from").getAsInt(),
                    end.get("to").getAsInt()));
            return calendar.getTime();
        }else {
            return getTime(end);
        }
    }

    private int randomBetween(int from, int to) {
        return from + (int) (Math.random() * (to - from));
    }

    protected Date toStartTime(JsonObject obj) {
        if (obj.get("asap").getAsBoolean()) {
            return new Date();
        }

        return getTime(obj.getAsJsonObject("start"));
    }

    protected Date getTime(JsonObject time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(
            time.get("timezone").getAsString()));
        calendar.set(Calendar.HOUR, time.get("hour").getAsInt());
        calendar.set(Calendar.MINUTE, time.get("min").getAsInt());
        calendar.set(Calendar.AM_PM,
            "AM".equalsIgnoreCase(time.get("am").getAsString()) ?
                Calendar.AM : Calendar.PM);
        return calendar.getTime();
    }

    private QueueSettingsRepository queueSettingsRepository;

	public QueueSettingsRepository getQueueSettingsRepository() {
		return queueSettingsRepository;
	}

	public void setQueueSettingsRepository(QueueSettingsRepository queueSettingsRepository) {
		this.queueSettingsRepository = queueSettingsRepository;
	}
}

