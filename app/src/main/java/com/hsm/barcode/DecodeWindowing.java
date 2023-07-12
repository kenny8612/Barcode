package com.hsm.barcode;

/**
 * Used for configuring a Decode Window
 *
 */
public class DecodeWindowing{
	public DecodeWindowing(){
	}

	/**
	 * Decode Window coordinate settings.  Rectangular image 
	 * region of which at least part of the decoded symbol must overlap to 
	 * be considered a valid decode (excluding nMode = 3, which means that 
	 * the entire barcode must be contained within the window)
	 *
	 */
	public static final class DecodeWindow{
		public DecodeWindow(){
		}
		/** Upper left X coordinate setting */
		public int  UpperLeftX;
		/** Upper left Y coordinate setting */
		public int  UpperLeftY;
		/** Lower right X coordinate setting */
		public int 	LowerRightX;
		/** Lower right Y coordinate setting */
		public int  LowerRightY;
	}

	/**
	 * Decode Window Min/Max Limits that are allowed to be 
	 * specified. 
	 *
	 */
	public final static class DecodeWindowLimits{
		public DecodeWindowLimits(){
		}
		/** Upper left X coordinate minimum value */
		public int UpperLeft_X_Min;
		/** Upper left X coordinate maximum value */
		public int UpperLeft_X_Max;
		/** Upper left Y coordinate minimum value */
		public int UpperLeft_Y_Min;
		/** Upper left Y coordinate maximum value */
		public int UpperLeft_Y_Max;
		/** Lower right X coordinate minimum value */
		public int LowerRight_X_Min;
		/** Lower right X coordinate maximum value */
		public int LowerRight_X_Max;
		/** Lower right Y coordinate minimum value */
		public int LowerRight_Y_Min;
		/** Lower right Y coordinate maximum value */
		public int LowerRight_Y_Max;
	}

	/**
	 * Decode Window Mode setting.  Limits that are allowed to be 
	 * specified. 
	 *
	 */
	public final static class DecodeWindowMode{
		private DecodeWindowMode(){
		}
		/** Disables Decode Window */
		public static final int DECODE_WINDOW_MODE_DISABLED = 0;
		/** Window around aimer (center of image must be covered, since the 
		 * aimer coordinates offset the window from center) */
		public static final int DECODE_WINDOW_MODE_AIMER  = 1;
		/** Window as defined in field of view */
		public static final int DECODE_WINDOW_MODE_FIELD_OF_VIEW = 2;
		/** Sub-image or window as defined in field of view (barcode must be
		 * decodable within entire window) */
		public static final int DECODE_WINDOW_MODE_SUB_IMAGE = 3;
		/** Window around aimer, similar as mode 1. But no need to set the REC. Decoder
		 * will search aimer offset by itself.*/
		public static final int DECODE_WINDOW_MODE_EXACT_AIMER = 4;
	}

	/**
	 * Show Decode Window setting to ensure the appropriate 
	 * window is being set for application (for debug purposes).  Enable this 
	 * setting to draw a window around the configured DecodeWindow.
	 *
	 * Settings:
	 *
	 * 	0 = disabled
	 *
	 * 	1 = white window will be drawn
	 *
	 * 	2 = black window will be drawn
	 *
	 *
	 */
	public final static class DecodeWindowShowWindow{
		private DecodeWindowShowWindow(){
		}
		/** Disables show window, window will not be drawn */
		public static final int DECODE_WINDOW_SHOW_WINDOW_DISABLED = 0;
		/** White window will be drawn (i.e. can be used for dark images) */
		public static final int DECODE_WINDOW_SHOW_WINDOW_WHITE  = 1;
		/** Black window will be drawn (i.e. can be used for bright images) */
		public static final int DECODE_WINDOW_SHOW_WINDOW_BLACK = 2;
	}
}