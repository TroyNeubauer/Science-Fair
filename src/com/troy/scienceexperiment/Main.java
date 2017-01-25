package com.troy.scienceexperiment;

import java.io.File;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import com.troy.scienceexperiment.format.*;
import com.troyberry.util.NumberUtil;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {
	
	private static List<ImageType> images = new ArrayList<ImageType>();
	public static final int TRIAL_COUNT = 10;

	/**
	 * Entry point of the program
	 */
	public static void main(String[] args) throws IOException, RowsExceededException, WriteException {// Bad to throw exceptions out of main but I am pressed for time
		WritableWorkbook book = Workbook.createWorkbook(new File("output.xls"));
		//Add the images to use
		ImageType black = new ImageType("black");
		ImageType normal = new ImageType("normal");
		ImageType professional = new ImageType("professional");
		images.add(black);
		images.add(normal);
		images.add(professional);
		DataMaster data = new DataMaster();
		
		//Create all the formats to use
		PNGFormat png = new PNGFormat(images);
		JPEGFormat jpeg = new JPEGFormat(images);
		BMPFormat bmp = new BMPFormat(images);
		GIFFormat gif = new GIFFormat(images);
		data.addFormats(png, jpeg, bmp, gif);
		data.addImages(black, normal, professional);
		
		
		SpreadSheetWriter.write(book, png.write(data), data);
		SpreadSheetWriter.write(book, jpeg.write(data), data);
		SpreadSheetWriter.write(book, bmp.write(data), data);
		SpreadSheetWriter.write(book, gif.write(data), data);
		SpreadSheetWriter.showAverages(book, data);
		book.write();
		book.close();

	}
	
	/**
	 * Prints out the data in a list
	 * @param data The data to print
	 */
	private static void printResults(List<Data> data) {
		if(data == null || data.isEmpty())return;
		System.out.println("Results for format " + data.get(0).getFormat().getName());
		for(Data d : data){
			if(d.getTrial() == 1)System.out.println("   Image " + d.getImageType().getName());
			System.out.println("      " + d);
		}
	}

}
