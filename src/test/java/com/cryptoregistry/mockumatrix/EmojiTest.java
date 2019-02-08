package com.cryptoregistry.mockumatrix;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

public class EmojiTest {

	public static Collection<Emoji> emojis = EmojiManager.getAll();

	@Test
	public void test() {
		
		List<String> list = new ArrayList<String>();
		
		InputStream in = this.getClass().getResourceAsStream("/example-status.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> map = mapper.readValue(in, Map.class);
			String text = (String) map.get("text");
			IntStream istream = text.codePoints();
			istream.forEach(codepoint -> {
			      list.add(  new String(Character.toChars(codepoint)) ); 
		    });
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EmojiString es = new EmojiString();
		
		for(String s: list){
			Emoji em = null;
			Iterator<Emoji> iter = emojis.iterator();
			while(iter.hasNext()){
				Emoji tmp = iter.next();
				if(tmp.getUnicode().equals(s)){
					em = tmp;
				}
			}
			if(em == null) es.add(new EmojiStringItem(s, null));
			else es.add(new EmojiStringItem(s, em));
		}
		
		for(EmojiStringItem item: es.list){
			if(item.hasEmoji()) {
				String path = item.bwEmojiResourcePath();
				try
				{
					InputStream in_ = this.getClass().getResourceAsStream(path);
					if(in_ != null) {
						System.err.println(path+" emoji exists!");
					}else{
						System.err.println(path+" emoji exists NOT!");
					}
					in.close();
				}catch(Exception x){
					x.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void testNormalize() {
		
		Collection<String> tags = EmojiManager.getAllTags();
		String example = null;
		for(String t : tags){
			example = t;
			break;
		}
		
		Set<Emoji> relaxed = EmojiManager.getForTag(example);
		Assert.assertNotNull(relaxed);
		Assert.assertTrue(relaxed.size()>0);
		
		
		EmojiString es = new EmojiString();
		es.add(new EmojiStringItem("One"));
		es.add(new EmojiStringItem("Two"));
		es.add(new EmojiStringItem("Three"));
		es.add(new EmojiStringItem((Emoji)relaxed.toArray()[0]));
		es.add(new EmojiStringItem("Five"));
		es.add(new EmojiStringItem("Six"));
		
		es.normalize();
		
		Assert.assertTrue(es.list.size() == 3);
	
		
	}

}
