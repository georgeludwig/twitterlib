package com.sixbuilder.pages;

import java.io.File;
import java.io.InputStream;
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
import com.georgeludwigtech.common.util.FileUtil;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.helpers.TestPage;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.components.RecommendedTweetDisplay;
import com.sixbuilder.twitterlib.services.TweetItemDAO;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedTweetTestPage {

	@Inject
	private TweetItemDAO tweetItemDAO;

	// @Persist
	// private List<TweetItem> tweetItemList;
	//
	@Property
	private TweetItem tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private AlertManager alertManager;

	SetManager curationSetMgr;

	SetManager queuedSetMgr;

	void init() throws Exception {
		// clear all contents of test dir, including SetItem dir
		File testRootDir = new File(TestPage.getTestRoot());
		FileUtil.clearDirectory(testRootDir);
		// copy pending tweet file from resources to test dir
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream is = classLoader.getResourceAsStream("pendingTweets.txt");
		File target = new File(testRootDir
				+ SerializableRecordHelper.FILE_SEPARATOR + "pendingTweets.txt");
		FileUtil.copy(is, target);
		SetManager curationSetMgr = null;
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(
				getTempFileRootDir(), curationSetMgr);
		for (TweetItem ti : getTweetItems()) {
			curationSetMgr.addSetItem(new SetItemImpl(ti.getTweetId()));
		}
	}
	
	public String getQueueId() {
		// we actually need queue type and customer id
		return "test-queue";
	}

	void setupRender() throws Exception {
		if (firstLoad == null) {
			synchronized (getTempFileRootDir()) {
				if (firstLoad == null) {
					firstLoad = true;
					init();
				}
			}
		}
	}

	@Persist
	private Boolean firstLoad;

	@OnEvent(RecommendedTweetConstants.CURATING_TWEETS_EVENT)
	public List<TweetItem> getCurating() throws Exception {
		curationSetMgr = RecommendedTweetDisplay.getCurationSetManager(
				getTempFileRootDir(), curationSetMgr);
		Set<SetItem> c = curationSetMgr.getSet();
		List<TweetItem> ret = new ArrayList<TweetItem>();
		for (TweetItem ti : getTweetItems()) {
			if (c.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		return ret;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT)
	public List<TweetItem> getPublishing() throws Exception {
		queuedSetMgr = RecommendedTweetDisplay.getQueuedSetManager(
				getTempFileRootDir(), queuedSetMgr);
		Set<SetItem> q = queuedSetMgr.getSet();
		List<TweetItem> ret = new ArrayList<TweetItem>();
		for (TweetItem ti : getTweetItems()) {
			if (q.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		return ret;
	}

	public List<TweetItem> getTweetItems() throws Exception {
		return tweetItemDAO.getAll();
	}

	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager STUFF
		tweetItemDAO.delete(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully deleted",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff
		tweetItemDAO.update(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully selected to be published",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff
		tweetItemDAO.update(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully meh'd",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT)
	public TweetItem shortenUrl(TweetItem tweetItem) throws Exception {
		tweetItem.setShortenedUrl(shortenUrlUsingBitly(tweetItem.getUrl()));
		tweetItemDAO.update(tweetItem);
		return tweetItem;
	}

	@OnEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT)
	public TweetItem load(String id) throws Exception {
		return tweetItemDAO.findById(id);
	}

	private String shortenUrlUsingBitly(String url) {
		return "http://bitly/tweet";
	}

	@Persist
	private File setManagerRootDir;

	public File getTempFileRootDir() throws Exception {
		if (setManagerRootDir == null) {
			String testRoot = TestPage.getTestRoot();
			if (testRoot.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
				testRoot = testRoot + SerializableRecordHelper.FILE_SEPARATOR;
			File f = new File(testRoot + "curationSetManager");
			if (!f.exists())
				f.mkdirs();
			setManagerRootDir = f;
		}
		return setManagerRootDir;
	}

	public void setTempFileRootDir(File f) {
		setManagerRootDir = f;
	}
}
