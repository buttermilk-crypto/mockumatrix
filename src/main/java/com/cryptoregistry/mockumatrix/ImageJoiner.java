package com.cryptoregistry.mockumatrix;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.cryptoregistry.mockumatrix.CmdLineParser.Option;
import com.cryptoregistry.mockumatrix.CmdLineParser.OptionException;

public class ImageJoiner {


	public static void main(String[] args) {
		
		CmdLineParser parser = new CmdLineParser();
		Option<String> leftOpt = parser.addStringOption('l', "left");
		Option<String> rightOpt = parser.addStringOption('r', "right");
		Option<String> outOpt = parser.addStringOption('o', "output");
	
		try {
			parser.parse(args);
		} catch (OptionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String leftpath = parser.getOptionValue(leftOpt, "");
		String rightpath = parser.getOptionValue(rightOpt, "");
		String outpath = parser.getOptionValue(outOpt, "output.jpg");
		
		BufferedImage left = null, right = null;
		try {
			left = ImageIO.read(new File(leftpath));
			right = ImageIO.read(new File(rightpath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		BufferedImage base = new BufferedImage(left.getWidth()+right.getWidth(), left.getHeight(), left.getType());
		Graphics2D g = base.createGraphics();
		Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHints(rh);
		g.drawImage(left, 0, 0, null);
		g.drawImage(right, left.getWidth(), 0, null);
		g.dispose();
		
		try {
			ImageIO.write(base, "JPEG", new File(outpath));
			System.err.println("Wrote: "+outpath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
