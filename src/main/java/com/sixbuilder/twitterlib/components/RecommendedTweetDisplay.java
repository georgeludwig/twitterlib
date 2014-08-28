package com.sixbuilder.twitterlib.components;

import java.io.File;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.georgeludwigtech.common.setmanager.FileSystemSetManagerImpl;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.sixbuilder.actionqueue.QueueItemProcessor;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.services.QueueManager;

/**
 * A component that shows the recommended tweets, curating, publishing and published.
 */
@Events({
	RecommendedTweetConstants.CURATING_TWEETS_EVENT,
	RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT,
	RecommendedTweetConstants.PUBLISH_TWEET_EVENT, 
	RecommendedTweetConstants.DELETE_TWEET_EVENT,
	RecommendedTweetConstants.SHORTEN_URL_EVENT, 
	RecommendedTweetConstants.LOAD_TWEET_EVENT,
	RecommendedTweetConstants.MEH_TWEET_EVENT})
public class RecommendedTweetDisplay {
	
	public static final String UPDATE_ALL_LISTS = "updateAllLists";
	
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
	@Parameter
	private String queueId;
	
	public String getQueueId() {
		return queueId;
	}

	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}

	@InjectComponent
	private Zone curateZone;
	
	@InjectComponent
	private Zone publishingZone;
	
	@Property
	private TweetItem tweet;
	
	@Parameter(required = true, allowNull = false)
	private File tempFileRootDir;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private AlertManager alertManager;
	
	@Cached
	public List<TweetItem> getCurating() {
		return triggerEvent(RecommendedTweetConstants.CURATING_TWEETS_EVENT);
	}

	@Cached
	public List<TweetItem> getPublishing() {
		return triggerEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT);
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		triggerEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT, resources.getContainerResources());
		SetManager cSm = getCurationSetManager(tempFileRootDir,curationSetMgr);
		SetManager qSm = getQueuedSetManager(tempFileRootDir,queuedSetMgr);
		cSm.removeSetItem(tweetItem.getTweetId());
		qSm.removeSetItem(tweetItem.getTweetId());
		// TODO remove corresponding QueueItem from queue, if it exists
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@Inject
	private QueueManager queueManager;
	
	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, resources.getContainerResources());
		triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, resources.getContainerResources());
		SetManager cSm = getCurationSetManager(tempFileRootDir,curationSetMgr);
		SetManager qSm = getQueuedSetManager(tempFileRootDir,queuedSetMgr);
		cSm.removeSetItem(tweetItem.getTweetId());
		qSm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		// TODO calculate target time based on queue settings
//		JsonObject queue=queueManager.get(queueId);
		// TODO add a QueueItem to the cloudant queue
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, resources.getContainerResources());
		SetManager cSm = getCurationSetManager(tempFileRootDir,curationSetMgr);
		SetManager qSm = getQueuedSetManager(tempFileRootDir,queuedSetMgr);
		cSm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		qSm.removeSetItem(tweetItem.getTweetId());
		// TODO remove corresponding QueueItem from queue, if it exists
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	SetManager curationSetMgr;
	
	public static SetManager getCurationSetManager(File tempFileRootDir, SetManager curationSetMgr) throws Exception {
		synchronized(tempFileRootDir+QueueItemProcessor.CURATION_SET_MANAGER_NAME) {
			if(curationSetMgr==null) {
				String s=tempFileRootDir.getAbsolutePath();
				if(!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
					s=s+SerializableRecordHelper.FILE_SEPARATOR;
				s=s+QueueItemProcessor.CURATION_SET_MANAGER_NAME;
				SetManager sm=new FileSystemSetManagerImpl(new File(s));
				curationSetMgr=sm;
			}
		}
		return curationSetMgr;
	}
	
	SetManager queuedSetMgr;
	
	public static SetManager getQueuedSetManager(File tempFileRootDir, SetManager queuedSetMgr) throws Exception {
		synchronized(tempFileRootDir+QueueItemProcessor.QUEUED_SET_MANAGER_NAME) {
			if(queuedSetMgr==null) {
				String s=tempFileRootDir.getAbsolutePath();
				if(!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
					s=s+SerializableRecordHelper.FILE_SEPARATOR;
				s=s+QueueItemProcessor.QUEUED_SET_MANAGER_NAME;
				SetManager sm=new FileSystemSetManagerImpl(new File(s));
				queuedSetMgr=sm;
			}
		}
		return queuedSetMgr;
	}
	
	@SuppressWarnings("unchecked")
	public List<TweetItem> triggerEvent(String event) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, EMPTY_OBJECT_ARRAY, callback);
		return (List<TweetItem>) callback.getResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<TweetItem> triggerEvent(String event, ComponentResources resources) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, EMPTY_OBJECT_ARRAY, callback);
		return (List<TweetItem>) callback.getResult();
	}

	@OnEvent(UPDATE_ALL_LISTS)
	public void updateAllLists() {
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}
	
}
