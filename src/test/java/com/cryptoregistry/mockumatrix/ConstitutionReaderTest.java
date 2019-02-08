package com.cryptoregistry.mockumatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.junit.Test;

import com.twitter.Validator;

public class ConstitutionReaderTest {

	@Test
	public void test0() {
		
		Validator v = new Validator();
		
		File infile = new File("./constitution.txt");
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
				// so this is one stanza and must be broken up into tweetable segments
				 ArrayList<String> segStrings = new ArrayList<String>();
				 buf = new StringBuffer();
				 StringTokenizer st = new StringTokenizer(text);
			     while (st.hasMoreTokens()) {
			        String tok = st.nextToken();
			        if((buf.length()+tok.length()+1)<140) {
			        	buf.append(tok+" ");
			        }else{
			        	// buf reached max size
			        	segStrings.add(buf.toString()+"/"); 
			        	buf = new StringBuffer();
			        	buf.append(tok+" ");
			        }
			     }
			     
			     segStrings.add(buf.toString()); 
				
			 
			     for(String seg: segStrings){
			    	if( v.isValidTweet(seg)){
			    	 System.out.println(seg);
			    	}else{
			    		 System.err.println(seg);
			    	}
			    
			    	count++;
			     }
			     
			     System.err.println();
				
			}
			
			System.err.println(count);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}


