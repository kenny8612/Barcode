package com.hsm.barcode;

/** Decode options class defining various decode settings */
public class DecodeOptions{

	/** Data structure that is used to retrieve attributes of last acquired image */
	public DecodeOptions(){
	}
	/** @deprecated Relative contrast */
	public int PrintWeight;
	/** @deprecated Decoding modes */
	public int DecodeMode;
	/** @deprecated Size of the window used in the Advanced Linear decoding mode */
	public int LinearRange;
	/** Video reverse (linear only) */
	public int VideoReverse;
	/** Amount of time decoder will spend on each image */
	public int DecAttemptLimit;
	/** @deprecated Amount of time decoder will spend searching for a barcode */
	public int SearchLimit;
	/** @deprecated Timeout setting decWaitForDecode will wait before checking callback (WinCE Only) */
	public int CallbackWaitTimeout;
	/** Maximum number of barcodes to read in a single image  */
	public int MultiReadCount;
}		
