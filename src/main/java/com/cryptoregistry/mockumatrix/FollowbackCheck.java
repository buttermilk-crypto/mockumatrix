package com.cryptoregistry.mockumatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.cryptoregistry.mockumatrix.CmdLineParser.Option;
import com.cryptoregistry.mockumatrix.CmdLineParser.OptionException;

import twitter4j.Friendship;
import twitter4j.HttpResponseCode;
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

/**
 * <p>For my account, look at all my followers and see if I am following back, 
 * and then look at my friends who are not following me.</p>
 * 
 * <p>-u  does an unfollow on people not following me</p>
 * <p>-l writes out unfollowers only to not-following-back.txt</p>
 * 
 * 
 * @author Dave
 *
 */

public class FollowbackCheck {

	String target; // screen name of target who we will get followers from
	String screenName; // current user, comes from configuration

	CopyOnWriteArrayList<Long> notFollowingBackList = new CopyOnWriteArrayList<Long>();
	ArrayList<Long> iShouldFollowList = new ArrayList<Long>();
	ArrayList<Long> mutualList = new ArrayList<Long>();
	
	BufferedWriter bufOut;

	int max = 500; // 1m

	public FollowbackCheck() {
		super();
	}

	public void run(String[] args) {
		

		CmdLineParser parser = new CmdLineParser();
		Option<String> listOpt = parser.addStringOption('l', "list"); // the
																		// list
																		// we
																		// use
																		// as a
																		// Sacred
																		// Tab.
		Option<Boolean> unfollowOpt = parser.addBooleanOption('u', "unfollow"); // if
																				// set,
																				// unfollow
																				// those
																				// not
																				// following
																				// back
		Option<Boolean> followOpt = parser.addBooleanOption('f', "followback");
	
		

		try {
			parser.parse(args);
		} catch (OptionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String sacredTab = parser.getOptionValue(listOpt, null);
		if (sacredTab == null)
			sacredTab = "Sacred Tab"; // default to "Sacred Tab"

		Boolean unfollow = parser.getOptionValue(unfollowOpt, false);
		Boolean follow = parser.getOptionValue(followOpt, false);
		
		System.out.println("request unfollow: "+unfollow);
		System.out.println("request follow: "+follow);

		ConfigurationBuilder cb = new ConfigurationBuilder();
		// cb.setJSONStoreEnabled(true);

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		// RATE LIMITS HANDLING - if it looks like we are hitting a limit, pause
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			@Override
			public void onRateLimitStatus(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				System.err.println("Limit:" + stat.getRemaining() + "/"
						+ stat.getLimit());

				try {
					if (stat.getRemaining() == 1) {
						Thread.sleep(15*60*1000); // fifteen minutes
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}

			@Override
			public void onRateLimitReached(RateLimitStatusEvent event) {
				RateLimitStatus stat = event.getRateLimitStatus();
				System.err.println("Limit reached:" + stat.getRemaining() + "/" + stat.getLimit());
				System.err.println("reset time in seconds :" + stat.getResetTimeInSeconds());
				System.err.println("get seconds until reset :" + stat.getSecondsUntilReset());
				System.err.println("Exiting!");
				
						System.exit(1);
				
			}
		});

		// set our screen name

		try {
			screenName = twitter.getAccountSettings().getScreenName();
			target = screenName;
			System.err.println("Using screen name: " + screenName
					+ ", target is " + target);
		} catch (TwitterException e2) {
			e2.printStackTrace();
		}

		// find our sacred tab's id. If not found, bail out with error message.

		long sacredTabId = -1;

		try {
			ResponseList<UserList> lists = twitter.list().getUserLists(target);
			ListIterator<UserList> iter = lists.listIterator();
			while (iter.hasNext()) {
				UserList ul = iter.next();
				if (sacredTab.equals(ul.getName())) {
					sacredTabId = ul.getId();
					break;
				}
			}

		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (sacredTabId == -1) {
			System.err
					.println("Sorry, need a Sacred Tab id to work with. Please use -l with list name");
			System.exit(-1);
		} else {
			System.out.println("Sacred Tab ID = " + sacredTabId);
		}

		// now collect IDs of Sacred Tab users

		ArrayList<Long> sacredTabUserIds = new ArrayList<Long>();
		PagableResponseList<User> membersList;
		long cursor = -1;
		try {
			do {

				membersList = twitter.list().getUserListMembers(sacredTabId,
						cursor);
				for (User u : membersList) {
					sacredTabUserIds.add(u.getId());
				}

			} while ((cursor = membersList.getNextCursor()) != 0);

		} catch (TwitterException e1) {
			e1.printStackTrace();
			// if fail, bail
			System.err.println("Bailing, sorry!");
			System.exit(-1);
		}

		System.out.println("Sacred Tab user count: " + sacredTabUserIds.size());
		

		// OK, now iterate over the accounts following me, looking here for ones I've missed to follow back

		int maxCount = 0;

		cursor = -1;
		IDs ids = null;
		
		/*
		do {

			try {
			//	ids = twitter.getFriendsIDs(target,cursor); //who we are following
				ids = twitter.getFollowersIDs(target, cursor); // who is following us
				if (ids.getIDs().length == 0) continue;

			} catch (TwitterException e) {
				if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED || e.getStatusCode() == HttpResponseCode.NOT_FOUND) {
					System.err.println("getFollowersIDs, got a " + e.getStatusCode() + ", continuing...");
				} else {
					e.printStackTrace();
					continue;
				}
			}

			long[] array = ids.getIDs();
			ArrayList<long[]> list = loadup(100, array); // collect 100 IDs

			bunchloop: for (long[] bunch : list) {

				ResponseList<Friendship> res = null;

				try {
					res = twitter.friendsFollowers().lookupFriendships(bunch);
				} catch (TwitterException e) {
					if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED
							|| e.getStatusCode() == HttpResponseCode.NOT_FOUND) {

						System.err.println("lookupUsers, got a "
								+ e.getStatusCode() + ", continuing...");
						System.err.println(Arrays.toString(bunch));
						continue bunchloop;

					} else {
						e.printStackTrace();
					}
				}
				
				//sleep one minute after lookupFriendships() call 
				try {
					Thread.sleep(1000*61);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				for (Friendship f : res) {
					// this is a fully hydrated follower of target
					maxCount++;

					String screenName = f.getScreenName();
					String name = f.getName();
					long id = f.getId();
					boolean iAmFollowing = f.isFollowing(); // if false, I am not following
					
					if (iAmFollowing) {
						mutualList.add(id);
					}else {
						System.out.println("They are following me But I am not following back: " + name + ", " + screenName);
						iShouldFollowList.add(id);
					}
				}
			}

			if (maxCount > max)
				break;

		} while ((cursor = ids.getNextCursor()) != 0);
		
		System.out.println("Processed = " + maxCount + " followers");
		System.out.println("Mutual = "+ this.mutualList.size());
		System.out.println("I should follow back = "+ this.iShouldFollowList.size());
		
		*/
		
		maxCount = 0;
		cursor = -1;
		
		// Now, looking at who i am following, people not following me back
		
		do {

			try {
				ids = twitter.getFriendsIDs(target,cursor); //who we are following
			//	ids = twitter.getFollowersIDs(target, cursor); // who is following us
				if (ids.getIDs().length == 0) continue;

			} catch (TwitterException e) {
				if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED || e.getStatusCode() == HttpResponseCode.NOT_FOUND) {
					System.err.println("getFollowersIDs, got a " + e.getStatusCode() + ", continuing...");
				} else {
					e.printStackTrace();
					continue;
				}
			}

			long[] array = ids.getIDs();
			ArrayList<long[]> list = loadup(100, array); // collect 100 IDs

			bunchloop: for (long[] bunch : list) {

				ResponseList<Friendship> res = null;

				try {
					res = twitter.friendsFollowers().lookupFriendships(bunch);
				} catch (TwitterException e) {
					if (e.getStatusCode() == HttpResponseCode.UNAUTHORIZED
							|| e.getStatusCode() == HttpResponseCode.NOT_FOUND) {

						System.err.println("lookupUsers, got a "
								+ e.getStatusCode() + ", continuing...");
						System.err.println(Arrays.toString(bunch));
						continue bunchloop;

					} else {
						e.printStackTrace();
					}
				}
				
				//sleep one minute after lookupFriendships() call 
				try {
					Thread.sleep(1000*61);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				for (Friendship f : res) {
					// this is a fully hydrated follower of target
					maxCount++;

					String screenName = f.getScreenName();
					String name = f.getName();
					long id = f.getId();
					boolean accountIsFollowingMe = f.isFollowedBy(); // if false, they are not following me
																		
					if (!accountIsFollowingMe) {
						
						System.err.println("Not following me back: " + name+ ", " + screenName);
						println(name+", "+screenName);
						
						if (sacredTabUserIds.contains(id)) {
							System.err.println(name + " is sacred, ignoring!");
						} else {
							notFollowingBackList.add(id);
							if(notFollowingBackList.size() >= 10) {
								if(unfollow) unfollow(twitter);
							}
						}
					}
				}
			}


		} while ((cursor = ids.getNextCursor()) != 0);

		System.out.println("Processed = " + maxCount + " people I am following");
		System.out.println("Not following me back = "+ this.notFollowingBackList.size());

		// ok, now unfollow if "unfollow" flag is set

    
		
		if(follow){
			for (Long id : iShouldFollowList) {
				try {
					twitter.friendsFollowers().createFriendship(id);
					System.err.println("Created friendship with "+id);
					//sleep one minute after createFriendship() call 
					try {
						Thread.sleep(1000*61);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done!");
	}

	public static void main(String[] args) {

		new FollowbackCheck().run(args);

	}

	/**
	 * Load count number of array into a new array of Long, then add to
	 * ArrayList. Last item will be remainder
	 * 
	 * @param count
	 * @param array
	 * @return ArrayList<Long[]>
	 */
	public ArrayList<long[]> loadup(int count, long[] array) {

		System.err.println("entering loadup: " + count + ", " + array.length);

		ArrayList<long[]> list = new ArrayList<long[]>();

		int groupCount = array.length / count;

		System.err.println("groupCount = " + groupCount);

		for (int i = 0; i < groupCount; i++) {
			long[] bunch = new long[count];
			System.arraycopy(array, (i * count), bunch, 0, count);
			list.add(bunch);
		}

		// now handle remainder if required.
		if (array.length != count) {
			int rem = array.length - (groupCount * count);
			long[] bunch = new long[rem];
			System.arraycopy(array, array.length - rem, bunch, 0, rem);
			list.add(bunch);
		}

		System.err.println("list size = " + list.size());

		return list;
	}
	
	
	private void println(String out){
		try {
			
			File parent = new File(Paths.get(".").toAbsolutePath().normalize().toString());
			File notFollowingBack = new File(parent, "not-following-back.txt");
			
			Path path = notFollowingBack.toPath();
			out = out+"\n";
			byte[] bytes = out.getBytes(StandardCharsets.UTF_8);
		    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		    
		}catch(IOException x){
			x.printStackTrace();
		}
	}
	
	private synchronized void unfollow(Twitter twitter) {
		
			for (Long id : notFollowingBackList) {
				try {
					twitter.friendsFollowers().destroyFriendship(id);
					System.err.println("Unfollowed "+id);
					
					//sleep 10 sec after destroyFriendship() call 
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				
			notFollowingBackList.clear();
			
		}
	}

}
