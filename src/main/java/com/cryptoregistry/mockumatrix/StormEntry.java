package com.cryptoregistry.mockumatrix;

import java.util.ArrayList;
import java.util.List;

public class StormEntry {

	long tweetId = -1;
	String tweetText;
	List<String> attachmentPaths;

	public StormEntry() {
		attachmentPaths = new ArrayList<String>();
	}

	public StormEntry(String text) {
		this();
		tweetText = text;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}

	public List<String> getAttachmentPaths() {
		return attachmentPaths;
	}

	public String getTweetText() {
		return tweetText;
	}
	
}
