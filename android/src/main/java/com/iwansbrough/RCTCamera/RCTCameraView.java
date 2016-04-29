// /**
//  * Created by Fabrice Armisen (farmisen@gmail.com) on 1/3/16.
//  */

// package com.lwansbrough.RCTCamera;

// import com.facebook.react.bridge.LifecycleEventListener;
// import android.view.OrientationEventListener;
// import android.hardware.SensorManager;

// import com.facebook.react.uimanager.*;
// // import android.hardware.SensorManager;
// // import android.view.OrientationEventListener;
// import android.app.Activity;
// import android.content.Context;
// import android.content.pm.PackageManager;
// import android.graphics.SurfaceTexture;
// import android.hardware.camera2.CameraAccessException;
// import android.hardware.camera2.CameraCaptureSession;
// import android.hardware.camera2.CameraCharacteristics;
// import android.hardware.camera2.CameraDevice;
// import android.hardware.camera2.CameraManager;
// import android.hardware.camera2.CaptureRequest;
// import android.hardware.camera2.params.StreamConfigurationMap;
// import android.hardware.Camera;
// import android.media.MediaRecorder;
// import android.os.Build;
// import android.os.Handler;
// import android.os.HandlerThread;
// import android.support.v4.content.ContextCompat;
// import android.util.Log;
// import android.util.Size;
// import android.util.SparseIntArray;
// import android.view.Surface;
// import android.view.TextureView;
// import android.view.ViewGroup;
// import android.view.WindowManager;
// import android.widget.Toast;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.List;

// public class RCTCameraView extends ViewGroup implements LifecycleEventListener {
//     private static final String TAG = "RCTCameraView";
//     private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
//     private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
//     private ThemedReactContext mContext;
//     private TextureView mTextureView;
//     private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
//         @Override
//         public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//             Log.v(TAG, "onSurfaceTextureAvailable");
//             setupCamera(width, height);
//             startCamera();
//         }

//         @Override
//         public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

//         }

//         @Override
//         public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//             return false;
//         }

//         @Override
//         public void onSurfaceTextureUpdated(SurfaceTexture surface) {

//         }
//     };
//     private CameraDevice mCameraDevice;
//     private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
//         @Override
//         public void onOpened(CameraDevice cameraDevice) {
//             mCameraDevice = cameraDevice;
//             startPreview();
//         }

//         @Override
//         public void onDisconnected(CameraDevice cameraDevice) {
//             cameraDevice.close();
//             mCameraDevice = null;
//         }

//         @Override
//         public void onError(CameraDevice cameraDevice, int error) {
//             cameraDevice.close();
//             mCameraDevice = null;
//         }
//     };
//     private OrientationEventListener mOrientationEventListener;
//     private HandlerThread mBackgroundHandlerThread;
//     private Handler mBackgroundHandler;
//     private String mCameraId;
//     private Size mPreviewSize;
//     private Size mVideoSize;
//     private MediaRecorder mMediaRecorder;
//     private CaptureRequest.Builder mCaptureRequestBuilder;
//     private static SparseIntArray ORIENTATIONS = new SparseIntArray();
//     static {
//         ORIENTATIONS.append(Surface.ROTATION_0, 0);
//         ORIENTATIONS.append(Surface.ROTATION_90, 90);
//         ORIENTATIONS.append(Surface.ROTATION_180, 180);
//         ORIENTATIONS.append(Surface.ROTATION_270, 270);
//     }
//     private static class CompareSizeByArea implements Comparator<Size> {
//         @Override
//         public int compare(Size lhs, Size rhs) {
//             return Long.signum((long) lhs.getWidth() * lhs.getHeight() / (long) rhs.getWidth() * rhs.getHeight());
//         }
//     }

//     public RCTCameraView(ThemedReactContext context, Activity activity) {
//         super(context);
//         Log.v(TAG, "constructor");
//         mContext = context;
//         context.addLifecycleEventListener(this);
//         activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
//         mTextureView = new TextureView(context);
//         mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//         addView(mTextureView);
//         setupOrientationEventListener();
//     }

//     @Override
//     public void onHostResume() {
//         Log.v(TAG, "onHostResume");
//         // if (mTextureView.isAvailable()) {
//         //     mSurfaceTextureListener.onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
//         // }
//         // Actvity `onResume`
//     }

//     @Override
//     public void onHostPause() {
//         Log.v(TAG, "onHostPause");
//         // Actvity `onPause`
//     }

//     @Override
//     public void onHostDestroy() {
//         Log.v(TAG, "onHostDestroy");
//         // Actvity `onDestroy`
//     }

//     private void setupCamera(int width, int height) {
//         Log.v(TAG, "setupCamera");
//         CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//         try {
//             for (String cameraId : cameraManager.getCameraIdList()) {
//                 Log.v(TAG, "currentCameraIdInLoop: " + cameraId);
//                 int lensFacing = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING);
//                 if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
//                     continue;
//                 }
//                 mCameraId = cameraId;
//                 Log.v(TAG, "mCameraId: " + mCameraId);
//                 // return;
//             }
//             applyHacks();
//             updatePreviewAndVideoSize();
//         } catch (CameraAccessException e) {
//             e.printStackTrace();
//         }
//     }

//     private void applyHacks() {
//         // try hacking samsung
//         try {
//             Camera camera = Camera.open();
//             Camera.Parameters parameters = camera.getParameters();
//             parameters.set("cam_mode", 1);
//             parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
//             camera.setParameters(parameters);
//         } catch (Exception e) {
//             Log.v(TAG, "Couln't hack samsung (expected)");
//             // expected to fail in most cases, no worries...
//         }
//     }

//     private void startCamera() {
//         Log.v(TAG, "startCamera");
//         CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//         try {
//             cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
//         } catch (CameraAccessException e) {
//             e.printStackTrace();
//         }
//     }

//     private void updatePreviewAndVideoSize() {
//         Log.v(TAG, "updatePreviewAndVideoSize");
//         try {
//             CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//             CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
//             StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//             WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//             int deviceOrientation = windowManager.getDefaultDisplay().getRotation();
//             Log.v(TAG, "deviceOrientation: " + Integer.toString(deviceOrientation));
//             int totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
//             Log.v(TAG, "totalRotation: " + Integer.toString(totalRotation));
//             boolean swapRotation = totalRotation == 90 || totalRotation == 270;
            
//             Log.v(TAG, "swapRotation: " + Boolean.toString(swapRotation));
//             int rotatedWidth = this.getWidth();
//             int rotatedHeight = this.getHeight();
//             if (swapRotation) {
//                 rotatedWidth = this.getHeight();
//                 rotatedHeight = this.getWidth();
//             }
//             Log.v(TAG, "rotatedWidth: " + Integer.toString(rotatedWidth));
//             Log.v(TAG, "rotatedHeight: " + Integer.toString(rotatedHeight));
//             mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
//             Log.v(TAG, "mPreviewSize.width: " + Integer.toString(mPreviewSize.getWidth()));
//             Log.v(TAG, "mPreviewSize.height: " + Integer.toString(mPreviewSize.getHeight()));
//             mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
//             Log.v(TAG, "mVideoSize.width: " + Integer.toString(mVideoSize.getWidth()));
//             Log.v(TAG, "mVideoSize.height: " + Integer.toString(mVideoSize.getHeight()));
//         } catch (CameraAccessException e) {
//             e.printStackTrace();
//         }
//     }

//     private void updateOrientation() {
//         Log.v(TAG, "onOrientationChanged");
//         layoutTextureView();
//     }

//     private void startPreview() {
//         Log.v(TAG, "startPreview");
//         SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
//         surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//         Surface previewSurface = new Surface(surfaceTexture);

//         try {
//             mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//             mCaptureRequestBuilder.addTarget(previewSurface);
//             mCameraDevice.createCaptureSession(
//                 Arrays.asList(previewSurface),
//                 new CameraCaptureSession.StateCallback() {
//                     @Override
//                     public void onConfigured(CameraCaptureSession session) {
//                         try {
//                             session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
//                         } catch (CameraAccessException e) {
//                             e.printStackTrace();
//                         }
//                     }

//                     @Override
//                     public void onConfigureFailed(CameraCaptureSession session) {
//                         Toast.makeText(mContext, "Unable to setup camera preview", Toast.LENGTH_SHORT).show();
//                     }
//                 },
//                 null
//              );
//         } catch (CameraAccessException e) {
//             e.printStackTrace();
//         }
//     }

//     private void closeCamera() {
//         Log.v(TAG, "closeCamera");
//         if(mCameraDevice != null) {
//             mCameraDevice.close();
//             mCameraDevice = null;
//         }
//     }

//     private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
//         Log.v(TAG, "sensorToDeviceRotation");
//         int sensorOrienatation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//         deviceOrientation = ORIENTATIONS.get(deviceOrientation);
//         return (sensorOrienatation + deviceOrientation + 360) % 360;
//     }

//     private static Size chooseOptimalSize(Size[] choices, int width, int height) {
//         Log.v(TAG, "chooseOptimalSize");
//         List<Size> bigEnough = new ArrayList<Size>();
//         for (Size option : choices) {
//             if (option.getHeight() == option.getWidth() * height / width && option.getWidth() >= width && option.getHeight() >= height) {
//                 bigEnough.add(option);
//             }
//         }
//         if (bigEnough.size() > 0) {
//             return Collections.min(bigEnough, new CompareSizeByArea());
//         } else {
//             return choices[0];
//         }
//     }

//     public void setAspect(int aspect) {
//     }

//     public void setCameraType(final int type) {
//     }

//     public void setCaptureQuality(String captureQuality) {
//     }

//     public void setTorchMode(int torchMode) {
//     }

//     public void setFlashMode(int flashMode) {
//     }

//     public void setOrientation(int orientation) {
//     }

//     private void setupOrientationEventListener() {
//         mOrientationEventListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {
//             @Override
//             public void onOrientationChanged(int orientation) {
//                 Log.v(TAG, "onOrientationChanged");
//                 updateOrientation();
//             }
//         };
//         if (mOrientationEventListener.canDetectOrientation()) {
//             mOrientationEventListener.enable();
//         } else {
//             mOrientationEventListener.disable();
//         }
//     }

//     @Override
//     protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//         Log.v(TAG, "onLayout: " + Integer.toString(left) + ", " + Integer.toString(top) + ", " + Integer.toString(right) + ", " + Integer.toString(bottom));
//         layoutTextureView(left, top, right, bottom);
//     }

//     private void layoutTextureView() {
//         layoutTextureView(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
//     }

//     private void layoutTextureView(int left, int top, int right, int bottom) {
//         Log.v(TAG, "layoutTextureView: " + Integer.toString(left) + ", " + Integer.toString(top) + ", " + Integer.toString(right) + ", " + Integer.toString(bottom));
//         mTextureView.layout(left, top, right, bottom);
//         this.postInvalidate(left, top, right, bottom);
//     }
// }
