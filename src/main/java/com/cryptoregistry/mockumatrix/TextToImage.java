package com.cryptoregistry.mockumatrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * <p>Given a file of text strings, make those into images.</p>
 * 
 * <p>We would like the images to be of a standard width with variable height, and 
 * for the text to be the same font size.</p> 
 * 
 * <p>The text is line-delimited and a special format with a # at the beginning of the first line of each para:<br/>
 * 
 * 	1    # name of section (used as file name)<br/>
 *  2..n text lines<br/>
 *  3    line separator<br/>
 *   <br/>
 *  </p>
 * 
 * @author Dave
 *
 */

public class TextToImage {

	File textFile;
	File parentFolder;
	
	public TextToImage(File textFile) {
		this.textFile = textFile;
		parentFolder = textFile.getParentFile();
	}
	
	public ArrayList<TextToImageResult> createImages() {
		
		Font font = new Font("Book Antiqua", Font.PLAIN, 32);
		BufferedImage parchment = null;
		try {
			parchment = ImageIO.read(new File(parentFolder, "parchment.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<TextToImageResult> res = new ArrayList<TextToImageResult>();
		ArrayList<ArrayList<String>> bigList = list(textFile);
		for(ArrayList<String> para: bigList){
			// for each paragraph
			String title = para.get(0);
			String fileName = fileName(title);
			
			String [] array = new String[para.size()-1];
			for(int i = 0;i<para.size();i++){
				if(i == 0) continue;
				array[i-1] = para.get(i);
			}
			File f = new File(parentFolder, fileName);
			res.add(new TextToImageResult(f,title));
		//	Texter c = new Texter(array, f);
		//	c.write();
			BufferedImage bi = GraphicsUtil.renderText(font, Color.BLACK, parchment, join(array), title, 1240, 620);
			try {
			    ImageIO.write(bi, "jpg", f);
			} catch (IOException e) {
			   throw new RuntimeException(e);
			}
			
		}
		
		return res;
	}
	
	private String join(String[] array){
		StringBuffer buf = new StringBuffer();
		for(String s: array){
			buf.append(s);
			buf.append(" ");
		}
		buf.deleteCharAt(buf.length()-1);
		return buf.toString();
	}
	
	/**
	 * Filter input like "Article I, Section 2" and output "ArticleISection2.jpg"
	 * 
	 * @param input
	 * @return
	 */
	private String fileName(String input){
		StringBuffer buf = new StringBuffer();
		buf.append(input.replaceAll("[^a-zA-Z0-9.-]", "_"));
		buf.append(".jpg");
		return buf.toString();
	}
	
	private static ArrayList<ArrayList<String>> list(File infile) {

		ArrayList<ArrayList<String>> para = new ArrayList<ArrayList<String>>();

		try (FileInputStream fin = new FileInputStream(infile);
				InputStreamReader reader = new InputStreamReader(fin);
				BufferedReader breader = new BufferedReader(reader);) {

			ArrayList<String> list = new ArrayList<String>();
			String s = null;
			while ((s = breader.readLine()) != null) {
				if (s.trim().equals("")) { // if blank line
					if(list.size()>0) {
						para.add(list);
						list = new ArrayList<String>();
					}
				} else {					// not blank line
					if (s.trim().startsWith("#")) continue;
					list.add(s.trim());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return para;
	}
}

