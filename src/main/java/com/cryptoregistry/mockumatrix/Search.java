package com.cryptoregistry.mockumatrix;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Search {

	public static void main(String[] args) {

		String qstring = "from:realDonaldTrump";

		 ConfigurationBuilder cb = new ConfigurationBuilder();
		    cb.setJSONStoreEnabled(true);
		    cb.setTweetModeExtended(true);
		    
			Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		
		// RATE LIMITS HANDLING - if it looks like we are hitting a limit, pause
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			@Override
			public void onRateLimitStatus(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				System.err.println("Limit:"+stat.getRemaining()+"/"+ stat.getLimit());
			}

		
			@Override
			public void onRateLimitReached(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				int secReset = stat.getSecondsUntilReset();
				System.err.println("Hit limit, sleeping for "+secReset+" seconds...");
				try {
					Thread.sleep((secReset*1000)+5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		
		try {

			Query query = new Query(qstring);
			QueryResult result;

			String text = "";
			
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				System.err.println("Found "+tweets.size());
				for (Status status : tweets) {
					
					String raw = TwitterObjectFactory.getRawJSON(status);
					
					try {
						ObjectMapper mapper = new ObjectMapper();
						@SuppressWarnings("unchecked")
						Map<String,Object> map = mapper.readValue(new StringReader(raw), Map.class);
						text = (String) map.get("full_text");
						System.err.println(text);
				//	String aliasedText = EmojiParser.parseToAliases(statusText);
					
					}catch(Exception x){
						x.printStackTrace();
					}
					
					
					
					MemeGen gen = new MemeGen(status.getUser().getScreenName());
					gen.gen(text, status, MemeGen.TEXT_STYLE.TRADITIONAL);
				}
			} while ((query = result.nextQuery()) != null);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}

	}

}
