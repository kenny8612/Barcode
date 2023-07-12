package com.hsm.barcode;

public final class ExposureValues {
	private ExposureValues(){
	}

	/**
	 * ExpsoureSettings class that defines most common setting used during image 
	 * acquisition.
	 *
	 */
	public final static class ExposureSettings{
		public ExposureSettings(){
		}
		/** @deprecated Exposure method to use */
		public static final int DEC_ES_EXPOSURE_METHOD = 0;
		/** Target white value in image to try and achieve */
		public static final int DEC_ES_TARGET_VALUE = 1;
		/** The percentile the white target value should be at */
		public static final int DEC_ES_TARGET_PERCENTILE = 2;
		/** How close to the target while value must image be */
		public static final int DEC_ES_TARGET_ACCEPT_GAP = 3;
		/** This is the maximum exposure allowed to use */
		public static final int DEC_ES_MAX_EXP = 4;
		/** This is the maximum gain allowed to use */
		public static final int DEC_ES_MAX_GAIN = 5;
		/** @deprecated Frame rate to use - fixed cannot be changed */
		public static final int DEC_ES_FRAME_RATE = 8;
		/** The image must conform to the auto-exposure requirements, if not, it's rejected */
		public static final int DEC_ES_CONFORM_IMAGE = 9;
		/** The number of time it will attempt to conform */
		public static final int DEC_ES_CONFORM_TRIES = 10;
		/** @deprecated Specular exclusion value to use */
		public static final int DEC_ES_SPECULAR_EXCLUSION = 11;
		/** @deprecated If specular exclusion is set, what is the saturation value  */
		public static final int DEC_ES_SPECULAR_SAT = 12;
		/** @deprecated If specular exclusion is set, what is the limit  */
		public static final int DEC_ES_SPECULAR_LIMIT = 13;
		/** If exposure mode is FIXED, what is the exposure */
		public static final int DEC_ES_FIXED_EXP = 14;
		/** If exposure mode is FIXED, what is the gain */
		public static final int DEC_ES_FIXED_GAIN = 15;
		/** If exposure mode is FIXED, what is the frame rate (fixed, has no effect) */
		public static final int DEC_ES_FIXED_FRAME_RATE = 16;
	}

	/**
	 * ExposureSettingsMinMax class that defines Minimum and Maximum settings 
	 * for ExposureSettings
	 *
	 */
	public class ExposureSettingsMinMax{
		public ExposureSettingsMinMax(){
		}
		/** @deprecated Minimum exposure method setting */
		public static final int DEC_MIN_ES_EXPOSURE_METHOD = 0;
		/** @deprecated Maximum exposure method setting */
		public static final int DEC_MAX_ES_EXPOSURE_METHOD = 2;
		/** Minimum target while value */
		public static final int DEC_MIN_ES_TARGET_VALUE = 1;
		/** Maximum target white value */
		public static final int DEC_MAX_ES_TARGET_VALUE = 255;
		/** Minimum target percentile */
		public static final int DEC_MIN_ES_TARGET_PERCENTILE = 1;
		/** Maximum target percentile */
		public static final int DEC_MAX_ES_TARGET_PERCENTILE = 99;
		/** Minimum target acceptance gap */
		public static final int DEC_MIN_ES_TARGET_ACCEPT_GAP = 1;
		/** Maximum target acceptance gap */
		public static final int DEC_MAX_ES_TARGET_ACCEPT_GAP = 50;
		/** Minimum exposure setting */
		public static final int DEC_MIN_ES_MAX_EXP = 1;
		/** Maximum exposure setting */
		public static final int DEC_MAX_ES_MAX_EXP = 7874;
		/** Minimum gain setting */
		public static final int DEC_MIN_ES_MAX_GAIN = 1;
		/** Maximum gain setting */
		public static final int DEC_MAX_ES_MAX_GAIN = 4;
		/** @deprecated Minimum frame rate setting */
		public static final int DEC_MIN_ES_FRAME_RATE = 1;
		/** @deprecated Maximum frame rate setting */
		public static final int DEC_MAX_ES_FRAME_RATE = 30;
		/** Minimum conform image setting */
		public static final int DEC_MIN_ES_CONFORM_IMAGE = 0;
		/** Maximum conform image setting */
		public static final int DEC_MAX_ES_CONFORM_IMAGE = 1;
		/** Minimum conform tries setting */
		public static final int DEC_MIN_ES_CONFORM_TRIES = 1;
		/** Maximum conform tries setting */
		public static final int DEC_MAX_ES_CONFORM_TRIES = 8;
		/** @deprecated Minimum specular exclusion setting */
		public static final int DEC_MIN_ES_SPECULAR_EXCLUSION = 0;
		/** @deprecated Maximum specular exclusion setting */
		public static final int DEC_MAX_ES_SPECULAR_EXCLUSION = 4;
		/** @deprecated Minimum specular saturation setting */
		public static final int DEC_MIN_ES_SPECULAR_SAT = 200;
		/** @deprecated Maximum specular saturation setting */
		public static final int DEC_MAX_ES_SPECULAR_SAT = 255;
		/** @deprecated Minimum specular limit setting */
		public static final int DEC_MIN_ES_SPECULAR_LIMIT = 1;
		/** @deprecated Maximum specular limit setting */
		public static final int DEC_MAX_ES_SPECULAR_LIMIT = 5;
		/** Minimum fixed exposure setting */
		public static final int DEC_MIN_ES_FIXED_EXP = 1;
		/** Maximum fixed exposure setting */
		public static final int DEC_MAX_ES_FIXED_EXP = 7874;
		/** Minimum fixed gain setting */
		public static final int DEC_MIN_ES_FIXED_GAIN = 1;
		/** Maximum fixed gain setting */
		public static final int DEC_MAX_ES_FIXED_GAIN = 4;
		/** Minimum fixed frame rate setting */
		public static final int DEC_MIN_ES_FIXED_FRAME_RATE = 1;
		/** Maximum fixed frame rate setting */
		public static final int DEC_MAX_ES_FIXED_FRAME_RATE = 30;
	}

	/**
	 * ExposureMode class that defines the exposure mode that will be 
	 * used during image capture.
	 *
	 */
	public final static class ExposureMode{
		public ExposureMode(){
		}
		/** Fixed exposure */
		public static final int FIXED = 0;
		/** Auto-exposure will be used */
		public static final int HHP = 2;
		/** Cellphone mode will be used */
		// public static final int CELLPHONE = 6;

	}

	/**
	 * @deprecated ExposureMethod class that defines the auto exposure method that will be 
	 * used during image capture.
	 *
	 */
	public final static class ExposureMethod{
		public ExposureMethod(){
		}
		/** Auto exposure is determined across entire image */
		public static final int UNIFORM = 0;
		/** Auto exposure is determined based on center only */
		public static final int CENTER_ONLY = 1;
		/** Auto exposure is determined based primarily on center only, but weighted */
		public static final int CENTER_WEIGHTED = 2;
	}

	/**
	 * ConformImage class that defines whether or not the image should conform 
	 * to exposure settings.
	 *
	 */
	public final static class ConformImage{
		public ConformImage(){
		}
		/** Image do not need to conform to exposure settings */
		public static final int DO_NOT_CONFORM_IMAGE = 0;
		/** Image must conform to exposure settings */
		public static final int CONFORM_IMAGE = 1;
	}

	/**
	 * @deprecated SpecularExclusion class that defines the specular exclusion setting in 
	 * the ExposureSettings class that will be used during image capture.
	 *
	 */
	public final class SpecularExclusion {
		public SpecularExclusion(){
		}
		/** No specular will be removed. */
		public static final int OFF = 0;
		/** Specular will be removed minimally. */
		public static final int MINIMAL = 1;
		/** Specular will be removed moderately. */
		public static final int MODERATE = 2;
		/** Specular will be removed aggressively. */
		public static final int AGGRESSIVE = 3;
		/** Do not use */
		public static final int SPECIAL = 4;
	}
}
