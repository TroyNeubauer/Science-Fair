package com.troy.scienceexperiment;

import com.troy.scienceexperiment.format.Format;

/**
 * An instance of this class represents a single point of data for a single image format and trial
 * @author Troy Neubauer
 *
 */
public class Data {
	
	private final Format format;
	private final ImageType imageType;
	private final long size, time;
	private final int trial;
	
	public Data(Format format, ImageType imageType, long size, long time, int trial) {
		this.format = format;
		this.imageType = imageType;
		this.size = size;
		this.time = time;
		this.trial = trial;
	}
	
	@Override
	public String toString() {
		return "Trial " + trial + ": Time taken " + time + " nanoseconds  Size: " + size + " bytes";
	}

	public Format getFormat() {
		return format;
	}

	public ImageType getImageType() {
		return imageType;
	}

	public long getSize() {
		return size;
	}

	public long getTime() {
		return time;
	}
	
	public int getTrial() {
		return trial;
	}
}
