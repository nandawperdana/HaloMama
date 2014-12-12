package com.nandanu.halomama;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nandanu.halomama.controller.AmazonClientManager;
import com.nandanu.halomama.controller.DynamoDBRouter;
import com.nandanu.halomama.model.Question;
import com.nandanu.halomama.roboto.RobotoTextView;

public class RecordActivity extends Activity implements SurfaceHolder.Callback {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * camera
	 */
	// private CameraPreview mCameraPreview;
	// private PictureCallback mPicture;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private PictureCallback rawCallback;
	private ShutterCallback shutterCallback;
	private PictureCallback jpegCallback;
	private Camera mCamera;
	private MediaRecorder mMediaRecorder = new MediaRecorder();
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/*
	 * widgets
	 */
	private ProgressDialog progress;
	private RobotoTextView tvRandom;
	private ImageButton btnBatalkan;
	private Chronometer mChronometer;
	private ImageButton recordButton;
	private ImageButton btnRandom;
	// private FrameLayout preview;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	// instantiate interface for databaserouter
	DynamoDBRouter router = null;

	/*
	 * vars
	 */
	private boolean cameraFront = false;
	private boolean isRecording = false;
	private String[] randomText = { "Pengalaman tersedih bareng ibu?",
			"Aku punya cerita sendiri...", "Ibuku itu..." };
	private ArrayList<Question> listQuestion = new ArrayList<Question>();
	private String fileVideoPath, fileImagePath;
	private Uri uriPath;
	private static SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_record);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
		mChronometer = (Chronometer) findViewById(R.id.chronometer);
		tvRandom = (RobotoTextView) findViewById(R.id.textViewRandomText);
		btnRandom = (ImageButton) findViewById(R.id.buttonRandom);
		btnBatalkan = (ImageButton) findViewById(R.id.imageButtonBatalkan1);
		recordButton = (ImageButton) findViewById(R.id.buttonRecord);

		/*
		 * camera init
		 */
		mCamera = getCameraInstance();
		// mCameraPreview = new CameraPreview(myContext, mCamera);
		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		new GetQuestion().execute();

		rawCallback = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				Log.d("Log", "onPictureTaken - raw");
			}
		};

		shutterCallback = new ShutterCallback() {

			@Override
			public void onShutter() {
				// TODO Auto-generated method stub
				Log.i("Log", "onShutter'd");
			}
		};

		jpegCallback = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				FileOutputStream outStream = null;
				try {
					File fileImage = getOutputMediaFile(MEDIA_TYPE_IMAGE);
					fileImagePath = fileImage.toString();
					outStream = new FileOutputStream(fileImage);
					outStream.write(data);
					outStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Maaf! Gadget anda tidak memiliki kamera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}

		btnRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub
				Random rand = new Random();
				int max = listQuestion.size() - 1;
				int r = rand.nextInt((max - 0) + 1) + 0;
				String txt = listQuestion.get(r).getQuestion();
				// String text = randomText[r];
				tvRandom.setText(txt);
			}
		});

		btnBatalkan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub

				Intent i = new Intent(RecordActivity.this, DescActivity.class);

				i.putExtra("batalkan", true);
				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			}
		});

		recordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRecording) {
					v.startAnimation(buttonClick);
					// stop recording and release camera
					// captureImage();
					mMediaRecorder.stop(); // stop the recording
					releaseMediaRecorder(); // release the MediaRecorder object
					// mCamera.lock(); // take camera access back from
					// MediaRecorder

					mChronometer.stop();
					recordButton
							.setBackgroundResource(R.drawable.fab_record_mdpi);
					// inform the user that recording has stopped
					// setCaptureButtonText("Capture");
					isRecording = false;
					Intent i = new Intent(RecordActivity.this,
							UploadActivity.class);

					i.putExtra("VIDEO_PATH", fileVideoPath);
					i.putExtra("IMAGE_PATH", fileImagePath);
					i.putExtra("VIDEO_URI", uriPath);

					i.addCategory(Intent.CATEGORY_HOME);
					// closing all the activity
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					// add new flag to start new activity
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(i);
				} else {
					// initialize video camera
					if (prepareVideoRecorder()) {
						// Camera is available and unlocked, MediaRecorder is
						// prepared,
						// now you can start recording
						recordButton
								.setBackgroundResource(R.drawable.fab_stop_mdpi);
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

	/**
	 * capture image
	 */
	private void captureImage() {
		// TODO Auto-generated method stub
		mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isDeviceSupportCamera()) {
			Toast toast = Toast.makeText(RecordActivity.this,
					"Maaf, gadget anda tidak memiliki kamera!",
					Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
	}

	/**
	 * find selfie camera
	 * 
	 * @return
	 */
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

	/**
	 * find back camera
	 * 
	 * @return
	 */
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

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(findFrontFacingCamera()); // attempt to get a Camera
														// instance
			if (findFrontFacingCamera() == -1) {
				c = Camera.open(findBackFacingCamera());
			}
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

	/**
	 * prepare video recorder
	 * 
	 * @return
	 */
	private boolean prepareVideoRecorder() {

		// Step 1: Unlock and set camera to MediaRecorder
		mMediaRecorder = new MediaRecorder(); // Works well
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// mMediaRecorder.setMaxDuration(10000);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		uriPath = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
		fileVideoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
		mMediaRecorder.setOutputFile(fileVideoPath);

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
				"HaloMama");
		// File mediaStorageDir = new File("/sdcard/", "HaloMama");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("HaloMama", "gagal membuat direktori");
				return null;
			}
		}

		// Create a media file name
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		// .format(new Date());
		long timestamp = System.currentTimeMillis();
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ pref.getString("USERNAME", "") + "-" + timestamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ pref.getString("USERNAME", "") + "-" + timestamp + ".mp4");
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
			Toast.makeText(getApplicationContext(), "Kamera tidak tersedia!",
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

	private class GetQuestion extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(RecordActivity.this);
			progress.setMessage("Get Question ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			acm = new AmazonClientManager(RecordActivity.this);
			router = new DynamoDBRouter(acm);

			listQuestion = router.scanQuestion2();
			return null;
		}

	}

}
