package com.troy.scienceexperiment.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.troy.scienceexperiment.*;

public abstract class Format {
	
	protected boolean lossy;
	protected String format;
	protected List<ImageType> images = new ArrayList<ImageType>();

	public Format(boolean lossy, String format, List<ImageType> images) {
		this.lossy = lossy;
		this.format = format;
		this.images = images;
	}

	public List<Data> writeBasicImages(DataMaster dataMaster) {
		System.out.println("\n\n######## Starting image format "+format.toUpperCase()+" ########");
		List<Data> data = new ArrayList<Data>();
		for(ImageType image : images) {
			for(int i = 1; i <= Main.TRIAL_COUNT; i++) {
				try {
					long startTime = System.nanoTime();
					File file = new File(format + "." + format);
					ImageIO.write(image.getImage(), format, file);
					long endTime = System.nanoTime();
					long time = endTime - startTime;
					long size = file.length();
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
