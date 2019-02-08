package com.cryptoregistry.mockumatrix;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Blockomagic {
				
	String target; // screen name of target who we will get followers from to block
	String screenName; // current user, comes from configuration
	
	public Blockomagic(String target) {
		super();
		this.target = target;
	}

	public void run() {
		
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		    cb.setJSONStoreEnabled(true);
		    
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
			
			// set the screen name
			
			try {
				screenName = twitter.getAccountSettings().getScreenName();
				System.err.println("Using screen name: "+screenName);
			} catch (TwitterException e2) {
				e2.printStackTrace();
			}
			
			// OK, now iterate over target's followers. 
			
			try {
			 long cursor = -1;
			 IDs ids = null;
				do{
				 ids = twitter.getFollowersIDs(target, cursor);
					long [] array = ids.getIDs();
					for(long userId: array){
						try {
							User blockedUser = twitter.createBlock(userId);
							System.out.println("Blocked "+blockedUser.getScreenName()+", "+blockedUser.getName());
						}catch(Exception x){
							x.printStackTrace();
						}
					}
				} while ((cursor = ids.getNextCursor()) != 0);
				
			} catch (TwitterException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Done!");
	}
	
	
	public static void main(String[] args) {
		if(args.length == 1){
			new Blockomagic(args[0]).run();
		}else{
			System.err.println("Takes one argument, the target's screen name");
		}
	}

}
