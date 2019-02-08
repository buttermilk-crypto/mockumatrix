package com.cryptoregistry.mockumatrix.client;

import java.awt.Dimension;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import asia.redact.bracket.properties.Properties;

public class TestStatusApp {
	
	 public static JTabbedPane tabbedPane;

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InputStream in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("twitter4j.properties");
				Properties props = Properties.Factory.getInstance(in);
				// fail on oauth.consumerKey=OS05yvqkIkhNqKYe2zux9N2ln
				if(props.get("oauth.consumerKey").equals("OS05yvqkIkhNqKYe2zux9N2ln")) {
					System.exit(1);
				}
				createAndShowGUI(props);
			}
		});
		
	}

	public static void createAndShowGUI(Properties props) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		JFrame frame = new JFrame("Twitter Status Sender");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabbedPane = new JTabbedPane();
	    tabbedPane.setPreferredSize(new Dimension(700,350));
	    //tabbedPane.setBackground(new Color(208, 228, 254));
	    
	    StatusPanel s1 = new StatusPanel();
	    s1.setName("Top Level Status");
	    StatusPanel s2 = new StatusPanel();
	    s2.setName("Reply #1");
	    StatusPanel s3 = new StatusPanel();
	    s3.setName("Reply #2");
	    StatusPanel s4 = new StatusPanel();
	    s4.setName("Reply #3");
	    StatusPanel s5 = new StatusPanel();
	    s5.setName("Reply #4");
	    StatusPanel s6 = new StatusPanel();
	    s6.setName("Reply #5");
	    StatusPanel s7 = new StatusPanel();
	    s7.setName("Reply #6");
	   
	    
	    tabbedPane.addTab(s1.getName(), s1);
	    tabbedPane.addTab(s2.getName(), s2);
	    tabbedPane.addTab(s3.getName(), s3);
	    tabbedPane.addTab(s4.getName(), s4);
	    tabbedPane.addTab(s5.getName(), s5);
	    tabbedPane.addTab(s6.getName(), s6);
	    tabbedPane.addTab(s7.getName(), s7);
	  
	    
	    ControlsPanel cp =  new ControlsPanel();
	    cp.setTopLevelStatus(s1);
	    cp.addReply(s2);
	    cp.addReply(s3);
	    cp.addReply(s4);
	    cp.addReply(s5);
	    cp.addReply(s6);
	    cp.addReply(s7);
	    
	    
	    tabbedPane.addTab("Controls", cp);
	    
	    frame.getContentPane().add(tabbedPane);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);

	}

}
