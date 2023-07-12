package com.hsm.barcode;

/**
 * Imager properties of connected engine.
 *
 */
public class ImagerProperties{
	/** Imager Properties structure containing information about connected engine */
	public ImagerProperties(){
	}
	/** Size of this structure */
	public int Size;
	/** Engine ID of connected engine */
	public int EngineID;
	/** Height of image */
	public int Rows;
	/** Width of image */
	public int Columns;
	/** Bits per pixel of image */
	public int BitsPerPixel;
	/** Image rotation value */
	public int Rotation;
	/** Aimer X coordinate offset */
	public int AimerXoffset;
	/** Aimer Y coordinate offset */
	public int AimerYoffset;
	/** Y depth of image */
	public int YDepth;
	/** Color format of image */
	public int ColorFormat;
	/** Number of buffers */
	public int NumBuffers;
	/** Major PSOC revision */
	public int PSOCMajorRev;
	/** Major PSOC revision */
	public int PSOCMinorRev;
	/** Engine serial number */
	public String EngineSerialNum;
	/** Engine Firmware ID */
	public int FirmwareEngineID;
	/** Type of aimer */
	public int AimerType;
	/** Color of aimer */
	public int AimerColor;
	/** Color of illumination */
	public int IllumColor;
	/** Optics of connected engine */
	public int Optics;
	/** Engine part number */
	public String EnginePartNum;
}
