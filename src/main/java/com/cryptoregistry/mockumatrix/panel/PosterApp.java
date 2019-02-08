package com.cryptoregistry.mockumatrix.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import asia.redact.bracket.properties.Properties;

public class PosterApp implements ActionListener {
	
	JLayeredPane panel;
	ClearPanel panelBackground = new ClearPanel();
	ClearPanel panelForegroundText = new ClearPanel();
	
	final JFileChooser fc = new JFileChooser();
	String filePath;
	
	public PosterApp() {
		fc.setPreferredSize(new Dimension(800,600));
		ImagePreviewPanel preview = new ImagePreviewPanel();
		fc.setAccessory(preview);
		fc.addPropertyChangeListener(preview);
		File userDir = new File(System.getProperty("user.home", ""));
		fc.setCurrentDirectory(userDir);
	}
	
	public BufferedImage getScreenShot(Component component) {
		
	    BufferedImage image = new BufferedImage(
	      component.getWidth(),
	      component.getHeight(),
	      BufferedImage.TYPE_INT_RGB
	      );
	 
	    component.paintAll( image.getGraphics() );
	    return image;
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
	
	public static void createAndShowGUI(Properties props) {
		
		JFrame frame = new JFrame("PosterApp");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLayeredPane lpane = new JLayeredPane();

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		
		PosterApp app = new PosterApp();
		app.panel = lpane;
	    
	    Font font = new Font("Impact", Font.PLAIN, 42);
		
		//app.panelBackground.setBounds(0, 0, 600, 600);
		app.panelBackground.load();
		app.panelBackground.setText("");
	    app.panelBackground.setFont(font);
	    app.panelBackground.setForeground(Color.black);
	    app.panelBackground.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		
		//app.panelForegroundText.setBounds(0, 0, 600, 600);
		app.panelForegroundText.setOpaque(false);
		
		app.panelForegroundText.setText("");
	    app.panelForegroundText.setFont(font);
	    app.panelForegroundText.setForeground(Color.white);
	    app.panelForegroundText.getDocument().addDocumentListener(new MyDocumentListener(app.panelBackground));
		
		lpane.add(app.panelBackground, new Integer(0), 0);
		lpane.add(app.panelForegroundText, new Integer(1), 0);
	    
	    frame.getContentPane().add(lpane);
	    
	    JMenuBar menuBar = new JMenuBar();
	    JMenu menu = new JMenu("File");
	    menuBar.add(menu);
	    
	    JMenuItem openMenuItem = new JMenuItem("Open");
	    openMenuItem.setActionCommand("open");
	    openMenuItem.addActionListener(app);
	    menu.add(openMenuItem);
	    
	    JMenuItem fontMenuItem = new JMenuItem("Font");
	    fontMenuItem.setActionCommand("set-font");
	    fontMenuItem.addActionListener(app);
	    menu.add(fontMenuItem);
	    
	    JMenuItem saveMenuItem = new JMenuItem("Save");
	    saveMenuItem.setActionCommand("save");
	    saveMenuItem.addActionListener(app);
	    menu.add(saveMenuItem);
	    
	    frame.setJMenuBar(menuBar);
	    
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}
	
	private final void setSize(Dimension d) {
	        panel.setPreferredSize(d);
	        panel.setBounds(0,0, (int) d.getWidth(), (int) d.getHeight());
	       int ct = panel.getComponentCount();
	       for(int i = 0; i<ct; i++) {
	    	   panel.getComponent(i).setPreferredSize(d);
	    	   panel.getComponent(i).setBounds(0,0, (int) d.getWidth(), (int) d.getHeight());
	       }
	        Container c = panel.getTopLevelAncestor();
	     
	        if (c instanceof JFrame) {
	            JFrame f = (JFrame) c;
	            f.pack();
	        }
	 }

	@Override
	public void actionPerformed(ActionEvent e) {
		
	  if("open".equals(e.getActionCommand())){
		
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					String path = file.getCanonicalPath();
					filePath = path;
					panelBackground.setPath(path);
					Dimension imgDim = panelBackground.load();
					System.err.println(imgDim.getWidth()+","+imgDim.getHeight());
					this.setSize(imgDim);
					
					System.err.println(panelBackground.getImageDimension());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {

			}
		  
	  }else if("save".equals(e.getActionCommand())){
		 
		ClearPanel p = (ClearPanel) panel.getComponent(1);
		String path = p.getPath();
		if(path == null) return;
		File pathFile = new File(path);
		File parent = pathFile.getParentFile();
		fc.setCurrentDirectory(parent);
		
		int retVal = fc.showSaveDialog((Component)e.getSource());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			
			File file = fc.getSelectedFile();
			BufferedImage img = getScreenShot(panel);
				
			try {
				ImageIO.write(img, "JPEG", file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}else{
			return;
		}
	 }else if("set-font".equals(e.getActionCommand())){
		 
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
					 panelBackground.setFont(font);
					 font = new Font("Impact", Font.PLAIN, sz);
					 panelForegroundText.setFont(font);
				}
				
			});
			
			JOptionPane.showMessageDialog( null, fontSizeList, "Select A Font Size:", JOptionPane.QUESTION_MESSAGE);
		 
	 }
	  
	  
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
