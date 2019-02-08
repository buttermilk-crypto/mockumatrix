package com.cryptoregistry.mockumatrix.panel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class PosterControlsPanel extends JPanel {
	
	// Create a file chooser
	final static JFileChooser fc = new JFileChooser();
	
	JButton btnAttachImage;
	String filePath;
	final ClearPanel backgroundPanel, foregroundPanel;
	
	public PosterControlsPanel(ClearPanel foregroundPanel, ClearPanel backgroundPanel) {
		this.backgroundPanel=backgroundPanel;
		this.foregroundPanel=foregroundPanel;
		
		fc.setPreferredSize(new Dimension(800,600));
		ImagePreviewPanel preview = new ImagePreviewPanel();
		fc.setAccessory(preview);
		fc.addPropertyChangeListener(preview);
		// fix, not portable
		File userDir = new File("C:/Users/Dave/Desktop");
		fc.setCurrentDirectory(userDir);
		
		btnAttachImage = new JButton("Attach Image");
		btnAttachImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnAttachImage) {
					int returnVal = fc.showOpenDialog(btnAttachImage);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						try {
							String path = file.getCanonicalPath();
							filePath = path;
							backgroundPanel.setPath(path);
							backgroundPanel.load();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {

					}
				}
			}
		});
		
		this.setLayout(new FlowLayout());
		add(btnAttachImage);
		
		String[] fontSize = { "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "60", "72" };

		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		JComboBox<String> fontSizeList = new JComboBox<String>(fontSize);
		fontSizeList.setSelectedIndex(3);
		fontSizeList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 @SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
			     String fSize = (String)cb.getSelectedItem();
			     int sz = Integer.valueOf(fSize);
			     Font font = new Font("Impact", Font.PLAIN, sz);
				 backgroundPanel.setFont(font);
				 font = new Font("Impact", Font.PLAIN, sz);
				 foregroundPanel.setFont(font);
			}
			
		});
		
		add(fontSizeList);
		
	}

	private static final long serialVersionUID = 1L;

}
