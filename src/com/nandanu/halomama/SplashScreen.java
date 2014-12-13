package com.nandanu.halomama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;

import com.amazonaws.services.s3.AmazonS3Client;
import com.nandanu.halomama.controller.AmazonClientManager;
import com.nandanu.halomama.controller.DynamoDBRouter;
import com.nandanu.halomama.controller.Util;
import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.model.People;
import com.nandanu.halomama.roboto.RobotoTextView;

public class SplashScreen extends Activity {
	/*
	 * dynamo db
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	DynamoDBRouter router = null;
	/*
	 * widgets
	 */
	private RobotoTextView splashText;

	/*
	 * vars
	 */
	public static boolean exists = false;
	public static boolean checked = false;
	private SharedPreferences pref;
	private static String KEY_FIRST_RUN = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);

		pref = SplashScreen.this.getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		splashText = (RobotoTextView) findViewById(R.id.textViewSplashLogo);
		splashText.setText(Html.fromHtml(getString(R.string.splash)));
		new ConnectToServer().execute();
	}

	private class ConnectToServer extends AsyncTask<String, String, Boolean> {
		// @Override
		// protected void onPreExecute() {
		// super.onPreExecute();
		// progress = new ProgressDialog(SplashScreen.this);
		// progress.setMessage("Koneksi ke server ...");
		// progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progress.setIndeterminate(true);
		// progress.show();
		// }

		@Override
		protected Boolean doInBackground(String... params) {
			acm = new AmazonClientManager(SplashScreen.this);
			router = new DynamoDBRouter(acm);
			/*
			 * sign in
			 */
			if (pref.contains(Constants.TAG_TWITTER_USERNAME)
					&& pref.contains(Constants.TAG_TWITTER_FULLNAME)) {
				People p = new People();
				p.prepareSignIn(pref.getString(Constants.TAG_TWITTER_USERNAME,
						""));
				router.signIn(p);
			}

			/*
			 * checking bucket
			 */
			AmazonS3Client sS3Client = Util
					.getS3Client(getApplicationContext());
			return Util.doesBucketExist();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			checked = true;
			exists = result;
			if (!pref.contains("KEY_FIRST_RUN")) {
				KEY_FIRST_RUN = "something";
				Log.d("First", "First run!");
				SharedPreferences.Editor edit = pref.edit();
				edit.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
				Intent i = new Intent(SplashScreen.this, DescActivity.class);
				startActivity(i);
			} else {
				Log.d("Second...", "Second run...!");
				Intent i = new Intent(SplashScreen.this, StreamActivity.class);
				startActivity(i);
			}
			// progress.dismiss();

			// close this activity
			SplashScreen.this.finish();
		}
	}
}
