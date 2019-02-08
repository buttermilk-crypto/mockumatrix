package com.cryptoregistry.mockumatrix;


import org.junit.Test;
import org.junit.Assert;

public class TextUtilTest {

	@Test
	public void test0() {
		String s = "HI there :joy:  :joy_cat:";
		String out = TextUtil.replaceEmojiCodes(s);
		System.err.println(out);
	}
	
	@Test
	public void test1() {
		String s = "HI there [attachment.jpg] [another.jpg]";
		StormEntry out = TextUtil.removeAttachments(s);
		Assert.assertEquals(2,out.attachmentPaths.size());
	}
	
	
	@Test
	public void test1a() {
		String s = "HI there [here/attachment.jpg] [there/another.jpg]";
		StormEntry out = TextUtil.removeAttachments(s);
		Assert.assertEquals(2,out.attachmentPaths.size());
	}
	
	@Test
	public void test2() {
		String s = "HI there ";
		StormEntry out = TextUtil.removeAttachments(s);
		Assert.assertEquals(0,out.attachmentPaths.size());
	}
	
	@Test
	public void storm0() {
		StormFrame frame = new StormFrame("./src/test/resources/StormTest/tweets.txt");
		frame.load();
		
	}

}
