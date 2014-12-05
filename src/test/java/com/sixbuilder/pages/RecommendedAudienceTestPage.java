package com.sixbuilder.pages;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

import twitter4j.User;

import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.FileUtil;
import com.georgeludwigtech.common.util.SerializableRecordHelper;
import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.jobsequence.discovery.JobDiscoverNewUsers;
import com.sixbuilder.twitterlib.components.RecommendedTweet;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedAudienceTestPage {

	void init() throws Exception {
		// clear all contents of test dir
		String userPath=AbstractTestSixBuilder.getTestUserPath();
		FileUtil.clearDirectory(new File(userPath));
		AbstractTestSixBuilder.setUpBasicFiles(userPath);
		// copy pending notified json file from resources to test dir
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream is = classLoader.getResourceAsStream("pendingNotifiedUsers.json");
		File target = new File(userPath+SerializableRecordHelper.FILE_SEPARATOR + "pendingNotifiedUsers.json");
		FileUtil.copy(is, target);
		// set up set managers
		List<User>userList=JobDiscoverNewUsers.readUsersFromJson(target);
		SetManager sm=PersistenceUtil.getTargetAudienceSetManager(getAccountsRoot(),getUserId());
		for(User u:userList) {
			sm.addSetItem(new SetItemImpl(String.valueOf(u.getScreenName())));
		}
	}

	public File getAccountsRoot() throws Exception {
		return new File(AbstractTestSixBuilder.getTestRoot());
	}
	
	public String getUserId() {
		return AbstractTestSixBuilder.PRIMARY_TEST_USER_NAME;
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
	
}
