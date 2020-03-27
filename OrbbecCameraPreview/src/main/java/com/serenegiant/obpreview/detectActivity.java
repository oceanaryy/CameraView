/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.serenegiant.obpreview;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.serenegiant.common.BaseActivity;

import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.Recorder;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;
import org.openni.android.OpenNIView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public final class detectActivity extends BaseActivity implements CameraDialog.CameraDialogParent,
        OpenNIHelper.DeviceOpenListener {
	private static final boolean DEBUG = true;    // TODO set false on release
	private static final String TAG = "MainActivity";

	/**
	 * preview resolution(width)
	 * if your camera does not support specific resolution and mode,
	 * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
	 */
	private static final int PREVIEW_WIDTH = 640;
	/**
	 * preview resolution(height)
	 * if your camera does not support specific resolution and mode,
	 * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
	 */
	private static final int PREVIEW_HEIGHT = 480;
	/**
	 * preview mode
	 * if your camera does not support specific resolution and mode,
	 * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
	 * 0:YUYV, other:MJPEG
	 */
	private static final int PREVIEW_MODE = UVCCamera.FRAME_FORMAT_MJPEG;

	/**
	 * for accessing USB
	 */
	private USBMonitor mUSBMonitor;
	 /**
	 * Handler to execute camera releated methods sequentially on private thread
	 */
	private UVCCameraHandler mCameraHandler;
	/**
	 * for camera preview display
	 */
	private CameraViewInterface mUVCCameraView;
	/**
	 * for open&start / stop&close camera preview
	 */

	private OpenNIView mDepthView;
    private OpenNIView mIRView;
    private OpenNIView mColorView;
    private OpenNIHelper mOpenNIHelper;
    private boolean mDeviceOpenPending = false;
    private Device mDevice;
    private Recorder mRecorder;
    private String mRecordingName;
    private String mRecording;
    private int mActiveDeviceID = -1;
    private boolean isUvcDevice = false;


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//하단바 제거
		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
		//상단바 제거
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (DEBUG) Log.v(TAG, "onCreate:");
		setContentView(R.layout.soface_detect);
		final View view = findViewById(R.id.camera_view);
		mUVCCameraView = (CameraViewInterface) view;
		mUVCCameraView.setAspectRatio((double)0.88);
		mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
		mCameraHandler = UVCCameraHandler.createHandler(this, mUVCCameraView,
				2, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);

		//init openni context
        mOpenNIHelper = new OpenNIHelper(this);
        OpenNI.setLogAndroidOutput(true);
        OpenNI.setLogMinSeverity(0);
        OpenNI.initialize();

        mDepthView = (OpenNIView) findViewById(R.id.depth_view);
        mIRView = (OpenNIView) findViewById(R.id.ir_view);
        mColorView = (OpenNIView) findViewById(R.id.color_view);
		RelativeLayout next = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		next.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										/*해당 페이지의 카메라 스톱*/
										onPause();
										onStop();
										/*보내는 스트림(ir,dep,rgb) 종료*/
										mIrStream.stop();
										mDepthStream.stop();
										mExit= true;
										/*디스플레이로 보내는 스레드 인터럽트로 종료*/
										m_thread.interrupt();
										/*다음페이지로 넘어감 (해당부분 통신 코딩 필요)*/
										Intent intent = new Intent(getApplicationContext(), passwordActivity.class);
										startActivity(intent);
									}
								}
		);
	}
	/* 뒤로가기 물리버튼 막기 */
	@Override
	public void onBackPressed() { //super.onBackPressed(); }
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG) Log.v(TAG, "onStart:");
		mUSBMonitor.register();

		if (mUVCCameraView != null)
			mUVCCameraView.onResume();

        if(mOpenNIHelper != null) {
            mOpenNIHelper.requestDeviceOpen(this);
        }

        List<UsbDevice> deviceList = mUSBMonitor.getDeviceList(new DeviceFilter(0x2bc5, -1, 239, 2, -1, null, null, null, false));
		if (!deviceList.isEmpty()) {
			Runnable mRunnable = new Runnable() {
				@Override
				public void run() {
					mUSBMonitor.requestPermission(deviceList.get(0));
				}
			};
			Handler mHandler = new Handler();
			mHandler.postDelayed(mRunnable, 500);
		}
	}

	private void showUVCCameraDialog()
    {
        if (!mCameraHandler.isOpened()) {
            CameraDialog.showDialog(detectActivity.this);

        } else {
            mCameraHandler.close();
        }
    }

	@Override
	protected void onStop() {
		if (DEBUG) Log.v(TAG, "onStop:");
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mCameraHandler.close();
			}
		}, 0);
		if (mUVCCameraView != null)
			mUVCCameraView.onPause();
		mUSBMonitor.unregister();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (DEBUG) Log.v(TAG, "onDestroy:");
		if (mCameraHandler != null) {
			mCameraHandler.release();
			mCameraHandler = null;
		}
		if (mUSBMonitor != null) {
			mUSBMonitor.destroy();
			mUSBMonitor = null;
		}
		mUVCCameraView = null;

        mOpenNIHelper.shutdown();
        OpenNI.shutdown();
		super.onDestroy();
	}

	protected void checkPermissionResult(final int requestCode, final String permission, final boolean result) {
		super.checkPermissionResult(requestCode, permission, result);
		if (!result && (permission != null)) {
		}
	}

	private Surface mSurface;

	private void startPreview() {
		final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
		if (mSurface != null) {
			mSurface.release();
		}
		mSurface = new Surface(st);
		mCameraHandler.startPreview(mSurface);
	}


    private Thread m_thread;
    private VideoStream mDepthStream;
    private VideoStream mIrStream;
    private VideoStream mColorStream;
    private boolean mExit = false;

    void startOpenNIPreviewThread() {
        m_thread = new Thread(){
            @Override
            public void run() {
                List<VideoStream> streams = new ArrayList<VideoStream>();

                streams.add(mDepthStream);
                mDepthStream.start();

                if (isUvcDevice) {
                    streams.add(mIrStream);
                    mIrStream.start();
                    mIRView.setBaseColor(Color.WHITE);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UVCCameraTextureView uvcCameraView = (UVCCameraTextureView)mUVCCameraView;
                            uvcCameraView.setVisibility(View.INVISIBLE);
                            mColorView.setVisibility(View.VISIBLE);
                        }
                    });

                    streams.add(mColorStream);
                    mColorStream.setVideoMode(new VideoMode(PREVIEW_WIDTH, PREVIEW_HEIGHT, 15, PixelFormat.YUYV.toNative()));
                    mColorStream.start();
                }

                mDepthView.setBaseColor(Color.YELLOW);
                while (!mExit) {
                    try {
                        OpenNI.waitForAnyStream(streams, 100);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        continue;
                    }
                    VideoFrameRef depthFrame = mDepthStream.readFrame();
                    if (depthFrame != null) {
                        mDepthView.update(depthFrame);
                        depthFrame.release();
                    }

                    if (isUvcDevice) {
                        mIRView.update(mIrStream);
                    }
                    else {
                        mColorView.update(mColorStream);
                    }
                }
            }
        };
        m_thread.start();
    }

	private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
		@Override
		public void onAttach(final UsbDevice device) {
			//Toast.makeText(detectActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
			if (DEBUG) Log.v(TAG, "onConnect:");
			mCameraHandler.open(ctrlBlock);
			startPreview();
		}

		@Override
		public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
			if (DEBUG) Log.v(TAG, "onDisconnect:");
			if (mCameraHandler != null) {
				mCameraHandler.close();
			}
		}

		@Override
		public void onDettach(final UsbDevice device) {
			Toast.makeText(detectActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(final UsbDevice device) {
		}
	};

	@Override
	public USBMonitor getUSBMonitor() {
		return mUSBMonitor;
	}

	@Override
	public void onDialogResult(boolean canceled) {
		if (canceled) {
            showUVCCameraDialog();
		}
	}

    @Override
    public void onDeviceOpened(UsbDevice aDevice) {
        Log.d(TAG, "Permission granted for device " +aDevice.getDeviceName());
        mDeviceOpenPending = false;
        mDevice = Device.open();
        //Find device ID
        List<DeviceInfo> devices = OpenNI.enumerateDevices();
        for(int i=0; i < devices.size(); i++)
        {
            if(devices.get(i).getUri().equals(mDevice.getDeviceInfo().getUri())){
                mActiveDeviceID = i;
                break;
            }
        }

        mDepthStream = VideoStream.create(mDevice, SensorType.DEPTH);
        if (mDevice.hasSensor(SensorType.COLOR)) {
            isUvcDevice = false;
            mColorStream = VideoStream.create(mDevice, SensorType.COLOR);
        }
        else {
            isUvcDevice = true;
            mIrStream = VideoStream.create(mDevice, SensorType.IR);
        }
        startOpenNIPreviewThread();
    }

    @Override
    public void onDeviceOpenFailed(String s) {

    }

	@Override
	public void onDeviceNotFound() {

	}

	static {
	    System.loadLibrary("OpenNI2.jni");
    }
	//취소 버튼 클릭
	public void mOnCancel(View v){
		Intent intent = new Intent(this, PopupSelectActivity.class);
		intent.putExtra("data", "결제를 취소하시겠습니까?");
		startActivityForResult(intent, 1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
			case KeyEvent.KEYCODE_HOME:
				finish();
				System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}
