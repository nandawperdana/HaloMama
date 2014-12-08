package com.nandanu.halomama;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class RecordActivity extends Activity implements SurfaceHolder.Callback {
	// camera
	// private CameraPreview mCameraPreview;
	// private PictureCallback mPicture;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private Camera mCamera;
	private MediaRecorder mMediaRecorder = new MediaRecorder();
	private MediaController mMediaController;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// widgets
	private Chronometer mChronometer;
	private ImageButton recordButton;
	// private FrameLayout preview;
	private VideoView preview;

	// var
	private boolean cameraFront = false;
	private boolean isRecording = false;
	private Context myContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_record);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		myContext = this;

		mChronometer = (Chronometer) findViewById(R.id.chronometer);

		mCamera = getCameraInstance();

		// mCameraPreview = new CameraPreview(myContext, mCamera);
		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}

		recordButton = (ImageButton) findViewById(R.id.buttonRecord);
		recordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRecording) {
					// stop recording and release camera
					mMediaRecorder.stop(); // stop the recording
					releaseMediaRecorder(); // release the MediaRecorder object
					// mCamera.lock(); // take camera access back from
					// MediaRecorder

					mChronometer.stop();
					recordButton.setBackgroundResource(R.drawable.fab_record_mdpi);
					// inform the user that recording has stopped
					// setCaptureButtonText("Capture");
					isRecording = false;
					Intent i = new Intent(RecordActivity.this,
							UploadActivity.class);

					// i.addCategory(Intent.CATEGORY_HOME);
					// // closing all the activity
					// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//
					// // add new flag to start new activity
					// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(i);
				} else {
					// initialize video camera
					if (prepareVideoRecorder()) {
						// Camera is available and unlocked, MediaRecorder is
						// prepared,
						// now you can start recording
						recordButton.setBackgroundResource(R.drawable.fab_stop_mdpi);
						mMediaRecorder.start();
						mChronometer.setBase(SystemClock.elapsedRealtime());
						mChronometer.start();

						// inform the user that recording has started
						// setCaptureButtonText("Stop");
						isRecording = true;
					} else {
						// prepare didn't work, release the camera
						releaseMediaRecorder();
						// inform user
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isDeviceSupportCamera()) {
			Toast toast = Toast.makeText(myContext,
					"Sorry, your phone does not have a camera!",
					Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
	}

	public int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	public int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent i = new Intent(getApplicationContext(), DescActivity.class);

		i.addCategory(Intent.CATEGORY_HOME);
		// closing all the activity
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// add new flag to start new activity
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(findFrontFacingCamera()); // attempt to get a Camera
														// instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private boolean prepareVideoRecorder() {

		// Step 1: Unlock and set camera to MediaRecorder
		mMediaRecorder = new MediaRecorder(); // Works well
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO)
				.toString());

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d("ERROR", "IllegalStateException preparing MediaRecorder: "
					+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d("ERROR",
					"IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder(); // if you are using MediaRecorder, release it
								// first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			// mCamera.release();
			// mCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"#HaloMama");
		// File mediaStorageDir = new File("/sdcard/", "HaloMama");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("HaloMama", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "HaloMama_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			mCamera.setParameters(params);
		} else {
			Toast.makeText(getApplicationContext(), "Camera not available!",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		finish();
	}

}
