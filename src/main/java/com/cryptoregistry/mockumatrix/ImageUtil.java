package com.cryptoregistry.mockumatrix;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;


public class ImageUtil {
	
	public static BufferedImage shearImage(BufferedImage image) {
		// Flip the image vertically
		BufferedImage dest = createCompatible(image);
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(50.0, -image.getHeight());
		tx.shear(0.1, 0.0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, dest);
		return dest;
	}

	public static BufferedImage verticalFlipImage(BufferedImage image) {
		// Flip the image vertically
		BufferedImage dest = createCompatible(image);
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight());
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, dest);
		return dest;
	}
	
	public static BufferedImage horizontalFlipImage(BufferedImage image){
		// Flip the image vertically
		BufferedImage dest = new BufferedImage(image.getWidth(),image.getHeight(), image.getType());
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, dest);
		return dest;
	}
	
	public static BufferedImage flip180Image(BufferedImage image){
		// Flip the image vertically
		BufferedImage dest = new BufferedImage(image.getWidth(),image.getHeight(), image.getType());
		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		tx.translate(-image.getWidth(null), -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, dest);
		return dest;
	}


	public static BufferedImage copyImageByDrawing(BufferedImage image) {
		return scaledImage(image, image.getWidth(), image.getHeight());
	}

	public static BufferedImage scaledImage(BufferedImage image, int width, int height) {
		BufferedImage dest = new BufferedImage(width, height, image.getType());
		Graphics2D graphics = dest.createGraphics();
		Map<RenderingHints.Key,Object> rh = new HashMap<RenderingHints.Key,Object>();
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHints(rh);
		
		graphics.drawImage(image, 0, 0, width, height, null);

		graphics.dispose();
		return dest;
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static BufferedImage createCompatible(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.getRaster().createCompatibleWritableRaster();
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		
	}
	
	public static final BufferedImage clone(BufferedImage image) {
	    BufferedImage cl = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
	    Graphics2D g2d = cl.createGraphics();
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();
	    return cl;
	}
}
