package com.troy.scienceexperiment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Represents an image that is used
 * @author Troy Neubauer
 *
 */
public class ImageType {
	
	private BufferedImage image;
	private String name;

	public ImageType(String path) {
		this.name = path;
		String file = "origionals/" + path + ".png";
		try {
			this.image = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.err.println("Unable to load image " + file);
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(!(obj instanceof ImageType)) return false;
		return this.name.equals(((ImageType)obj).name);
	}
	
	

}
