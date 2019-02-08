package com.cryptoregistry.mockumatrix;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.vdurmont.emoji.EmojiParser;

import twitter4j.Status;


public class MemeGen {
	
	String twitterHandle;
	List<String> imageList;
		
	public MemeGen(String twitterHandle){
		this.twitterHandle = twitterHandle;
		createImageList();
		loadImages();
		loadWordList();
	}
	
	List<String> quoteWords = new ArrayList<String>();
		
	BufferedImage [] imageSource;
	
	public enum TEXT_STYLE {
		TRADITIONAL, CENTERED;
	}
	
	public void gen(String text, Status status, TEXT_STYLE ts) {
		
		//String text = status.getText();
		
	
		//if(status.isRetweet()){
		//	text = status.getRetweetedStatus().getText();
		//}
		
		// normalize text
		text = text.replaceAll("\\s+", " ");
		text = text.replaceAll("&amp;", "&");
		
		// deal with emoji (at the moment strip)
		//text = text.replaceAll("\\p{C}", "");
		text = EmojiParser.removeAllEmojis(text);
		
	//	for(String item: quoteWords){
	//		if(text.contains(item)){
	//			String rep = "\""+item+"\"";
	//			text = text.replace(item, rep);
	//		}
	//	}
		
		System.err.println(text);
		
		String name = "@"+status.getUser().getScreenName();
		String realName = status.getUser().getName();
		long id = status.getId();
	//	if(name.equalsIgnoreCase("@realDonaldTrump")){
	//		name = "President Bannon";
	//		realName = "President Bannon";
	//	}
		
		Date d = status.getCreatedAt();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String dval = format.format(d);
		
		int count = 0;
		for(BufferedImage img: imageSource){
			
			BufferedImage clone = deepCopy(img);
			
			String tmp = name.length()<realName.length() ? name : realName; 
			
			String title = tmp+" "+dval;
			try {
				
				BufferedImage res = null;
				if(ts.equals(TEXT_STYLE.TRADITIONAL)) {
					res = ImageOverlay.overlay(clone, title, text);
				}else if(ts.equals(TEXT_STYLE.CENTERED)) {
					res = ImageOverlay.overlayCenter(clone, text+"\n"+title);
				}
				
				
				File parent = new File("output/"+twitterHandle);
				parent.mkdirs();
				
				File outfile = new File(parent, count+"-"+d.getTime()+"-"+ts.name()+".jpg");
				ImageIO.write(res, 
						"jpeg", 
						outfile
				);
				
			//	File outChunker = new File(parent, count+"-"+d.getTime()+"chunker.jpg");
			//	Chunker c = new Chunker(text+"\n"+title,outChunker);
			//	c.write();
				
				File exifTemp = new File(parent, count+"-"+d.getTime()+"-exif.jpg");
				Map<String,String> map = new HashMap<String,String>();
				map.put("status-text", text);
				map.put("title", title);
				map.put("id", String.valueOf(id));
				if(new Exif(map).write(outfile, exifTemp)) {
					// successful
					Files.copy(exifTemp.toPath(), outfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					exifTemp.delete();
				}else{
					// log
					System.err.println("Failed");
				}
				
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void createImageList() {
		
		imageList = new ArrayList<String>();
		File dir = new File("src/main/resources/"+this.twitterHandle);
		if(!dir.exists()) {
			dir = new File("src/main/resources/Default");
		}
		
		File [] items = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith("jpg")) return true;
				return false;
			}
		});
		
		for(File item : items){
			System.out.println("found "+item);
			try {
				imageList.add(item.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Image Count: "+imageList.size());
		
	}
	
	private void loadImages() {
		imageSource = new BufferedImage[imageList.size()];
		int count = 0;
		for(String filePath : imageList){
			try (
			   InputStream input = new FileInputStream(filePath);
			){
				BufferedImage img = ImageIO.read(input);
				imageSource[count] = img;
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	
	private BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private void loadWordList() {
		String line = null;
		try (
				InputStream in  = this.getClass().getClass().getResourceAsStream("/quote-words.txt");
			    InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8"));
			    BufferedReader br = new BufferedReader(isr);
			) {
			    while ((line = br.readLine()) != null) {
			    	if(line.length()>1 && !line.startsWith("#")) {
			    		quoteWords.add(line);
			    	}
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
