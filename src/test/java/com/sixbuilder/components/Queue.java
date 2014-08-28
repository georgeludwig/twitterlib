package com.sixbuilder.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sixbuilder.twitterlib.services.QueueManager;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import javax.inject.Inject;

@Import(library = {"queue-manager.js", "react.js", "queue.js", "init-queue.js"})
public class Queue {

    /**
     * Cloudant document id
     */
    @Parameter
    private String queueId;

    /**
     * Time interval between two updates
     */
    @Parameter
    private int updateInterval;

    @Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String clientId;

    private String assignedClientId;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private QueueManager queueManager;

    @Inject
    private Request request;

    @BeginRender
    boolean addElement(MarkupWriter writer){
        assignedClientId = javaScriptSupport.allocateClientId(clientId);
        writer.element("div", "id", assignedClientId);
        writer.end();
        return true;
    }

    @AfterRender
    void addJavaScript(){
        JSONObject callbacks = new JSONObject()
            .put("get", getQueueURL())
            .put("update", getQueueUpdateURL());

        JSONObject params = new JSONObject()
            .put("callbacks", callbacks)
            .put("lazyLoad", false)
            .put("element", assignedClientId);

        javaScriptSupport.addInitializerCall("initQueue", params);
    }

    private String getQueueUpdateURL() {
        return resources.createEventLink("update", queueId).toURI();
    }

    private String getQueueURL(){
        return resources.createEventLink("get", queueId).toURI();
    }

    @OnEvent("update")
    JsonObject updateQueue(String queueId) throws Exception {
        JsonObject queue = (JsonObject) new JsonParser().parse(request.getParameter("queue"));
        queueManager.update(queueId, queue);
        return success(queue);
    }

    @OnEvent("get")
    JsonObject getQueue(String queueId) throws Exception {
        JsonObject queue = queueManager.get(queueId);
        return success(queue);
    }

    private JsonObject success(JsonObject queue) {
        JsonObject result = new JsonObject();
        result.add("success", new JsonPrimitive(true));
        result.add("queue", queue);
        return result;
    }

}
