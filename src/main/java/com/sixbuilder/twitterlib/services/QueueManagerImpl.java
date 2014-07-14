package com.sixbuilder.twitterlib.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class QueueManagerImpl implements QueueManager {

    private final CouchDbClient dbClient;

    private final static String queueAsString =
        "{start : {hour: 12, min: 00, am: \"PM\", timezone: \"US/Pacific\"}," +
        "   end : {hour: 12, min: 30, am: \"PM\", timezone: \"US/Pacific\", from:2, to:5}," +
        "  asap : true," +
        "random : true}";

    public QueueManagerImpl(CouchDbClient dbClient) {
        this.dbClient = dbClient;
    }

    public JsonObject create(String documentId) {
        JsonObject queue = (JsonObject)new JsonParser().parse(queueAsString);
        queue.add("_id", new JsonPrimitive(documentId));
        Response response = dbClient.save(queue);
        queue.add("_rev", new JsonPrimitive(response.getRev()));
        return queue;
    }

    public void update(String documentId, JsonObject queue) {
        System.out.println(queue);
        queue.add("_id", new JsonPrimitive(documentId));
        queue.add("start-time", new JsonPrimitive(
            toStartTime(queue).getTime()));
        queue.add("end-time", new JsonPrimitive(
            toEndTime(queue).getTime()));
        Response response = dbClient.update(queue);
        queue.add("_rev", new JsonPrimitive(response.getRev()));
    }

    public JsonObject get(String documentId) {
        return dbClient.find(JsonObject.class, documentId);
    }

    public void remove(JsonObject queue) {
        dbClient.remove(queue);
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

