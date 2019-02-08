package com.cryptoregistry.mockumatrix.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;

import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ControlsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Twitter twitter;
	private String screenName;
	
	StatusPanel topLevelStatus;
	List<StatusPanel> replyPanelList;

	public ControlsPanel() {
		
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		    cb.setJSONStoreEnabled(true);
		    
			twitter = new TwitterFactory(cb.build()).getInstance();
			
			// RATE LIMITS HANDLING - if it looks like we are hitting a limit, pause
			twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

				@Override
				public void onRateLimitStatus(RateLimitStatusEvent event) {
					RateLimitStatus stat = event.getRateLimitStatus();
					System.err.println("Limit:"+stat.getRemaining()+"/"+ stat.getLimit());
				}

			
				@Override
				public void onRateLimitReached(RateLimitStatusEvent event) {
					RateLimitStatus stat = event.getRateLimitStatus();
					int secReset = stat.getSecondsUntilReset();
					System.err.println("Hit limit, sleeping for "+secReset+" seconds...");
					try {
						Thread.sleep((secReset*1000)+5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			});
			
		
		
		replyPanelList = new ArrayList<StatusPanel>();
		
		JButton btnSend = new JButton("Send");
		
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					screenName = twitter.getAccountSettings().getScreenName();
					System.err.println("Using screen name: "+screenName);
				} catch (TwitterException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
				Status first = null;
				String text = topLevelStatus.getStatusText().getText();
				
				// text is required
				if(text == null || text.trim().isEmpty()) {
					System.out.println("Failing, because text is empty on top level");
					return;
				}
				
				String path = topLevelStatus.getFilePath().getText();
				
				StatusUpdate status = null;
				
				// if there is an image path, validate it exists
				
				if(path == null || path.trim().isEmpty()){
					// prepare without image
					status = new StatusUpdate(text);
				}else{
					File f = new File(path);
					if(!f.exists()) {
						throw new RuntimeException("path is not null, but file not exist, bailing out");
					}else{
						// prepare with image
						status = new StatusUpdate(text);
						status.setMedia(f);
					}
				}
				
				try {
					first = twitter.updateStatus(status);
					System.err.println("Posted Top Level Status: "+first.toString());
				} catch (TwitterException e1) {
					e1.printStackTrace();
					return;
				}
				
				
				// now post the replies
				for(StatusPanel sp: replyPanelList){
					
					text = sp.getStatusText().getText();
					
					// text is required
					if(text == null || text.trim().isEmpty()) {
						System.out.println("Skipping "+sp.getName()+" because text is empty");
						continue;
					}
					
					path = sp.getFilePath().getText();
					
					status = null;
					
					// if there is an image path, validate it exists
					
					if(path == null || path.trim().isEmpty()){
						// prepare without image
						status = new StatusUpdate(text);
					}else{
						File f = new File(path);
						if(!f.exists()) {
							throw new RuntimeException("path is not null, but file not exist, bailing out");
						}else{
							// prepare with image
							status = new StatusUpdate(text);
							status.setMedia(f);
						}
					}
					
					try {
						status.setInReplyToStatusId(first.getId());
						twitter.updateStatus(status);
						System.err.println("Posted: "+status.toString());
					} catch (TwitterException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(383, Short.MAX_VALUE)
					.addComponent(btnSend)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(88, Short.MAX_VALUE)
					.addComponent(btnSend)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}

	public StatusPanel getTopLevelStatus() {
		return topLevelStatus;
	}

	public void setTopLevelStatus(StatusPanel topLevelStatus) {
		this.topLevelStatus = topLevelStatus;
	}

	public void addReply(StatusPanel p) {
		this.replyPanelList.add(p);
	}
	
	
	
}
