package com.cryptoregistry.mockumatrix;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

/**
 * <p>This is a "frame" on which to hang tweet text and other data. This frame
 * will be serialized and if things stop in mid-storm, we can restart where
 * we left off.</p> 
 * 
 * <p>The input is a file containing text with a line between the entries.<p/>
 * 
 * <p>for an attachment, use [path/to/file.jpg] format. Up to 4 attachments are possible</p>
 * 
 * <p>the location of the input file will become the location of the base dir of attachments
 * and also where we save the frame file itself</p>
 * 
 * @author Dave
 *
 */
public class StormFrame {

	String path;
	File baseFolder;
	
	List<StormEntry> entries;

	public StormFrame(String path) {
		super();
		this.path = path;
		File input = new File(this.path);
		if(!input.exists()) throw new RuntimeException("input file does not exist: "+path);
		baseFolder = input.getParentFile();
	}
	
	/**
	 * load and parse the input into entries.
	 */
	public void load() {
		entries = TextUtil.prepareStormText(new File(path)); // validation errors will emanate here
		if(entries.size() == 0) {
			throw new RuntimeException("Sorry, no entries. No point in proceeding.");
		}
		
		// ok, now save as a frame in same folder
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, entries);
			File frame = new File(baseFolder, "frame.json");
			Files.write(writer.toString().getBytes("UTF-8"), frame);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}



