package com.cryptoregistry.mockumatrix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.cryptoregistry.mockumatrix.panel.ClearPanel;

public class Main {
	
	private JFrame frame = new JFrame();
	private JLayeredPane lpane = new JLayeredPane();
	private ClearPanel panelBlue = new ClearPanel();
	private ClearPanel panelGreen = new ClearPanel();

	public Main() {
		
	//	String text = "\n\n\n\nYou have to go on and be crazy. Craziness is like heaven.\n\t\t\t\tJimi Hendrix";
		String text ="\n\n\nResist.";
		
		frame.setPreferredSize(new Dimension(600, 600));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(lpane, BorderLayout.CENTER);
		lpane.setBounds(0, 0, 600, 600);
		
		Font font = new Font("Impact", Font.PLAIN, 36);
		
	//	panelBlue.setPath("C:/Users/Dave/Desktop/mockumatrix/output/sister.jpg");
		panelBlue.setPath("C:/Users/Dave/Desktop/orb/orb.jpg");
		panelBlue.setBounds(0, 0, 600, 600);
		panelBlue.load();
		panelBlue.setText(text);
	    panelBlue.setFont(font);
	    panelBlue.setForeground(Color.black);
	    panelBlue.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		
		panelGreen.setBounds(0, 0, 600, 600);
		panelGreen.setOpaque(false);
		
		panelGreen.setText(text);
	    panelGreen.setFont(font);
	    panelGreen.setForeground(Color.white);
	    panelGreen.getDocument().addDocumentListener(new MyDocumentListener(panelBlue));
		
		lpane.add(panelBlue, new Integer(0), 0);
		lpane.add(panelGreen, new Integer(1), 0);
		frame.pack();
		frame.setVisible(true);
		
		Dimension d = panelGreen.getSize();
		
		BufferedImage b = new BufferedImage((int)d.getWidth(),(int)d.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = b.createGraphics();
		Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		frame.print(g);
		g.dispose();
		
		try {
			ImageIO.write(b, "JPG", new File("C:/Users/Dave/Desktop/resist/output/resist.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new Main();
	}
}

class MyDocumentListener implements DocumentListener {
	
	JTextPane slave;
	
	MyDocumentListener(JTextPane slave){
		this.slave=slave;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			String text = e.getDocument().getText(0, e.getDocument().getLength());
			slave.setText(text);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			String text = e.getDocument().getText(0, e.getDocument().getLength());
			slave.setText(text);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			String text = e.getDocument().getText(0, e.getDocument().getLength());
			slave.setText(text);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
	}
}
