package com.hsm.barcode;

/**
 * Symbology configuration for setting symbology settings.
 *
 */
public class SymbologyConfig{
	/** Symbology Config structure used for configuring symbologies. */
	public SymbologyConfig(int symbologyID){
		this.symID = symbologyID;
	}
	/** Symbology ID */
	public int symID;
	/** Logical OR of valid masks: SYM_MASK_FLAGS, SYM_MASK_MIN_LEN, SYM_MASK_MAX_LEN */
	public int Mask;
	/** Logical OR of valid flags for the given symbology */
	public int Flags;
	/** Minimum length for valid barcode string for this symbology */
	public int MinLength;
	/** Maximum length for valid barcode string for this symbology */
	public int MaxLength;
}


