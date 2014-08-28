package com.sixbuilder.twitterlib.services;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.ektorp.CouchDbConnector;

import com.google.gson.JsonObject;

public class QueueManagerImpl implements QueueManager {

    private final CouchDbConnector dbClient;

    private final static String queueAsString =
        "{start : {hour: 12, min: 00, am: \"PM\", timezone: \"US/Pacific\"}," +
        "   end : {hour: 12, min: 30, am: \"PM\", timezone: \"US/Pacific\", from:2, to:5}," +
        "  asap : true," +
        "random : true}";

    public QueueManagerImpl(CouchDbConnector dbClient) {
        this.dbClient = dbClient;
    }

    public JsonObject create(String documentId) throws Exception {
        QueueSettings qs=new QueueSettings();
        qs.setId(documentId);
    	dbClient.update(qs);
    	JsonObject jso=qs.toJsonObject();
        return jso;
    }

    public void update(String documentId, JsonObject queue) throws Exception {
        System.out.println(queue);
        QueueSettings qs=QueueSettings.fromJsonObject(queue);
        dbClient.update(qs);
        queue.addProperty(QueueSettings._REV, qs.getRevision());
    }

    public JsonObject get(String documentId) throws Exception {
    	QueueSettings js=dbClient.get(QueueSettings.class, documentId);
    	JsonObject jso=js.toJsonObject();
        return jso;
    }

    public void remove(JsonObject queue) {
        dbClient.delete(queue);
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

}

