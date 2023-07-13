package com.zebra.zebrascanner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.ImageFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class ZebraScanner {
    private static final String TAG = "ZebraScanner";


    private static final int BCRDR_MSG_ERROR = 0x000001;
    private static final int BCRDR_MSG_SHUTTER = 0x000002;
    private static final int BCRDR_MSG_FOCUS = 0x000004;
    private static final int BCRDR_MSG_ZOOM = 0x000008;
    private static final int BCRDR_MSG_PREVIEW_FRAME = 0x000010;
    private static final int BCRDR_MSG_VIDEO_FRAME = 0x000020;
    private static final int BCRDR_MSG_POSTVIEW_FRAME = 0x000040;
    private static final int BCRDR_MSG_RAW_IMAGE = 0x000080;
    private static final int BCRDR_MSG_COMPRESSED_IMAGE = 0x000100;
    private static final int BCRDR_MSG_LAST_DEC_IMAGE = 0x000200;
    private static final int BCRDR_MSG_DEC_COUNT = 0x000400;
    // Add bar code reader specific values here
    private static final int BCRDR_MSG_DECODE_COMPLETE = 0x010000;
    private static final int BCRDR_MSG_DECODE_TIMEOUT = 0x020000;
    private static final int BCRDR_MSG_DECODE_CANCELED = 0x040000;
    private static final int BCRDR_MSG_DECODE_ERROR = 0x080000;
    private static final int BCRDR_MSG_DECODE_EVENT = 0x100000;
    private static final int BCRDR_MSG_FRAME_ERROR = 0x200000;
    private static final int BCRDR_MSG_ALL_MSGS = 0x3F03FF;

    public static final int SDLPROP_MODEL_NUM = 1;
    public static final int SDLPROP_SERIAL_NUM = 2;
    public static final int SDLPROP_MAX_FRAME_BUFFER_SIZE = 3;
    public static final int SDLPROP_HORIZONTAL_RES = 4;
    public static final int SDLPROP_VERTICAL_RES = 5;
    public static final int SDLPROP_IMGKIT_VER = 6;
    public static final int SDLPROP_ENGINE_VER = 7;
    public static final int SDLPROP_GUID = 8;
    public static final int SDLPROP_DATE_MFG = 9;
    public static final int SDLPROP_DATE_SVC = 10;
    public static final int SDLPROP_BTLD_FW_VER = 11;
    public static final int SDLPROP_DPM_CAL_COORD = 12;
    public static final int SDLPROP_ENGINE_ID = 13;
    public static final int SDLPROP_HW_VER = 14;
    public static final int SDLPROP_DVC_CLASS = 15;
    public static final int SDLPROP_SW_PROT_VER = 16;
    public static final int SDLPROP_ACTUAL_HORZ_RES = 17;

    // Result codes for functions that return and integer status

    /**
     * Function completed successfully
     */
    public static final int BCR_SUCCESS = 0;

    /**
     * Function failed
     */
    public static final int BCR_ERROR = -1;
    // onDecodeComplete status codes passed as the length value

    /**
     * onDecodeComplete length value indicating that the decode timed out
     */
    public static final int DECODE_STATUS_TIMEOUT = 0;

    /**
     * onDecodeComplete length value indicating that the decode was canceled
     */
    public static final int DECODE_STATUS_CANCELED = -1;

    /**
     * onDecodeComplete length value indicating that an error occurred
     */
    public static final int DECODE_STATUS_ERROR = -2;

    /**
     * onDecodeComplete length value indicating a multi-decode event
     */
    public static final int DECODE_STATUS_MULTI_DEC_COUNT = -3;

    // Miscellaneous event ID's

    /**
     * Scan mode changed event ID
     */
    public static final int BCRDR_EVENT_SCAN_MODE_CHANGED = 5;

    /**
     * Motion detected event ID
     */
    public static final int BCRDR_EVENT_MOTION_DETECTED = 6;

    /**
     * Scanner reset event ID
     */
    public static final int BCRDR_EVENT_SCANNER_RESET = 7;


    public native String stringFromJNI();

    private native int sdlApiOpen(Object reader_this, int readerId);

    public native int sdlApiClose();

    public native int sdlApiStartDecode();

    public native int sdlApiStopDecode();

    public native int sdlApiGetNumParameter(int dwParameter);

    public native int sdlApiSetNumParameter(int dwParameter, int dwValue);

    public native String sdlApiGetStrParameter(int dwParameter);

    public native int sdlApiGetNumProperty(int eProperty);

    public native String sdlApiGetStrProperty(int eProperty);

    public native int sdlApiSetDefaultParameters();

    public native byte[] sdlApiGetLastDecImage();


    private EventHandler mEventHandler;
    private DecodeCallback mDecodeCallback;
    private ErrorCallback mErrorCallback;
    private VideoCallback mVideoCallback;
    private PictureCallback mSnapshotCallback;
    private PreviewCallback mPreviewCallback;
    private boolean mOneShot;
    private boolean mWithBuffer;

    public ZebraScanner() {
        Looper aLooper;

        mEventHandler = null;
        mDecodeCallback = null;

        mErrorCallback = null;
        mPreviewCallback = null;
        mSnapshotCallback = null;
        mVideoCallback = null;


        aLooper = Looper.myLooper();
        if (null == aLooper)
            aLooper = Looper.getMainLooper();
        if (aLooper != null) {
            mEventHandler = new EventHandler(this, aLooper);
        }

    }

    public int openScanner(int readerId) {
        return sdlApiOpen(new WeakReference<ZebraScanner>(this), readerId);
    }

    public final void setDecodeCallback(DecodeCallback cb) {
        mDecodeCallback = cb;
    }

    private class EventHandler extends Handler {
        private ZebraScanner mReader;

        public EventHandler(ZebraScanner rdr, Looper looper) {
            super(looper);
            mReader = rdr;
        }

        @Override
        public void handleMessage(Message msg) {
            //Log.e(TAG, String.format("Event message: %X, arg1=0x%x, arg2=%d", msg.what, msg.arg1, msg.arg2));
            switch (msg.what) {
                case BCRDR_MSG_DECODE_COMPLETE:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onDecodeComplete(msg.arg1, msg.arg2, (byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_DECODE_TIMEOUT:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onDecodeComplete(0, 0, (byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_DECODE_CANCELED:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onDecodeComplete(0, DECODE_STATUS_CANCELED, (byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_FRAME_ERROR:
                    // TODO:
                case BCRDR_MSG_DECODE_ERROR:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onDecodeComplete(0, DECODE_STATUS_ERROR, (byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_DECODE_EVENT:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onEvent(msg.arg1, msg.arg2, (byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_SHUTTER:
                    // We do not support the shutter callback
                    return;

                case BCRDR_MSG_COMPRESSED_IMAGE:
                    if (mSnapshotCallback != null) {
                        int iCX;
                        int iCY;

                        iCX = (msg.arg1 >> 0) & 0xFFFF;
                        iCY = (msg.arg1 >> 16) & 0xFFFF;
                        mSnapshotCallback.onPictureTaken(msg.arg2, iCX, iCY, (byte[]) msg.obj, mReader);
                    } else {
                        Log.e(TAG, "BCRDR_MSG_COMPRESSED_IMAGE event with no snapshot callback");
                    }
                    return;

                case BCRDR_MSG_VIDEO_FRAME:
                    if (mVideoCallback != null) {
                        int iCX;
                        int iCY;

                        iCX = (msg.arg1 >> 0) & 0xFFFF;
                        iCY = (msg.arg1 >> 16) & 0xFFFF;
                        mVideoCallback.onVideoFrame(msg.arg2, iCX, iCY, (byte[]) msg.obj, mReader);
                    } else {
                        Log.e(TAG, "BCRDR_MSG_VIDEO_FRAME event with no video callback");
                    }
                    return;

                case BCRDR_MSG_PREVIEW_FRAME:
                    if (mPreviewCallback != null) {
                        PreviewCallback cb = mPreviewCallback;
                        cb.onPreviewFrame((byte[]) msg.obj, mReader);
                    }
                    return;

                case BCRDR_MSG_ERROR:
                    Log.e(TAG, "Error " + msg.arg1);
                    if (mErrorCallback != null) {
                        mErrorCallback.onError(msg.arg1, mReader);
                    }
                    return;

                case BCRDR_MSG_DEC_COUNT:
                    if (mDecodeCallback != null) {
                        mDecodeCallback.onDecodeComplete(msg.arg1, DECODE_STATUS_MULTI_DEC_COUNT, (byte[]) msg.obj, mReader);
                    }
                    return;

                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    ;

    private static void postEventFromNative(Object reader_ref, int what, int arg1, int arg2, Object obj) {
        @SuppressWarnings("unchecked")
        ZebraScanner c = (ZebraScanner) ((WeakReference<ZebraScanner>) reader_ref).get();
        if ((c != null) && (c.mEventHandler != null)) {
            Message m = c.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            c.mEventHandler.sendMessage(m);
        }
    }

    public interface DecodeCallback {
        /**
         * Called when a decode operation has completed, either due to a timeout,
         * a successful decode or canceled by the user.  This callback is invoked
         * on the event thread  was called from.
         *
         * @param symbology the symbology of decoded bar code if any
         * @param length    if positive, indicates the length of the bar code data,
         *                  otherwise, DECODE_STATUS_TIMEOUT if the request timed out or
         *                  DECODE_STATUS_CANCELED if stopDecode() is called before a successful
         *                  decode or timeout.
         * @param data      the contents of the decoded bar code
         * @param reader    the BarCodeReader service object.
         */
        void onDecodeComplete(int symbology, int length, byte[] data, ZebraScanner reader);

        /**
         * Called to indicate that the decoder detected an event such as MOTION DECTECTED.
         * This callback is invoked on the event thread  was called from.
         *
         * @param event  the type of event that has occurred
         * @param info   additional event information, if any, else zero
         * @param data   data associated with the event, if any, else null
         * @param reader the BarCodeReader service object.
         */
        void onEvent(int event, int info, byte[] data, ZebraScanner reader);
    }

    ;

    public interface ErrorCallback {
        /**
         * Callback for reader errors.
         *
         * @param error  error code:
         *               <ul>
         *               <li>
         *               <li>
         *               </ul>
         * @param reader the BarCodeReader service object
         */
        void onError(int error, ZebraScanner reader);
    }

    ;

    /**
     * Callback interface used to deliver copies of preview frames as
     * they are displayed.
     */
    public interface PreviewCallback {
        /**
         * Called as preview frames are displayed.  This callback is invoked
         * on the event thread  was called from.
         *
         * @param data   the contents of the preview frame in the format defined
         *               is never called, the default will be the YCbCr_420_SP (NV21) format.
         * @param reader the BarCodeReader service object.
         */
        void onPreviewFrame(byte[] data, ZebraScanner reader);
    }

    ;


    /**
     * Callback interface used to supply image data in video capture mode.
     */
    public interface VideoCallback {
        /**
         * Called when image data is available during video capture mode.
         * The format of the data depends on the current value of the
         * IMG_FILE_FORMAT and IMG_VIDEOSUB parameters.
         *
         * @param format format of the image (IMG_FORMAT_JPEG, IMG_FORMAT_BMP, or IMG_FORMAT_TIFF)
         * @param width  horizontal resolution of the image
         * @param height vertical resolution of the image
         * @param data   a byte array of the video frame
         * @param reader the BarCodeReader service object
         */
        void onVideoFrame(int format, int width, int height, byte[] data, ZebraScanner reader);
    }

    ;


    public interface PictureCallback {
        /**
         * Called when image data is available after a picture is taken.
         * The format of the data depends on the current value of the
         * IMG_FILE_FORMAT and IMG_VIDEOSUB parameters.
         *
         * @param format format of the image (IMG_FORMAT_JPEG, IMG_FORMAT_BMP, or IMG_FORMAT_TIFF)
         * @param width  horizontal resolution of the image
         * @param height vertical resolution of the image
         * @param data   a byte array of the picture data
         * @param reader the BarCodeReader service object
         */
        void onPictureTaken(int format, int width, int height, byte[] data, ZebraScanner reader);
    }


    static {
        System.loadLibrary("native-lib");
    }
}
