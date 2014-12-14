package it.fhab.halomama;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.fhab.halomama.controller.AlertDialogManager;
import it.fhab.halomama.controller.AmazonClientManager;
import it.fhab.halomama.controller.ConnectionDetector;
import it.fhab.halomama.controller.DynamoDBRouter;
import it.fhab.halomama.controller.Util;
import it.fhab.halomama.model.Constants;
import it.fhab.halomama.model.HaloMama;
import it.fhab.halomama.model.People;
import it.fhab.halomama.roboto.RobotoTextView;
import twitter4j.Twitter;
import twitter4j.TwitterException;
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
import android.view.Window;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;

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
	private ConnectionDetector cd; // Internet Connection detector
	// Alert Dialog Manager
	private AlertDialogManager alert = new AlertDialogManager();
	public static boolean exists = false;
	public static boolean checked = false;
	private SharedPreferences pref;
	private boolean firstRun;
	private HaloMama hm = null;
	private int retweetCountPop = 0;
	private Bitmap bmpPop, bmpPref;
	private static String first_run = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);

		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
		firstRun = pref.getBoolean("FIRST_RUN", true);
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

		Toast.makeText(
				SplashScreen.this,
				pref.getString(Constants.TAG_TWITTER_USERNAME, "") + " device "
						+ pref.getString(Constants.TAG_DEVICE_OS, "") + "ava "
						+ pref.getString(Constants.TAG_TWITTER_IMG_URL, ""),
				Toast.LENGTH_LONG).show();
		new ConnectToServer().execute();
	}

	/**
	 * prepare server
	 * 
	 * @author Aslab-NWP
	 * 
	 */
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
			if (pref.contains(Constants.TAG_TWITTER_USERNAME)) {
				String deviceOs = pref.getString(Constants.TAG_DEVICE_OS, "");
				String username = pref.getString(
						Constants.TAG_TWITTER_USERNAME, "");

				People p = new People(acm.getIdentityId(), deviceOs);
				p.prepareSignIn(username);
				router.signIn(p);

				// get popular
				hm = router.getPopularHaloMama();
				bmpPop = getAvatarImage(hm.getAvatarURL());
				bmpPref = getAvatarImage(Constants.TAG_TWITTER_IMG_URL);

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
					retweetCountPop = status.getRetweetedStatus()
							.getRetweetCount();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			Intent i;
			if (!pref.contains(Constants.TAG_TWITTER_USERNAME)) {
				first_run = "pertama";
				Log.d("First", "First run!");

				i = new Intent(SplashScreen.this, DescActivity.class);
				Toast.makeText(SplashScreen.this, "first", Toast.LENGTH_LONG)
						.show();
			} else {
				Log.d("Second...", "Second run...!");

				i = new Intent(SplashScreen.this, StreamActivity.class);
				i.putExtra("objhalomama", hm);
				i.putExtra("imgbmppop", bmpPop);
				i.putExtra("imgbmppref", bmpPref);
				i.putExtra("retweet", retweetCountPop);
				Log.e("GAMBAR", "" + bmpPop);
				Log.e("NAMA", hm.getUserNameTwitter());
			}

			pref.edit().putBoolean("FIRST_RUN", false);
			pref.edit().putString("first_run", first_run);
			pref.edit().commit();
			startActivity(i);

			// progress.dismiss();

			// close this activity
			SplashScreen.this.finish();
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
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
