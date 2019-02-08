package com.cryptoregistry.mockumatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Test;

import com.twitter.Validator;

public class TextOrganizerTest {

	@Test
	public void test0() {
		
		File infile = new File("./tweets.txt");
		try (
				FileInputStream fin = new FileInputStream(infile);
				InputStreamReader reader = new InputStreamReader(fin);
				BufferedReader breader = new BufferedReader(reader);
		) {
			
			ArrayList<String> list = new ArrayList<String>();
			String s = null;
			StringBuffer buf = new StringBuffer();
			while( (s = breader.readLine()) != null){
				if(s.trim().equals("")){
					String text = buf.toString().trim();
					if(text.length()==0)continue;
					list.add(text);
					buf = new StringBuffer();
				}else{
					if(s.trim().startsWith("#")) continue;
					buf.append(s);
					buf.append(" ");
				}
			}
			
			int count = 0;
			for(String text: list){
				String numbered = "";
				if(count == 0) numbered = text+" [Thread]";
				else if(count == list.size()-1){
					numbered = text+" /end";
				}else{
					numbered = count+" "+text;
				}
						
				Validator v = new Validator();
				if(v.isValidTweet(numbered)) {
					System.err.println("is valid: "+numbered);
				}
				else{
					System.err.println("is NOT valid: ["+ v.getTweetLength(numbered)+ "]"+text);
				}
				count++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}


