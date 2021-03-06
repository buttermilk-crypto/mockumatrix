package com.cryptoregistry.mockumatrix;

/*
 * Copyright 2012-2013 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 * 
 * https://raw.githubusercontent.com/awslabs/java-meme-generator-sample/master/projects/MemeWorker/src/com/amazonaws/memes/ImageOverlay.java
 */

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
public class ImageOverlay {

    private static final int MAX_FONT_SIZE = 42;
    private static final int BOTTOM_MARGIN = 16;
    private static final int TOP_MARGIN = 5;
    private static final int SIDE_MARGIN = 24;
    
    public static BufferedImage overlayCenter(BufferedImage image, String text)
            throws IOException, InterruptedException {
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawStringCentered(graphics, text, image, Orientation.CENTER);
        graphics.dispose();
        return image;
    }

    public static BufferedImage overlay(BufferedImage image, String title, String text)
            throws IOException, InterruptedException {
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawStringCentered(graphics, title, image, Orientation.TOP);
        drawStringCentered(graphics, text, image, Orientation.BOTTOM);
        graphics.dispose();
        return image;
    }
    
   public enum Orientation {
    	TOP,BOTTOM,CENTER;
    }

    /**
     * Draws the given string centered, as big as possible, on either the top or
     * bottom 20% of the image given.
     */
    private static void drawStringCentered(Graphics2D g, String text, BufferedImage image, Orientation orientation) throws InterruptedException {
        if (text == null)
            text = "";

        int height = 0;
        int fontSize = MAX_FONT_SIZE;
        int maxCaptionHeight = image.getHeight() / 3;
        int maxLineWidth = image.getWidth() - SIDE_MARGIN * 2;
        String formattedString = "";

        do {
        	if(orientation == Orientation.TOP)
        		g.setFont(new Font("Impact", Font.BOLD, fontSize));
        	else
        		g.setFont(new Font("Impact", Font.PLAIN, fontSize));
        	
            RenderingHints rh = new RenderingHints(
                     RenderingHints.KEY_TEXT_ANTIALIASING,
                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHints(rh);

            // first inject newlines into the text to wrap properly
            StringBuilder sb = new StringBuilder();
            int left = 0;
            int right = text.length() - 1;
            while ( left < right ) {

                String substring = text.substring(left, right + 1);
                Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(substring, g);
                while ( stringBounds.getWidth() > maxLineWidth ) {
                	
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    // look for a space to break the line
                    boolean spaceFound = false;
                    for ( int i = right; i > left; i-- ) {
                        if ( text.charAt(i) == ' ' ) {
                            right = i - 1;
                            spaceFound = true;
                            break;
                        }
                    }
                    substring = text.substring(left, right + 1);
                    stringBounds = g.getFontMetrics().getStringBounds(substring, g);

                    // If we're down to a single word and we are still too wide,
                    // the font is just too big.
                    if ( !spaceFound && stringBounds.getWidth() > maxLineWidth ) {
                        break;
                    }
                }
                sb.append(substring).append("\n");
                left = right + 2;
                right = text.length() - 1;
            }

            formattedString = sb.toString();

            // now determine if this font size is too big for the allowed height
            height = 0;
            for ( String line : formattedString.split("\n") ) {
                Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line, g);
                height += stringBounds.getHeight();
            }
            fontSize--;
        } while ( height > maxCaptionHeight );
        
        int countLines = 0;
        for ( @SuppressWarnings("unused") String line : formattedString.split("\n") ) {
        	countLines++;
        }

        // draw the string one line at a time
        int y = 0;
        if ( orientation == Orientation.TOP) {
            y = TOP_MARGIN + g.getFontMetrics().getHeight();
        } else if(orientation == Orientation.BOTTOM) {
            y = image.getHeight() - height - BOTTOM_MARGIN + g.getFontMetrics().getHeight();
        }else if(orientation == Orientation.CENTER){
        	 y = (image.getHeight()/2) -  ((g.getFontMetrics().getHeight() * countLines)/2);
        }
        
        for ( String line : formattedString.split("\n") ) {
            // Draw each string twice for a shadow effect
            Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line, g);
            
            g.setColor(Color.BLACK);
            g.drawString(line, (image.getWidth() - (int) stringBounds.getWidth()) / 2 + 2, y + 2);
            g.setColor(Color.WHITE);
            g.drawString(line, (image.getWidth() - (int) stringBounds.getWidth()) / 2, y);
            y += g.getFontMetrics().getHeight();
        }
    }
    
    
}
