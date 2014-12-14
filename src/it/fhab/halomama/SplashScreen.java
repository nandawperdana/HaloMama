package it.fhab.halomama;

import it.fhab.halomama.controller.AlertDialogManager;
import it.fhab.halomama.controller.AmazonClientManager;
import it.fhab.halomama.controller.ConnectionDetector;
import it.fhab.halomama.controller.DynamoDBRouter;
import it.fhab.halomama.controller.Util;
import it.fhab.halomama.model.Constants;
import it.fhab.halomama.model.DownloadModel;
import it.fhab.halomama.model.HaloMama;
import it.fhab.halomama.model.People;
import it.fhab.halomama.roboto.RobotoTextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;

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
	private ProgressBar pbSplash;

	/*
	 * vars
	 */
	private ConnectionDetector cd; // Internet Connection detector
	// Alert Dialog Manager
	private AlertDialogManager alert = new AlertDialogManager();
	public static boolean exists = false;
	public static boolean checked = false;
	private SharedPreferences pref;
	private HaloMama hm = null;
	private int retweetCountPop = 0;
	private Bitmap bmpPop, bmpPref, bmpThumbPop;
	private static String first_run = "";
	private TransferManager mManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);

		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);

		pbSplash = (ProgressBar) findViewById(R.id.progressBarSplash);
		splashText = (RobotoTextView) findViewById(R.id.textViewSplashLogo);
		splashText.setText(Html.fromHtml(getString(R.string.splash)));

		/**
		 * Check if Internet present
		 */
		cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(SplashScreen.this,
					"Kesalahan koneksi internet",
					"Mohon koneksikan perangkat anda", false);
			// stop executing code by return
			return;
		}
		new ConnectToServer().execute();
	}

	/**
	 * prepare server
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class ConnectToServer extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Animation anim = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
			anim.setFillAfter(true);
			pbSplash = new ProgressBar(SplashScreen.this);
			pbSplash.setVisibility(View.VISIBLE);
			pbSplash.setIndeterminate(true);
			pbSplash.startAnimation(anim);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			acm = new AmazonClientManager(SplashScreen.this);
			router = new DynamoDBRouter(acm);
			mManager = new TransferManager(
					Util.getCredProvider(SplashScreen.this));

			// publishProgress("start");
			/*
			 * checking bucket
			 */
			AmazonS3Client sS3Client = Util.getS3Client(SplashScreen.this);
			// try {
			// Thread.sleep(7000);
			// } catch (InterruptedException e) {
			// return null;
			// }

			/*
			 * sign in
			 */
			if (pref.contains(Constants.TAG_TWITTER_USERNAME)) {
				String deviceOs = pref.getString(Constants.TAG_DEVICE_OS, "");
				String username = pref.getString(
						Constants.TAG_TWITTER_USERNAME, "");

				People p = new People(acm.getIdentityId(), deviceOs);
				p.prepareSignIn(username);
				router.signIn(p);

				/*
				 * get popular
				 */
				hm = router.getPopularHaloMama();

				bmpPref = getAvatarImage(pref.getString(
						Constants.TAG_TWITTER_IMG_URL, ""));
				bmpPop = getAvatarImage(hm.getAvatarURL());

				/*
				 * get thumbnail
				 */
				try {
					String fileBucket = Util.getPrefix(SplashScreen.this)
							+ hm.getUserNameTwitter() + "-"
							+ hm.getCreatedDate() + ".jpg";
//					Log.e("nama file thumbnail ", fileBucket);
					DownloadModel dl = new DownloadModel(SplashScreen.this,
							fileBucket, mManager);

					File file = dl.downloadThumbnail();

					if (file != null) {
						bmpThumbPop = BitmapFactory.decodeFile(file
								.getAbsolutePath());
					}
				} catch (AmazonS3Exception e) {

				}

				/*
				 * twitter
				 */
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY",
						Constants.TWITTER_CONSUMER_KEY));
				builder.setOAuthConsumerSecret(pref.getString(
						"CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET));

				AccessToken accessToken = new AccessToken(pref.getString(
						"ACCESS_TOKEN", Constants.TWITTER_ACCESS_TOKEN),
						pref.getString("ACCESS_TOKEN_SECRET",
								Constants.TWITTER_ACCESS_TOKEN_SECRET));
				Twitter twitter = new TwitterFactory(builder.build())
						.getInstance(accessToken);

				String tweetId = hm.getTweetId();

				twitter4j.Status status;
				try {
					status = twitter.showStatus(Long.parseLong(tweetId));
					if (status.getRetweetedStatus() == null) {
						retweetCountPop = 0;
					} else
						retweetCountPop = status.getRetweetedStatus()
								.getRetweetCount();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// publishProgress("stop");
			return Util.doesBucketExist();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			checked = true;
			exists = result;
			// progress.dismiss();
			pbSplash.setVisibility(View.INVISIBLE);
			if (exists) {
				Intent i;
				if (!pref.contains(Constants.TAG_TWITTER_USERNAME)) {
					first_run = "pertama";
//					Log.d("First", "First run!");

					i = new Intent(SplashScreen.this, DescActivity.class);
					Toast.makeText(SplashScreen.this, "first",
							Toast.LENGTH_LONG).show();
				} else {
//					Log.d("Second...", "Second run...!");

					i = new Intent(SplashScreen.this, StreamActivity.class);
					i.putExtra("objhalomama", hm);
					i.putExtra("imgbmppop", bmpPop);
					i.putExtra("imgbmppref", bmpPref);
					i.putExtra("retweet", retweetCountPop);
					i.putExtra("imgthumb", bmpThumbPop);
				}

				pref.edit().putBoolean("FIRST_RUN", false);
				pref.edit().putString("first_run", first_run);
				pref.edit().commit();
				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

				// close this activity
				SplashScreen.this.finish();
			} else {
				alert.showAlertDialog(SplashScreen.this,
						"Kesalahan koneksi server", "Koneksi server gagal",
						false);
			}

		}
	}

	/**
	 * get image avatar from url
	 * 
	 * @param url
	 * @return bitmap
	 */
	private Bitmap getAvatarImage(String urlsrc) {
		// get bitmap

		Bitmap result = null;
		try {
			URL url = new URL(urlsrc);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			result = BitmapFactory.decodeStream(input);
			return result;

			// result = BitmapFactory.decodeStream((InputStream) new URL(urlsrc)
			// .getContent());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			Log.e("error ambil", e.getMessage() + " url " + urlsrc);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			Log.e("error ambil ioe", e.getMessage());
			return null;
		}

	}
}
