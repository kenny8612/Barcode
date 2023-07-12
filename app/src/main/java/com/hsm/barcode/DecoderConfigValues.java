package com.hsm.barcode;

/**
 * These are the various classes used for configuring the decoder functionality
 */
public final class DecoderConfigValues {
	private DecoderConfigValues(){
	}

	/**
	 * Symbology ID class that defines the supported symbology IDs.
	 *
	 */
	public final static class SymbologyID{
		private SymbologyID(){
		}
		/** Aztec Code */
		public static final int SYM_AZTEC = 0;
		/** Codabar */
		public static final int SYM_CODABAR = 1;
		/** Code 11 */
		public static final int SYM_CODE11 = 2;
		/** Code 128 */
		public static final int SYM_CODE128 = 3;
		/** Code 39 */
		public static final int SYM_CODE39 = 4;
		/** @deprecated Code49 */
		public static final int SYM_CODE49 = 5;
		/** Code 93 */
		public static final int SYM_CODE93 = 6;
		/** EAN/UCC Composite */
		public static final int SYM_COMPOSITE = 7;
		/** Data Matrix */
		public static final int SYM_DATAMATRIX = 8;
		/** EAN-8 */
		public static final int SYM_EAN8 = 9;
		/** EAN-13 */
		public static final int SYM_EAN13 = 10;
		/** Interleaved 2 of 5*/
		public static final int SYM_INT25 = 11;
		/** Maxicode */
		public static final int SYM_MAXICODE = 12;
		/** Micro PDF */
		public static final int SYM_MICROPDF = 13;
		/** @deprecated OCR */
		public static final int SYM_OCR = 14;
		/** PDF417 */
		public static final int SYM_PDF417 = 15;
		/** @deprecated Postnet */
		public static final int SYM_POSTNET = 16;
		/** QR Code */
		public static final int SYM_QR = 17;
		/** Reduced Space Symbology (RSS-14, RSS Limited, RSS Expanded) */
		public static final int SYM_RSS = 18;
		/** UPC-A */
		public static final int SYM_UPCA = 19;
		/** UPC-E0 */
		public static final int SYM_UPCE0 = 20;
		/** UPC-E1 */
		public static final int SYM_UPCE1 = 21;
		/** ISBT 128*/
		public static final int SYM_ISBT = 22;
		/** British Post [2D Postal] */
		public static final int SYM_BPO = 23;
		/** Canadian Post [2D Postal] */
		public static final int SYM_CANPOST =24;
		/** Australian Post [2D Postal] */
		public static final int SYM_AUSPOST = 25;
		/** Straight 2 of 5 IATA (two-bar start/stop) */
		public static final int SYM_IATA25 = 26;
		/** Codablock F */
		public static final int SYM_CODABLOCK = 27;
		/** Japanese Post [2D Postal] */
		public static final int SYM_JAPOST = 28;
		/** Planet Code [2D Postal] */
		public static final int SYM_PLANET = 29;
		/** KIX (Netherlands) Post [2D Postal] */
		public static final int SYM_DUTCHPOST = 30;
		/** MSI */
		public static final int SYM_MSI = 31;
		/** TCIF Linked Code 39 (TLC39) */
		public static final int SYM_TLCODE39 = 32;
		/** Trioptic Code */
		public static final int SYM_TRIOPTIC = 33;
		/** Code 32 Italian Pharmacy Code*/
		public static final int SYM_CODE32 = 34;
		/** Straight 2 of 5 Industrial (three-bar start/stop) */
		public static final int SYM_STRT25 = 35;
		/** Matrix 2 of 5 */
		public static final int SYM_MATRIX25 = 36;
		/** @deprecated Plessey Code */
		public static final int SYM_PLESSEY = 37;
		/** China Post */
		public static final int SYM_CHINAPOST = 38;
		/** Korean Post */
		public static final int SYM_KOREAPOST = 39;
		/** Telepen */
		public static final int SYM_TELEPEN = 40;
		/** @deprecated Code 16K */
		public static final int SYM_CODE16K = 41;
		/** @deprecated Posicode */
		public static final int SYM_POSICODE = 42;
		/** Coupon Code */
		public static final int SYM_COUPONCODE = 43;
		/** USPS 4 State (Intelligent Mail Barcode) [2D Postal]*/
		public static final int SYM_USPS4CB = 44;
		/** UPU 4 State [2D Postal] */
		public static final int SYM_IDTAG = 45;
		/** @deprecated Label */
		public static final int SYM_LABEL = 46;
		/** GS1 128 */
		public static final int SYM_GS1_128 = 47;
		/** Hanxin */
		public static final int SYM_HANXIN = 48;
		/** Grid Matrix */
		public static final int SYM_GRIDMATRIX = 49;
		/** Used to default and disable postal codes */
		public static final int SYM_POSTALS = 50;
		/** Used to enable SYM_PLANET, SYM_POSTNET, SYM_USPS4CB & SYM_IDTAG */
		public static final int SYM_US_POSTALS1 = 51;
		/** DotCode */
		public static final int SYM_DOTCODE = 52;
		/** Digimarc */
	    public static final int SYM_DIGIMARC = 53;
		/** DPM */
		public static final int SYM_DPM = 54;
		/** Number of Symbologies */
		public static final int SYMBOLOGIES = 55;
		/** All Symbologies */
		public static final int SYM_ALL = 100;
	}

	/**
	 * Symbology Flags class that defines the flags used by the SymbologyConfig class
	 *
	 */
	public final class SymbologyFlags {
		private SymbologyFlags(){
		}

		// Flags for use by SymbologyConfig class
		/** Enable Symbology bit */
		public static final int SYMBOLOGY_ENABLE                = 0x00000001;
		/** Enable usage of check character */
		public static final int SYMBOLOGY_CHECK_ENABLE          = 0x00000002;
		/** Send check character */
		public static final int SYMBOLOGY_CHECK_TRANSMIT        = 0x00000004;
		/** Include the start and stop characters in the decoded result string */
		public static final int SYMBOLOGY_START_STOP_XMIT       = 0x00000008;
		/** @deprecated Code39 append mode */
		public static final int SYMBOLOGY_ENABLE_APPEND_MODE    = 0x00000010;
		/** Enable Code39 Full ASCII */
		public static final int SYMBOLOGY_ENABLE_FULLASCII      = 0x00000020;
		/** UPC-A/UPC-e Send Num Sys */
		public static final int SYMBOLOGY_NUM_SYS_TRANSMIT      = 0x00000040;
		/** Enable 2 digit Addenda (UPC & EAN) */
		public static final int SYMBOLOGY_2_DIGIT_ADDENDA       = 0x00000080;
		/** Enable 5 digit Addenda (UPC & EAN) */
		public static final int SYMBOLOGY_5_DIGIT_ADDENDA       = 0x00000100;
		/** Only allow codes with addenda (UPC & EAN) */
		public static final int SYMBOLOGY_ADDENDA_REQUIRED      = 0x00000200;
		/** Include Addenda separator space in returned string */
		public static final int SYMBOLOGY_ADDENDA_SEPARATOR     = 0x00000400;

		/** Translate UPCA to EAN13 if EAN 13 and UPC are enabled */
		public static final int SYMBOLOGY_UPCA_TRANSLATE_TO_EAN13 =	0x00200000;

		/** Extended UPC-E */
		public static final int SYMBOLOGY_EXPANDED_UPCE         = 0x00000800;
		/** UPC-E1 enable (use SYMBOLOGY_ENABLE for UPC-E0) */
		public static final int SYMBOLOGY_UPCE1_ENABLE          = 0x00001000;
		/** Enable UPC composite codes */
		public static final int SYMBOLOGY_COMPOSITE_UPC         = 0x00002000;
		/** Include australian postal bar data in string */
		public static final int SYMBOLOGY_AUSTRALIAN_BAR_WIDTH  = 0x00010000;
		/** @deprecated Enable OR dISBALE Code 128 FNC2 append functionality */
		public static final int SYMBOLOGY_128_APPEND			= 0x00080000;
		/** Enable RSS Expanded bit */
		public static final int SYMBOLOGY_RSE_ENABLE            = 0x00800000;
		/** Enable RSS Limited bit */
		public static final int SYMBOLOGY_RSL_ENABLE            = 0x01000000;
		/** Enable RSS bit */
		public static final int SYMBOLOGY_RSS_ENABLE            = 0x02000000;
		/** Enable all RSS versions */
		public static final int SYMBOLOGY_RSX_ENABLE_MASK       = 0x03800000;
		/** Telepen Old Style mode */
		public static final int SYMBOLOGY_TELEPEN_OLD_STYLE     = 0x04000000;
		/** @deprecated PosiCode Limited of 1 */
		public static final int SYMBOLOGY_POSICODE_LIMITED_1    = 0x08000000;
		/** @deprecated PosiCode Limited of 2 */
		public static final int SYMBOLOGY_POSICODE_LIMITED_2    = 0x10000000;
		/** Codabar concatenate. */
		public static final int SYMBOLOGY_CODABAR_CONCATENATE   = 0x20000000;

		// Australian Post Customer Interpret Mode (Note: ONLY one configuration can be set)
		/** Numeric N Table */
		public static final int SYMBOLOGY_AUS_POST_NUMERIC_N_TABLE				= 0x00100000;
		/** Alphanumeric C Table */
		public static final int SYMBOLOGY_AUS_POST_ALPHANUMERIC_C_TABLE			= 0x00200000;
		/** Combination N and C Tables */
		public static final int SYMBOLOGY_AUS_POST_COMBINATION_N_AND_C_TABLES	= 0x00400000;

		/** Digimarc Compatible mode */
		public static final int SYMBOLOGY_DIGIMARC_COMPATIBLE   = 0x00000000;
		/** Digimarc Enhanced mode */
		public static final int SYMBOLOGY_DIGIMARC_ENHANCED     = 0x01000000;
		/** Digimarc Native mode */
		public static final int SYMBOLOGY_DIGIMARC_NATIVE       = 0x02000000;
		/** Digimarc Payload mode */
		public static final int SYMBOLOGY_DIGIMARC_PAYLOAD      = 0x03000000;
		/** Digimarc mode mask */
		public static final int SYMBOLOGY_DIGIMARC_MODE         = 0x03000000;

		//SymbologyConfig class masks
		/** Flags are valid */
		public static final int SYM_MASK_FLAGS                  = 0x00000001;
		/** Min Length valid */
		public static final int SYM_MASK_MIN_LEN                = 0x00000002;
		/** Max Length valid */
		public static final int SYM_MASK_MAX_LEN                = 0x00000004;
		/** All fields valid */
		public static final int SYM_MASK_ALL                    = 0x00000007;

	}

	/**
	 * Engine ID class that defines the engine ID.
	 *
	 */
	public final static class EngineID{
		private EngineID(){
		}
		/** Unknown Engine ID */
		public static final int UNKNOWN	= -1;
		/** No Engine ID */
		public static final int NONE=0;
		/** @deprecated 4200 Engine ID */
		public static final int IMAGER_4200_ENGINE=1;
		/** @deprecated SE1200 Laser Engine ID */
		public static final int LASER_SE1200_ENGINE=2;
		/** @deprecated SE1223 Laser Engine ID */
		public static final int LASER_SE1223_ENGINE=3;
		/** @deprecated IT4000 Engine ID */
		public static final int IMAGER_IT4000_ENGINE=5;
		/** @deprecated IT4100 Engine ID */
		public static final int IMAGER_IT4100_ENGINE=6;
		/** @deprecated IT4300 Engine ID */
		public static final int IMAGER_IT4300_ENGINE=7;
		/** IT5100 Engine ID */
		public static final int IMAGER_IT5100_ENGINE=8;
		/** IT5300 Engine ID */
		public static final int IMAGER_IT5300_ENGINE=9;
		/** N5603 Engine ID */
		public static final int IMAGER_N5603_ENGINE=12;
		/** N5600 Engine ID */
		public static final int IMAGER_N5600_ENGINE=13;
		/** N3601 Engine ID */
		public static final int IMAGER_N3601_ENGINE=16;
		/** N6700 Engine ID */
		public static final int IMAGER_N6700_ENGINE=17;
		/** N6703 Engine ID */
		public static final int IMAGER_N6703_ENGINE=18;
		/** EX30 Engine ID */
		public static final int IMAGER_EX30_ENGINE=19;
		/** N3606 Engine ID */
		public static final int IMAGER_N3603_ENGINE=20;
		/** N4603 Engine ID */
		public static final int IMAGER_N4603_ENGINE=21;
		/** N5703 Engine ID */
		public static final int IMAGER_N5703_ENGINE=22;
		/** FRESNEL Engine ID */
		public static final int IMAGER_FRESNEL_ENGINE=23;
	}

	/**
	 * Engine Type class that defines the engine type
	 *
	 */
	public final static class EngineType{
		private EngineType(){
		}
		/** Unknown Engine Type */
		public static final int UNKNOWN	= -1;
		/** No Engine Type */
		public static final int NONE = 0;
		/** Imager Engine Type */
		public static final int IMAGER = 1;
		/** Laser Engine Type */
		public static final int LASER = 2;
	}

	/**
	 * OCR Mode class that defines the mode settings
	 *
	 */
	public final static class OCRMode{
		private OCRMode(){
		}
		/** OCR Disabled */
		public static final int OCR_OFF = 0;
		/** OCR Normal Video (black high) */
		public static final int OCR_NORMAL_VIDEO =1;
		/** OCR Inverse Video (white high) */
		public static final int OCR_INVERSE = 2;
		/** OCR Both Video (white & black high) */
		public static final int OCR_BOTH = 3;
	}

	/**
	 * OCR Template class that defines the OCR predefined templates
	 *
	 */
	public final static class OCRTemplate{
		private OCRTemplate(){
		}
		/** OCR User Template */
		public static final int USER = 0x0001;
		/** OCR Passport Template */
		public static final int PASSPORT = 0x0002;
		/** OCR ISBN Template */
		public static final int ISBN = 0x0004;
		/** OCR Price Field Template */
		public static final int PRICE_FIELD = 0x0008;
		/** OCR Micre13B Template */
		public static final int MICRE13B = 0x0010;
	}


	public final static class DPMMode{
		private DPMMode(){
		}
		/** DPM Disabled */
		public static final int DPM_OFF = 0;

		public static final int DPM_DOTPEEN =1;

		public static final int DPM_REFLECTIVE = 2;

	}

	public final static class DPMSIZE{
		private DPMSIZE(){
		}
		/** DPM Reflective size */
		public static final int DPM_NORMAL = 0;

		public static final int DPM_SMALL =1;

	}

	/**
	 * Lights Mode class that defines the lights mode settings
	 *
	 */
	public final static class LightsMode{
		private LightsMode(){
		}
		/** Neither aimer or illumination */
		public static final int ILLUM_AIM_OFF = 0;
		/** Aimer only */
		public static final int AIMER_ONLY = 1;
		/** Illumination only */
		public static final int ILLUM_ONLY = 2;
		/** Aimer and illumination alternating */
		public static final int ILLUM_AIM_ON = 3;
		/** Both aimer and illumination */
		public static final int CONCURRENT = 4;
	}








}
