package com.lwansbrough.RCTCamera;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.facebook.react.uimanager.*;

import com.facebook.react.uimanager.annotations.ReactProp;

public class RCTCameraViewManager extends ViewGroupManager<RCTCamera2View> {
    private static final String REACT_CLASS = "RCTCamera";
    private Activity mActivity = null;
    private RCTCameraModule mCameraModule = null;

    public RCTCameraViewManager(Activity activity, RCTCameraModule cameraModule) {
        mActivity = activity;
        mCameraModule = cameraModule;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public RCTCamera2View createViewInstance(ThemedReactContext context) {
        RCTCamera2View cameraView = new RCTCamera2View(context, mActivity);
        mCameraModule.setCameraView(cameraView);
        return cameraView;
    }

    @ReactProp(name = "aspect")
    public void setAspect(RCTCamera2View view, int aspect) {
        view.setAspect(aspect);
    }

    @ReactProp(name = "captureMode")
    public void setCaptureMode(RCTCamera2View view, int captureMode) {
        // TODO - implement video mode
    }

    @ReactProp(name = "captureTarget")
    public void setCaptureTarget(RCTCamera2View view, int captureTarget) {
        // No reason to handle this props value here since it's passed again to the RCTCameraModule capture method
    }

    @ReactProp(name = "type")
    public void setType(RCTCamera2View view, int type) {
        view.setCameraType(type);
    }

    @ReactProp(name = "captureQuality")
    public void setCaptureQuality(RCTCamera2View view, String captureQuality) {
        view.setCaptureQuality(captureQuality);
    }

    @ReactProp(name = "torchMode")
    public void setTorchMode(RCTCamera2View view, int torchMode) {
        view.setTorchMode(torchMode);
    }

    @ReactProp(name = "flashMode")
    public void setFlashMode(RCTCamera2View view, int flashMode) {
        view.setFlashMode(flashMode);
    }

    @ReactProp(name = "orientation")
    public void setOrientation(RCTCamera2View view, int orientation) {
        view.setOrientation(orientation);
    }

    @ReactProp(name = "captureAudio")
    public void setCaptureAudio(RCTCamera2View view, boolean captureAudio) {
        // TODO - implement video mode
    }
}
