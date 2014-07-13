package com.sixbuilder.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.georgeludwigtech.common.setmanager.SetItem;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.sixbuilder.helpers.TestPage;
import com.sixbuilder.services.TweetItemDAO;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.components.RecommendedTweetDisplay;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedTweetTestPage {
		
	@Inject
	private TweetItemDAO tweetItemDAO;

	@Persist
	private List<TweetItem> tweetItemList;
	
	@Property
	private TweetItem tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private AlertManager alertManager;
	
	SetManager curationSetMgr;
	
	SetManager queuedSetMgr;
	
	void setupRender() throws Exception {
		if(firstLoad==null) {
			synchronized(getTempFileRootDir()) {
				if(firstLoad==null) {
					firstLoad=true;
				}
			}
		}
		synchronized(firstLoad) {
			if(firstLoad) {
				// clear any setItems from previous test
				// assumption is, OOB process sets up original SetItems
				curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(getTempFileRootDir(),curationSetMgr);
				queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(getTempFileRootDir(),queuedSetMgr);
				curationSetMgr.clear();
				queuedSetMgr.clear();
				// this is where we get main list 
				tweetItemList = getTweetItems();
				// add all tweets to curation setmanager
				for(TweetItem ti:tweetItemList) {
					curationSetMgr.addSetItem(new SetItemImpl(ti.getTweetId()));
				}
				firstLoad=false;
			}
		}
	}
	
	@Persist
	private Boolean firstLoad;
	
	@OnEvent(RecommendedTweetConstants.CURATING_TWEETS_EVENT)
	public List<TweetItem> getCurating() throws Exception {
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(getTempFileRootDir(),curationSetMgr);
		Set<SetItem>c=curationSetMgr.getSet();
		List<TweetItem>ret=new ArrayList<TweetItem>();
		for(TweetItem ti:tweetItemList) {
			if(c.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		return ret;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT)
	public List<TweetItem> getPublishing() throws Exception {
		queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(getTempFileRootDir(),queuedSetMgr);
		Set<SetItem>q=queuedSetMgr.getSet();
		List<TweetItem>ret=new ArrayList<TweetItem>();
		for(TweetItem ti:tweetItemList) {
			if(q.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		return ret;
	}

	public List<TweetItem> getTweetItems() {
		return tweetItemDAO.getAll();
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(getTempFileRootDir(),curationSetMgr);
		queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(getTempFileRootDir(),queuedSetMgr);	
		curationSetMgr.removeSetItem(tweetItem.getTweetId());
		queuedSetMgr.removeSetItem(tweetItem.getTweetId());
		tweetItemList.remove(tweetItem);
		tweetItemDAO.delete(tweetItem);
		alertManager.success(String.format("Message with id %s was successfully deleted", tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(getTempFileRootDir(),curationSetMgr);
		queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(getTempFileRootDir(),queuedSetMgr);	
		curationSetMgr.removeSetItem(tweetItem.getTweetId());
		queuedSetMgr.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		tweetItemDAO.update(tweetItem);
		alertManager.success(String.format("Message with id %s was successfully selected to be published", tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(getTempFileRootDir(),curationSetMgr);
		queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(getTempFileRootDir(),queuedSetMgr);	
		curationSetMgr.addSetItem(new SetItemImpl(tweetItem.getTweetId()));
		queuedSetMgr.removeSetItem(tweetItem.getTweetId());
		tweetItemDAO.update(tweetItem);
	}
	
	@OnEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT)
	public TweetItem shortenUrl(TweetItem tweetItem) {
		tweetItem.setShortenedUrl(shortenUrlUsingBitly(tweetItem.getUrl()));
		tweetItemDAO.update(tweetItem);
		return tweetItem;
	}

	@OnEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT)
	public TweetItem load(String id) {
		return tweetItemDAO.findById(id);
	}
	
	private String shortenUrlUsingBitly(String url) {
		return "http://bitly/tweet";
	}

	@Persist
	private File setManagerRootDir;
	
	public File getTempFileRootDir() throws Exception {
		if(setManagerRootDir==null) {
			String testRoot=TestPage.getTestRoot();
			if(testRoot.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
				testRoot=testRoot+SerializableRecordHelper.FILE_SEPARATOR;
			File f=new File(testRoot+"curationSetManager");
			if(!f.exists())
				f.mkdirs();
			setManagerRootDir=f;
		}
		return setManagerRootDir;
	}
	
	public void setTempFileRootDir(File f) {
		setManagerRootDir=f;
	}
}
