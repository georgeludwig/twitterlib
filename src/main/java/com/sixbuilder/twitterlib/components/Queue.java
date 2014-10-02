package com.sixbuilder.twitterlib.components;

import java.util.List;

import javax.inject.Inject;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.TargetTimeCalculator;
import com.sixbuilder.twitterlib.services.QueueItemDAO;
import com.sixbuilder.twitterlib.services.QueueSettings;
import com.sixbuilder.twitterlib.services.QueueSettingsDAO;

@Import(library = {"queue-manager.js", "react.js", "queue.js", "init-queue.js"})
public class Queue {

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
    @Parameter
    private QueueType queueType;

    @Parameter
    private String userId;
    
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

    @Inject QueueSettingsDAO queueSettingsDAO;
    
    @Inject QueueItemDAO queueItemDAO;

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
        return resources.createEventLink("update", userId).toURI();
    }

    private String getQueueURL(){
        return resources.createEventLink("get", userId).toURI();
    }

    @OnEvent("update")
    JsonObject updateQueue(String userId) throws Exception {
        JsonObject queue = (JsonObject) new JsonParser().parse(request.getParameter("queue"));
        QueueSettings settings=QueueSettings.fromJsonObject(queue);
        queueSettingsDAO.update(settings);
        recalcQueue(settings);
        // TODO serialize tweeetitems
        return success(settings.toJsonObject());
    }

    @OnEvent("get")
    JsonObject getQueue(String userId) throws Exception {
		QueueSettings settings=queueSettingsDAO.getQueueSettings(queueType,userId);
        JsonObject queue = settings.toJsonObject();
        return success(queue);
	}

    private JsonObject success(JsonObject queue) {
        JsonObject result = new JsonObject();
        result.add("success", new JsonPrimitive(true));
        result.add("queue", queue);
        return result;
    }

    private void recalcQueue(QueueSettings queueSettings) {
    	// get current contents of queue for user
    	List<QueueItem>queueItems=queueItemDAO.getPending(queueType, userId);
    	// re-calc target times based on current queue settings
		boolean changed=TargetTimeCalculator.calcTargetTime(queueSettings, null, queueItems, System.currentTimeMillis(),true);
		// re-serialize existing items if they were changed
		if(changed&&queueItems.size()>0) {
			queueItemDAO.update(queueItems);
			// TODO update the tweetItems by triggering container event
		}		
    }
    
    @SuppressWarnings("unchecked")
	public List<TweetItem> triggerEvent(String event, ComponentResources resources) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, EMPTY_OBJECT_ARRAY, callback);
		return (List<TweetItem>) callback.getResult();
	}

}
