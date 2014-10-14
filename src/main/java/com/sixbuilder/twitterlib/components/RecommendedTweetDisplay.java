package com.sixbuilder.twitterlib.components;

import java.io.File;
import java.util.ArrayList;
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
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.concurrent.threadpool.ThreadPoolSession;
import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueItemStatus;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;
import com.sixbuilder.twitterlib.helpers.TargetTimeCalculator;
import com.sixbuilder.twitterlib.helpers.Util;
import com.sixbuilder.twitterlib.services.QueueItemDAO;
import com.sixbuilder.twitterlib.services.QueueSettings;
import com.sixbuilder.twitterlib.services.QueueSettingsDAO;
import com.sixbuilder.twitterlib.services.TweetItemDAO;

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
	
	@Parameter(required = true, allowNull = false)
	@Property
	private File accountsRoot;
	
	@Parameter(required = true, allowNull = false)
	@Property
	private String userId;
	
	@Inject
	private QueueItemDAO queueItemDAO;
	@Inject
	private QueueSettingsDAO queueSettingsDAO;
	@Inject
	private TweetItemDAO tweetItemDAO;
	
	@Parameter
	@Property
	private QueueType queueType;
	
	@InjectComponent
	private Zone curateZone;
	
	@InjectComponent
	private Zone publishingZone;

	@Inject
    private Request request;
	
	@Property
	private TweetItem tweet;
	
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
		// find any QueueItems for this id, and delete them as well
		List<QueueItem> itemList=queueItemDAO.getPending(queueType,AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
		for(QueueItem item:itemList) {
			if(item.getTweetId().equals(tweetItem.getTweetId()))
				queueItemDAO.delete(item);
		}
		// adjust setmanagers
		SetManager cSm = getCurationSetManager(curationSetMgr);
		SetManager qSm = getQueuedSetManager(queuedSetMgr);
		cSm.removeSetItem(tweetItem.getTweetId());
		qSm.removeSetItem(tweetItem.getTweetId());
		triggerEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}
	
	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		// get queue settings for this user
		getQueueSettingsRunnable qsr=new getQueueSettingsRunnable(queueType,userId,queueSettingsDAO.getRepo());
		// get current contents of queue for user
		getQueueItemsRunnable qir=new getQueueItemsRunnable(queueType,userId,queueItemDAO.getRepo());
		List<Runnable>rl=new ArrayList<Runnable>();
		rl.add(qsr);
		rl.add(qir);
		ThreadPoolSession.execute(rl,Util.getUiThreadPool());
		// create new queueItem
		QueueItem actionQueueItem=new QueueItem();
		actionQueueItem.setDateCreated(System.currentTimeMillis());
		actionQueueItem.setTweetId(tweetItem.getTweetId());
		actionQueueItem.setQueueType(queueType);
		actionQueueItem.setStatus(QueueItemStatus.PENDING);
		actionQueueItem.setUserId(userId);
		// re-calc target times based on current queue settings
		boolean changed=TargetTimeCalculator.calcTargetTime(qsr.queueSettings, actionQueueItem, qir.queueItems, System.currentTimeMillis(),false);
		// serialize new queue item
		queueItemDAO.add(actionQueueItem);
		// set target date for tweet item
		tweetItem.setPublish(true);
		tweetItem.setTargetPublicationDate(actionQueueItem.getTargetDate());
		tweetItem.setPubTargetDisplay(TargetTimeCalculator.getTimeDisplayString(qsr.queueSettings.getTimeZoneId(), actionQueueItem.getTargetDate()));
		// serialize tweet item, to save it's target date for proper sorting
		tweetItemDAO.update(accountsRoot,userId,tweetItem);
		// re-serialize existing items if they were changed
		if(changed&&qir.queueItems.size()>0) {
			queueItemDAO.update(qir.queueItems);
		}
		// adjust set managers
		SetManager cSm = getCurationSetManager(curationSetMgr);
		SetManager qSm = getQueuedSetManager(queuedSetMgr);
		cSm.removeSetItem(tweetItem.getTweetId());
		qSm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		// remove corresponding QueueItem from queue, if it exists
		List<QueueItem> itemList=queueItemDAO.getPending(queueType,userId);
		for(QueueItem item:itemList) {
			if(item.getTweetId().equals(tweetItem.getTweetId()))
				queueItemDAO.delete(item);
		}
		tweetItem.setPubTargetDisplay(null);
		tweetItemDAO.update(accountsRoot, userId, tweetItem);
		// adjust set managers
		SetManager cSm = getCurationSetManager(curationSetMgr);
		SetManager qSm = getQueuedSetManager(queuedSetMgr);
		cSm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		qSm.removeSetItem(tweetItem.getTweetId());
		triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, resources.getContainerResources());
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	// this is the event called by ajax poller to update publishing list, if publishing daemon has published something
	Object onRefreshPublishingZone() {
		return request.isXHR() ? publishingZone.getBody() : null;
	}
	
	SetManager curationSetMgr;
	
	public SetManager getCurationSetManager(SetManager curationSetMgr) throws Exception {
		synchronized(userId+PersistenceUtil.CURATION_SET_MANAGER_NAME) {
			if(curationSetMgr==null) {
				SetManager sm=PersistenceUtil.getCurationSetManager(accountsRoot, userId);
				curationSetMgr=sm;
			}
		}
		return curationSetMgr;
	}
	
	SetManager queuedSetMgr;
	
	public SetManager getQueuedSetManager(SetManager queuedSetMgr) throws Exception {
		synchronized(userId+PersistenceUtil.QUEUED_SET_MANAGER_NAME) {
			if(queuedSetMgr==null) {
				SetManager sm=PersistenceUtil.getQueuedSetManager(accountsRoot, userId);
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
	
	@OnEvent(Queue.UPDATE_QUEUE_VIEW_EVENT)
	public void updatePublishingZone() {
		ajaxResponseRenderer.addRender(publishingZone);
	}
	
	class getQueueSettingsRunnable implements Runnable {
		private QueueType queueType;
		private String userId;
		QueueSettingsRepository repo;
		public getQueueSettingsRunnable(QueueType queueType,String userId,QueueSettingsRepository repo) {
			this.queueType=queueType;
			this.userId=userId;
			this.repo=repo;
		}
		public QueueSettings queueSettings;
		public void run() {
			queueSettings=repo.getQueueSettings(queueType, userId);
		}
	}
	
	class getQueueItemsRunnable implements Runnable {
		private QueueType queueType;
		private String userId;
		private QueueItemRepository repo;
		public getQueueItemsRunnable(QueueType queueType,String userId,QueueItemRepository repo) {
			this.queueType=queueType;
			this.userId=userId;
			this.repo=repo;
		}
		public List<QueueItem> queueItems;
		public void run() {
			queueItems=repo.getPending(queueType, userId);
		}
	}
	
}
