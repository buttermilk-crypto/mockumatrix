package com.cryptoregistry.mockumatrix;

import java.util.ArrayList;
import java.util.stream.IntStream;

import com.vdurmont.emoji.Emoji;

public class EmojiStringItem {

	String str;
	Emoji emoji;

	public EmojiStringItem(String ch, Emoji emoji) {
		this.str = ch;
		this.emoji = emoji;
	}
	
	public EmojiStringItem(Emoji emoji) {
		this.str = null;
		this.emoji = emoji;
	}
	
	public EmojiStringItem(String ch) {
		this.str = ch;
		
	}

	public boolean hasEmoji() {
		return emoji != null;
	}

	@Override
	public String toString() {
		return "EmojiStringItem [str=" + str + ", emoji=" + emoji + "]\n";
	}

	public String bwEmojiResourcePath() {
		if (hasEmoji()) {
			String ustring = uniformat(emoji.getUnicode());
			return "/e1-png-bw/" + ustring+ ".png";
		} else
			return null;
	}

	private String uniformat(String s) {
		
		ArrayList<String> list = new ArrayList<String>();
		IntStream istream = s.codePoints();
		istream.forEach(codepoint -> {
			String hexCode = Integer.toHexString(codepoint);
			if(hexCode.length()<4) {
				String hex = "0000" + hexCode;
				hex = hex.substring(hex.length() - 4);
			      list.add(hex); 
			}else{
				list.add(hexCode);
			}
			
	    });
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			buf.append(list.get(i));
			buf.append("-");
		}
		if (buf.length() > 0)
			buf.deleteCharAt(buf.length() - 1);
		
		return buf.toString();
	}

}
