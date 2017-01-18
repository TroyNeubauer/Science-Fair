package com.troy.scienceexperiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.troy.scienceexperiment.format.*;
import com.troyberry.util.NumberUtil;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Main {
	
	private static List<ImageType> images = new ArrayList<ImageType>();
	public static final int TRIAL_COUNT = 10;

	public static void main(String[] args) throws IOException {
		
		ImageType black = new ImageType("black");
		ImageType normal = new ImageType("normal");
		ImageType professional = new ImageType("professional");
		images.add(black);
		images.add(normal);
		//images.add(professional);
		DataMaster data = new DataMaster(TRIAL_COUNT);
		
		new PNGFormat(images).write(data);
		new JPEGFormat(images).write(data);
		new BMPFormat(images).write(data);
		new GIFFormat(images).write(data);
		
		//data.addFormats(png, jpeg, bmp, gif);
		//data.addImages(black, normal, professional);

	}

	private static void printResults(List<Data> data) {
		if(data == null || data.isEmpty())return;
		System.out.println("Results for format " + data.get(0).getFormat().getName());
		for(Data d : data){
			if(d.getTrial() == 1)System.out.println("   Image " + d.getImageType().getName());
			System.out.println("      " + d);
		}
	}

}
