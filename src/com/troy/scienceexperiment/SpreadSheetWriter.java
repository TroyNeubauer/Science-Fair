package com.troy.scienceexperiment;

import java.text.DecimalFormat;
import java.util.*;

import com.troy.scienceexperiment.format.Format;
import com.troyberry.util.NumberUtil;
import com.troyberry.util.StringFormatter;

import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

public class SpreadSheetWriter {
	private static int count = 0;
	private static DecimalFormat df = new DecimalFormat("#.###");
	
	public static void write(WritableWorkbook workbook, List<Data> data, DataMaster dataMaster) throws RowsExceededException, WriteException {
		WritableSheet sheet = workbook.createSheet(data.get(0).getFormat().getName().toUpperCase() + " Format", count++);
		
		int ay = 3;
		int ax = 4;
		int y = 2;
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
			
			
		} else {
			sheet.addCell(new Label(0, 0, "Raw Data"));
			sheet.addCell(new Label(0, 1, "Size (KB)"));
			sheet.addCell(new Label(1, 1, "Time (MS)"));
			long count = 0;

			sheet.addCell(new Label(ax, ay - 3, "Averaged Data"));
			sheet.addCell(new Label(ax, ay - 1, "Size (KB)"));
			sheet.addCell(new Label(ax + 1, ay - 1, "Time (MS)"));
			for(Data d : data) {
				if(d.getTrial() == 1) {
					sheet.addCell(new Label(0, y++, "Image " + StringFormatter.capitalizeFirstLetter(d.getImageType().getName())));
					sheet.addCell(new Label(ax - 1, ay, "Image " + StringFormatter.capitalizeFirstLetter(d.getImageType().getName()) + " Average" ));
				}
				count += d.getSize();
				sheet.addCell(new Number(0, y++, Double.parseDouble(StringFormatter.clip(d.getSize() / 1024.0, 3))));
				if(d.getTrial() == Main.TRIAL_COUNT) {
					sheet.addCell(new Number(ax, ay++, Double.parseDouble(StringFormatter.clip((double)count / Main.TRIAL_COUNT / 1024.0, 3))));
					count = 0;
				}
			}
			y = 2;
			count = 0;
			ay = 3;
			ax++;
			for(Data d : data) {
				if(d.getTrial() == 1) y++;
				count += d.getTime();
				sheet.addCell(new Number(1, y++, Double.parseDouble(StringFormatter.clip(d.getTime() / 1000000.0, 4))));
				if(d.getTrial() == Main.TRIAL_COUNT) {
					sheet.addCell(new Number(ax, ay++, Double.parseDouble(StringFormatter.clip(((double)count / Main.TRIAL_COUNT) / 1000000.0, 4))));
					count = 0;
				}
			}
		}
	}
	
	public static void showAverages(WritableWorkbook workbook, DataMaster data) throws RowsExceededException, WriteException {
		WritableSheet sheet = workbook.createSheet("Average", count++);
		int y = 0;
		sheet.addCell(new Label(0, 0, "Image"));
		sheet.addCell(new Label(0 + 4, 0, "Image"));
		y++;
		ImageType lastImage = null;
		Format lastFormat = null;
		double lastPercent = -1.0;
		int itCount = 0;
		double totalSize = 0, totalTime = 0;
		boolean lastLossy = false;
		for(ImageType image : data.getImages()) {
			sheet.addCell(new Label(0, y, StringFormatter.capitalizeFirstLetter(image.getName())));
			sheet.addCell(new Label(0 + 4, y, StringFormatter.capitalizeFirstLetter(image.getName())));
			sheet.addCell(new Label(1, y, "Format"));
			sheet.addCell(new Label(1 + 4, y, "Format"));
			sheet.addCell(new Label(2, y, "Size (KB)"));
			sheet.addCell(new Label(2 + 4, y, "Time (MS)"));
			y++;
			for(Format format : data.getFormats()) {
				List<DataMasterPoint> points = data.getAll(format, image);
				if(points.isEmpty()) continue;
				Collections.sort(points, peferectNodeSorter);
				for(DataMasterPoint p : points) {
					if((lastImage != p.getImage() || lastFormat != p.getFormat() || lastPercent != NumberUtil.roundOff(p.getPercent(), 1)) && (itCount != 0)) {
						if(!(lastFormat == null)){
							if(writeAverages(sheet, lastFormat.getName().toUpperCase(), totalSize, totalTime, y, lastPercent, lastLossy)) y++;
							totalSize = 0.0;
							totalTime = 0;
						}
					}
					totalSize += p.getSizeInKB();
					totalTime += p.getTimeInMS();
					itCount++;
					lastImage = p.getImage();
					lastFormat = p.getFormat();
					lastPercent = NumberUtil.roundOff(p.getPercent(), 1);
					lastLossy = p.isLossy();
				}
				if(writeAverages(sheet, lastFormat.getName().toUpperCase(), totalSize, totalTime, y, lastPercent, lastLossy)) y++;
				totalSize = 0.0;
				totalTime = 0;
				
			}

			lastImage = null;
			lastFormat = null;
		}
	}
	
	private static boolean writeAverages(WritableSheet sheet, String formatName, double totalSize, double totalTime, int y, double percent, boolean showPercent) throws RowsExceededException, WriteException {
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
		return (aSize != 0) && (aTime != 0);
		
	}
	
	private static Comparator<DataMasterPoint> peferectNodeSorter = new Comparator<DataMasterPoint>() {

		@Override
		public int compare(DataMasterPoint n0, DataMasterPoint n1) {
			return n0.compareTo(n1);
		}
	};
}
