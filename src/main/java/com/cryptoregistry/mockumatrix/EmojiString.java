package com.cryptoregistry.mockumatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * An EmojiString has a list of Characters or Emoji objects
 * 
 * @author Dave
 *
 */
public class EmojiString {
	
	List<EmojiStringItem> list;

	public EmojiString() {
		list = new ArrayList<EmojiStringItem>();
	}

	public boolean add(EmojiStringItem e) {
		return list.add(e);
	}

	public List<EmojiStringItem> getList() {
		return list;
	}

	@Override
	public String toString() {
		return "EmojiString [ " + list + " ]";
	}
	
	public void normalize() {
		List<EmojiStringItem> listNew = new ArrayList<EmojiStringItem>();
		
		for(EmojiStringItem item: list){
			if(item.hasEmoji()) {
				listNew.add(item);
			} else {
				if(listNew.size() == 0) {
					listNew.add(item);
				}else{
					int pos = listNew.size()-1;
					EmojiStringItem top = listNew.get(pos);
					if(top.hasEmoji()){
						listNew.add(item);
					}else{
						top.str = top.str + item.str;
					}
				}
			}
		}
		
		list = listNew;
	}

}
