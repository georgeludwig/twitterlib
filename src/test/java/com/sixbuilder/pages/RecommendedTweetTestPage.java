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
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.georgeludwigtech.common.setmanager.SetItem;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.FileUtil;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceClient;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceRequest;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceResponse;
import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.actionqueue.QueueItem;
import com.sixbuilder.actionqueue.QueueItemRepository;
import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.QueueSettingsRepository;
import com.sixbuilder.twitterlib.services.TweetItemDAO;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedTweetTestPage {

	@Property
	private String dbAccountName=AbstractTestSixBuilder.DBACCOUNT;
	@Property
	private String dbPassword=AbstractTestSixBuilder.DBPWD;
	@Property
	private String userId=AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME;
	@Property
	private QueueType queueType=QueueType.TEST;
	@Property
	private String queueSettingsDbName=QueueSettingsRepository.DBNAME;
	@Property
	private String queueItemDbName=QueueItemRepository.DBNAME;
	
	@Inject
	private TweetItemDAO tweetItemDAO;

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
		// create snapshots
		PendingTweetFileUtil util=new PendingTweetFileUtil(AbstractTestSixBuilder.getTestUserPath()+PendingTweetFileUtil.FILENAME);	
		for(TweetItem ti:util.getPendingTweetMap().values()) {
			UrlSnapshotServiceRequest req=new UrlSnapshotServiceRequest();
			req.setTargetUrl(ti.getUrl());
			req.setServiceUrl("http://my.6builder.com:3001");
			req.setWidth(300);
			req.setHeight(240);
			UrlSnapshotServiceResponse resp = UrlSnapshotServiceClient.snap(req);
			ti.setSnapshotUrl(resp.getImageUrl());
		}
		util.serialize();
		// create curation setitems
		File testRootDir=new File(AbstractTestSixBuilder.getTestRoot());
		SetManager curationSetMgr = PersistenceUtil.getCurationSetManager(testRootDir,AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
		for (TweetItem ti : getTweetItems()) {
			curationSetMgr.addSetItem(new SetItemImpl(ti.getTweetId()));
		}
		// clear out any existing queue items
		QueueItemRepository repo=getQueueItemRepository();
		List<QueueItem>itemList=repo.getAll();
		repo.delete(itemList);
	}
	
	private QueueItemRepository qRepo;
	private Integer repoSem=new Integer(0);
	QueueItemRepository getQueueItemRepository() {
		if(qRepo==null) {
			synchronized(repoSem) {
				if(qRepo==null) {
					HttpClient httpClient = new StdHttpClient.Builder()
						.host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443)
						.username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD)
						.enableSSL(true).relaxedSSLSettings(true).build();
					CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
					CouchDbConnector db = new StdCouchDbConnector(queueItemDbName, dbInstance);
					db.createDatabaseIfNotExists();
					QueueItemRepository qr = new QueueItemRepository(db);
					qRepo=qr;
				}
			}
		}
		return qRepo;
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
		for (TweetItem ti : getTweetItems()) {
			if (c.contains(new SetItemImpl(ti.getTweetId())))
				ret.add(ti);
		}
		return ret;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT)
	public List<TweetItem> getPublishing() throws Exception {
		queuedSetMgr = PersistenceUtil.getQueuedSetManager(
				new File(AbstractTestSixBuilder.getTestRoot()), AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME);
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
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
		tweetItemDAO.delete(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully deleted",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
		tweetItemDAO.update(tweetItem);
		alertManager.success(String.format(
				"Message with id %s was successfully selected to be published",
				tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.MEH_TWEET_EVENT)
	public void meh(TweetItem tweetItem) throws Exception {
		// the ReccommendedTweetDisplay should have handled the set manager stuff as well
		// as the queue item stuff
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

	public File getAccountsRoot() throws Exception {
		return new File(AbstractTestSixBuilder.getTestRoot());
	}
	
//	public String getAccountName() {
//		return AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME;
//	}
}
