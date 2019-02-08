package com.cryptoregistry.mockumatrix.client;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import asia.redact.bracket.properties.Properties;

import java.awt.*;
import java.util.Hashtable;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

class LineBreakPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// The LineBreakMeasurer used to line-break the paragraph.
	private LineBreakMeasurer lineMeasurer;

	// index of the first character in the paragraph.
	private int paragraphStart;

	// index of the first character after the end of the paragraph.
	private int paragraphEnd;
	
	float drawPosX, drawPosY;

	private static final Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();

	static {
		map.put(TextAttribute.FAMILY, "Impact");
		map.put(TextAttribute.SIZE, new Float(36.0));
	}

	private static AttributedString vanGogh = new AttributedString(
			"Many people believe that Vincent van Gogh painted his best works "
					+ "during the two-year period he spent in Provence.",
			map);

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.white);
		

		Graphics2D g2d = (Graphics2D) g;
		
		  RenderingHints rh = new RenderingHints(
                  RenderingHints.KEY_TEXT_ANTIALIASING,
                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         g2d.setRenderingHints(rh);

		// Create a new LineBreakMeasurer from the paragraph.
		// It will be cached and re-used.
		if (lineMeasurer == null) {
			AttributedCharacterIterator paragraph = vanGogh.getIterator();
			paragraphStart = paragraph.getBeginIndex();
			paragraphEnd = paragraph.getEndIndex();
			FontRenderContext frc = g2d.getFontRenderContext();
			lineMeasurer = new LineBreakMeasurer(paragraph, frc);
		}

		// Set break width to width of Component.
		float breakWidth = (float) getSize().width;
		
		// Set position to the index of the first character in the paragraph.
		lineMeasurer.setPosition(paragraphStart);

		// Get lines until the entire paragraph has been displayed.
		while (lineMeasurer.getPosition() < paragraphEnd) {

			// Retrieve next layout. A cleverer program would also cache
			// these layouts until the component is re-sized.
			TextLayout layout = lineMeasurer.nextLayout(breakWidth);

			// Compute pen x position. If the paragraph is right-to-left we
			// will align the TextLayouts to the right edge of the panel.
			// Note: this won't occur for the English text in this sample.
			// Note: drawPosX is always where the LEFT of the text is placed.
			drawPosX = layout.isLeftToRight() ? 0 : breakWidth
					- layout.getAdvance();

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(g2d, drawPosX+20, drawPosY+20);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}
	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InputStream in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("twitter4j.properties");
				Properties props = Properties.Factory.getInstance(in);

				createAndShowGUI(props);
			}
		});
	}

	private static void createAndShowGUI(Properties props) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		JFrame frame = new JFrame("layout");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new LineBreakPanel());
		frame.getContentPane().setPreferredSize(new Dimension(600,300));
		frame.pack();
		frame.setVisible(true);

	}

}
