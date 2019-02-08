package com.cryptoregistry.mockumatrix;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class TwitterSquare {

	public static void main(String[] args) {
		if(args.length != 1) {
			throw new RuntimeException("Length = "+args.length);
		}
		twitterSquareTheFile(args[0], 600);
	}
	
    public static void twitterSquareTheFile(String path, int width) {
		
		System.err.println("Working in: "+path);

		File folder = new File(path);
		if (!folder.isDirectory()) {
			throw new RuntimeException("Fail, not folder: " + path);
		}
		
		File parent = new File(folder, "output");
		parent.mkdirs();

		File[] imgFiles = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().contains(".twittersquare-")) return false;
				if(pathname.getName().contains(".shortstack-")) return false;
				if(pathname.getName().contains(".trim-")) return false;
				if(pathname.getName().contains(".full-")) return false;
				return pathname.getName().endsWith(".png")
						|| pathname.getName().endsWith(".jpg")
						|| pathname.getName().endsWith(".jpeg");
			}
		});

		for (int i = 0; i < imgFiles.length; i++) {
			twitterSquare(parent, imgFiles[i], width);
		}
	}
	
	/**
	 * Make an image into a 600 by 600 square 
	 * 
	 * @param imgFile
	 * @param width
	 */
	public static void twitterSquare(File parentDir, File imgFile, int width) {
		try {
			BufferedImage img = ImageIO.read(imgFile);
			
			double w = img.getWidth();
			double h = img.getHeight();
			double factor = width/w;
			
			BufferedImage scaled = ImageUtil.scaledImage(img, width, (int)(h*factor));
			BufferedImage flipped = ImageUtil.verticalFlipImage(scaled);
			
			int baseHeight = (int) scaled.getHeight()*3;
			int dif = (int)((baseHeight - width)/2);
			
			BufferedImage base = new BufferedImage((int)width, baseHeight, scaled.getType());
			Graphics2D g = base.createGraphics();
			Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHints(rh);
			g.drawImage(flipped, 0, 0, null);
			g.drawImage(scaled, 0, scaled.getHeight(), null);
			g.drawImage(flipped, 0, (int)(scaled.getHeight()*2), null);
			g.dispose();
			
			// this is the 600 by 600 twitter square made with three images
			File n = new File(parentDir, imgFile.getName()+".full-"+width+"px.jpg");
			System.err.println("Writing: "+n.getCanonicalPath());
			ImageIO.write(base, "JPG", n);
			
			BufferedImage sub = base.getSubimage(0, dif, width, width);
			
			// this is the 600 by 600 twitter square made with three images
			n = new File(parentDir, imgFile.getName()+".twittersquare-"+width+"px.jpg");
			System.err.println("Writing: "+n.getCanonicalPath());
			ImageIO.write(sub, "JPG", n);
			
			
			// this is a 600x600 twitter square made with 2 images only
			int shortStackHeight = (int) scaled.getHeight()*2;
			BufferedImage shortStack = new BufferedImage(width, shortStackHeight, scaled.getType());
			g = shortStack.createGraphics();
			rh = new HashMap<RenderingHints.Key,Object>();
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHints(rh);
			g.drawImage(scaled, 0, 0, null);
			g.drawImage(flipped, 0, scaled.getHeight(), null);
			g.dispose();
			
			if(shortStack.getHeight() < width)
				sub = shortStack.getSubimage(0, 0, width, shortStack.getHeight());
			else 
				sub = shortStack.getSubimage(0, 0, width, width);
			
			n = new File(parentDir, imgFile.getName()+".shortstack-"+width+"px.jpg");
			System.err.println("Writing: "+n.getCanonicalPath());
			ImageIO.write(sub, "JPG", n);
			
			// now 600x300
			
			sub = shortStack.getSubimage(0, 0, width, width/2);
			
			n = new File(parentDir, imgFile.getName()+".trim-"+width/2+"px.jpg");
			System.err.println("Writing: "+n.getCanonicalPath());
			ImageIO.write(sub, "JPG", n);
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
