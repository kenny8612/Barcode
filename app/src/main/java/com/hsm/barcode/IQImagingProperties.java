package com.hsm.barcode;

/**
 * IQ Imaging Class
 *
 */
public class IQImagingProperties{
	/** Class for IQ Imaging Structure that defines the parameters used during 
	 * the attempt to acquire an Intelligent Image */
	public IQImagingProperties(){
	}
	/** Ratio of barcode height to narrow element width (Practical range 1 to 1000) */
	public int AspectRatio;
	/** Offset in X direction, relative to barcode center (Practical range -500 to 500) */
	public int X_Offset;
	/** Offset in Y direction, relative to barcode center (Practical range -500 to 500) */
	public int Y_Offset;
	/** Width of image in IntellBarcodeUnits (Practical range -500 to 500) */
	public int Width;
	/** Height of image in IntellBarcodeUnits (Practical range -500 to 500) */
	public int Height;
	/** # pixels/IntellBarcodeUnits (Practical range 1 to 100) */
	public int Resolution;
	/** IQ Image Format */
	public int Format;
	/** Must be set to -1 */
	public int Reserved;

	/**
	 * Defines the supported image formats for Intelligent Imaging
	 *
	 */
	public final static class IQImageFormat{
		private IQImageFormat(){
		}
		/** Raw Binary Image Format */
		public static final int RAW_BINARY = 0;
		/** Raw Gray Image Format */
		public static final int RAW_GRAY = 1;
	}
}
