package com.troy.scienceexperiment.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.troy.scienceexperiment.*;

/**
 * Represents an image format
 * @author Troy Neubauer
 *
 */
public abstract class Format {
	
	protected boolean lossy;
	protected String format;
	protected List<ImageType> images = new ArrayList<ImageType>();

	public Format(boolean lossy, String format, List<ImageType> images) {
		this.lossy = lossy;
		this.format = format;
		this.images = images;
	}

	/**
	 * Writes the output images for each base image (black, professional) 10 times for 10 trials
	 * @param dataMaster The data master to put that data into
	 * @return A list of data gathered
	 */
	public List<Data> writeBasicImages(DataMaster dataMaster) {
		System.out.println("\n\n######## Starting image format "+format.toUpperCase()+" ########");
		List<Data> data = new ArrayList<Data>();
		for(ImageType image : images) {
			for(int i = 1; i <= Main.TRIAL_COUNT; i++) {
				try {
					File file = new File(format + "." + format);
					//Get the nanotime before to time
					long startTime = System.nanoTime();
					ImageIO.write(image.getImage(), format, file);
					//Get the nanotime after then subtract to get time taken
					long endTime = System.nanoTime();
					long time = endTime - startTime;
					long size = file.length();// Gets the size of the newly exported file
					//Add the data
					data.add(new Data(this, image, size, time, i));
					dataMaster.add(new DataMasterPoint(image, this, time / 1000000.0, size / 1024.0, 1.0, false));
				} catch (IOException e) {
					System.out.println("Unable to export image " + image.getName() + " in the format " + format);
					e.printStackTrace();
				}
			}
		}
		
		return data;
	}
	
	@Override
	public String toString() {
		return "Format: " + format + " lossy: " + lossy;
	}
	
	public String getName() {
		return format;
	}
	
	public abstract List<Data> write(DataMaster data);
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(!(obj instanceof Format)) return false;
		return this.format.equals(((Format)obj).format);
	}

}
