package com.hsm.barcode;

import com.hsm.barcode.DecodeWindowing.DecodeWindow;
import com.hsm.barcode.DecodeWindowing.DecodeWindowLimits;
import com.hsm.barcode.easydl.LicenseData;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
//import android.util.Log;

public class Decoder {

	/**
	 * Observer object (listener). Must be registered and it is used to notify that a
	 * multiple decode result is available.
	 */
	private DecoderListener observer;

	private static final String TAG = "Decoder.java";
    private static String VERSION = "";

	static {
		try{
			//if current run android device API level > 25（7.1.1）
			if (Build.VERSION.SDK_INT > 25){
				//Log.d(TAG, "Load Libs:");
				System.loadLibrary("HSMDecoderBAPI");
			}else {//API level < 7.1.1
				System.loadLibrary("HsmKil");
				//Log.d(TAG, "libHsmKil.so loaded");
				System.loadLibrary("HHPScanInterface");
				//Log.d(TAG, "libHHPScanInterface.so loaded");
				System.loadLibrary("HSMDecoderAPI");
				//Log.d(TAG, "libHSMDecoderAPI.so loaded");
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
			Log.i(TAG, "load libs failed");
		}
	}

	/**
	 * Native API methods
	 *
	 */

	/**
	 * Method used to connect to the engine and initialize the API.
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void connectDecoderLibrary() throws DecoderException;

	/**
	 * Method used to disconnect from the engine and deinitialize the API.
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void disconnectDecoderLibrary() throws DecoderException;

	/**
	 * Method used to get associated error message with an error code.
	 *
	 * @param error - ResultId to get error message associated
	 * @return Error message string
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getErrorMessage(int error) throws DecoderException;

	/**
	 * Retrieves the maximum message length capable of the Decoder.
	 *
	 * @return The maximum message length
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getMaxMessageLength() throws DecoderException;

	/**
	 * Retrieves the EngineID of the connected Engine.
	 *
	 * @return @EngineID of connected engine
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getEngineID() throws DecoderException;

	/**
	 * Retrieves the Major revision of the PSOC of the connected Engine.
	 *
	 * @return Major PSOC revision of connected Engine
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getPSOCMajorRev() throws DecoderException;

	/**
	 * Retrieves the Minor revision of the PSOC of the connected Engine.
	 *
	 * @return Minor PSOC revision of connected Engine
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getPSOCMinorRev() throws DecoderException;

	/**
	 * Retrieves the serial number of the connected Engine.
	 *
	 * @return Serial number of connected Engine
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getEngineSerialNumber() throws DecoderException;

	/**
	 * Retrieves the engine type of the connected Engine.
	 *
	 * @return @EngineType of connected Engine
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getEngineType() throws DecoderException;

	/**
	 * Retrieves the revision of the API.
	 *
	 * @return Revision of the API
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getAPIRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Decoder.
	 *
	 * @return Revision of the Decoder
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getDecoderRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Secondary Decoder.
	 *
	 * @deprecated Secondary Decoder currently unsupported
	 * @return Revision of the Secondary Decoder
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getSecondaryDecoderRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Decoder Control Logic.
	 *
	 * @return Revision of the Decoder Control Logic
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getControlLogicRevision() throws DecoderException;

	/**
	 * @deprecated Retrieves the revision of the Decoder Threading.
	 *
	 * @deprecated Threading currently unsupported
	 * @return Revision of the Decoder Threading
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getDecThreadsRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Digimarc Decoder.
	 *
	 * @return Revision of the Digimarc Decoder
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getDecoderDigimarcRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Scan Driver.
	 *
	 * @return Revision of the Scan Driver
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getScanDriverRevision() throws DecoderException;

	/**
	 * Retrieves the imager properties of the connected engine.
	 *
	 * @param imgProp ImagerProperties structure
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getImagerProperties(ImagerProperties imgProp) throws DecoderException;

	/**
	 * Retrieves last image sent to the Decoder.
	 *
	 * @param imgAtt to fill
	 * @return RAW image array
	 * @throws DecoderException with appropriate error code and message
	 */
	public native byte[] getLastImage(ImageAttributes imgAtt) throws DecoderException;

	/**
	 * Sets last image that will be sent to the Decoder.
	 *
	 * @param img - RAW image array
	 * @param w - width of image
	 * @param h - height of image
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setLastImage(byte[] img, int w, int h) throws DecoderException;


	/**
	 * Retrieves an IQ Image (intelligent imaging)
	 * @param IQParams describing the location and parameters of the IQ image
	 *        to be retrieved
	 * @param bitmap returned
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getIQImage(IQImagingProperties IQParams, Bitmap bitmap) throws DecoderException;

	/**
	 * Sets the decode window mode to be used.
	 *
	 * @param nMode DecodeWindowMode setting (see {@link DecodeWindowing.DecodeWindowMode})
	 * @throws DecoderException
	 */
	public native void setDecodeWindowMode(int nMode) throws DecoderException;

	/**
	 * Retrieves the current decode window mode setting
	 *
	 * @return DecodeWindowMode
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getDecodeWindowMode() throws DecoderException;

	/**
	 * Sets the decode window parameters.
	 *
	 * @param window - DecodeWindow structure to be set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setDecodeWindow(DecodeWindow window) throws DecoderException;

	/**
	 * Retrieves the decode window parameters.
	 *
	 * @param window - DecodeWindow structure to be retrieved
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getDecodeWindow(DecodeWindow window) throws DecoderException;

	/**
	 * Retrieves the decode window parameter limits.
	 *
	 * @param limits - DecodeWindowLimits structure to be set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getDecodeWindowLimits(DecodeWindowLimits limits) throws DecoderException;

	/**
	 * Retrieves the barcode Bounds.
	 *
	 * @param Bounds - HsmBarcodeBounds structure to be set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getLastBarcodeBounds(HsmBarcodeBounds Bounds) throws DecoderException;

	/**
	 * enable the AUX
	 *
	 * @param on - AUX to be enable or disable
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void EnableAux(boolean on) throws DecoderException;

	/**
	 * Sets the show decode window setting.  To be used for debug purpose only
	 * when using Decode Windowing.
	 *
	 * @param nMode - ShowDecodeWindowMode setting
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setShowDecodeWindow(int nMode) throws DecoderException;

	/**
	 * Retrieves the current show decode window setting.
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getShowDecodeWindow() throws DecoderException;

	/**
	 * Enables the passed in symbology ID.
	 *
	 * @param symID - SymbologyID to be enabled
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void enableSymbology(int symID) throws DecoderException;

	/**
	 * Disables the passed in symbology ID.
	 *
	 * @param symID - SymbologyID to be enabled
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void disableSymbology(int symID) throws DecoderException;

	/**
	 * Sets the passed in symbology ID defaults.
	 *
	 * @param symID - SymbologyID to be defaulted
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setSymbologyDefaults(int symID) throws DecoderException;

	/**
	 * Sets the current configuration of the passed in structure.
	 *
	 * @param symConfig - SymbologyConfig to set.
	 * @throws DecoderException
	 */
	public native void setSymbologyConfig(SymbologyConfig symConfig) throws DecoderException;

	/**
	 * Retrieves the current configuration of the passed in structure.
	 *
	 * @param symConfig - SymbologyConfig to retrieve.
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getSymbologyConfig(SymbologyConfig symConfig) throws DecoderException;

	/**
	 * Retrieves the default configuration of the passed in structure.
	 *
	 * @param symConfig - SymbologyConfig to retrieve.
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getSymbologyConfigDefaults(SymbologyConfig symConfig) throws DecoderException;

	/**
	 * Retrieves the minimum range of the passed in symbology ID.
	 *
	 * @param symID - SymbologyID minimum range to
	 * @return Minimum range of the passed in SymbologyID
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getSymbologyMinRange(int symID) throws DecoderException;

	/**
	 * Retrieves the maximum range of the passed in symbology ID.
	 *
	 * @param symID - SymbologyID maximum range to
	 * @return Maximum range of the passed in SymbologyID
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getSymbologyMaxRange(int symID) throws DecoderException;

	/**
	 * Sets the lights mode for scanning.
	 *
	 * @param Mode - LightsMode setting to set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setLightsMode(int Mode) throws DecoderException;

	/**
	 * Retrieves the current lights mode for scanning.
	 *
	 * @return LightsMode setting returned
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getLightsMode() throws DecoderException;

	/**
	 * Sets the decode attempt limit (or the amount of time it will spend
	 * decoding a particular image).
	 *
	 * @param limit - in milliseconds
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setDecodeAttemptLimit(int limit) throws DecoderException;

	/**
	 * Sets the OCR template configuration (pre-defined).
	 *
	 * @param template - OCRTemplates setting
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setOCRTemplates(int template) throws DecoderException;

	/**
	 * Gets the OCR active template configuration.
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getOCRTemplates() throws DecoderException;

	/**
	 * Sets the OCR User defined template.
	 *
	 * @param template - byte array of the template
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setOCRUserTemplate(byte[] template) throws DecoderException;

	/**
	 * Gets the OCR User defined template.
	 *
	 * @return template - byte array of the template
	 * @throws DecoderException with appropriate error code and message
	 */
	public native byte[] getOCRUserTemplate() throws DecoderException;

	/**
	 * Sets the OCR Mode (used for enabling/disabling)
	 *
	 * @param mode - OCRMode to set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setOCRMode(int mode) throws DecoderException;

	/**
	 * Gets the OCR Mode
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getOCRMode() throws DecoderException;

	/**
	 * Sets the DPM Mode (used for dotpeen mode/reflective mode/disabling)
	 *
	 * @param mode - DPMMode to set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setDPMMode(int mode) throws DecoderException;

	/**
	 * Gets the DPM Mode
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getDPMMode() throws DecoderException;
	/**
	 * Sets the DPM Reflective Size (used for Normal/small)
	 *
	 * @param size - DPM Reflective Size to set
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setDPMReflectiveSize(int size) throws DecoderException;

	/**
	 * Gets the DPM Reflective Size
	 *
	 * @throws DecoderException with appropriate error code and message
	 */
	public native int getDPMReflectiveSize() throws DecoderException;
	/**
	 * Method used to start decoding.  Upon successful return, user must get the
	 * resulting barcode data using the proceeding native API methods.
	 *
	 * @param dwTimeout - timeout in milliseconds to automatically stop scanning
	 * @throws DecoderException
	 */
	public native void waitForDecode( int dwTimeout) throws DecoderException;

	/**
	 * Method to start decoding.  Upon successful return decoded result will be
	 * passed back via the result argument.
	 *
	 * @param dwTimeout - length of time in milliseconds to timeout
	 * @param result - DecodeResult structure
	 * @throws DecoderException
	 */
	public native void waitForDecodeTwo( int dwTimeout, DecodeResult result) throws DecoderException;

	/**
	 * This function is used to read multiple symbols using a single function
	 * call. When called, this function attempts to find and decode unique
	 * symbols once, and use the {@link #callbackMultiRead()} function to notify
	 * the application data is available.
	 *
	 * This function continues to find and decode symbols until the time
	 * specified in the dwTimeout parameter has expired, or until one of the
	 * {@link #callbackKeepGoing()} functions returns false.
	 *
	 * @param dwTimeout - length of time in milliseconds to timeout
	 * @throws DecoderException
	 */
	public native void waitMultipleDecode( int dwTimeout) throws DecoderException;

	/**
	 * Gets the Barcode data when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return byte array with barcode data
	 * @throws DecoderException
	 */
	public native byte[] getBarcodeByteData() throws DecoderException;

	/**
	 * Gets the Barcode CodeID when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return byte value of CodeID
	 * @throws DecoderException
	 */
	public native byte getBarcodeCodeID() throws DecoderException;

	/**
	 * Gets the Barcode AimID when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return byte value of AimID
	 * @throws DecoderException
	 */
	public native byte getBarcodeAimID() throws DecoderException;

	/**
	 * Gets the Barcode AimID when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return byte value of AimID
	 * @throws DecoderException
	 */
	public native byte getBarcodeAimModifier() throws DecoderException;

	/**
	 * Gets the Barcode length when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return byte value of length
	 * @throws DecoderException
	 */
	public native int  getBarcodeLength() throws DecoderException;

	/**
	 * Gets the Barcode Data in string form when {@link #waitForDecode(int)} succeeds.
	 *
	 * @return string value of barcode data
	 * @throws DecoderException
	 */
	public native String getBarcodeData() throws DecoderException;

	/**
	 * Gets the last decode time for a successful decode
	 *
	 * @return last decode time in milliseconds
	 * @throws DecoderException
	 */
	public native int getLastDecodeTime() throws DecoderException;

	/**
	 * Gets a single frame from the engine.
	 *
	 * @param bitmap image
	 * @throws DecoderException
	 */
	public native void getSingleFrame(Bitmap bitmap) throws DecoderException;

	/**
	 * Gets a single frame (1/4 size) from the engine.
	 *
	 * @param  bitmap image
	 * @throws DecoderException
	 */
	public native void getPreviewFrame(Bitmap bitmap) throws DecoderException;

	/**
	 * Starts scanning operation (image taking, not decoding).
	 *
	 * @throws DecoderException
	 */
	public native void startScanning() throws DecoderException;

	/**
	 * Stops scanning operation (image taking, not decoding).
	 *
	 * @throws DecoderException
	 */
	public native void stopScanning() throws DecoderException;

	/**
	 * Gets the image width of the connected engine.
	 *
	 * @return Image width in pixels
	 * @throws DecoderException
	 */
	public native int getImageWidth() throws DecoderException;

	/**
	 * Gets the image height of the connected engine.
	 *
	 * @return Image height in pixels
	 * @throws DecoderException
	 */
	public native int getImageHeight() throws DecoderException;

	/**
	 * Sets the exposure mode to be used during scanning.
	 *
	 * @param mode to be set see
	 * @throws DecoderException
	 */
	public native void setExposureMode(int mode) throws DecoderException;

	/**
	 * Gets the exposure mode currently set to be used during scanning.
	 *
	 * @return ExposureValues.ExposureMode currently set
	 * @throws DecoderException
	 */
	public native int getExposureMode() throws DecoderException;

	/**
	 * Sets the current exposure settings for the image sensor.
	 *
	 * @param array of exposure settings to set [ExposureValues.ExposureSettings tag, Value]
	 * @throws DecoderException
	 */
	public native void setExposureSettings(int[] array) throws DecoderException;

	/**
	 * Gets the current exposure settings for the image sensor.
	 *
	 * @param array - [ExposureValues.ExposureSettings tag, Value] array to set
	 * @throws DecoderException
	 */
	public native void getExposureSettings(int[] array) throws DecoderException;

	/**
	 * Gets the decode options parameters.
	 *
	 * @param options - DecodeOptions structure to be get
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void getDecodeOptions(DecodeOptions options) throws DecoderException;

	/**
	 * Sets the decode options parameters.
	 *
	 * @param options - DecodeOptions structure to set. (By sending -1 parameter will be ignored)
	 * @throws DecoderException with appropriate error code and message
	 */
	public native void setDecodeOptions(DecodeOptions options) throws DecoderException;

	/**
	 * Native API Callbacks
	 *
	 */

	/**
	 * Callback used to stop decoding. It is used in conjunction with
	 * {@link #waitForDecode(int)},  {@link #waitForDecodeTwo(int, DecodeResult)}
	 * and {@link #waitMultipleDecode(int)}.
	 *
	 * @return true if the Decoder should continue looking for symbols,
	 * otherwise false to stop the decode process.
	 */
	public boolean callbackKeepGoing()
	{
		//Log.d(TAG, "callbackKeepGoing");

		if(observer != null)
		{
			return(observer.onKeepGoingCallback());
		}

		return true;

	}

	/**
	 * Callback used for MultiRead (Shotgun). Upon successful decode when using
	 * {@link #waitMultipleDecode(int)}, the API calls this function when data is available
	 * and can be retrieved.
	 *
	 * @return true if the Decoder is to continue to look for additional
	 * symbols, otherwise return false to stop decode attempts.
	 */
	public boolean callbackMultiRead()
	{
		//Log.d(TAG, "callbackMultiRead");

		if(observer != null)
		{
			return(observer.onMultiReadCallback());
		}

		return false;
	}

	/**
	 * Other
	 *
	 */

	/**
	 * Used to register the listeners
	 *
	 * @param observer
	 */
	public void setDecoderListeners(DecoderListener observer)
	{
		this.observer = observer;
	}

	/**
	 * Sets direct decoder parameter.
	 * Only use it to set a parameter you've got from Honeywell.
	 * These tags/parameters can control the internal behavior of the decoder.
	 * There is no public documentation about them.
	 *
	 * @param tag
	 * @param value
	 * @throws DecoderException
	 */
	public native void setDecodeParameter(int tag, int value) throws DecoderException;

	/**
	 * Gets direct decoder parameter.
	 *
	 * @param tag
	 * @return parameter
	 * @throws DecoderException
	 */
	public native int getDecodeParameter(int tag) throws DecoderException;

	/**
	 * Sets direct redundancy parameter.
	 *
	 * @param value
	 * @return parameter
	 * @throws DecoderException
	 */
	public native int SetRedundancy(int[] value) throws DecoderException;

	/**
	 * Gets direct redundancy parameter.
	 *
	 * @param value
	 * @return parameter
	 * @throws DecoderException
	 */
	public native int GetRedundancy(int[] value) throws DecoderException;

	/**
	 * Sets timeout to put engine to the sleep mode.
	 *
	 * @param timeout to be set
	 * @throws DecoderException
	 */
	public native void setSleepTimeout(int timeout) throws DecoderException;

	/**
	 * Gets timeout to put engine to the sleep mode.
	 *
	 * @return timeout currently set
	 * @throws DecoderException
	 */
	public native int getSleepTimeout() throws DecoderException;

	/**
	 * Enable the plugin. 1 means us driver.
	 *
	 * @param pluginID the id of feature
	 * @throws DecoderException
	 */
	public native void enablePlugin(int pluginID) throws DecoderException;

	/**
	 * Disable the plugin. 1 means us driver.
	 *
	 * @param pluginID the id of feature
	 * @throws DecoderException
	 */
	public native void disablePlugin(int pluginID) throws DecoderException;

	/**
	 * Sets current to the illumination.
	 *
	 * @param current to be set
	 * @throws DecoderException
	 */
	public native void setIlluminationIntensity(int current) throws DecoderException;

	/**
	 * Gets illumination current. It just for n3601, n3603 , n3601ve
	 *
	 * @return lluminationIntensity currently set
	 * @throws DecoderException
	 */
	public native int getIlluminationIntensity() throws DecoderException;

	/**
	 * Retrieves the revision of the HAL.
	 *
	 * @return Revision of the HAL
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getHALRevision() throws DecoderException;

	/**
	 * Retrieves the revision of the Deconvolution.
	 *
	 * @return Revision of the Deconvolution
	 * @throws DecoderException with appropriate error code and message
	 */
	public native String getDeconvolutionRevision() throws DecoderException;

	/**
	 * @brief On-line activation with entitlement_id.
	 * @param entitlement_id The key string get from
	 * @param storage_path The path to read/write some cache files.
	 * @return
	 * @throws DecoderException
	 */
	public native int RemoteEntitlementActivation(String entitlement_id, String storage_path) throws DecoderException;

	public native int ConsumeLicenseResponse(String entitlement_id, String storage_path, String file_name) throws DecoderException;

	public native int SetServerAddress(String server_address) throws DecoderException;

	public native String GetDeviceId() throws DecoderException;

	public native String GetServerAddress() throws DecoderException;

	public native String GetLastLicenseErrorString() throws DecoderException;

	public native String GetLicenseManagerRevision() throws DecoderException;

	public native String RemoveLicenseManagerBinding(String storage_path) throws DecoderException;


	/**
	 * Throws an exception. Helps auto testing exception code.
	 *
	 * @param errnum
	 * @return void
	 * @throws DecoderException
	 */
	public native void Thrower(int errnum) throws DecoderException;


}
