package com.dawn.decoderapijni;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

public class ScanCamera {
    private static ScanCamera mInstance;
    private static Camera mCamera;

    public ScanCamera() {
    }

    public static ScanCamera getInstance() {
        if (mInstance == null)
            mInstance = new ScanCamera();
        return mInstance;
    }

    public void cameraInit() {}

    public void cameraOpen(int port, int width, int height) {
        if (mCamera != null) return;

        try {
            mCamera = Camera.open(port);// 打开后置摄像头  openLegacy 指定版本
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);// 设置外形尺寸
            mCamera.setParameters(params);
            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(new ScanPreviewCallback());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int cameraClose() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return 0;
    }

    public void cameraStart() {
        if (mCamera == null)
            return;
        mCamera.setPreviewCallback(new ScanPreviewCallback());
        mCamera.startPreview();
    }

    public void cameraStop() {
        if (mCamera == null)
            return;
        mCamera.stopPreview();
    }

    public void cameraSetSurfaceHolder(SurfaceHolder surfaceHolder) {
    }

    public static class ScanPreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    }
}
