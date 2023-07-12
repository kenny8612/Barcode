package com.hsm.barcode;

/** Image Attributes class defining image information */
public class ImageAttributes{

	/** Data structure that is used to retrieve attributes of last acquired image */
	public ImageAttributes(){
	}
	/** Size of the image */
	public int ImageSize;
	/** Exposure valid settled on for the last image capture */
	public int ExposureValue;
	/** Gain value applied on last image captured */
	public int GainValue;
	/** Illumination white value */
	public int IlluminationValue;
	/** Illumination white value max */
	public int IlluminationMaxValue;
	/** @deprecated Illumination clip value  */
	public int IlluminationClipValue;
}		
