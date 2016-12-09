package com.troy.scienceexperiment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.troy.troyberry.logging.Timer;

public class Main {
	
	private static String[] IMAGE_FORMATS = new String[]{"jpg", "gif", "bmp", "png"};
	private static String[] BASE_IMAGE_FILES = new String[]{"black"};
	private static BufferedImage[] images;

	private static File blackImageFile = new File("./origionals/black.png");

	public static void main(String[] args) {
		images = new BufferedImage[BASE_IMAGE_FILES.length];
		for(int i = 0; i < BASE_IMAGE_FILES.length; i++){
			String image = BASE_IMAGE_FILES[i];
			try {
				images[i] = ImageIO.read(new File("./origionals/" + image + ".png"));
			} catch (IOException e) {
				System.err.println("Unable to read image " + image);
			}
		}
	
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		for(int i = 0; i < images.length; i++){
			System.out.println("\n\n\n############### Starting experiment for base image " + BASE_IMAGE_FILES[i] + " ###############");
			writeImages(images[i], IMAGE_FORMATS);
		}

	}
	
	private static void writeImages(BufferedImage origionalImage, String... formats) {
		long startTime = 0L, endTime = 0L;
		for (int trials = -5; trials < 10; trials++) {
			System.out.println("\n\nTrial " + (trials + 1) + "\n");
			for (int i = 0; i < formats.length; i++) {
				String format = formats[i];
				
				try {
					startTime = System.nanoTime();
					ImageIO.write(origionalImage, format, new File(format + "." + format));
				} catch (IOException e) {
					e.printStackTrace();
				}
				endTime = System.nanoTime();
				double millis = (double)(endTime - startTime)/1000000.0;
				System.out.println("format " + format + " took " + millis);
			}

			for (int i = 0; i < formats.length; i++) {
				String format = formats[i];
				File file = new File(format + "." + format);
				System.out.println("File format " + format + " uses " + file.length() + " bytes to be stored");
				file.delete();
			}
		}
	}

}
