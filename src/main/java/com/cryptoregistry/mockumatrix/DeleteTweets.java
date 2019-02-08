package com.cryptoregistry.mockumatrix;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class DeleteTweets {

	public static void main(String[] args) {
		
		String qstring = "from:usconstitution0";

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setJSONStoreEnabled(true);

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		// RATE LIMITS HANDLING - if it looks like we are hitting a limit, pause
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			@Override
			public void onRateLimitStatus(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				System.err.println("Limit:" + stat.getRemaining() + "/"
						+ stat.getLimit());
			}

			@Override
			public void onRateLimitReached(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				int secReset = stat.getSecondsUntilReset();
				System.err.println("Hit limit, sleeping for " + secReset
						+ " seconds...");
				try {
					Thread.sleep((secReset * 1000) + 5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		
		try {

			Query query = new Query(qstring);
			QueryResult result;

			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				System.err.println("Found "+tweets.size());
				for (Status status : tweets) {
					twitter.destroyStatus(status.getId());
				}
			} while ((query = result.nextQuery()) != null);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}

	}
	
}
