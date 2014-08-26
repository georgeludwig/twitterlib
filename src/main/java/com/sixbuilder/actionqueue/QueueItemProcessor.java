package com.sixbuilder.actionqueue;

import java.io.File;

import com.georgeludwigtech.common.setmanager.FileSystemSetManagerImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.georgeludwigtechnology.twitterutil.TwitterUtil;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;

public class QueueItemProcessor implements Runnable {
	
	public static final String CURATION_SET_MANAGER_ROOT="curationSetManager";
	public static final String CURATION_SET_MANAGER_NAME="curationSetMgr";
	public static final String QUEUED_SET_MANAGER_NAME="queuedSetMgr";
	public static final String PROCESSED_SET_MANAGER_NAME="processedSetMgr";
	
	public QueueItemProcessor(QueueItem queueItem, File accountRoot) {
		setQueueItem(queueItem);
		setAccountRoot(accountRoot);
	}

	public void run() {
		try {
			// process the queue item
			// right now we only handle curation queue
			if (getQueueItem().getQueueId() == QueueId.CURATION) {
				String accountName=getQueueItem().getUserId();
				// get the pending tweets file
				PendingTweetFileUtil util = getPendingTweetFileUtil(accountsRoot, accountName);
				// get the Queued set manager
				
				// get the twitterUtil
				TwitterUtil tUtil=getTwitterUtil(accountsRoot, accountName);
				// get the tweet
				TweetItem tItem=null;
				for(TweetItem ti:util.getPendingTweetMap().values()) {
					if(ti.getTweetId()==getQueueItem().getTweetId())
						tItem=ti;
				}
				// publish the tweet
				tUtil.setStatus(tItem.getSummary());
				// update the queue set manager
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private QueueItem queueItem;

	public QueueItem getQueueItem() {
		return queueItem;
	}

	public void setQueueItem(QueueItem queueItem) {
		this.queueItem = queueItem;
	}

	private File accountsRoot;

	public File getAccountsRoot() {
		return accountsRoot;
	}

	public void setAccountRoot(File accountRoot) {
		this.accountsRoot = accountRoot;
	}

	private static String checkPath(String s) {
		if (!s.endsWith(SerializableRecordHelper.FIELD_SEPARATOR))
			s = s + SerializableRecordHelper.FIELD_SEPARATOR;
		return s;
	}

	public static TwitterUtil getTwitterUtil(File accountsRoot,String accountName) throws Exception {
		User u=getUser(accountsRoot,accountName);
		String apiKey=u.getTwitterApiKey();
		String consumerSecret=u.getTwitterConsumerSecret();
		TwitterUtil twitterUtil=new TwitterUtil(getAccountPath(accountsRoot,accountName),apiKey,consumerSecret);
		twitterUtil.setScreenName(accountName);
		twitterUtil.setWaitForReset(true);
		return twitterUtil;
	}
	
	public static User getUser(File accountsRoot,String accountName) throws Exception {
		String s=getAccountPath(accountsRoot,accountName);
		File userFile=new File(s+User.DEFAULT_FILENAME);
		User u=new User(userFile);
		return u;
	}
	
	public static PendingTweetFileUtil getPendingTweetFileUtil(File accountsRoot, String accountName) throws Exception {
		String path = getAccountPath(accountsRoot, accountName);
		File f = new File(path + PendingTweetFileUtil.FILENAME);
		PendingTweetFileUtil util = new PendingTweetFileUtil(f, PendingTweetFileUtil.DEFAULT_NAME);
		return util;
	}
	
	public static String getAccountPath(File accountsRoot, String accountName) {
		String ar = accountsRoot.getAbsolutePath();
		ar = checkPath(ar);
		char[] chars = accountName.toLowerCase().toCharArray();
		StringBuilder b = new StringBuilder();
		b.append(ar);
		for (char c : chars) {
			b.append(c);
			b.append(SerializableRecordHelper.FIELD_SEPARATOR);
		}
		b.append(accountName);
		b.append(SerializableRecordHelper.FIELD_SEPARATOR);
		return b.toString();
	}
	
	public static SetManager getCurationSetManager(File accountsRoot, String accountName) throws Exception {
		File f = getCurationSetMgrRootDir(accountsRoot, accountName);
		String s = f.getAbsolutePath();
		if (!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
			s = s + SerializableRecordHelper.FILE_SEPARATOR;
		s = s + QueueItemProcessor.CURATION_SET_MANAGER_NAME;
		SetManager sm = new FileSystemSetManagerImpl(new File(s));
		return sm;
	}

	public static SetManager getQueuedSetManager(File accountsRoot, String accountName) throws Exception {
		File f = getCurationSetMgrRootDir(accountsRoot, accountName);
		String s = f.getAbsolutePath();
		if (!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
			s = s + SerializableRecordHelper.FILE_SEPARATOR;
		s = s + QueueItemProcessor.QUEUED_SET_MANAGER_NAME;
		SetManager sm = new FileSystemSetManagerImpl(new File(s));
		return sm;
	}
	
	public static SetManager getProcessedSetManager(File accountsRoot, String accountName) throws Exception {
		File f = getCurationSetMgrRootDir(accountsRoot, accountName);
		String s = f.getAbsolutePath();
		if (!s.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
			s = s + SerializableRecordHelper.FILE_SEPARATOR;
		s = s + QueueItemProcessor.PROCESSED_SET_MANAGER_NAME;
		SetManager sm = new FileSystemSetManagerImpl(new File(s));
		return sm;
	}

	public static File getCurationSetMgrRootDir(File accountsRoot, String accountName) throws Exception {
		String testRoot = getAccountPath(accountsRoot, accountName);
		if (testRoot.endsWith(SerializableRecordHelper.FILE_SEPARATOR))
			testRoot = testRoot + SerializableRecordHelper.FILE_SEPARATOR;
		File f = new File(testRoot + QueueItemProcessor.CURATION_SET_MANAGER_ROOT);
		if (!f.exists())
			f.mkdirs();
		return new File(testRoot);
	}
	
}
