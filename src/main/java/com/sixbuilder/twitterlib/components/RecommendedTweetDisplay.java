package com.sixbuilder.twitterlib.components;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.concurrent.threadpool.ThreadPoolSession;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceClient;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceRequest;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceResponse;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.Url;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemComparatorByTargetDate;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueItemStatus;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.datatypes.twitter.TweetUtil;
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
	RecommendedTweetConstants.LOAD_TWEET_EVENT,
	RecommendedTweetConstants.MEH_TWEET_EVENT})
public class RecommendedTweetDisplay {
	
	public static final String UPDATE_ALL_LISTS = "updateAllLists";
	public static final String URLSNAPSHOTSERVICEURL="http://54.167.23.23:8080";
	
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
		List<QueueItem> itemList=queueItemDAO.getPending(queueType,userId);
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
		if(tweetItem.isPublish()) {
			ajaxResponseRenderer.addRender(publishingZone);
		} else ajaxResponseRenderer.addRender(curateZone);
	}
	
	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		tweetItem.setDataMode(TweetItem.DATAMODE_DETAIL);
		boolean isMoved=!tweetItem.isPublish();
		tweetItem.setPublish(true);
		// shorten the url
		updateTweetItemWithBitly(tweetItem);
		// get queue settings for this user
		getQueueSettingsRunnable qsr=new getQueueSettingsRunnable(queueType,userId,queueSettingsDAO.getRepo());
		// get current contents of queue for user
		getQueueItemsRunnable qir=new getQueueItemsRunnable(queueType,userId,queueItemDAO.getRepo());
		List<Runnable>rl=new ArrayList<Runnable>();
		rl.add(qsr);
		rl.add(qir);
		ThreadPoolSession.execute(rl,Util.getUiThreadPool());
		// only create new queue item if there isn't already on
		boolean createNew=true;
		for(QueueItem qi:qir.queueItems) {
			if(qi.getTweetId().equals(tweetItem.getTweetId()))
				createNew=false;
		}
		if(createNew) {
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
		}
		// serialize tweet item, to save it's target date for proper sorting
		boolean success=false;
		while(!success) {
			tweetItemDAO.deleteById(accountsRoot, userId, tweetItem.getTweetId()); // this is a hack in case the user entered something in the url field just before clicking on publish
			tweetItemDAO.update(accountsRoot,userId,tweetItem);
			success=true;
		}
		if(isMoved) {
			ajaxResponseRenderer.addRender(curateZone);
			ajaxResponseRenderer.addRender(publishingZone);
		}
	}
	
	private void updateTweetItemWithBitly(TweetItem tweetItem) {
		try {
			// extract urls from tweet body
			String url=TweetUtil.getFirstUrl(tweetItem.getSummary());
			// determine start point of url within tweet body
			String summary=tweetItem.getSummary();
			int start=summary.indexOf(url);
			// encode url
			String accountPath=AccountManager.getAccountPath(accountsRoot.toString(), userId);
			User user=new User(AccountManager.getUserFile(accountPath));
			String shortUrl=shortenUrlUsingBitly(user,url);
			// reconstruct summary
			summary=summary.substring(0,start)+shortUrl+summary.substring(start+url.length());
			tweetItem.setSummary(summary);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Persist
	private Integer mehInc;
	
	private Integer getMehInc() {
		if(mehInc==null)
			mehInc=50;
		mehInc+=50;
		return mehInc;
	}
	
	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		if(tweetItem.isPublish())
			tweetItem.setDataMode(TweetItem.DATAMODE_DETAIL);
		else tweetItem.setDataMode(TweetItem.DATAMODE_SUMMARY);
		boolean isMoved=tweetItem.isPublish();
		tweetItem.setPublish(false);
		// remove corresponding QueueItem from queue, if it exists
		List<QueueItem> itemList=queueItemDAO.getPending(queueType,userId);
		for(QueueItem item:itemList) {
			if(item.getTweetId().equals(tweetItem.getTweetId()))
				queueItemDAO.delete(item);
		}
		tweetItem.setPubTargetDisplay(null);
		if(tweetItem.isPublish())
			tweetItem.setDisplayOrder(getMehInc()+tweetItem.getDisplayOrder());
		boolean success=false;
		while(!success) {
			tweetItemDAO.update(accountsRoot,userId,tweetItem);
			success=true;
		}
		// adjust set managers
		SetManager cSm = getCurationSetManager(curationSetMgr);
		SetManager qSm = getQueuedSetManager(queuedSetMgr);
		cSm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		qSm.removeSetItem(tweetItem.getTweetId());
		triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, resources.getContainerResources());
		if(isMoved) {
			ajaxResponseRenderer.addRender(curateZone);
			ajaxResponseRenderer.addRender(publishingZone);
		}
	}

	@OnEvent(RecommendedTweetConstants.REVISE_TWEET_EVENT)
	public void revise(TweetItem tweetItem) throws Exception {
		String url=tweetItem.getUrl();
		if(!processImageUrl(tweetItem, url)) {
			// make sure we have protocol for snapshot worker's benefit
			url=url.trim();
			if(!url.startsWith("http"))
				url="http://"+url;
			if(!(url.length()>4096)) {
				tweetItem.setDataMode(TweetItem.DATAMODE_DETAIL);
				// start thread for images
				getNewSnapshotWorker worker=new getNewSnapshotWorker(url);
				Thread t=new Thread(worker);
				t.start();
				try {
					String accountPath=AccountManager.getAccountPath(accountsRoot.toString(), userId);
					//String shortUrl=shortenUrlUsingBitly(user,url);
					t.join();
					tweetItem.setUrl(url);
					String oldId=tweetItem.getTweetId();
					tweetItem.setTweetId(String.valueOf(tweetItem.getDisplayOrder()+""+url.hashCode()));
					tweetItem.setShortenedUrl(url); // we no longer bitly encode here...it's done when they queue tweet for publication
					String s=worker.resp.getUrlTitle();
					if(s==null)
						s="";
					tweetItem.setSummary(s.trim()+" "+url);
					if(worker.resp.getSnapshotUrl()!=null) {
						tweetItem.setSnapshotUrl(worker.resp.getSnapshotUrl());
					}
					List<String>imgList=worker.resp.getImageUrlList();
					if(imgList!=null) {
						if(imgList.size()>0)
							tweetItem.setImgOneUrl(imgList.get(0));
						if(imgList.size()>1)
							tweetItem.setImgTwoUrl(imgList.get(1));
						if(imgList.size()>2)
							tweetItem.setImgThreeUrl(imgList.get(2));
					}
					tweetItemDAO.update(accountsRoot, userId, tweetItem);
					if(tweetItem.isPublish()) {
						SetManager qm=PersistenceUtil.getQueuedSetManager(accountsRoot, userId);
						qm.removeSetItem(oldId);
						qm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
					} else {
						SetManager cm=PersistenceUtil.getCurationSetManager(accountsRoot, userId);
						cm.removeSetItem(oldId);
						cm.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		triggerEvent(RecommendedTweetConstants.REVISE_TWEET_EVENT, resources.getContainerResources());
		if(tweetItem.isPublish())
			ajaxResponseRenderer.addRender(publishingZone);
		else ajaxResponseRenderer.addRender(curateZone);
	}
	
	@Inject
	@Path("classpath:com/sixbuilder/twitterlib/components/unsupportedImageFormat.png")
	@Property
	private Asset unsupportedImagePng;
	
	private boolean processImageUrl(TweetItem tweetItem,String url) {
		url=cleanImgUrl(url); // make sure it starts with http
		// reject extremely long urls...it;s probably in image encoded in-line
		if((url.trim().length()>4096)) {
			setNewImageUrl(tweetItem, unsupportedImagePng.toClientURL());
			return true;
		}
		boolean isImg=false;
		String mimeType=null;
		// get content type
		URLConnection conn=null;
		URL u=null;
		try {
			u=new URL(url);
			conn = u.openConnection();
			mimeType=conn.getContentType();
			// determine if mime type is image format
			if(mimeType.contains("image"))
				isImg=true;
			else if(checkForImageExt(url))
				isImg=true;
			else conn.getInputStream().close();
		} catch(Exception e ) {
			System.out.println(e.getMessage());
			try {
				conn.getInputStream().close();
			} catch(Exception ee) {}
		}	
		if(isImg) {
			boolean supported=false;
			String mime=null;
			// check for supported type based on mime
			for(String ext:imgExtList) {
				if(mimeType.contains(ext)) {
					supported=true;
					mime=ext;
				}
			}
			// some servers report incorrect mime type, so if necessary check based on img ext
			if(mime==null) {
				for(String ext:imgExtList) {
					if(url.contains("."+ext)) {
						supported=true;
						mime=ext;
					}
				}
			}
			if(supported) {
				// get image size
				try {
					conn = u.openConnection();
					Integer size = conn.getContentLength();
					if(url.toLowerCase().endsWith("gif")&&size>3000000) {
						url=unsupportedImagePng.toClientURL();
					} else {
						if(size>5000000)
							url=unsupportedImagePng.toClientURL();
					}
				} catch (Exception e) {
					e.printStackTrace();
					url=unsupportedImagePng.toClientURL();
				} finally {
					if(conn!=null) {
						try {
							conn.getInputStream().close();
						} catch(Exception e) {}
					}
				}
			} else {
				url=unsupportedImagePng.toClientURL();
				if(conn!=null) {
					try {
						conn.getInputStream().close();
					} catch(Exception e) {}
				}
			}
		}
		if(isImg)
			setNewImageUrl(tweetItem,url);
		return isImg;
	}
	
	private boolean checkForImageExt(String url) {
		boolean ret=false;
		for(String ext:imgExtList) {
			if(url.contains("."+ext))
				ret=true;
		}
		return ret;
	}
	
	private void setNewImageUrl(TweetItem tweetItem,String imgUrl) {
		// set the appropriate image url to the incoming url
		if(tweetItem.getImgIdx()==0)
			tweetItem.setSnapshotUrl(imgUrl.trim());
		if(tweetItem.getImgIdx()==1)
			tweetItem.setImgOneUrl(imgUrl.trim());
		if(tweetItem.getImgIdx()==2)
			tweetItem.setImgTwoUrl(imgUrl.trim());
		if(tweetItem.getImgIdx()==3)
			tweetItem.setImgThreeUrl(imgUrl.trim()); 
		try {
			// find the un-altered tweet item, in order to get the original url
			List<TweetItem>tiList=tweetItemDAO.getAll(accountsRoot,userId);
			TweetItem original=null;
			for(TweetItem ti:tiList) {
				if(ti.getTweetId().equals(tweetItem.getTweetId()))
					original=ti;
			}
			String originalUrl=tweetItem.getUrl();
			if(original!=null)
				originalUrl=original.getUrl();
			tweetItem.setUrl(originalUrl);
			// save it
			tweetItemDAO.update(accountsRoot, userId, tweetItem);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String cleanImgUrl(String url) {
		String ret=url;
		if(!ret.toLowerCase().startsWith("http"))
			ret="http://"+ret;
		return ret;
	}
		
	private static final List<String>imgExtList=Arrays.asList("jpg","JPG","jpeg","JPEG","webp","WEBP","gif","GIF","png","PNG","img","IMG");
	
//	private String stripUrls(String input) {
//		String[] sa=input.split(" ");
//		for(int i=0;i<sa.length;i++) {
//			if(sa[i].startsWith("http://")||sa[i].startsWith("https://"))
//				input=input.replaceAll(sa[i], "");
//		}
//		return input;
//	}

	private String shortenUrlUsingBitly(User user,String url) throws Exception {
		// bitly encode url
		String bitlyUserName = user.getBitlyUserName();
		String bitlyApiKey = user.getBitlyApiKey();
		if((bitlyUserName==null||bitlyUserName.trim().length()==0) ||
				(bitlyApiKey==null||bitlyApiKey.trim().length()==0)) {
			return url;
		}
		try {
			Url u = as(bitlyUserName, bitlyApiKey).call(shorten(url));
			String shortUrl=u.getShortUrl();
			return shortUrl;
		} catch(BitlyException e) {
			return url;
		}
	}
	
	class getNewSnapshotWorker implements Runnable {
	
		private String url;
		public UrlSnapshotServiceResponse resp;
		
		getNewSnapshotWorker(String url) {
			this.url=url;
		}
	
		@Override
		public void run() {
			try {
				UrlSnapshotServiceRequest req=new UrlSnapshotServiceRequest();
				req.setWidth(1280);
				req.setHeight(1024);
				req.setTargetUrl(url);
				req.setServiceUrl(URLSNAPSHOTSERVICEURL);
				resp=UrlSnapshotServiceClient.snap(req);
			} catch(Exception e) {
				// 
			}
		}
	
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
			List<QueueItem>qil=repo.getPending(queueType, userId);
			Collections.sort(qil,new QueueItemComparatorByTargetDate());
			queueItems=qil;
		}
	}
	
}
