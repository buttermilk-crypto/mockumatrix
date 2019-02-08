package com.cryptoregistry.mockumatrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.cryptoregistry.mockumatrix.CmdLineParser;
import com.cryptoregistry.mockumatrix.CmdLineParser.Option;
import com.cryptoregistry.mockumatrix.CmdLineParser.OptionException;

import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Constitution {

	public static void main(String[] args) {

		CmdLineParser parser = new CmdLineParser();
		Option<String> fileOpt = parser.addStringOption('f', "file");
		Option<Boolean> sendOpt = parser.addBooleanOption('s', "send");
		Option<String> restartIdOpt = parser.addStringOption("restartId");
	
		try {
			parser.parse(args);
		} catch (OptionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String filePath = parser.getOptionValue(fileOpt, null);
		if(filePath == null) {
			System.err.println("Need fileOpt!");
			System.exit(-1);
		}
		
		boolean send = parser.getOptionValue(sendOpt, false);
		String restartIdString = parser.getOptionValue(restartIdOpt, null);
		
		File textFile = new File(filePath);
		if (!textFile.exists()) {
			try {
				System.err.println("file does not exist: "
						+ textFile.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

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
		
		TextToImage tti = new TextToImage(textFile);
		ArrayList<TextToImageResult> list = tti.createImages();
		
		if(!send) {
			System.err.println("Not sending...");
			System.exit(0);
		}
		
		// reverse order
		
		Collections.reverse(list);
		
		Status previous = null;
		
		// if this is a restart, use the ID to load that status, assign it to our "previous"
		if(restartIdString != null){
		  try {
			previous = twitter.showStatus(Long.parseLong(restartIdString));
		  } catch (TwitterException e) {
			  e.printStackTrace();
			  System.exit(-1); // since we failed on expected call
		 }
		}
		
		for(TextToImageResult item: list){
			
			StatusUpdate s = null;
	
			System.err.println("Doing "+item.title);
			if(previous == null){
				s = new StatusUpdate(item.title);
			}else{
				s = new StatusUpdate(item.title+"  next: https://twitter.com/usconstitution0/status/"+previous.getId());
			}
			
			s.setMedia(item.file);
			
			try {
				previous = twitter.updateStatus(s);
				Map<String,RateLimitStatus> map = twitter.getRateLimitStatus("statuses");
				if(map != null){
					System.err.println("statuses = "+map.toString());
				}
			} catch (TwitterException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			try { // sleep interval 30 sec per tweet
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
		}

	}

}
