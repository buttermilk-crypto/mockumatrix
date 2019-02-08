package com.cryptoregistry.mockumatrix.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClearPanel extends JTextPane {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage image;
	private String path;
	
	public ClearPanel() {
        super();
        setText("");
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }
	
	   public void setPath(String path){
	    	this.path = path;
	    }
	   
	   public Dimension load() {
		   if(path != null) {
	    	 try {                
	             image = ImageIO.read(new File(path));
	          } catch (IOException ex) {
	              ex.printStackTrace();
	          }
	    	 
	    	 this.repaint();
		   }
		   
		   return this.getImageDimension();
	    }

    @Override
    protected void paintComponent(Graphics g) {
      
    	Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(rh);
		
    	if(image != null) {
    		g2d.drawImage(image, null, 0, 0);
    	}

    	 super.paintComponent(g);
    }

	public String getPath() {
		return path;
	}
    
	public Dimension getImageDimension() {
		if(image == null) return new Dimension(100,100);
		return new Dimension(image.getWidth(),image.getHeight());
	}

}
