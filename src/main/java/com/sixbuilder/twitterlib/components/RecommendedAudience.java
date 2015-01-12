package com.sixbuilder.twitterlib.components;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import twitter4j.User;

import com.georgeludwigtech.common.setmanager.SetItem;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.common.util.Time;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.jobsequence.discovery.JobDiscoverNewUsers;

public class RecommendedAudience {
	
	@Parameter
	File accountsRoot;
	
	@Parameter
	String userId;
	
	@Parameter
	Object returnPage;
	
	@Persist
	@Property
	private String currentUserId;
	
	@Persist
	private String previousUserId;
	
	@InjectComponent
	private Zone recommendedAudienceZone;
	
	@Inject
    private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private Request request;


	public String getPreviousUserId() {
		String s=previousUserId;
		return previousUserId;
	}

	public void setPreviousUserId(String previousUserId) {
		this.previousUserId = previousUserId;
	}

	private User user;
	/**
	 * returns the current twitter user that we are going to follow (or not)
	 */
	public User getUser() throws Exception {
		if(user==null) {
			// get the list of users
			// only use those that have not already been followed/ignored/processed
			SetManager tasm=PersistenceUtil.getTargetAudienceSetManager(accountsRoot, userId);
			List<SetItem>siList=new ArrayList<SetItem>(tasm.getSet());
			if(siList.size()>0) {
				SetItem si=siList.get(0);
				user=getUserMap().get(si.getName());
				currentUserId=user.getScreenName();
			}
		}
		return user;
	}
	
//	public User getPreviouseUser() throws Exception {
//		User ret=getUserMap().get(previousUserId);
//		currentUserId=previousUserId;
//		setPreviousUserId(null);
//		return ret;
//	}

	@Persist
	private Map<String,User>userMap;
	
	private Map<String,User>getUserMap() throws Exception {
		if(userMap==null) {
			String s=PersistenceUtil.getAccountPath(accountsRoot,userId);
			File f=new File(s+JobDiscoverNewUsers.TARGET_AUDIENCE_JSON_FILENAME);
			List<User>userList=JobDiscoverNewUsers.readUsersFromJson(f);
			Map<String,User>m=new HashMap<String,User>();
			for(User u:userList)
				m.put(u.getScreenName(), u);
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
	
	boolean okay;
	void onSelectedFromOkay() {
		okay=true;
	}
	
	Object onSuccess() throws Exception {
		if(undo)
			doUndo();
		if(ignore)
			doIgnore();
		if(follow)
			doFollow();
		if(okay) {
			return doOkay();
		}
		if (request.isXHR()) {
            ajaxResponseRenderer.addRender(recommendedAudienceZone);
        }
		return null;
	}
	
	private void doUndo() throws Exception {
		SetItemImpl impl=new SetItemImpl(previousUserId);
		SetManager ism=PersistenceUtil.getTargetAudienceIgnoreSetManager(accountsRoot, userId);
		ism.removeSetItem(impl);
		SetManager fsm=PersistenceUtil.getTargetAudienceFollowSetManager(accountsRoot, userId);
		fsm.removeSetItem(impl);
		SetManager sm=PersistenceUtil.getTargetAudienceSetManager(accountsRoot, userId);
		sm.addSetItem(impl);
		currentUserId=previousUserId;
		setPreviousUserId(null);
	}
	
	private void doIgnore() throws Exception {
		SetItemImpl impl=new SetItemImpl(getUser().getScreenName());
		SetManager ism=PersistenceUtil.getTargetAudienceIgnoreSetManager(accountsRoot, userId);
		ism.addSetItem(impl);
		SetManager sm=PersistenceUtil.getTargetAudienceSetManager(accountsRoot, userId);
		sm.removeSetItem(impl);
		setPreviousUserId(getUser().getScreenName());
	}
	
	private void doFollow() throws Exception {
		SetItemImpl impl=new SetItemImpl(getUser().getScreenName());
		SetManager fsm=PersistenceUtil.getTargetAudienceFollowSetManager(accountsRoot, userId);
		fsm.addSetItem(impl);
		SetManager sm=PersistenceUtil.getTargetAudienceSetManager(accountsRoot, userId);
		sm.removeSetItem(impl);
		setPreviousUserId(getUser().getScreenName());
	}
	
	private Object doOkay() {
		return returnPage;
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
	
	public String getTweetString() {
		String txt=null;
		try {
			// replace urls with hrefs
			String tweetBody=getUser().getStatus().getText();
			String[] tokens=tweetBody.split("\\s");
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<tokens.length;i++) {
				sb.append(" ");
				if(tokens[i].toLowerCase().trim().startsWith("http")) {
					String displayUrl=tokens[i];
					if(displayUrl.startsWith("http://"))
						displayUrl=displayUrl.substring(7);
					if(displayUrl.startsWith("https://"))
						displayUrl=displayUrl.substring(8);
					sb.append("<a href=\""+tokens[i]+"\" target=\"_blank\">"+displayUrl+"</a>");
				} else sb.append(tokens[i]);
			}
			txt=sb.toString();
			txt=txt.trim();
		} catch(Exception e) {}
		return txt;
	}
	
	public String getTimeSincePublication() throws Exception {
		long timeSince=System.currentTimeMillis()-getUser().getStatus().getCreatedAt().getTime();
		// seconds
		if(timeSince<Time.MIN_MILLIS) {
			int secs=(int)timeSince/1000;
			String s=secs+"s";
			return s;
		}
		// minutes
		if(timeSince<Time.HOUR_MILLIS) {
			int mins=(int)timeSince/(int)Time.MIN_MILLIS;
			String s=mins+"m";
			return s;
		}
		// hours
		if(timeSince<Time.DAY_MILLIS) {
			int hours=(int)timeSince/(int)Time.HOUR_MILLIS;
			String s=hours+"h";
			return s;
		}
		if(getUser().getStatus().getCreatedAt().getTime()>0) {
			// date
			SimpleDateFormat ssdf=new SimpleDateFormat("dd MMM");
			Date d=new Date(getUser().getStatus().getCreatedAt().getTime());
			String s=ssdf.format(d);
			return s;
		}
		return "?";
	}
}
