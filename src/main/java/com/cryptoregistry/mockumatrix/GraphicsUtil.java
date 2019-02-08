package com.cryptoregistry.mockumatrix;

/*
 *
 * Created on March 16, 2007, 4:34 PM
 *
 * Copyright 2006-2007 Nigel Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/
 * licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;


/**
 * 
 * @author nigel
 */
public class GraphicsUtil {
	/**
	 * Renders multiple paragraphs of text in an array to an image (created and
	 * returned).
	 *
	 * @param font
	 *            The font to use
	 * @param textColor
	 *            The color of the text
	 * @param text
	 *            The message in an array of strings (one paragraph in each
	 * @param width
	 *            The width the text should be limited to
	 * @return An image with the text rendered into it
	 */
	public static BufferedImage renderTextToImage(Font font, Color textColor,
			String text[], int width) {
		LinkedList<BufferedImage> images = new LinkedList<BufferedImage>();

		int totalHeight = 0;

		for (String paragraph : text) {
			BufferedImage paraImage = renderTextToImage(font, textColor,
					paragraph, width);
			totalHeight += paraImage.getHeight();
			images.add(paraImage);
		}

		BufferedImage image = createCompatibleImage(width, totalHeight);
		Graphics2D graphics = (Graphics2D) image.createGraphics();

		int y = 0;

		for (BufferedImage paraImage : images) {
			graphics.drawImage(paraImage, 0, y, null);
			y += paraImage.getHeight();
		}

		graphics.dispose();
		return image;
	}

	/**
	 * Renders a paragraph of text (line breaks ignored) to an image (created
	 * and returned).
	 *
	 * @param font
	 *            The font to use
	 * @param textColor
	 *            The color of the text
	 * @param text
	 *            The message
	 * @param width
	 *            The width the text should be limited to
	 * @return An image with the text rendered into it
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BufferedImage renderTextToImage(Font font, Color textColor,
			String text, int width) {
		Hashtable map = new Hashtable();
		map.put(TextAttribute.FONT, font);
		AttributedString attributedString = new AttributedString(text, map);
		AttributedCharacterIterator paragraph = attributedString.getIterator();

		FontRenderContext frc = new FontRenderContext(null, false, false);
		int paragraphStart = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

		float drawPosY = 0;

		// First time around, just determine the height
		while (lineMeasurer.getPosition() < paragraphEnd) {
			TextLayout layout = lineMeasurer.nextLayout(width);

			// Move it down
			drawPosY += layout.getAscent() + layout.getDescent()
					+ layout.getLeading();
		}

		BufferedImage image = createCompatibleImage(width, (int) drawPosY);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		drawPosY = 0;
		lineMeasurer.setPosition(paragraphStart);
		while (lineMeasurer.getPosition() < paragraphEnd) {
			TextLayout layout = lineMeasurer.nextLayout(width);

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			/*
			 * Compute pen x position. If the paragraph is right-to-left, we
			 * want to align the TextLayouts to the right edge of the panel.
			 */
			float drawPosX;
			if (layout.isLeftToRight()) {
				drawPosX = 0;
			} else {
				drawPosX = width - layout.getAdvance();
			}

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(graphics, drawPosX, drawPosY);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}

		graphics.dispose();
		return image;
	}

	/**
	 * Creates an image compatible with the current display
	 * 
	 * @return A BufferedImage with the appropriate color model
	 */
	public static BufferedImage createCompatibleImage(int width, int height) {
		GraphicsConfiguration configuration = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		return configuration.createCompatibleImage(width, height, Transparency.OPAQUE);
	}
	
	@SuppressWarnings("unchecked")
	public static BufferedImage renderText(Font font, Color textColor, 
			BufferedImage background, String text, String title, int width, int height) {
		
		//BufferedImage image = createCompatibleImage(width, height);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		
		Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		graphics.setRenderingHints(rh);
		graphics.setFont(font);
		graphics.setColor(textColor);
		
		graphics.drawImage(background, null, 0, 0);
		
		// write title
		graphics.drawString(title, 40, 40);
		
		@SuppressWarnings("rawtypes")
		Hashtable map = new Hashtable();
		map.put(TextAttribute.FONT, font);
		AttributedString attributedString = new AttributedString(text, map);
		AttributedCharacterIterator paragraph = attributedString.getIterator();

		int paragraphStart = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, graphics.getFontRenderContext());

		float drawPosY = 0;

		// First time around, just determine the height
		while (lineMeasurer.getPosition() < paragraphEnd) {
			TextLayout layout = lineMeasurer.nextLayout(width);

			// Move it down
			drawPosY += layout.getAscent() + layout.getDescent()
					+ layout.getLeading();
		}

		
		
		drawPosY = (height/2)-(drawPosY/2);
		lineMeasurer.setPosition(paragraphStart);
		while (lineMeasurer.getPosition() < paragraphEnd) {
			TextLayout layout = lineMeasurer.nextLayout(width-120);

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			/*
			 * Compute pen x position. If the paragraph is right-to-left, we
			 * want to align the TextLayouts to the right edge of the panel.
			 */
			float drawPosX;
			if (layout.isLeftToRight()) {
				drawPosX = 80;
			} else {
				drawPosX = width - layout.getAdvance()+40;
			}

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(graphics, drawPosX, drawPosY);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}

		graphics.dispose();
		return image;
	}
}
