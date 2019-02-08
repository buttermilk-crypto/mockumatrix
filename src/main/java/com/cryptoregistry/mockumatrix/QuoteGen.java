package com.cryptoregistry.mockumatrix;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

public class QuoteGen {
	
	String srcImgFolder;
	String [] quote;  //lines of text, one line per image
	int fontSize;
	
	
	public QuoteGen(String srcImgFolder, String [] quote, int fontSize) {
		this.srcImgFolder = srcImgFolder;
		this.quote = quote;
		this.fontSize = fontSize;
	}

	public void gen() {
		
		File dir = new File(srcImgFolder);
		File [] list = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if(pathname.getPath().endsWith("jpg")) return true;
				return false;
			}
			
		});
		
		if(list.length != quote.length) {
			throw new RuntimeException("Picture list and quote list must be same length");
		}
		
		Date d = new Date();
		int count = 0;
		for(File item: list){
			
			try (
				  InputStream input = new FileInputStream(item);
				){
						BufferedImage img = ImageIO.read(input);
						BufferedImage workingCopy = deepCopy(img);
						BufferedImage res = QuoteImageOverlay.overlay(workingCopy, quote[count], fontSize);
						File parent = new File("output/quote-gen");
						parent.mkdirs();
						ImageIO.write(res, 
								"jpeg", 
								new File(parent, count+"-"+d.getTime()+".jpg")
						);
						
				count++;
				
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	

}
