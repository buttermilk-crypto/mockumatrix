package com.cryptoregistry.mockumatrix.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private BufferedImage image;
	private String path;

    public ImagePanel() {
    	super(true);
    }
    
    public void setPath(String path){
    	this.path = path;
    }
    
    public void load() {
    	 try {                
             image = ImageIO.read(new File(path));
          } catch (IOException ex) {
              ex.printStackTrace();
          }
    	 
    	 this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(path != null) g.drawImage(image, 0, 0, this);       
    }

}
