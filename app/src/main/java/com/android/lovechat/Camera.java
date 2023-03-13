package com.android.lovechat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;

public class Camera {
    private TextureView mViewport = null;
    private CameraDevice mCamera = null;
    private CameraCaptureSession mSession;
    private Size cameraSize = null;

    public void setPreviewViewport(TextureView viewport) { mViewport = viewport; }

    public void start(Context context) {
        CameraDevice.StateCallback callback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCamera = camera;

                SurfaceTexture vpSurfaceTexture = mViewport.getSurfaceTexture();

                vpSurfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
                Surface vpSurface = new Surface(vpSurfaceTexture);

                try {
                    CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    builder.addTarget(vpSurface);

                    camera.createCaptureSession(Arrays.asList(vpSurface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mSession = session;
                            try {
                                mSession.setRepeatingRequest(builder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                mCamera.close();
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {}
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraSize = new Size(0, 0);
            CameraCharacteristics character = manager.getCameraCharacteristics(manager.getCameraIdList()[0]);
            cameraSize = character.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
            Log.i("", "camera size: " + cameraSize);

            manager.openCamera(manager.getCameraIdList()[0], callback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        mSession.close();
    }
}
