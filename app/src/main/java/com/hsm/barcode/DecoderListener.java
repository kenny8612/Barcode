package com.hsm.barcode;

/**
 * Listener interface for handling a multiple decode result from the Decoder API
 *
 */
public interface DecoderListener {
	/**
	 * Handler for listener when a keep going callback occurs
	 *
	 * @return true to continue looking for decoded results, otherwise false.
	 */
	public boolean onKeepGoingCallback();
	/**
	 * Handler for listener when a multiple decode result is available.
	 *
	 * @return true to continue looking for decoded results, otherwise false.
	 */
	public boolean onMultiReadCallback();
}
