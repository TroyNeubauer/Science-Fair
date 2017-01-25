package com.troy.scienceexperiment;

import java.util.List;

import com.troy.scienceexperiment.format.Format;

/**
 * Represents lossy data for a data point.
 * As apposed to the data class, this class has many data points in it for each compression ratio
 * @author Troy Neubauer
 *
 */
public class LossyData extends Data {
	
	private List<Float> percents;
	private List<Long> sizes, times;

	public LossyData(Format format, ImageType imageType, int trial, List<Float> percents, List<Long> sizes, List<Long> times) {
		super(format, imageType, -1, -1, trial);
		this.percents = percents;
		this.sizes = sizes;
		this.times = times;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append("Trial " + getTrial() + "\n");
		for(int i = 0; i < percents.size(); i++){
			float percent = percents.get(i).floatValue();
			sb.append("         Compression Level: " + percent);
			long time = times.get(i).longValue();
			sb.append(", Time Taken: " + time);
			long size = sizes.get(i).longValue();
			sb.append(" nanoseconds, File Size: " + size + " bytes\n");
		}
		
		return sb.toString();
	}

	protected List<Float> getPercents() {
		return percents;
	}

	protected List<Long> getSizes() {
		return sizes;
	}

	protected List<Long> getTimes() {
		return times;
	}
	
	

}
