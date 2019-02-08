/*
Copyright 2016, David R. Smith, All Rights Reserved

This file is part of TweetPepper.

TweetPepper is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

TweetPepper is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with TweetPepper.  If not, see <http://www.gnu.org/licenses/>.

*/
package com.cryptoregistry.mockumatrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

public class Texter {
	//input has newlines for line breaks for each line
	String [] input = null;
	File outPath = null;
	
	public Texter(String [] input, File outPath){
		this.input = input; 
		this.outPath = outPath;
	}
	
	public Texter(String in, File outPath){
		this.outPath = outPath;
		List<String> list = new ArrayList<String>();
	
	  // break string into words
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(in);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
			list.add(in.substring(start,end).trim());
		}
	    
		// now sentences might need splitting
		
	   
	    	
	   input = new String[list.size()];
	   list.toArray(input);
	  
	}
	
	public BufferedImage write() {
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		String [] list = createStrings();
		
		Font f = new Font("Bookman Old Style", Font.PLAIN, 12);
		g2.setFont(f);
		FontMetrics fm = g2.getFontMetrics(f);
		
		int x = 75, y=10, maxWidth = 0, maxHeight=0;
		for(String str: list){
			LineMetrics lm = fm.getLineMetrics(str, g2);
			float t_height = lm.getHeight();
			int stringWidth = fm.stringWidth(str);
			if(stringWidth>maxWidth)maxWidth = stringWidth;
			y+=t_height;
			maxHeight=y;
			//g2.drawString(str, x, y); don't actually draw here
		}
		
		// cleanup
		g2.dispose();
		
		
		// OK, we now have the measured rectangle for the text, which is x,y,maxWidth,maxHeight
		// make an image of this size and write it. 
		x = 10; y=10;
		img = new BufferedImage(maxWidth+x+10, maxHeight+y+10, BufferedImage.TYPE_INT_RGB);
		g2 = img.createGraphics();
		g2.setFont(f);
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		PlasmaFilter filter = (PlasmaFilter) Effects.plasmaFilter();
		filter.randomize();
		BufferedImage tmp = filter.filter(img, null);
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		BufferedImage tmp2 = op.filter(tmp, null);
		g2.drawImage(tmp2, null, 0, 0);
		
		for(String str: list){
			LineMetrics lm = fm.getLineMetrics(str, g2);
			float t_height = lm.getHeight();
			y+=t_height;
			g2.setPaint(Color.BLACK);
			g2.drawString(str, x+3, y+3); 
			g2.setPaint(Color.WHITE);
			g2.drawString(str, x, y); 
		}
		
		// cleanup
		g2.dispose();
		
		int width = 600;
		double w = img.getWidth();
		double h = img.getHeight();
		double factor = width/w;
		
		BufferedImage scaled = ImageUtil.scaledImage(img, width, (int)(h*factor));
         
		try {
		
			if(!outPath.getParentFile().exists()){
				outPath.getParentFile().mkdirs();
			}
		    ImageIO.write(scaled, "jpg", outPath);
		} catch (IOException e) {
		   throw new RuntimeException(e);
		}
		
		return scaled;
	}
	
	private String [] createStrings() {
		return input;
	}

}
