package com.cryptoregistry.mockumatrix;

import java.io.File;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.junit.Test;

public class ChunkerTest {
	
	
	
	@Test
	public void testSimple() {
		SimpleCombiner sc = new SimpleCombiner(null, new File("C:/Users/Dave/Desktop"));
		sc.meld();
	}

	@Test
	public void test0() {
		
		String [] input = {
			//"Say: \"Protest is not futile.\"",
			//"Say it.",
			//"Say: \"Resisting evil is not evil.\"",
			//"Say it.",
			"Say: \"The standard I walk by is the standard I accept.\"",
			"Say it.",
		};
		Chunker c = new Chunker(input, new File("output/test6.jpg"));
		c.write();
	}
	
	@Test
	public void test1() {
		String in = "This is a test. Of the end; https://something and more!";
		StringTokenizer st = new StringTokenizer(in);
		while (st.hasMoreElements()) {
			String t = st.nextToken();
			System.err.println(t);
		}
	}
	
	@Test
	public void test2() {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = "RT @SwiftOnSecurity: Any journalist want to follow-up on Ruby Giuliani's computer security company? @zackwhittaker https://t.co/7NV1KkoBsc";
		iterator.setText(source);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
		  System.out.println(source.substring(start,end));
		}
	}
	
	
	@Test
	public void test3() {
		File f = new File("C:/Users/Dave/Desktop/escapism/1");
		ImageMelder melder = new ImageMelder("The Trumpists work to de-legitimize protest. They publicize stories fitting their own narrative. If real stories are unavailable, they are fabricated.", f);
		melder.meld();
		
		f = new File("C:/Users/Dave/Desktop/escapism/2");
		melder = new ImageMelder("Breitbart's main function is publishing propaganda. This is similar to the Russian Pravda News. It's not an amature operation. They're well-funded and highly targeted.", f);
		melder.meld();
		
		f = new File("C:/Users/Dave/Desktop/escapism/3");
		melder = new ImageMelder("Infowars is like Breitbart. It has a European slant.", f);
		melder.meld();
		
		f = new File("C:/Users/Dave/Desktop/escapism/4");
		melder = new ImageMelder("Infowars does a lot of Trump Hagiography. Trump is seen crushing opponents of the regime.", f);
		melder.meld();
		
		f = new File("C:/Users/Dave/Desktop/escapism/5");
		melder = new ImageMelder("They believe they control the news cycle. People have limited time for news. In this way, they shape the discussion.", f);
		melder.meld();
		
	}
	
	@Test
	public void test4() {
		File f = new File("C:/Users/Dave/Desktop/response-wm");
		ImageMelder melder = new ImageMelder(null, f);
		melder.meld();
	}
}
