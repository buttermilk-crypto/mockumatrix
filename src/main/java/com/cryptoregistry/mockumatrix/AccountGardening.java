package com.cryptoregistry.mockumatrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.Friendship;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.conf.ConfigurationBuilder;

public class AccountGardening {
	
	private static final String sacredTabListName = "Sacred Tab"; // list name for unmanaged friends
	
	// this is our list of people who do not need to be following us to hang around, we do not curate them
	List<User> sacred = new ArrayList<User>();
				
				
	String screenName; // current user, comes from configuration

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

				@SuppressWarnings("static-access")
				@Override
				public void onRateLimitReached(RateLimitStatusEvent event) {
					RateLimitStatus stat = event.getRateLimitStatus();
					int secReset = stat.getSecondsUntilReset();
					System.err.println("Hit limit, sleeping for "+secReset+" seconds...");
					try {
						Thread.currentThread().sleep((secReset*1000)+5000);
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
			
			
			// get the sacred tab list and put those users into sacred
			UserList sacredTab = null;
			
			try {
				ResponseList<UserList> lists = twitter.getUserLists(screenName);
				Iterator<UserList> iter = lists.iterator();
				while(iter.hasNext()){
					UserList l = iter.next();
					if(l.getName().equals(sacredTabListName)) {
						sacredTab = l;
						break;
					}
				}
				
			} catch (TwitterException e1) {
				e1.printStackTrace();
				return;
			}
			
			if(sacredTab == null) {
				System.err.println("No Sacred Tab list found. Bailing.");
				return;
			}
			
			// this is expected to be quite a short list at this point.
			try {
			
				long cursor = -1;
				  PagableResponseList<User> sacredUsers;
		            do {
		            	sacredUsers = twitter.getUserListMembers(sacredTab.getId(), cursor);
		                for (User u : sacredUsers) {
		                    sacred.add(u);
		                }
		            } while ((cursor = sacredUsers.getNextCursor()) != 0);
				
			} catch (TwitterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// OK, now iterate over people I am following, see if they are following back. 
			// check sacred tabs and ignore status if the user is considered sacred
			
			  try {
				 long cursor = -1;
				 IDs ids = null;
					do{
					 ids = twitter.getFriendsIDs(screenName, cursor);
						long [] array = ids.getIDs();
						// array will be up to 5000 user ids. 
						// need to break up into units of 100. 
						int groupCount = array.length / 100;
						for(int i =0;i<groupCount;i++){
							long [] bunch = new long[100];
							System.arraycopy(array, (i*100), bunch, 0, 100);
							ResponseList<Friendship> friends = twitter.lookupFriendships(bunch);
							fr: for(Friendship friend: friends){
								// I am following them, but friend is not following me back
								if(!friend.isFollowedBy()) {
									// if sacred, give them a pass
									if(isSacred(friend.getId())){
										continue fr;
									}else{
											
									}
								}
							}
						}
						// now handle remainder if required.
						if(array.length%100!=0){
							int rem = array.length - (groupCount*100);
							long [] bunch = new long[rem];
							System.arraycopy(array, array.length-rem, bunch, 0, rem);
							ResponseList<Friendship> friends = twitter.lookupFriendships(bunch);
						}
						
						//twitter.lookupFriendships(ids);
						
					} while ((cursor = ids.getNextCursor()) != 0);
					
				} catch (TwitterException e) {
					e.printStackTrace();
					//System.exit(1);
				}
			
			// OK, now iterate over followers. 
			
		//	try {
		//	 long cursor = -1;
		//	 IDs ids = null;
		//		do{
		//		 ids = twitter.getFollowersIDs(screenName, cursor);
		//			long [] array = ids.getIDs();
		//			for(long userId: array){
		//				User u = twitter.users().showUser(userId);
		//				checkUserProfile(u);
		//			}
		//		} while ((cursor = ids.getNextCursor()) != 0);
		//		
		//	} catch (TwitterException e) {
		//		e.printStackTrace();
		//		System.exit(1);
		//	}
	}
	
	private boolean isSacred(long userId){
		for(User u : sacred) {
			if(u.getId() == userId) return true;
		}
		return false;
	}
	
	/**
	 * Do whatever is required for this follower based on their profile data - block if need be
	 * @param u
	 */
	private void checkUserProfile(User u){
		String desc = u.getDescription();
		
	}
	
	public static void main(String[] args) {
		new AccountGardening().run();
	}

}
