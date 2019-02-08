package com.cryptoregistry.mockumatrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.MicrosoftTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

/**
 * Must contain "title" and "status-text" update fields. 
 * 
 * @author Dave
 *
 */
public class Exif {

	private Map<String, String> updates;
	
	public Exif(Map<String, String> updates) {
		super();
		this.updates = updates;
	}

	public boolean write(File src, File dst) {
		
		// todo test src is jpg
		
		 try (
				 FileOutputStream fos = new FileOutputStream(dst);
	             OutputStream os = new BufferedOutputStream(fos)
	      ){
			 
	            TiffOutputSet outputSet = null;
	            
	            final ImageMetadata metadata = Imaging.getMetadata(src);
	            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
	            if (jpegMetadata != null) {
	                // note that exif might be null if no Exif metadata is found.
	                final TiffImageMetadata exif = jpegMetadata.getExif();

	                if (exif != null) {
	                    outputSet = exif.getOutputSet();
	                }
	            }

	            if (outputSet == null) {
	                outputSet = new TiffOutputSet();
	            }
	            
	           TiffOutputDirectory tdir = outputSet.getOrCreateRootDirectory();
	           tdir.removeField(ExifTagConstants.EXIF_TAG_SOFTWARE);
	           tdir.add(ExifTagConstants.EXIF_TAG_SOFTWARE, "Mockumatrix Image Generator 1.0");
	           
	           
	           
	     //      tdir.removeField(ExifTagConstants.EXIF_TAG_IMAGE_UNIQUE_ID);
	     //      tdir.add(ExifTagConstants.EXIF_TAG_IMAGE_UNIQUE_ID, updates.get("id"));
	           
	           
	           tdir.removeField(MicrosoftTagConstants.EXIF_TAG_XPAUTHOR);
	           tdir.add(MicrosoftTagConstants.EXIF_TAG_XPAUTHOR, "Mockumatrix");
	           
	           tdir.removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT);
	           tdir.add(TiffTagConstants.TIFF_TAG_COPYRIGHT, "Copyright 2016-2017, for www.mockumatrix.com. All Rights Reserved");

	           
	           tdir.removeField(MicrosoftTagConstants.EXIF_TAG_XPTITLE);
	           tdir.add(MicrosoftTagConstants.EXIF_TAG_XPTITLE, updates.get("title")); // twitter handle and date
	           
	           tdir.removeField(MicrosoftTagConstants.EXIF_TAG_XPSUBJECT);
	           tdir.add(MicrosoftTagConstants.EXIF_TAG_XPSUBJECT, updates.get("status-text"));
	           
	           tdir.removeField(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT);
	           tdir.add(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT, "Questions? Contact @mockumatrix on twitter.com");

	            new ExifRewriter().updateExifMetadataLossless(src, os, outputSet);
	       
	            
		 } catch (IOException | ImageReadException | ImageWriteException e) {
			e.printStackTrace();
			return false;
		}
		 
		 return true;
		
	}

}
