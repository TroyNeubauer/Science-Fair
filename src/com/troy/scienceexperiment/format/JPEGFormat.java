package com.troy.scienceexperiment.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import com.troy.scienceexperiment.*;
import com.troyberry.util.NumberUtil;

public class JPEGFormat extends Format {

	public JPEGFormat(List<ImageType> images) {
		super(true, "jpg", images);
	}

	@Override
	public List<Data> write(DataMaster dataMaster) {
		System.out.println("\n\n######## Starting image format "+format.toUpperCase()+" ########");
		List<Data> data = new ArrayList<Data>();
		for(ImageType image : images) {
			for(int i = 1; i <= Main.TRIAL_COUNT; i++) {
				List<Float> percents = new ArrayList<Float>();
				List<Long> sizes = new ArrayList<Long>(), times = new ArrayList<Long>();
				
				for(float percent = 0.0f; percent <= 1.0f; percent+= 0.1f) {
					percent = NumberUtil.roundOff(percent, 1);
					try {
						long startTime = System.nanoTime();
						File file = new File(format + "P" +("" +percent).substring(0, 3) + "." + format);
						JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
						jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
						jpegParams.setCompressionQuality(percent);
						
						final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
						writer.setOutput(new FileImageOutputStream(file));

						writer.write(null, new IIOImage(image.getImage(), null, null), jpegParams);
						long endTime = System.nanoTime();
						
						long time = endTime - startTime;
						long size = file.length();
						dataMaster.add(new DataMasterPoint(image, this, time / 1000000.0, size / 1024.0, (double)percent, true));
						percents.add(new Float(percent));
						sizes.add(new Long(size));
						times.add(new Long(time));
						
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
