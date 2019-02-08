package com.cryptoregistry.mockumatrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Worker that knows how to overlay text onto an image.
 */
public class EmojiImageOverlay {

    private static final int MAX_FONT_SIZE = 42;
    private static final int BOTTOM_MARGIN = 16;
    private static final int TOP_MARGIN = 5;
    private static final int SIDE_MARGIN = 24;

    public static BufferedImage overlay(BufferedImage image, EmojiString caption)
            throws IOException, InterruptedException {
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawEmojiString(graphics, caption, image);
       
        return image;
    }
    
    
    private static void drawEmojiString(Graphics2D g, EmojiString es, BufferedImage image){
    	
    	g.setFont(new Font("Impact", Font.PLAIN, MAX_FONT_SIZE));
    	
        RenderingHints rh = new RenderingHints(
                 RenderingHints.KEY_TEXT_ANTIALIASING,
                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
       
        g.setRenderingHints(rh);
        g.setFont(new Font("Impact", Font.PLAIN, 42));

        for(EmojiStringItem item: es.list) {
        	if(item.hasEmoji()){
        		// do emoji print
        	}else{
        		// do normal string print
        		
        	}
        }
        
    }
}
