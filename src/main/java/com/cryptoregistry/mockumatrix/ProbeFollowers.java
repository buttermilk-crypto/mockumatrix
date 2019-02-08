package com.cryptoregistry.mockumatrix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.cryptoregistry.db.DatasourceUtil;

import twitter4j.HttpResponseCode;
import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class ProbeFollowers {
				
	String target; // screen name of target who we will get followers from
	String screenName; // current user, comes from configuration
	
	int max = 1000000; // 1m
	
	private static final String updateString = "update follower set follower_username = ?, "+
			"follower_screenname = ?, "+
			"tweet_count = ?, "+
			"profile_string = ?, "+
			"favorites_count = ?, "+
			"followers_count = ?, "+
			"friends_count = ?, "+
			"created_on = ?, "+
			"is_protected = ?, "+
			"is_verified = ?, "+
			"last_status_date = ? "+
			"where follower_userId = ? ";
	
	
	public ProbeFollowers(String target) {
		super();
		this.target = target;
	}

	public void run() {
		
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		   // cb.setJSONStoreEnabled(true);
		    
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
			
			// set our screen name
			
			try {
				screenName = twitter.getAccountSettings().getScreenName();
				System.err.println("Using screen name: "+screenName+", target is "+target);
			} catch (TwitterException e2) {
				e2.printStackTrace();
			}
			
			// OK, now iterate over the target's followers. 
			
			int maxCount = 0;
			
			
			 long cursor = -1;
			 IDs ids = null;
				do{
					
					try {
							ids = twitter.getFollowersIDs(target, cursor);
							if(ids.getIDs().length == 0) continue;
				 
					} catch (TwitterException e) {
							if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED ||
								e.getStatusCode() == HttpResponseCode.NOT_FOUND) {
						 
								System.err.println("getFollowersIDs, got a "+e.getStatusCode()+", continuing...");
					       
							}else {
								e.printStackTrace();
								continue;
							}
					}
				 
					long [] array = ids.getIDs();
					ArrayList<long[]> list = loadup(100,array);
			bunchloop:	for(long[] bunch: list){
						ResponseList<User> res = null;
						
						try {
							res = twitter.users().lookupUsers(bunch);
						} catch (TwitterException e) {
							if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED ||
								e.getStatusCode() == HttpResponseCode.NOT_FOUND) {
						 
								System.err.println("lookupUsers, got a "+e.getStatusCode()+", continuing...");
								System.err.println(Arrays.toString(bunch));
								continue bunchloop;
					       
							}else {
								e.printStackTrace();
							}
						}
						
						for(User u: res){
							// this is a fully hydrated follower of target
							maxCount++;
						
							String screenName = u.getScreenName();
							String accountName = u.getName();
							long userId = u.getId();
							int tweetCount = u.getStatusesCount();
							String profileString = u.getDescription();
							if(profileString == null) profileString = "";
							int favoritesCount = u.getFavouritesCount();
							int followingCount = u.getFollowersCount();
							int friendsCount = u.getFriendsCount();
							Date createdOn = u.getCreatedAt();
							boolean isProtected = u.isProtected();
							boolean isVerified = u.isVerified();
							Status lastStatus = u.getStatus();
							Date lastStatusDate = null;
							if(lastStatus != null) lastStatusDate = lastStatus.getCreatedAt();
							
							System.err.println(maxCount+" "+accountName+", "+screenName+", friends="+friendsCount+", following="+followingCount);
							
							
							try (
								Connection con = DatasourceUtil.ds.getConnection();
							){
								
								// first test to see if this user is present.
								PreparedStatement ps = con.prepareStatement(
									"select count(*) as count from follower "+
									"where follower_userId = ?");
								ps.setString(1, String.valueOf(userId));
								ResultSet rs = ps.executeQuery();
								int count = 0;
								if (rs.next()) {
									count = rs.getInt("count");
								}
								ps.close();
								rs.close();
								
								// ok, is this user already in follower database? if so, update.
								// if not found, then do insert.
								
								if(count == 0){
									// insert
									ps = con.prepareStatement(
											"insert into follower values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
									);
									
										ps.setString(1, String.valueOf(userId));
										ps.setString(2, accountName);
										ps.setString(3, screenName);
										ps.setInt(4, tweetCount);
										ps.setString(5, profileString);
										ps.setInt(6, favoritesCount);
										ps.setInt(7, followingCount);
										ps.setInt(8, friendsCount);
										ps.setDate(9, new java.sql.Date(createdOn.getTime()));
										ps.setBoolean(10, isProtected);
										ps.setBoolean(11, isVerified);
										if(lastStatusDate != null)
											ps.setDate(12, new java.sql.Date(lastStatusDate.getTime()));
										else
											 ps.setDate(12, null);
										ps.setInt(13,  (maxCount/1000));
										ps.setInt(14,  maxCount);
										
										ps.executeUpdate();
										ps.close();
									
								}else{
									//else update info
									PreparedStatement ps1 = con.prepareStatement(updateString);
									
									ps1.setString(1, accountName);
									ps1.setString(2, screenName);
									ps1.setInt(3, tweetCount);
									ps1.setString(4, profileString);
									ps1.setInt(5, favoritesCount);
									ps1.setInt(6, followingCount);
									ps1.setInt(7, friendsCount);
									ps1.setDate(8, new java.sql.Date(createdOn.getTime()));
									ps1.setBoolean(9, isProtected);
									ps1.setBoolean(10, isVerified);
									if(lastStatusDate != null)
										ps1.setDate(11, new java.sql.Date(lastStatusDate.getTime()));
									else
										 ps1.setDate(11, null);
									ps1.setString(12, String.valueOf(userId));
									
									ps1.executeUpdate();
									ps1.close();
								}
								
								
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
						}
					}
		
					System.out.println("Processed "+maxCount+" users");
					if(maxCount>max) break;
					
				} while ((cursor = ids.getNextCursor()) != 0);
				
		
			
			System.out.println("Done!");
	}
	
	public static void main(String[] args) {
		if(args.length == 1){
			new ProbeFollowers(args[0]).run();
		}else{
			System.err.println("Takes one argument, the target's screen name");
		}
	}
	
	/**
	 * Load count number of array into a new array of Long, then add to ArrayList. Last item will be remainder
	 * 
	 * @param count
	 * @param array
	 * @return ArrayList<Long[]> 
	 */
	public ArrayList<long[]> loadup(int count, long [] array){
		
		System.err.println("entering loadup: "+count+", "+array.length);
		
		ArrayList<long[]> list = new ArrayList<long[]>();
		
		int groupCount = array.length / count;
		
		System.err.println("groupCount = "+groupCount);
		
		for(int i =0;i<groupCount;i++){
			long [] bunch = new long[count];
			System.arraycopy(array, (i*count), bunch, 0, count);
			list.add(bunch);
		}
		
		// now handle remainder if required.
		if(array.length!=count){
			int rem = array.length - (groupCount*count);
			long [] bunch = new long[rem];
			System.arraycopy(array, array.length-rem, bunch, 0, rem);
			list.add(bunch);
		}
		
		System.err.println("list size = "+list.size());
		
		return list;
	}

}
