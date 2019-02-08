package com.cryptoregistry.mockumatrix;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.ConfigurationBuilder;

import com.cryptoregistry.mockumatrix.CmdLineParser.Option;
import com.cryptoregistry.mockumatrix.CmdLineParser.OptionException;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
//import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

public class Storm {

	static Format format = Format.DEFAULT;
	static JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
	
	public static void main(String[] args) {
		
		CmdLineParser parser = new CmdLineParser();
		Option<String> fileOpt = parser.addStringOption('f', "file");
		Option<Boolean> sendOpt = parser.addBooleanOption('s', "send");
		Option<String> formatOpt = parser.addStringOption("format");
	
		try {
			parser.parse(args);
		} catch (OptionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// FIX ME
		format = Format.valueOf(parser.getOptionValue(formatOpt, "DEFAULT"));
		
		String filePath = parser.getOptionValue(fileOpt, null);
		if(filePath == null) {
			System.err.println("Need fileOpt!");
			System.exit(-1);
		}
		
		// if send not set, we won't post anything 
		boolean send = parser.getOptionValue(sendOpt, false);
		
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
		
		runSpellCheck(textFile);
		
		// runs validation
		ArrayList<StormEntry> list = TextUtil.prepareStormText(textFile);
		
		if(list.size() == 0) {
			throw new RuntimeException("Text contained no strings, bailing out");
		}
		
		if(!send) {
			System.out.println("Not sending anything...");
			System.exit(0);
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
		
		StormEntry top = list.get(0);
		
		// Initially create one status, get the ID
		try {
		//	Status status = twitter.updateStatus(top.tweetText);
			StatusUpdate update = new StatusUpdate(top.tweetText);
			
			// actually only does the last image - TODO use media Ids?
			//if(top.attachmentPaths.size() >0) {
			//	for(String path: top.attachmentPaths) update.setMedia(new File(path));
			//}
			
			   long [] mediaIds = null;
				if (top.attachmentPaths.size() > 0) {
					 mediaIds = new long[top.attachmentPaths.size()];
			         for(int j=0; j<top.attachmentPaths.size(); j++) {
			            UploadedMedia media = twitter.uploadMedia(new File(top.attachmentPaths.get(j)));
			            mediaIds[j] = media.getMediaId();
			         }
			         
			         update.setMediaIds(mediaIds);
				}
			
			Status status = twitter.updateStatus(update);
			
			long id = status.getId();
			System.out.println("established top level "+id+" "+top);
			
			// now send our tweet storm as replies one per minute
			for(int i = 1; i<list.size(); i++){
				StormEntry s = list.get(i);
				update = new StatusUpdate(s.tweetText);
				
				// actually only does the last image - TODO use media Ids?
				//if(s.attachmentPaths.size() >0) {
				//	for(String path: s.attachmentPaths) update.setMedia(new File(path));
				//}
				
				   mediaIds = null;
					if (s.attachmentPaths.size() > 0) {
						 mediaIds = new long[s.attachmentPaths.size()];
				         for(int j=0; j<s.attachmentPaths.size(); j++) {
				        	
				            UploadedMedia media = twitter.uploadMedia(new File(s.attachmentPaths.get(j)));
				            mediaIds[j] = media.getMediaId();
				         }
				         
				         update.setMediaIds(mediaIds);
					}
				
				update.setInReplyToStatusId(id);
				status = twitter.updateStatus(update);
				System.out.println("sent update: "+status.getId()+" "+s);
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done!");
	}

	enum Format {
		DEFAULT, PLAIN, NUMBERED;
	}
	
	private static void runSpellCheck(File textFile){
		try {
			String text = new String(Files.readAllBytes(textFile.toPath()), StandardCharsets.UTF_8);
			checkSpelling(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static void checkSpelling(String text) throws IOException {
		
		List<RuleMatch> matches = langTool.check(text);
		if (matches == null)
			return;

		for (RuleMatch match : matches) {
			if(match.getMessage().contains("smart")) continue;
			if(match.getMessage().contains("you repeated a whitespace")) continue;
			System.err.println(match.getMessage()+": "+text.substring(match.getFromPos(), match.getToPos()) );
			List<String> replace = match.getSuggestedReplacements();
			System.err.println(replace);
		}
	}

}
