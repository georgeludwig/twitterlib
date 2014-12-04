package com.sixbuilder.twitterlib.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.persistence.PendingNotifiedFileUtil;
import com.sixbuilder.datatypes.twitter.TwitterUser;

public class TwitterUserDAOImpl implements TwitterUserDAO {

	@Override
	public List<TwitterUser> getAll(File accountsRoot, String userId) throws Exception {
		String accountPath=AccountManager.getAccountPath(accountsRoot.toString(),userId);
		PendingNotifiedFileUtil util=new PendingNotifiedFileUtil(accountPath+PendingNotifiedFileUtil.FILENAME);
		List<TwitterUser>userList=new ArrayList<TwitterUser>();
		userList.addAll(util.getPendingNotifiedUserMap().values());
		return userList;
	}

	@Override
	public TwitterUser findById(File accountsRoot, String userId, Long id) throws Exception {
		TwitterUser twitterUser = null;
		for (TwitterUser tu : getAll(accountsRoot,userId)) {
			if (tu.getTwitterUserId()==id) {
				twitterUser = tu;
				break;
			}
		}
		return twitterUser;
	}

	@Override
	public void update(File accountsRoot, String userId, TwitterUser twitterUser) throws Exception {
		// we save all at once
		String accountPath=AccountManager.getAccountPath(accountsRoot.toString(),userId);
		PendingNotifiedFileUtil util=new PendingNotifiedFileUtil(accountPath+PendingNotifiedFileUtil.FILENAME);
		util.addRecordToCollection(twitterUser);
		util.serialize();
	}

}
