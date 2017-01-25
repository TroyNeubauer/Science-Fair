package com.troy.scienceexperiment;

import java.text.DecimalFormat;
import java.util.*;

import com.troy.scienceexperiment.format.Format;
import com.troyberry.util.NumberUtil;
import com.troyberry.util.StringFormatter;

import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

/**
 * A static class for writing data to a spreadsheet
 * @author Troy Neubauer
 *
 */
public class SpreadSheetWriter {
	private static int count = 0;
	
	/**
	 * Writes all the data stashed in data master to the spreadsheet workbook
	 * @param workbook The spreadsheet to write to
	 * @param data 
	 * @param dataMaster
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void write(WritableWorkbook workbook, List<Data> data, DataMaster dataMaster) throws RowsExceededException, WriteException {
		//Create a new sheet for this format
		WritableSheet sheet = workbook.createSheet(data.get(0).getFormat().getName().toUpperCase() + " Format", count++);
		
		// Some coordinates as to what row / column is being written to
		int ay = 3;
		int ax = 4;
		int y = 2;
		//If this format is lossy
		if(data.get(0) instanceof LossyData) {
			sheet.addCell(new Label(3, 2, "Raw Data"));
			sheet.addCell(new Label(3, 3, "Size (KB)"));
			sheet.addCell(new Label(4, 3, "Time (MS)"));
			long[] sizeCount = new long[((LossyData)data.get(0)).getPercents().size()];
			long[] timeCount = new long[((LossyData)data.get(0)).getPercents().size()];
			for(Data da : data) {
				LossyData d = (LossyData)da;
				if(da.getTrial() == 1) {
					sheet.addCell(new Label(0, y++, "Image " + StringFormatter.capitalizeFirstLetter(d.getImageType().getName())));
				}
				sheet.addCell(new Label(1, y++, "Trial " + da.getTrial()));
				// Loop through each compression ratio inside each data point
				for(int i = 0; i < ((LossyData)data.get(0)).getPercents().size(); i++) {
					float percent = d.getPercents().get(i).floatValue();
					long time = d.getTimes().get(i).longValue();
					long size = d.getSizes().get(i).longValue();
					sheet.addCell(new Label(2, y, "Ratio " + percent));
					sheet.addCell(new Number(3, y, NumberUtil.truncate(size / 1024.0, 3)));
					sheet.addCell(new Number(4, y, NumberUtil.truncate(time / 1000000.0, 4)));
					sizeCount[i] += d.getSize();
					timeCount[i] += d.getSize();

					y++;
				}
				if(d.getTrial() == Main.TRIAL_COUNT) {
					
				}
			}
			
		//If this format isan't lossy
		} else {
			sheet.addCell(new Label(0, 0, "Raw Data"));
			sheet.addCell(new Label(0, 1, "Size (KB)"));
			sheet.addCell(new Label(1, 1, "Time (MS)"));
			// The running total of the size of the images (used to average)
			long count = 0;

			sheet.addCell(new Label(ax, ay - 3, "Averaged Data"));
			sheet.addCell(new Label(ax, ay - 1, "Size (KB)"));
			sheet.addCell(new Label(ax + 1, ay - 1, "Time (MS)"));
			// This for loop handles all processing of the data points for the size
			for(Data d : data) {
				// If this is the first trial, The image has changed so we need to rewrite what image is being used to the spreadsheet
				if(d.getTrial() == 1) {
					sheet.addCell(new Label(0, y++, "Image " + StringFormatter.capitalizeFirstLetter(d.getImageType().getName())));
					sheet.addCell(new Label(ax - 1, ay, "Image " + StringFormatter.capitalizeFirstLetter(d.getImageType().getName()) + " Average" ));
				}
				count += d.getSize();
				//Write the size of this data point to the spreadsheet
				sheet.addCell(new Number(0, y++, Double.parseDouble(StringFormatter.clip(d.getSize() / 1024.0, 3))));
				//If this is the last trial, we need to write the average to the averages column in the spreadsheet
				if(d.getTrial() == Main.TRIAL_COUNT) {
					sheet.addCell(new Number(ax, ay++, Double.parseDouble(StringFormatter.clip((double)count / Main.TRIAL_COUNT / 1024.0, 3))));
					count = 0;
				}
			}
			y = 2;
			count = 0;
			ay = 3;
			ax++;
			// This for loop handles all processing of the data points for the time taken to export
			for(Data d : data) {
				//If this is the first trial, move the pointer down
				if(d.getTrial() == 1) y++;
				count += d.getTime();
				sheet.addCell(new Number(1, y++, Double.parseDouble(StringFormatter.clip(d.getTime() / 1000000.0, 4))));
				//If this is the last trial, we need to write the average to the averages column in the spreadsheet
				if(d.getTrial() == Main.TRIAL_COUNT) {
					sheet.addCell(new Number(ax, ay++, Double.parseDouble(StringFormatter.clip(((double)count / Main.TRIAL_COUNT) / 1000000.0, 4))));
					count = 0;
				}
			}
		}
	}
	/**
	 * This method uses all the data and writes the averages to another "Averages" sheet in the spreadsheet
	 * @param workbook The excel file to write to
	 * @param data The data
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void showAverages(WritableWorkbook workbook, DataMaster data) throws RowsExceededException, WriteException {
		WritableSheet sheet = workbook.createSheet("Average", count++);
		int y = 0;
		//Add some column headers to the new sheet
		sheet.addCell(new Label(0, 0, "Image"));
		sheet.addCell(new Label(0 + 4, 0, "Image"));
		y++;
		ImageType lastImage = null;
		Format lastFormat = null;
		double lastPercent = -1.0;
		int itCount = 0;
		double totalSize = 0, totalTime = 0;
		boolean lastLossy = false;
		//Loop through all the images tested
		for(ImageType image : data.getImages()) {
			//Setup some column headers so we know what the data in the sreadsheet means
			sheet.addCell(new Label(0, y, StringFormatter.capitalizeFirstLetter(image.getName())));
			sheet.addCell(new Label(0 + 4, y, StringFormatter.capitalizeFirstLetter(image.getName())));
			sheet.addCell(new Label(1, y, "Format"));
			sheet.addCell(new Label(1 + 4, y, "Format"));
			sheet.addCell(new Label(2, y, "Size (KB)"));
			sheet.addCell(new Label(2 + 4, y, "Time (MS)"));
			y++;
			// Loop through all the formats tested for each image
			for(Format format : data.getFormats()) {
				// Get all data points with this image format / image combo
				List<DataMasterPoint> points = data.getAll(format, image);
				if(points.isEmpty()) continue;
				// Sort the data to be in numerical order
				Collections.sort(points, peferectNodeSorter);
				// Loop through all data points in this image format / image combo
				for(DataMasterPoint p : points) {
					// If we are using the same format / image combo as last time...
					if((lastImage != p.getImage() || lastFormat != p.getFormat() || lastPercent != NumberUtil.roundOff(p.getPercent(), 1)) && (itCount != 0)) {
						if(!(lastFormat == null)){
							if(writeAverages(sheet, lastFormat.getName().toUpperCase(), totalSize, totalTime, y, lastPercent, lastLossy)) y++;//Poor solution to a bug caused by the averages not resetting and the pointer not moving
							totalSize = 0.0;
							totalTime = 0;
						}
					}
					// Add to the averages
					totalSize += p.getSizeInKB();
					totalTime += p.getTimeInMS();
					itCount++;
					// Reset what the parameters were last time because we are about to loop again...
					lastImage = p.getImage();
					lastFormat = p.getFormat();
					lastPercent = NumberUtil.roundOff(p.getPercent(), 1);// Round the % to 1 decimal place because 0.8 sometimes goes to 0.799999 because of floating point storage issues
					lastLossy = p.isLossy();
				}
				//Write the averages from the last iteration of the loop
				if(writeAverages(sheet, lastFormat.getName().toUpperCase(), totalSize, totalTime, y, lastPercent, lastLossy)) y++;//Poor solution to a bug caused by the averages not resetting and the pointer not moving
				totalSize = 0.0;
				totalTime = 0;
				
			}

			lastImage = null;
			lastFormat = null;
		}
	}
	
	/**
	 * Writes the averages for some data points to the spreadsheet
	 * @param sheet The sheet to write to
	 * @param formatName
	 * @param totalSize
	 * @param totalTime
	 * @param y CUrrent y pointer
	 * @param percent
	 * @param showPercent
	 * @return returns a boolean because I had a bug. Poor solution to a bug caused by the averages not resetting and the pointer not moving
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private static boolean writeAverages(WritableSheet sheet, String formatName, double totalSize, double totalTime, int y, double percent, boolean showPercent) throws RowsExceededException, WriteException {
		//Averaged size
		double aSize = (totalSize / Main.TRIAL_COUNT);
		double aTime = (totalTime / Main.TRIAL_COUNT);
		sheet.addCell(new Number(2, y, aSize));
		sheet.addCell(new Number(2 + 4, y, aTime));
		if(showPercent) {
			sheet.addCell(new Label(1, y, formatName + " "+ (int)(percent * 100) + "%"));
			sheet.addCell(new Label(1 + 4, y, formatName + " " + (int)(percent * 100) + "%"));
		} else {
			sheet.addCell(new Label(1, y, formatName));
			sheet.addCell(new Label(1 + 4, y, formatName));
		}
		return (aSize != 0) && (aTime != 0);//Poor solution to a bug caused by the averages not resetting and the pointer not moving
		
	}
	
	private static Comparator<DataMasterPoint> peferectNodeSorter = new Comparator<DataMasterPoint>() {

		@Override
		public int compare(DataMasterPoint n0, DataMasterPoint n1) {
			return n0.compareTo(n1);
		}
	};
}
