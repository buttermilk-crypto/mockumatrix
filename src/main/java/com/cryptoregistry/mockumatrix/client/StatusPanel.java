package com.cryptoregistry.mockumatrix.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.SwingConstants;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

public class StatusPanel extends JPanel implements DocumentListener {

	private static final long serialVersionUID = 1L;

	JTextArea statusText;
	JButton btnAttachImage;
	JLabel filePath;
	JLabel lblCharacterCount;

	// Create a file chooser
	final static JFileChooser fc = new JFileChooser();

	JLanguageTool langTool = new JLanguageTool(new BritishEnglish());

	private static final int MAX_LENGTH = 140;
	private JButton btnCheckSpelling;
	
	ImagePanel imagePanel;

	public StatusPanel() {

		statusText = new JTextArea();
		statusText.setText("");
		statusText.setEditable(true);
		statusText.setLineWrap(true);
		statusText.setWrapStyleWord(true);
		Document doc = statusText.getDocument();
		if (doc instanceof AbstractDocument) {
			AbstractDocument ad = (AbstractDocument) doc;
			ad.setDocumentFilter(new StatusDocumentFilter());
		} else {
			throw new RuntimeException("Not the right document type");
		}
		doc.addDocumentListener(this);

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
							filePath.setText(path);
							imagePanel.setPath(path);
							imagePanel.load();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {

					}
				}
			}
		});

		filePath = new JLabel("");
		filePath.setHorizontalAlignment(SwingConstants.LEFT);

		lblCharacterCount = new JLabel("#");

		btnCheckSpelling = new JButton("Check Spelling");
		btnCheckSpelling.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					checkSpelling(statusText.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		
		imagePanel = new ImagePanel();

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(imagePanel, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addComponent(statusText, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnAttachImage)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCheckSpelling)
							.addPreferredGap(ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
							.addComponent(lblCharacterCount, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
						.addComponent(filePath, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(statusText, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnAttachImage)
							.addComponent(btnCheckSpelling))
						.addComponent(lblCharacterCount))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(filePath)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(imagePanel, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		int count = e.getDocument().getLength();
		lblCharacterCount.setText(String.valueOf(count)+"/"+String.valueOf(MAX_LENGTH - count));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		int count = e.getDocument().getLength();
		lblCharacterCount.setText(String.valueOf(count)+"/"+String.valueOf(MAX_LENGTH - count));
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		int count = e.getDocument().getLength();
		lblCharacterCount.setText(String.valueOf(count)+"/"+String.valueOf(MAX_LENGTH - count));
	}

	private void checkSpelling(String text) throws IOException {
		
		List<RuleMatch> matches = langTool.check(text);
		if (matches == null)
			return;

		for (RuleMatch match : matches) {
			if(match.getMessage().equals("Two consecutive dots")) continue;
			System.out.println("Potential error at characters "
					+ match.getFromPos() + "-" + match.getToPos() + ": "+ match.getMessage());
		}
	}

	public JTextArea getStatusText() {
		return statusText;
	}

	public JLabel getFilePath() {
		return filePath;
	}
	
	
}
