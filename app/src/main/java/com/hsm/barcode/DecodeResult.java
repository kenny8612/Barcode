package com.hsm.barcode;

/**
 * Used to store decoded results data
 *
 */
public class DecodeResult {
	public DecodeResult(){
	}
	/** This variable will contain the barcode data as a string type */
	public String barcodeData;
	/** This variable will contain the Honeywell Code ID for the decoded symbology */
	public byte codeId;
	/** This variable will contain the AIM ID for the decoded symbology */
	public byte aimId;
	/** This variable will contain the code modifier for the decoded symbology */
	public byte aimModifier;
	/** This variable will contain the length of the barcode data that was decoded */
	public int length;
	/** This variable will contain the barcode data as a byte[] type */
	public byte[] byteBarcodeData;
}
