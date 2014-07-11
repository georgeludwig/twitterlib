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

import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.setmanager.FileSystemSetManagerImpl;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * A component that shows the recommended tweets, curating, publishing and published.
 */
@Events({
	RecommendedTweetConstants.CURATING_TWEETS_EVENT,
	RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT,
	RecommendedTweetConstants.PUBLISHED_TWEETS_EVENT,
	RecommendedTweetConstants.PUBLISH_TWEET_EVENT, 
	RecommendedTweetConstants.DELETE_TWEET_EVENT,
	RecommendedTweetConstants.SHORTEN_URL_EVENT, 
	RecommendedTweetConstants.SAVE_TWEET_EVENT,
	RecommendedTweetConstants.LOAD_TWEET_EVENT,
	RecommendedTweetConstants.MEH_TWEET_EVENT})
public class RecommendedTweetDisplay {
	
	public static final String CURATION_SET_MANAGER_NAME="curationSetMgr";
	public static final String QUEUED_SET_MANAGER_NAME="queuedSetMgr";
	
	public static final String UPDATE_ALL_LISTS = "updateAllLists";
	
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
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
	
	@Cached
	public List<TweetItem> getPublished() {
		return triggerEvent(RecommendedTweetConstants.PUBLISHED_TWEETS_EVENT);
	}
	
	SetManager curationSetMgr;
	
	public SetManager getCurationSetManager() throws Exception {
		if(curationSetMgr==null) {
			String s=tempFileRootDir.getAbsolutePath();
			if(!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
				s=s+SerializableRecordHelper.FILE_SEPARATOR;
			s=s+CURATION_SET_MANAGER_NAME;
			SetManager sm=new FileSystemSetManagerImpl(new File(s));
			curationSetMgr=sm;
		}
		return curationSetMgr;
	}
	
	SetManager queuedSetMgr;
	
	public SetManager getQueuedSetManager() throws Exception {
		if(queuedSetMgr==null) {
			String s=tempFileRootDir.getAbsolutePath();
			if(!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
				s=s+SerializableRecordHelper.FILE_SEPARATOR;
			s=s+QUEUED_SET_MANAGER_NAME;
			SetManager sm=new FileSystemSetManagerImpl(new File(s));
			queuedSetMgr=sm;
		}
		return queuedSetMgr;
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		triggerEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT, resources.getContainerResources());
		SetManager cSm = getCurationSetManager();
		SetManager qSm = getQueuedSetManager();
		cSm.removeSetItem(tweetItem.getTweetId());
		qSm.removeSetItem(tweetItem.getTweetId());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) {
		triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}
	
	@OnEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT)
	public void save(TweetItem tweetItem) {
		triggerEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) {
		triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
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
