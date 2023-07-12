package com.hsm.barcode;

/**
 * Decode Exception error code.
 */
public class DecoderException extends Exception {

	private static final long serialVersionUID = 1L;
	private int errorCode;

	/**
	 * Exception thrown by the API when an error occurs.
	 *
	 * @param code - integer value of the error code (ResultID)
	 * @param message - message associated with the error code
	 */
	public DecoderException (int code, String message) {
		super (message);
		this.errorCode = code;
	}

	/**
	 * Returns the error code set when an exception occurs.
	 *
	 * @return integer value of error code (ResultID)
	 */
	public int getErrorCode(){
		return this.errorCode;
	}

	/**
	 * Exception result codes for all functions.  Note: RESULT_SUCCESS will not
	 * throw an exception, but all other errors will.
	 *
	 */
	public final static class ResultID{
		private ResultID(){
		}
		/** Initialize */
		public static final int RESULT_INITIALIZE = -1;
		/** Operation was successful */
		public static final int RESULT_SUCCESS  = 0;
		/** An image was requested using an invalid image region */
		public static final int RESULT_ERR_BADREGION = 1;
		/** Error detected in image engine driver */
		public static final int RESULT_ERR_DRIVER = 2;
		/** Image engine driver reported busy */
		public static final int RESULT_ERR_ENGINEBUSY = 3;
		/** Memory allocation failed */
		public static final int RESULT_ERR_MEMORY = 4;
		/** Image engine unable to decode a symbology */
		public static final int RESULT_ERR_NODECODE = 5;
		/** No image available */
		public static final int RESULT_ERR_NOIMAGE = 6;
		/** Could not communicate with imager */
		public static final int RESULT_ERR_NORESPONSE = 7;
		/** Not connected to image engine */
		public static final int RESULT_ERR_NOTCONNECTED = 8;
		/** One of the function parameters was invalid */
		public static final int RESULT_ERR_PARAMETER = 9;
		/** The operation was not supported by the engine */
		public static final int RESULT_ERR_UNSUPPORTED = 10;
		/** Trigger state is false */
		public static final int RESULT_ERR_NOTRIGGER = 11;
		/** IQ image fail */
		public static final int RESULT_ERR_BADSMARTIMAGE = 12;
		/** Requested IQ image too large */
		public static final int RESULT_ERR_SMARTIMAGETOOLARGE = 13;
		/** IQ image fail */
		public static final int RESULT_ERR_TOO_MUCH_INTERPOLATION = 14;
		/** Invalid structure size */
		public static final int RESULT_ERR_WRONGRESULTSTRUCT = 15;
		/** Could not create async decode thread */
		public static final int RESULT_ERR_THREAD = 16;
		/** Asynchronous decode was canceled */
		public static final int RESULT_ERR_CANCEL = 17;
		/** An exception was detected in the deoder */
		public static final int RESULT_ERR_EXCEPTION = 18;
		/** Scanned barcode is not a valid IQ host barcode */
		public static final int RESULT_ERR_UNSUPPORTED_IQ_BARCODE = 19;
		/** Error loading EXM file. */
		public static final int RESULT_ERR_LOAD_EXMFILE = 20;
		/** Not a valid configuration file. */
		public static final int RESULT_ERR_EXMFILE_INVALID = 21;
		/** Section missing from exm file. */
		public static final int RESULT_ERR_MISSING_EXMSECTION = 22;
		/** Error processing exm file section. */
		public static final int RESULT_ERR_PROCESSING_EXMSECTION = 23;
		/** No data to send */
		public static final int RESULT_ERR_NODATA = 24;
		/** License error, pls check error msg.*/
		public static final int RESULT_ERR_LICENSE_MANAGER = 25;
		/** Unknown error number. */
		public static final int RESULT_ERR_UNKNOWN = 26;
	}

}