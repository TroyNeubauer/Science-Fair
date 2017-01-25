package com.troy.scienceexperiment;

import com.troy.scienceexperiment.format.Format;
import com.troyberry.util.NumberUtil;

/**
 * Represents a single data point in dataMaster (different than the data class)
 * @author Troy Neubauer
 *
 */
public class DataMasterPoint implements Comparable {
	
	private double timeInMS, sizeInKB, percent;
	private ImageType image;
	private Format format;
	private boolean lossy;

	public DataMasterPoint(ImageType image, Format format, double timeInMS, double sizeInKB, double percent, boolean lossy) {
		this.image = image;
		this.format = format;
		this.timeInMS = timeInMS;
		this.sizeInKB = sizeInKB;
		this.percent = percent;
		this.lossy = lossy;
	}

	protected double getTimeInMS() {
		return timeInMS;
	}

	protected double getSizeInKB() {
		return sizeInKB;
	}

	protected double getPercent() {
		return percent;
	}

	protected ImageType getImage() {
		return image;
	}

	protected Format getFormat() {
		return format;
	}

	@Override
	public int compareTo(Object o) {
		DataMasterPoint point = (DataMasterPoint)o;// Kinda unsafe but as long as we don't so something like this.compareTo(new String()) we *should* be fine
		if(point.percent < this.percent) {
			return +1;
		} else if(point.percent > this.percent) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return image.getName() + " of format " + format.getName().toUpperCase() + ", " + sizeInKB + " KB " + timeInMS + " MS " + NumberUtil.roundOff(percent, 1) + "%";
	}

	public boolean isLossy() {
		return lossy;
	}
}
