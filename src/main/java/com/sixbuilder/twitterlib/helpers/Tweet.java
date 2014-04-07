package com.sixbuilder.twitterlib.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sixbuilder.twitterlib.components.RecommendedTweet;

/**
 * Class that represents a tweet that actually exists, in constrast to
 * {@link RecommendedTweet}, which is a recommendation of a tweet to be posted.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class Tweet {

	/**
	 * Id of this twit.
	 */
	private String id;

	/**
	 * User profile (author) of this twit.
	 */
	private String twitterUsernameId;

	/**
	 * Id of the Twitter client that posted this tweet.
	 */
	private String twitterClientId;

	/**
	 * Tells whether this tweet is a retwit of another.
	 */
	private boolean retweet;

	/**
	 * Original retwitted twit id.
	 */
	private String retwittedId;

	/**
	 * Original retwitted twit screen name.
	 */
	private String retwittedTwitterUsername;

	/**
	 * Original retwitted twit posted date.
	 */
	private Date retwittedDate;

	/**
	 * Date and time this twit was posted.
	 */
	private Date posted;

	/**
	 * Content (text) of this twit.
	 */
	private String content;

	/**
	 * List of tweets connected to this one, forming a conversation.
	 */
	private List<Tweet> conversation;

	/**
	 * Lists to which this tweet author username may be added.
	 */
	private List<String> lists = new ArrayList<String>();

	/**
	 * Lists to which this tweet author username was already added.
	 */
	private List<Boolean> listed = new ArrayList<Boolean>();

	/**
	 * List of user profiles for reply-all feature.
	 */
	private List<String> replyAll;

	private boolean clearEnabled;

	private boolean conversationEnabled;
	
	private boolean deleteEnabled;
	private boolean deleteQueued;
	private boolean deleteCompleted;
	
	private boolean followEnabled;
	private boolean followQueued;
	private boolean followCompleted;

	private boolean replyEnabled;
	private boolean replyQueued;
	private boolean replyCompleted;

	private boolean replyAllEnabled;
	private boolean replyAllQueued;
	private boolean replyAllCompleted;

	private boolean retwitEnabled;
	private boolean retwitQueued;
	private boolean retwitCompleted;

	private boolean favoriteEnabled;
	private boolean favoriteQueued;
	private boolean favoriteCompleted;

	private boolean listEnabled;
	private boolean listQueued;
	private boolean listCompleted;

	/**
	 * URL of 6BU profile image.
	 */
	private String profilePictureUrl;

	/**
	 * 6BU user name.
	 */
	private String username;

	/**
	 * Returns the value of the id field.
	 * 
	 * @return a {@link String}.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id field.
	 * 
	 * @param id
	 *            a {@link String}.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the value of the twitterUsernameId field.
	 * 
	 * @return a {@link String}.
	 */
	public String getTwitterUsernameId() {
		return twitterUsernameId;
	}

	/**
	 * Sets the value of the twitterUsernameId field.
	 * 
	 * @param twitterUsernameId
	 *            a {@link String}.
	 */
	public void setTwitterUsernameId(String twitterUsernameId) {
		this.twitterUsernameId = twitterUsernameId;
	}

	/**
	 * Returns the value of the twitterClientId field.
	 * 
	 * @return a {@link String}.
	 */
	public String getTwitterClientId() {
		return twitterClientId;
	}

	/**
	 * Sets the value of the twitterClientId field.
	 * 
	 * @param twitterClientId
	 *            a {@link String}.
	 */
	public void setTwitterClientId(String twitterClientId) {
		this.twitterClientId = twitterClientId;
	}

	/**
	 * Returns the value of the retweet field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isRetweet() {
		return retweet;
	}

	/**
	 * Sets the value of the retweet field.
	 * 
	 * @param retweet
	 *            a {@link boolean}.
	 */
	public void setRetweet(boolean retweet) {
		this.retweet = retweet;
	}

	/**
	 * Returns the value of the retwittedId field.
	 * 
	 * @return a {@link String}.
	 */
	public String getRetwittedId() {
		return retwittedId;
	}

	/**
	 * Sets the value of the retwittedId field.
	 * 
	 * @param retwittedId
	 *            a {@link String}.
	 */
	public void setRetwittedId(String retwittedId) {
		this.retwittedId = retwittedId;
	}

	/**
	 * Returns the value of the retwittedTwitterUsername field.
	 * 
	 * @return a {@link String}.
	 */
	public String getRetwittedTwitterUsername() {
		return retwittedTwitterUsername;
	}

	/**
	 * Sets the value of the retwittedTwitterUsername field.
	 * 
	 * @param retwittedTwitterUsername
	 *            a {@link String}.
	 */
	public void setRetwittedTwitterUsername(String retwittedTwitterUsername) {
		this.retwittedTwitterUsername = retwittedTwitterUsername;
	}

	/**
	 * Returns the value of the retwittedDate field.
	 * 
	 * @return a {@link Date}.
	 */
	public Date getRetwittedDate() {
		return retwittedDate;
	}

	/**
	 * Sets the value of the retwittedDate field.
	 * 
	 * @param retwittedDate
	 *            a {@link Date}.
	 */
	public void setRetwittedDate(Date retwittedDate) {
		this.retwittedDate = retwittedDate;
	}

	/**
	 * Returns the value of the posted field.
	 * 
	 * @return a {@link Date}.
	 */
	public Date getPosted() {
		return posted;
	}

	/**
	 * Sets the value of the posted field.
	 * 
	 * @param posted
	 *            a {@link Date}.
	 */
	public void setPosted(Date posted) {
		this.posted = posted;
	}

	/**
	 * Returns the value of the content field.
	 * 
	 * @return a {@link String}.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the value of the content field.
	 * 
	 * @param content
	 *            a {@link String}.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the value of the conversation field.
	 * 
	 * @return a {@link List<Tweet>}.
	 */
	public List<Tweet> getConversation() {
		return conversation;
	}

	/**
	 * Sets the value of the conversation field.
	 * 
	 * @param conversation
	 *            a {@link List<Tweet>}.
	 */
	public void setConversation(List<Tweet> conversation) {
		this.conversation = conversation;
	}

	/**
	 * Returns the value of the lists field.
	 * 
	 * @return a {@link List<String>}.
	 */
	public List<String> getLists() {
		return lists;
	}

	/**
	 * Sets the value of the lists field.
	 * 
	 * @param lists
	 *            a {@link List<String>}.
	 */
	public void setLists(List<String> lists) {
		this.lists = lists;
	}

	/**
	 * Returns the value of the listed field.
	 * 
	 * @return a {@link List<Boolean>}.
	 */
	public List<Boolean> getListed() {
		return listed;
	}

	/**
	 * Sets the value of the listed field.
	 * 
	 * @param listed
	 *            a {@link List<Boolean>}.
	 */
	public void setListed(List<Boolean> listed) {
		this.listed = listed;
	}

	/**
	 * Returns the value of the replyAll field.
	 * 
	 * @return a {@link List<String>}.
	 */
	public List<String> getReplyAll() {
		return replyAll;
	}

	/**
	 * Sets the value of the replyAll field.
	 * 
	 * @param replyAll
	 *            a {@link List<String>}.
	 */
	public void setReplyAll(List<String> replyAll) {
		this.replyAll = replyAll;
	}

	/**
	 * Returns the value of the clearEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isClearEnabled() {
		return clearEnabled;
	}

	/**
	 * Sets the value of the clearEnabled field.
	 * 
	 * @param clearEnabled
	 *            a {@link boolean}.
	 */
	public void setClearEnabled(boolean clearEnabled) {
		this.clearEnabled = clearEnabled;
	}

	/**
	 * Returns the value of the conversationEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isConversationEnabled() {
		return conversationEnabled;
	}

	/**
	 * Sets the value of the conversationEnabled field.
	 * 
	 * @param conversationEnabled
	 *            a {@link boolean}.
	 */
	public void setConversationEnabled(boolean conversationEnabled) {
		this.conversationEnabled = conversationEnabled;
	}

	/**
	 * Returns the value of the deleteEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isDeleteEnabled() {
		return deleteEnabled;
	}

	/**
	 * Sets the value of the deleteEnabled field.
	 * 
	 * @param deleteEnabled
	 *            a {@link boolean}.
	 */
	public void setDeleteEnabled(boolean deleteEnabled) {
		this.deleteEnabled = deleteEnabled;
	}

	/**
	 * Returns the value of the deleteQueued field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isDeleteQueued() {
		return deleteQueued;
	}

	/**
	 * Sets the value of the deleteQueued field.
	 * 
	 * @param deleteQueued
	 *            a {@link boolean}.
	 */
	public void setDeleteQueued(boolean deleteQueued) {
		this.deleteQueued = deleteQueued;
	}

	/**
	 * Returns the value of the deleteCompleted field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isDeleteCompleted() {
		return deleteCompleted;
	}

	/**
	 * Sets the value of the deleteCompleted field.
	 * 
	 * @param deleteCompleted
	 *            a {@link boolean}.
	 */
	public void setDeleteCompleted(boolean deleteCompleted) {
		this.deleteCompleted = deleteCompleted;
	}
	
	/**
	 * Returns the value of the followEnabled field.
	 * @return a {@link boolean}.
	 */
	public boolean isFollowEnabled() {
		return followEnabled;
	}

	/**
	 * Sets the value of the followEnabled field.
	 * @param followEnabled a {@link boolean}.
	 */
	public void setFollowEnabled(boolean followEnabled) {
		this.followEnabled = followEnabled;
	}

	/**
	 * Returns the value of the followQueued field.
	 * @return a {@link boolean}.
	 */
	public boolean isFollowQueued() {
		return followQueued;
	}

	/**
	 * Sets the value of the followQueued field.
	 * @param followQueued a {@link boolean}.
	 */
	public void setFollowQueued(boolean followQueued) {
		this.followQueued = followQueued;
	}

	/**
	 * Returns the value of the followCompleted field.
	 * @return a {@link boolean}.
	 */
	public boolean isFollowCompleted() {
		return followCompleted;
	}

	/**
	 * Sets the value of the followCompleted field.
	 * @param followCompleted a {@link boolean}.
	 */
	public void setFollowCompleted(boolean followCompleted) {
		this.followCompleted = followCompleted;
	}

	/**
	 * Returns the value of the replyEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyEnabled() {
		return replyEnabled;
	}

	/**
	 * Sets the value of the replyEnabled field.
	 * 
	 * @param replyEnabled
	 *            a {@link boolean}.
	 */
	public void setReplyEnabled(boolean replyEnabled) {
		this.replyEnabled = replyEnabled;
	}

	/**
	 * Returns the value of the replyQueued field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyQueued() {
		return replyQueued;
	}

	/**
	 * Sets the value of the replyQueued field.
	 * 
	 * @param replyQueued
	 *            a {@link boolean}.
	 */
	public void setReplyQueued(boolean replyQueued) {
		this.replyQueued = replyQueued;
	}

	/**
	 * Returns the value of the replyCompleted field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyCompleted() {
		return replyCompleted;
	}

	/**
	 * Sets the value of the replyCompleted field.
	 * 
	 * @param replyCompleted
	 *            a {@link boolean}.
	 */
	public void setReplyCompleted(boolean replyCompleted) {
		this.replyCompleted = replyCompleted;
	}

	/**
	 * Returns the value of the replyAllEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyAllEnabled() {
		return replyAllEnabled;
	}

	/**
	 * Sets the value of the replyAllEnabled field.
	 * 
	 * @param replyAllEnabled
	 *            a {@link boolean}.
	 */
	public void setReplyAllEnabled(boolean replyAllEnabled) {
		this.replyAllEnabled = replyAllEnabled;
	}

	/**
	 * Returns the value of the replyAllQueued field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyAllQueued() {
		return replyAllQueued;
	}

	/**
	 * Sets the value of the replyAllQueued field.
	 * 
	 * @param replyAllQueued
	 *            a {@link boolean}.
	 */
	public void setReplyAllQueued(boolean replyAllQueued) {
		this.replyAllQueued = replyAllQueued;
	}

	/**
	 * Returns the value of the replyAllCompleted field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isReplyAllCompleted() {
		return replyAllCompleted;
	}

	/**
	 * Sets the value of the replyAllCompleted field.
	 * 
	 * @param replyAllCompleted
	 *            a {@link boolean}.
	 */
	public void setReplyAllCompleted(boolean replyAllCompleted) {
		this.replyAllCompleted = replyAllCompleted;
	}

	/**
	 * Returns the value of the retwitEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isRetwitEnabled() {
		return retwitEnabled;
	}

	/**
	 * Sets the value of the retwitEnabled field.
	 * 
	 * @param retwitEnabled
	 *            a {@link boolean}.
	 */
	public void setRetwitEnabled(boolean retwitEnabled) {
		this.retwitEnabled = retwitEnabled;
	}

	/**
	 * Returns the value of the retwitQueued field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isRetwitQueued() {
		return retwitQueued;
	}

	/**
	 * Sets the value of the retwitQueued field.
	 * 
	 * @param retwitQueued
	 *            a {@link boolean}.
	 */
	public void setRetwitQueued(boolean retwitQueued) {
		this.retwitQueued = retwitQueued;
	}

	/**
	 * Returns the value of the retwitCompleted field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isRetwitCompleted() {
		return retwitCompleted;
	}

	/**
	 * Sets the value of the retwitCompleted field.
	 * 
	 * @param retwitCompleted
	 *            a {@link boolean}.
	 */
	public void setRetwitCompleted(boolean retwitCompleted) {
		this.retwitCompleted = retwitCompleted;
	}

	/**
	 * Returns the value of the favoriteEnabled field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isFavoriteEnabled() {
		return favoriteEnabled;
	}

	/**
	 * Sets the value of the favoriteEnabled field.
	 * 
	 * @param favoriteEnabled
	 *            a {@link boolean}.
	 */
	public void setFavoriteEnabled(boolean favoriteEnabled) {
		this.favoriteEnabled = favoriteEnabled;
	}

	/**
	 * Returns the value of the favoriteQueued field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isFavoriteQueued() {
		return favoriteQueued;
	}

	/**
	 * Sets the value of the favoriteQueued field.
	 * 
	 * @param favoriteQueued
	 *            a {@link boolean}.
	 */
	public void setFavoriteQueued(boolean favoriteQueued) {
		this.favoriteQueued = favoriteQueued;
	}

	/**
	 * Returns the value of the favoriteCompleted field.
	 * 
	 * @return a {@link boolean}.
	 */
	public boolean isFavoriteCompleted() {
		return favoriteCompleted;
	}

	/**
	 * Sets the value of the favoriteCompleted field.
	 * 
	 * @param favoriteCompleted
	 *            a {@link boolean}.
	 */
	public void setFavoriteCompleted(boolean favoriteCompleted) {
		this.favoriteCompleted = favoriteCompleted;
	}

	/**
	 * Returns the value of the listCompleted field.
	 * @return a {@link boolean}.
	 */
	public boolean isListCompleted() {
		return listCompleted;
	}

	/**
	 * Returns the value of the listEnabled field.
	 * @return a {@link boolean}.
	 */
	public boolean isListEnabled() {
		return listEnabled;
	}

	/**
	 * Sets the value of the listEnabled field.
	 * @param listEnabled a {@link boolean}.
	 */
	public void setListEnabled(boolean listEnabled) {
		this.listEnabled = listEnabled;
	}

	/**
	 * Returns the value of the listQueued field.
	 * @return a {@link boolean}.
	 */
	public boolean isListQueued() {
		return listQueued;
	}

	/**
	 * Sets the value of the listQueued field.
	 * @param listQueued a {@link boolean}.
	 */
	public void setListQueued(boolean listQueued) {
		this.listQueued = listQueued;
	}

	/**
	 * Sets the value of the listCompleted field.
	 * @param listCompleted a {@link boolean}.
	 */
	public void setListCompleted(boolean listCompleted) {
		this.listCompleted = listCompleted;
	}

	/**
	 * Returns the value of the profilePictureUrl field.
	 * 
	 * @return a {@link String}.
	 */
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	/**
	 * Sets the value of the profilePictureUrl field.
	 * 
	 * @param profilePictureUrl
	 *            a {@link String}.
	 */
	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	/**
	 * Returns the value of the username field.
	 * 
	 * @return a {@link String}.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the value of the username field.
	 * 
	 * @param username
	 *            a {@link String}.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}
