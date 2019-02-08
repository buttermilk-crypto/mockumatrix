package com.cryptoregistry.mockumatrix;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

/**
 * @author David R. Smith
 * 
 */
public class GetHomeTimeline {

	static String[] usual_suspects = { 
	//	"SheriffClarke",
	//	"Nigel_Farage", 
	//	"NathanDamigo", 
	//	 "nytimes",
	//	 "ThaRightStuff",
	//	"andieiamwhoiam",
		    "realDonaldTrump", 
	//	    "mockumatrix",
	//	    "seanspicer",
	//	    "AnnCoulter", 
	//	    "Evan_McMullin",
	//		"JackPosobiec",
	//    	"lucasnolan_", 
	//		"PrisonPlanet",
	//		"TRobinsonNewEra",
	//		"realEmilyYoucis",
	//		"RichardBSpencer", 
	//		"Scaramucci", 
	//		"seanhannity", 
	//		"ScottAdamsSays", 
	//		"EricTrump", 
	//		"DonaldJTrumpJr", 
	//		"jazzhandmcfeels", 
	//		"ReactionaryTree", 
	//		"Cernovich", 
	//    	"bakedalaska",
	//		"PizzaPartyBen",
	//		"Lauren_Southern",
		//	"precession_", 
		//	"AusAesthetics", 
	//		"JohnRiversX4",
	//		"TeamTrump",
	//      "mitchellvii",
	//	"RealJamesWoods",
	//	"SwiftOnSecurity",
	//	"OriginalAussie", 
	//	"JRubinBlogger", 
	//	"fluent_SARAcasm", 
	//	"carrieffisher", 
	//	"alwaystheself",
	//	"arthur_affect",
	//	"sarahkendzior",
	//	"kurteichenwald",
	//	"Evan_McMullin", 
	//	"BernieSanders",
	//	"RepAdamSchiff",
	//	"pussyrrriot",
	//	"tolokno",
	//	"nytimes",
	//	"ciccmaher",
	//	"JamesGleick",
	//	"AmandaRivkin",
	//	"SenSanders",
	//	"mockumatrix",
	//	"MMFlint",
	
		};

	/**
	 * Usage: java twitter4j.examples.timeline.GetHomeTimeline
	 *
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		    cb.setJSONStoreEnabled(true);
		    cb.setTweetModeExtended(true); // !!!
		    
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

		for (String name : GetHomeTimeline.usual_suspects) {
			
			String twitterHandle = name;

			try {
				// gets Twitter instance with default credentials
			
				ResponseList<Status> statuses = twitter.getUserTimeline(twitterHandle);
			//	int limit = statuses.getRateLimitStatus().getLimit();
			//	int remaining = statuses.getRateLimitStatus().getRemaining();
			//	System.out.println("Rate limits: "+remaining+"/"+limit);
			//	if(remaining == 0) {
			//		System.err.println("Warn: API rate limit! Bailing out...");
			//		return;
			//	}
				
				
				MemeGen gen = new MemeGen(twitterHandle);
			//	File parent = new File("output/"+twitterHandle);
			//	parent.delete();
				
				String text = "";

				for (Status status : statuses) {
					
					String raw = TwitterObjectFactory.getRawJSON(status);
					System.err.println(raw);
					
					
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
					
					//String text = status.getText();
					 gen.gen(text, status, MemeGen.TEXT_STYLE.TRADITIONAL);
				  //   gen.gen(status, MemeGen.TEXT_STYLE.CENTERED);
				}
			} catch (TwitterException te) {
				System.out.println("FAILED ON: " + name);
				te.printStackTrace();
				System.out.println("Failed to get timeline: " + te.getMessage());
				// System.exit(-1);
			}
		}
	}
}