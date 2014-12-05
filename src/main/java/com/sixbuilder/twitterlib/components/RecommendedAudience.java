package com.sixbuilder.twitterlib.components;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

import twitter4j.User;

import com.georgeludwigtech.common.setmanager.SetItem;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.jobsequence.discovery.JobDiscoverNewUsers;

public class RecommendedAudience {
	
	@Parameter
	File accountsRoot;
	
	@Parameter
	String userId;
	
	@Persist
	@Property
	Long currentUserId;
	
	@Persist
	@Property
	Long previousUserId;

	private User user;
	/**
	 * returns the current twitter user that we are going to follow (or not)
	 */
	public User getUser() throws Exception {
		if(user==null) {
			// get the list of users
			// only use those that have not already been followed/ignored/processed
			SetManager tasm=PersistenceUtil.getTargetAudienceSetManager(accountsRoot, userId);
			Set<SetItem>set=tasm.getSet();
			for(User u:getUserMap().values()) {
				if(set.contains(new SetItemImpl(String.valueOf(u.getId())))&&user==null) {
					if(currentUserId!=null)
						previousUserId=currentUserId;
					currentUserId=u.getId();
					user=u;
				}
			}
		}
		return user;
	}
	
	public User getPreviouseUser() throws Exception {
		User ret=getUserMap().get(previousUserId);
		currentUserId=previousUserId;
		previousUserId=null;
		return ret;
	}

	@Persist
	private Map<Long,User>userMap;
	
	private Map<Long,User>getUserMap() throws Exception {
		if(userMap==null) {
			String s=PersistenceUtil.getAccountPath(accountsRoot,userId);
			File f=new File(s+JobDiscoverNewUsers.TARGET_AUDIENCE_JSON_FILENAME);
			List<User>userList=JobDiscoverNewUsers.readUsersFromJson(f);
			Map<Long,User>m=new HashMap<Long,User>();
			for(User u:userList)
				m.put(u.getId(), u);
			userMap=m;
		}
		return userMap;
	}
	
	boolean undo;
	void onSelectedFromUndo() {
		undo=true;
		ignore=false;
		follow=false;
	}
	
	boolean ignore;
	void onSelectedFromIgnore() {
		undo=false;
		ignore=true;
		follow=false;
	}
	
	boolean follow;
	void onSelectedFromFollow() {
		undo=false;
		ignore=false;
		follow=true;
	}
	
	Object onSuccess() {
		if(undo)
			return doUndo();
		if(ignore)
			return doIgnore();
		if(follow)
			return doFollow();
		return null;
	}
	
	private Object doUndo() {
		return null;
	}
	
	private Object doIgnore() {
		return null;
	}
	
	private Object doFollow() {
		return null;
	}
	
	public String getProfileBannerUrl() throws Exception {
		User u=getUser();
		String s=u.getProfileBannerURL();
		s=s.replace("web","");
		s=s+"1500x500";
		return s;
	}
	
	public String getStatusesCount() throws Exception {
		return format(getUser().getStatusesCount());
	}
	
	public String getFollowersCount() throws Exception {
		return format(getUser().getFollowersCount());
	}
	
	public String getFriendsCount() throws Exception {
		return format(getUser().getFriendsCount());
	}
	
	private String format(int i) {
		if(i<10000)
			return formatDecimal(i);
		if(i<1000000) {
			double d=i;
			d=d/1000;
			String s=checkZ(formatK(d));
			return s+"K";
		}
		double d=i;
		d=d/1000000;
		String s=checkZ(formatK(d));
		return s+"M";
	}
	
	private String checkZ(String s) {
		if(s.endsWith(".0"))
			s=s.replace(".0", "");
		return s;
	}
	
	private String formatDecimal(int i) {
		DecimalFormat formatter = new DecimalFormat("#,###");
		String ret=formatter.format(i);
		return ret;
	}
	
	private String formatK(double d) {
		DecimalFormat formatter = new DecimalFormat("#,###.0");
		String ret=formatter.format(d);
		return ret;
	}
}
