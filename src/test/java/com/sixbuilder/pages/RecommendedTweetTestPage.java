package com.sixbuilder.pages;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.jobsequence.discovery.JobDiscoverContentOfMutualInterestNew;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.TweetItemComparatorByDisplayOrder;
import com.sixbuilder.twitterlib.helpers.TweetItemComparatorByTargetDate;
import com.sixbuilder.twitterlib.services.QueueItemDAO;
import com.sixbuilder.twitterlib.services.TweetItemDAO;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedTweetTestPage {

	@Property
	private String userId=AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME;

	@Property
	private QueueType queueType=QueueType.TEST;
	
	@Inject
	private TweetItemDAO tweetItemDAO;
	@Inject
	private QueueItemDAO queueItemDAO;

	@Property
	private TweetItem tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private AlertManager alertManager;

	SetManager curationSetMgr;

	SetManager queuedSetMgr;

	void init() throws Exception {
		// clear all contents of test dir
		String userPath=AbstractTestSixBuilder.getTestUserPath();
		FileUtil.clearDirectory(new File(userPath));
		AbstractTestSixBuilder.setUpBasicFiles(userPath);
		// copy pending tweet file from resources to test dir
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream is = classLoader.getResourceAsStream("pendingTweets.txt");
		File target = new File(userPath+SerializableRecordHelper.FILE_SEPARATOR + "pendingTweets.txt");
		FileUtil.copy(is, target);
		PendingTweetFileUtil util=new PendingTweetFileUtil(AbstractTestSixBuilder.getTestUserPath()+PendingTweetFileUtil.FILENAME);	
		// sort tweet items by score, and assign display order
		//JobDiscoverContentOfMutualInterestNew.sortByScoreAssignDisplayOrder(util);
		// create snapshots, assign ids
		JobDiscoverContentOfMutualInterestNew.createSnapshotsAndBitly(new User(), util,-1);
		// create curation setitems
		File testRootDir=new File(AbstractTestSixBuilder.getTestRoot());
		SetManager curationSetMgr = PersistenceUtil.getCurationSetManager(testRootDir,AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
		for (TweetItem ti : util.getPendingTweetMap().values()) {
			curationSetMgr.addSetItem(new SetItemImpl(ti.getTweetId()));
			if(ti.getImgOneUrl().equals(TweetItem.UNINIT_IMG_ONE_URL))
				ti.setImgOneUrl("");
			if(ti.getImgTwoUrl().equals(TweetItem.UNINIT_IMG_TWO_URL))
				ti.setImgTwoUrl("");
			if(ti.getImgThreeUrl().equals(TweetItem.UNINIT_IMG_THREE_URL))
				ti.setImgThreeUrl("");
		}
		util.serialize();
		// clear out any existing queue items
		List<QueueItem>itemList=queueItemDAO.getAll();
		queueItemDAO.delete(itemList);
	}
	
	public String getQueueId() {
		// we actually need queue type and customer id
		return "test-queue";
	}

	void setupRender() throws Exception {
		if (firstLoad == null) {
			synchronized (AbstractTestSixBuilder.getTestRoot()) {
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
		curationSetMgr = PersistenceUtil.getCurationSetManager(
				new File(AbstractTestSixBuilder.getTestRoot()), AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
		Set<SetItem> c = curationSetMgr.getSet();
		List<TweetItem> ret = new ArrayList<TweetItem>();
		for(TweetItem ti : getTweetItems()) {
			if (c.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		Collections.sort(ret,new TweetItemComparatorByDisplayOrder());
		return ret;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT)
	public List<TweetItem> getPublishing() throws Exception {
		queuedSetMgr = PersistenceUtil.getQueuedSetManager(
				new File(AbstractTestSixBuilder.getTestRoot()), AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
		Set<SetItem> q = queuedSetMgr.getSet();
		List<TweetItem> ret = new ArrayList<TweetItem>();
		for(TweetItem ti : getTweetItems()) {
			if (q.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		Collections.sort(ret,new TweetItemComparatorByTargetDate());
		return ret;
	}

	public List<TweetItem> getTweetItems() throws Exception {
		return tweetItemDAO.getAll(getAccountsRoot(),userId);
	}

	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
		//tweetItemDAO.delete(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully deleted",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
		//tweetItemDAO.update(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully selected to be published",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
		//tweetItemDAO.update(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully meh'd",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT)
	public TweetItem load(String id) throws Exception {
		return tweetItemDAO.findById(getAccountsRoot(),userId,id);
	}

	public File getAccountsRoot() throws Exception {
		return new File(AbstractTestSixBuilder.getTestRoot());
	}

}
