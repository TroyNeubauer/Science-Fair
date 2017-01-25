package com.troy.scienceexperiment;

import java.util.ArrayList;
import java.util.List;

import com.troy.scienceexperiment.format.Format;

/**
 * A class to stash data about each test
 * @author Troy Neubauer
 *
 */
public class DataMaster {
	private List<Format> formats;
	private List<ImageType> images;
	private List<DataMasterPoint> points;

	public DataMaster() {
		formats = new ArrayList<Format>();
		images = new ArrayList<ImageType>();
		points = new ArrayList<DataMasterPoint>();
	}
	
	public void addFormats(Format... formatsToAdd) {
		for(Format f : formatsToAdd) {
			formats.add(f);
		}
	}
	
	public void addImages(ImageType... imagesToAdd) {
		for(ImageType f : imagesToAdd){
			images.add(f);
		}
	}
	
	public List<DataMasterPoint> getAll(Format format, ImageType image) {
		List<DataMasterPoint> result = new ArrayList<DataMasterPoint>();
		for(DataMasterPoint point : points){
			if(point.getImage().equals(image) && point.getFormat().equals(format)) {
				result.add(point);
			}
		}
		return result;
	}
	
	public List<DataMasterPoint> getAll(Format format, ImageType image, float percent) {
		List<DataMasterPoint> result = new ArrayList<DataMasterPoint>();
		for(DataMasterPoint point : points){
			if(point.getImage().equals(image) && point.getFormat().equals(format) && point.getPercent() == percent) {
				result.add(point);
			}
		}
		
		return result;
	}
	
	public void add(DataMasterPoint point) {
		points.add(point);
	}
	
	public List<ImageType> getImages() {
		return images;
	}
	
	public List<Format> getFormats() {
		return formats;
	}
}
