package com.troy.scienceexperiment.format;

import java.util.List;

import com.troy.scienceexperiment.*;

public class BMPFormat extends Format {

	public BMPFormat(List<ImageType> images) {
		super(false, "bmp", images);
	}

	@Override
	public List<Data> write(DataMaster data) {
		return super.writeBasicImages(data);
	}

}
