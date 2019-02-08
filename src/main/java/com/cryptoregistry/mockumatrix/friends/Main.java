package com.cryptoregistry.mockumatrix.friends;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Main {
	
	private static Twitter twitter = new TwitterFactory().getInstance();

	public static void main(String[] args) {
	
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			@Override
			public void onRateLimitStatus(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				System.err.println("Limit:"+stat.getRemaining()+"/"+ stat.getLimit());
				
			}

			@Override
			public void onRateLimitReached(RateLimitStatusEvent event) {
				System.err.println("Bailing on Rate Limit! "+event.getRateLimitStatus());
				System.exit(1);
			}
			
		});
		
		try {
			IDs ids = twitter.getFollowersIDs(-1);
			if(ids.hasNext()) {
				long nextCursor = ids.getNextCursor();
				long [] array = ids.getIDs();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

	}

}
