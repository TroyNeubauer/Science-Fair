package com.troy.scienceexperiment.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.troy.scienceexperiment.*;
import com.troyberry.util.NumberUtil;

/**
 * Represents the GIF format
 * @author Troy Neubauer
 *
 */
public class GIFFormat extends Format {

	public GIFFormat(List<ImageType> images) {
		super(false, "gif", images);
	}
	
	/**
	 * Writes the images and gets the results. Doesn't call super.writeBasicImages() because GIF needs to be exported for each
	 * compression ratio.
	 * @see Format#writeBasicImages(DataMaster);
	 */
	@Override
	public List<Data> write(DataMaster dataMaster) {
		System.out.println("\n\n######## Starting image format "+format.toUpperCase()+" ########");
		List<Data> data = new ArrayList<Data>();
		for(ImageType image : images) {
			for(int i = 1; i <= Main.TRIAL_COUNT; i++) {
				List<Float> percents = new ArrayList<Float>();
				List<Long> sizes = new ArrayList<Long>(), times = new ArrayList<Long>();
				// This time also loop through 11 compression ratios of 0.0 - 1.0
				for(float percent = 0.0f; percent <= 1.0f; percent+= 0.1f) {
					percent = NumberUtil.roundOff(percent, 1);
					try {
						File file = new File(format + "P" +("" +percent).substring(0, 3) + "." + format);
						long startTime = System.nanoTime();
						GifSequenceWriter writer = new GifSequenceWriter
								(new FileImageOutputStream(file), image.getImage().getType(), percent, 1, false);
						
						writer.writeToSequence(image.getImage());
						writer.close();
						long endTime = System.nanoTime();
						
						long time = endTime - startTime;
						long size = file.length();
						
						percents.add(new Float(percent));
						sizes.add(new Long(size));
						times.add(new Long(time));
						dataMaster.add(new DataMasterPoint(image, this, time / 1000000.0, size / 1024.0, percent, true));
						
					} catch (IOException e) {
						System.out.println("Unable to export image " + image.getName() + " in the format " + format);
						e.printStackTrace();
					}
				}
				data.add(new LossyData(this, image, i, percents, sizes, times));
				
			}
		}
		
		return data;
	}

}
