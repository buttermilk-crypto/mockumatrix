package com.cryptoregistry.mockumatrix;

import java.io.File;

import org.junit.Test;

public class TextToImageTest {

	@Test
	public void test() {
		TextToImage t = new TextToImage(new File("C:/Users/Dave/Desktop/constitution/constitution.txt"));
		t.createImages();
	}
	
	@Test
	public void test1() {
		
	}

}
