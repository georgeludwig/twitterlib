package com.sixbuilder.actionqueue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;

import com.georgeludwigtech.common.setmanager.FileSystemSetManagerImpl;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
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
	
	public QueueItemProcessor(File accountsRoot,CouchDbConnector db) {
		setAccountsRoot(accountsRoot);
		setDbConnector(db);
		queuedItemSet=new ArrayList<QueueItem>();
	}
	
	public void run() {
		while (true) {
			try {
				// get the next queueItem
				QueueItem qi=getNextQueueItem();
				// process the queue item
				processQueueItem(qi);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void processQueueItem(QueueItem qi) {
		// right now we only handle curation queue
		if (qi.getQueueId() == QueueId.CURATION) {
			try {
				String accountName = qi.getUserId();
				// get the pending tweets file
				PendingTweetFileUtil util = getPendingTweetFileUtil(accountsRoot, accountName);
				// get the twitterUtil
				TwitterUtil tUtil = getTwitterUtil(accountsRoot, accountName);
				// get the tweet
				TweetItem tItem = null;
				for (TweetItem ti : util.getPendingTweetMap().values()) {
					if (ti.getTweetId() == qi.getTweetId())
						tItem = ti;
				}
				// publish the tweet
				if(tItem.isAttachSnapshot()) {
					tUtil.setStatus(tItem.getSummary(), new URL(tItem.getSnapshotUrl()));
				} else tUtil.setStatus(tItem.getSummary());
				// update the queue set manager
				SetManager queuedSm = getQueuedSetManager(accountsRoot, accountName);
				queuedSm.removeSetItem(qi.getId());
				// update the processed set manager
				SetManager procdSm = getQueuedSetManager(accountsRoot, accountName);
				procdSm.addSetItem(new SetItemImpl(qi.getId()));
				// update the queueitem in the db
				qi.setStatus(QueueItemStatus.COMPLETE);
				getDbConnector().update(qi);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private File accountsRoot;

	public File getAccountsRoot() {
		return accountsRoot;
	}

	public void setAccountsRoot(File accountsRoot) {
		this.accountsRoot = accountsRoot;
	}

	private CouchDbConnector dbConnector;
	
	public CouchDbConnector getDbConnector() {
		return dbConnector;
	}

	public void setDbConnector(CouchDbConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	private static String checkPath(String s) {
		if (!s.endsWith(SerializableRecordHelper.FIELD_SEPARATOR))
			s = s + SerializableRecordHelper.FIELD_SEPARATOR;
		return s;
	}
	
	private List<QueueItem>queuedItemSet;
	
	public void addQueueItem(QueueItem queueItem) {
		synchronized(queuedItemSet) {
			queuedItemSet.add(queueItem);
			Collections.sort(queuedItemSet,new QueueItemComparatorByTargetDate());
			queuedItemSet.notifyAll();
		}
	}
	
	public void addQueueItems(Collection<QueueItem> queueItems) {
		synchronized(queuedItemSet) {
			queuedItemSet.addAll(queueItems);
			Collections.sort(queuedItemSet,new QueueItemComparatorByTargetDate());
			queuedItemSet.notifyAll();
		}
	}

	private QueueItem getNextQueueItem() throws Exception {
		synchronized(queuedItemSet) {
			if(queuedItemSet.size()>0)
				return queuedItemSet.remove(0);
			queuedItemSet.wait();
			return queuedItemSet.remove(0);
		}
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
