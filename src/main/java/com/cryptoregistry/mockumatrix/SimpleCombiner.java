package com.cryptoregistry.mockumatrix;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class SimpleCombiner {

	File folder;
	String title;

	public SimpleCombiner(String title, File folder) {
		super();
		this.folder = folder;
		this.title = title; 
		if(!folder.isDirectory()) throw new RuntimeException("not a directory");
	}
	
	public void meld() {
		
		File [] list = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File path) {
				
				if(path.getName().equals("result.jpg")) return false;
						
				if( path.getName().toLowerCase().endsWith("jpg") 
						|| path.getName().toLowerCase().endsWith("png")) return true;
				
				return false;
			}
		});
		
		int width = 600;
		int totalHeight = 0;
		List<BufferedImage> images = new ArrayList<BufferedImage>(); 
		for(File item: list){
			try {
				BufferedImage img = ImageIO.read(item);
				if(img.getWidth()!=600){
					
					double w = img.getWidth();
					double h = img.getHeight();
					double factor = width/w;
					
					BufferedImage scaled = ImageUtil.scaledImage(img, width, (int)(h*factor));
					totalHeight+=scaled.getHeight();
					images.add(scaled);
					
				}else{
					totalHeight+=img.getHeight();
					images.add(img);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			BufferedImage res = new BufferedImage(width,totalHeight,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = res.createGraphics();
			Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHints(rh);
			
			int currentY = 0;
			for(BufferedImage img: images){
				g.drawImage(img, 0, currentY, img.getWidth(), img.getHeight(), null);
				currentY+=img.getHeight();
			}
			g.dispose();
			
			
			BufferedImage full = null;
			// now create title
			if(title != null){
			
			Chunker c = new Chunker(title, new File(folder, "0.jpg"));
			BufferedImage title = c.write();
			
			int fullHeight = (int)(totalHeight+title.getHeight());
			
			// now split res into two equal pieces
			full = new BufferedImage(width,fullHeight,BufferedImage.TYPE_INT_RGB);
			g = full.createGraphics();
			rh = new HashMap<RenderingHints.Key,Object>();
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHints(rh);
			
			
			
			int half = res.getHeight()/2;
			int bottom = half+title.getHeight();
			
			BufferedImage lower = res.getSubimage(0, half, 600, half);
			
			g.drawImage(res, 0, 0, null);
			g.drawImage(title, 0, half, null);
			g.drawImage(lower, 0, bottom, null);
			
			g.dispose();
			
			new File(folder, "0.jpg").delete();
			
			}
			
			try {
				if(full != null){
					ImageIO.write(full, "JPEG", new File(folder, "result.jpg"));
				}else{
					ImageIO.write(res, "JPEG", new File(folder, "result.jpg"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
}
