package com.sixbuilder.twitterlib.helpers;

import com.sixbuilder.twitterlib.TweetEngagementConstants;

/**
 * Represents the actions that can be performed on a {@link Tweet}.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 *
 */
public enum Action {
	
	FOLLOW("Follow", "fa-arrow-circle-o-right", TweetEngagementConstants.FOLLOW_TWEET_EVENT), // FIXME: temporary icon
	REPLY("Reply", "fa-reply", TweetEngagementConstants.REPLY_TWEET_EVENT),
	REPLY_ALL("Reply All", "ReplyAll", "fa-reply-all", TweetEngagementConstants.REPLY_ALL_TWEET_EVENT),
	RETWEET("Retweet", "fa-retweet", TweetEngagementConstants.RETWEET_TWEET_EVENT),
	FAVORITE("Favorite", "fa-star", TweetEngagementConstants.FAVORITE_TWEET_EVENT),
	LIST("List", "fa-list", TweetEngagementConstants.LIST_TWEET_EVENT),
	DELETE("Delete", "fa-trash-o", TweetEngagementConstants.DELETE_TWEET_EVENT);
	
	private Action(String name, String cssClass, String fontAwesomeIconName, String eventName) {
		this.name = name;
		this.cssClass = cssClass;
		this.eventName = eventName;
		
		if (fontAwesomeIconName != null) {
			this.fontAwesomeIconName = fontAwesomeIconName;
		}
		else {
			this.fontAwesomeIconName = "fa-question-circle";
		}
	}

	private Action(String name, String fontAwesomeIconName, String eventName) {
		this(name, "tweetAction" + name, fontAwesomeIconName, eventName);
	}
	
	final private String name;
	
	final private String cssClass;
	
	final private String fontAwesomeIconName;
	
	final private String eventName;

	public boolean isEnabled(Tweet tweet) {
		switch (this) {
			case DELETE: return tweet.isDeleteEnabled();
			case FOLLOW: return tweet.isFollowEnabled();
			case REPLY: return tweet.isReplyEnabled();
			case REPLY_ALL: return tweet.isReplyAllEnabled();
			case FAVORITE: return tweet.isFavoriteEnabled();
			case RETWEET: return tweet.isRetweetEnabled();
			case LIST: return tweet.isListEnabled();
			default: throw new RuntimeException("Should never happen");
		}
	}
	
	public void setEnabled(Tweet tweet, boolean value) {
		switch (this) {
			case DELETE: tweet.setDeleteEnabled(value); break;
			case FOLLOW: tweet.setFollowEnabled(value); break;
			case REPLY: tweet.setReplyEnabled(value); break;
			case REPLY_ALL: tweet.setReplyAllEnabled(value); break;
			case FAVORITE: tweet.setFavoriteEnabled(value); break;
			case RETWEET: tweet.setRetweetEnabled(value); break;
			case LIST: tweet.setListEnabled(value); break;
			default: throw new RuntimeException("Should never happen: " + this);
		}
	}
	
	public boolean isQueued(Tweet tweet) {
		switch (this) {
			case DELETE: return tweet.isDeleteQueued();
			case FOLLOW: return tweet.isFollowQueued();
			case REPLY: return tweet.isReplyQueued();
			case REPLY_ALL: return tweet.isReplyAllQueued();
			case FAVORITE: return tweet.isFavoriteQueued();
			case RETWEET: return tweet.isRetweetQueued();
			case LIST: return tweet.isListQueued();
			default: throw new RuntimeException("Should never happen");
		}
	}
	
	public void setQueued(Tweet tweet, boolean value) {
		switch (this) {
			case DELETE: tweet.setDeleteQueued(value); break;
			case FOLLOW: tweet.setFollowQueued(value); break;
			case REPLY: tweet.setReplyQueued(value); break;
			case REPLY_ALL: tweet.setReplyAllQueued(value); break;
			case FAVORITE: tweet.setFavoriteQueued(value); break;
			case RETWEET: tweet.setRetweetQueued(value); break;
			case LIST: tweet.setListQueued(value); break;
			default: throw new RuntimeException("Should never happen: " + this);
		}
	}
	
	public boolean isCompleted(Tweet tweet) {
		switch (this) {
			case DELETE: return tweet.isDeleteCompleted();
			case FOLLOW: return tweet.isFollowCompleted();
			case REPLY: return tweet.isReplyCompleted();
			case REPLY_ALL: return tweet.isReplyAllCompleted();
			case FAVORITE: return tweet.isFavoriteCompleted();
			case RETWEET: return tweet.isRetweetCompleted();
			case LIST: return tweet.isListCompleted();
			default: throw new RuntimeException("Should never happen");
		}
	}
	
	public void setCompleted(Tweet tweet, boolean value) {
		switch (this) {
			case DELETE: tweet.setDeleteCompleted(value); break;
			case FOLLOW: tweet.setFollowCompleted(value); break;
			case REPLY: tweet.setReplyCompleted(value); break;
			case REPLY_ALL: tweet.setReplyAllCompleted(value); break;
			case FAVORITE: tweet.setFavoriteCompleted(value); break;
			case RETWEET: tweet.setRetweetCompleted(value); break;
			case LIST: tweet.setListCompleted(value); break;
			default: throw new RuntimeException("Should never happen");
		}
	}
	
	public ActionState getState(Tweet tweet) {
		final ActionState state;
		if (isQueued(tweet)) {
			state = ActionState.QUEUED;
		}
		else if (isCompleted(tweet)) {
			state = ActionState.COMPLETED;
		}
		else if (isEnabled(tweet)) {
			state = ActionState.ENABLED;
		}
		else {
			state = ActionState.DISABLED;
		}
		return state;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCssClass(Tweet tweet) {
		return cssClass;
	}

	/**
	 * Returns the value of the fontAwesomeIconName field.
	 * @return a {@link String}.
	 */
	public String getFontAwesomeIconName() {
		return fontAwesomeIconName;
	}

	/**
	 * Returns the value of the eventName field.
	 * @return a {@link String}.
	 */
	public String getEventName() {
		return eventName;
	}
	
}
