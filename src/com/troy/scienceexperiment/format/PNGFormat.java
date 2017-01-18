package com.troy.scienceexperiment.format;

import java.util.List;

import com.troy.scienceexperiment.*;

public class PNGFormat extends Format {

	public PNGFormat(List<ImageType> images) {
		super(false, "png", images);
	}

	@Override
	public List<Data> write(DataMaster data) {
		return super.writeBasicImages(data);
	}

}
