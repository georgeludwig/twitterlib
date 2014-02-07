package com.sixbuilder.helpers;

import java.util.ArrayList;
import java.util.List;

public class TweetItem implements Comparable<TweetItem> {
	
	public static final String RECORDDDELIM=System.getProperty("line.separator");
	public static final String FIELDDELIM=Character.toString((char) 31);
	public static final String RECORD_HEADER="ID"+FIELDDELIM+"PUB"+FIELDDELIM+"SRCH_NAME"+FIELDDELIM+"SCORE"+FIELDDELIM+
		"URL"+FIELDDELIM+"SHORT_URL"+FIELDDELIM+"DATE_TWEETED"+FIELDDELIM+"SUMMARY";
	
	public static final String UNINIT_SHORTENED_URL="shortenedUrl";
	
	public TweetItem() {}
	
	/**
	 * 
	 * The format of the record string is:
	 * searchName<tab>score<tab>url<tab>shortenedURL<tab>dateTweetedAsLong<tab>summary
	 * 
	 * note:there should be NO tabs in the data other than field separators!
	 * 
	 * @param tweetItemRecord
	 * @throws Exception 
	 */
	public TweetItem(String tweetItemRecord) throws Exception {
		String[] fields=tweetItemRecord.split(FIELDDELIM);
		setTweetId(fields[0]);
		setPublish(Boolean.parseBoolean(fields[1].trim()));
		setSearchName(fields[2].trim());
		setScore(Double.parseDouble(fields[3].trim()));
		setUrl(fields[4].trim());
		setShortenedUrl(fields[5].trim());
		setSnapshotUrl(fields[6].trim());
		setAttachSnapshot(Boolean.parseBoolean(fields[7].trim()));
		setRecommendedHashtags(fields[8].trim());
		setDateTweeted(SerializableRecordHelper.getDateTimeLong(fields[9].trim()));
		setSummary(fields[10].trim());
	}
	
	private String tweetId;
	
	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	private boolean publish=false;
	
	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	private String searchName="searchName";
	
	/**
	 * returns the name of the search that generated this TweetItem
	 * @return
	 */
	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	private double score;
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	private String url="url";
	
	public String getUrl() {
		if(url==null)
			url="";
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String shortenedUrl=UNINIT_SHORTENED_URL;
	
	public String getShortenedUrl() {
		if(shortenedUrl==null)
			return "";
		return shortenedUrl;
	}

	public void setShortenedUrl(String shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}
	
	private String snapshotUrl;

	public String getSnapshotUrl() {
		return snapshotUrl;
	}

	public void setSnapshotUrl(String snapshotUrl) {
		this.snapshotUrl = snapshotUrl;
	}
	
	private boolean attachSnapshot;

	public boolean isAttachSnapshot() {
		return attachSnapshot;
	}

	public void setAttachSnapshot(boolean attachSnapshot) {
		this.attachSnapshot = attachSnapshot;
	}

	private String recommendedHashtags;
	
	public String getRecommendedHashtags() {
		return recommendedHashtags;
	}

	public void setRecommendedHashtags(String recommendedHashtags) {
		this.recommendedHashtags = recommendedHashtags;
	}

	private Long dateTweeted=Long.decode("0");
	
	public Long getDateTweeted() {
		return dateTweeted;
	}

	public void setDateTweeted(Long dateTweeted) {
		this.dateTweeted = dateTweeted;
	}

	private String summary="summary";
	
	public String getSummary() {
		return summary;
	}

	public static String cleanStatus(String status ) {
		if(status==null||status.trim().length()==0)
			return "";
		String ret=status.trim().replace("\n"," ");
		ret=ret.replace("\t","");
		ret=ret.replace(FIELDDELIM, " ");
		ret=ret.replace(RECORDDDELIM, " ");
		return ret;
	}
	
	public void setSummary(String summary) {
		String s=cleanStatus(summary);
		this.summary = s;
	}

	public String toString() {
		return 
			getTweetId()+FIELDDELIM+
			isPublish()+FIELDDELIM+
			getSearchName()+FIELDDELIM+
			String.valueOf(getScore())+FIELDDELIM+
			getUrl().trim()+FIELDDELIM+
			getShortenedUrl()+FIELDDELIM+
			getSnapshotUrl()+FIELDDELIM+
			isAttachSnapshot()+FIELDDELIM+
			getRecommendedHashtags()+FIELDDELIM+
			SerializableRecordHelper.getDateTimeString(getDateTweeted())+FIELDDELIM+
			getSummary();
	}
		
	public static List<String>getHashtags(String tweet) {
		List<String>hashTags=new ArrayList<String>();
		// split summary on whitespace
		String[]words=tweet.split("\\s");
		for(int i=0;i<words.length;i++) {
			String word=words[i];
			if(word.startsWith("#")) {
				// remove any trailing punctuation 
				word="#"+trimPunctuation(word);
				word=word.toLowerCase();
				if(word.trim().length()>1)
					hashTags.add(word);
			}
		}
		return hashTags;
	}

	public int compareTo(TweetItem arg0) {
		if(this.getScore()<arg0.getScore())
			return 1;
		if(this.getScore()>arg0.getScore())
			return -1;
		return 0;
	}
	
	private static String trimPunctuation(String string) {
		String ret = string.replaceAll("\\W+", "");
		return ret;
	}

}
