package com.hsm.barcode;

import android.graphics.Point;

/**
 * @brief This class contains information where the barcode was found within the image.  It is important to take note that the coordinates returned are relative to the native position of the camera sensor.
 *        For most Android devices the camera sensor is mounted in a landscape orientation, therefore the coordinates returned are relative to a landscape image.  
 *        When a device's camera sensor is mounted in a landscape orientation but the real-time preview is view in a portrait orientation, Android translates the video stream so that it can be viewed in portrait
 *        mode. However, the images sent to the decoder are still in the device's native orientation (landscape) and therefore the BarcodeBounds coordinates will be relative to the landscape orientation.
 *        This means that the user will need to translate these coordinates to whatever orientation the device is in, if it is not the same as the native camera sensor orientation.   
 *
 */
public class HsmBarcodeBounds
{
	private int topLeftX, topLeftY, topRightX, topRightY,bottomLeftX,bottomLeftY,bottomRightX,bottomRightY;
	private int imgWidth = 0;
	private int imgHeight = 0;

	public HsmBarcodeBounds()
	{
	}

	/** @brief Top left point in the barcode */
	public Point getTopLeft()
	{
		return new Point(topLeftX,topLeftY);
	}

	/** @brief Top right point in the barcode */
	public Point getTopRight()
	{
		return new Point(topRightX,topRightY);
	}

	/** @brief Bottom left point in the barcode */
	public Point getBottomLeft()
	{
		return new Point(bottomLeftX,bottomLeftY);
	}

	/** @brief Bottom right point in the barcode */
	public Point getBottomRight()
	{
		return new Point(bottomRightX,bottomRightY);
	}

	/** @brief The width of the image the barcode was found in */
	public int getOriginalImageWidth()
	{
		return imgWidth;
	}

	/** @brief The height of the image the barcode was found in */
	public int getOriginalImageHeight()
	{
		return imgHeight;
	}
}
