package com.sixbuilder.twitterlib.helpers;

/**
 * Represents the actions that can be performed on a {@link Tweet}.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 *
 */
public enum Action {
	
	DELETE("Delete"),
	FOLLOW("Follow"),
	REPLY("Reply"),
	REPLY_ALL("Reply All", "ReplyAll"),
	RETWEET("Retweet"),
	LIST("List");
	
	private Action(String name, String cssClass) {
		this.name = "tweetAction" + name;
		this.cssClass = cssClass;
	}
	
	private Action(String name) {
		this(name, name);
	}
	
	final private String name;
	
	final private String cssClass;

	public boolean isEnabled(Tweet tweet) {
		switch (this) {
			case DELETE: return tweet.isDeleteEnabled();
			case FOLLOW: return tweet.isFollowEnabled();
			case REPLY: return tweet.isReplyEnabled();
			case REPLY_ALL: return tweet.isReplyAllEnabled();
			case RETWEET: return tweet.isRetwitEnabled();
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
			case RETWEET: tweet.setRetwitEnabled(value); break;
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
			case RETWEET: return tweet.isRetwitQueued();
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
			case RETWEET: tweet.setRetwitQueued(value); break;
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
			case RETWEET: return tweet.isRetwitCompleted();
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
			case RETWEET: tweet.setRetwitCompleted(value); break;
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
	
}
