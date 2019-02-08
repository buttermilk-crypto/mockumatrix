package com.cryptoregistry.mockumatrix.client;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.twitter.Validator;

public class StatusDocumentFilter extends DocumentFilter {

	Validator v = new Validator();

	public StatusDocumentFilter() {}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {

		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		StringBuffer buf = new StringBuffer(text);
		buf.insert(offs, str.toCharArray());
		String textToValidate = buf.toString();
		
		if (v.isValidTweet(textToValidate)) {
			super.insertString(fb, offs, str, a);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}

	}

	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {

		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		StringBuffer buf = new StringBuffer(text);
		buf.replace(offs, offs+length, str);
		String textToValidate = buf.toString();
		
		if (v.isValidTweet(textToValidate)) {
			super.replace(fb, offs, length, str, a);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
}
