package com.dawn.decoderapijni;

public class SensorParam {
    public int CaptureMode;
    public int AdjustImgs;
    public int SkipImgs;
    public int RotationMode;
    public int MinExpValue;
    public int MaxExpValue;
    public int MinGainValue;
    public int MaxGainValue;
    public int TargetLumaValue;

    public SensorParam() { }

    public int getCaptureMode() {
        return CaptureMode;
    }

    public void setCaptureMode(int captureMode) {
        CaptureMode = captureMode;
    }

    public int getAdjustImgs() {
        return AdjustImgs;
    }

    public void setAdjustImgs(int adjustImgs) {
        AdjustImgs = adjustImgs;
    }

    public int getSkipImgs() {
        return SkipImgs;
    }

    public void setSkipImgs(int skipImgs) {
        SkipImgs = skipImgs;
    }

    public int getRotationMode() {
        return RotationMode;
    }

    public void setRotationMode(int rotationMode) {
        RotationMode = rotationMode;
    }

    public int getMinExpValue() {
        return MinExpValue;
    }

    public void setMinExpValue(int minExpValue) {
        MinExpValue = minExpValue;
    }

    public int getMaxExpValue() {
        return MaxExpValue;
    }

    public void setMaxExpValue(int maxExpValue) {
        MaxExpValue = maxExpValue;
    }

    public int getMinGainValue() {
        return MinGainValue;
    }

    public void setMinGainValue(int minGainValue) {
        MinGainValue = minGainValue;
    }

    public int getMaxGainValue() {
        return MaxGainValue;
    }

    public void setMaxGainValue(int maxGainValue) {
        MaxGainValue = maxGainValue;
    }

    public int getTargetLumaValue() {
        return TargetLumaValue;
    }

    public void setTargetLumaValue(int targetLumaValue) {
        TargetLumaValue = targetLumaValue;
    }
}
