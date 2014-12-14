package it.fhab.halomama;

import it.fhab.halomama.controller.AmazonClientManager;
import it.fhab.halomama.controller.DynamoDBRouter;
import it.fhab.halomama.model.Constants;
import it.fhab.halomama.model.DownloadModel;
import it.fhab.halomama.model.HaloMama;
import it.fhab.halomama.roboto.RobotoTextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;

public class DoneUploadActivity extends Activity {
	/*
	 * widgets
	 */
	private ImageButton btnDone;
	private RobotoTextView tvUrl;
	private ProgressDialog progress;

	/*
	 * vars
	 */
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);
	private SharedPreferences pref;
	private String url = "http://halo-mama.com/@";
	private HaloMama hm = null;
	private int retweetCountPop = 0;
	private Bitmap bmp = null, bmpPref = null, bmpThumbPop;
	private TransferManager mManager;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	// instantiate interface for databaserouter
	DynamoDBRouter router = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_done_upload);

		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
		String username = pref.getString(Constants.TAG_TWITTER_USERNAME, "");

		tvUrl = (RobotoTextView) findViewById(R.id.textViewURLStream);
		btnDone = (ImageButton) findViewById(R.id.buttonDone);

		url += username;
		tvUrl.setText(url);
		tvUrl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setAnimation(buttonClick);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(url));
				startActivity(browserIntent);
			}
		});

		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setAnimation(buttonClick);
				new PrepareStream().execute();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		DoneUploadActivity.this.finish();
		System.exit(1);
	}

	/**
	 * async task to prepare stream screen activity
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class PrepareStream extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(DoneUploadActivity.this);
			progress.setMessage("Harap tunggu ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			acm = new AmazonClientManager(DoneUploadActivity.this);
			router = new DynamoDBRouter(acm);

			// get popular
			hm = router.getPopularHaloMama();
			bmp = getAvatarImage(hm.getAvatarURL());
			bmpPref = getAvatarImage(pref.getString(
					Constants.TAG_TWITTER_IMG_URL, ""));
			/*
			 * get thumbnail
			 */
			String fileBucket = hm.getUserNameTwitter() + "-"
					+ hm.getCreatedDate() + ".jpg";
			DownloadModel dl = new DownloadModel(DoneUploadActivity.this,
					fileBucket, mManager);

			File file = dl.downloadThumbnail();
			bmpThumbPop = BitmapFactory.decodeFile(file.getAbsolutePath());

			/*
			 * twitter
			 */
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY",
					Constants.TWITTER_CONSUMER_KEY));
			builder.setOAuthConsumerSecret(pref.getString("CONSUMER_SECRET",
					Constants.TWITTER_CONSUMER_SECRET));

			AccessToken accessToken = new AccessToken(pref.getString(
					"ACCESS_TOKEN", Constants.TWITTER_ACCESS_TOKEN),
					pref.getString("ACCESS_TOKEN_SECRET",
							Constants.TWITTER_ACCESS_TOKEN_SECRET));
			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);

			String tweetId = hm.getTweetId();

			twitter4j.Status status = null;
			try {
				status = twitter.showStatus(Long.parseLong(tweetId));
				if (status.getRetweetedStatus() == null) {
					retweetCountPop = 0;
				} else
					retweetCountPop = status.getRetweetedStatus()
							.getRetweetCount();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			Intent i = new Intent(DoneUploadActivity.this, StreamActivity.class);

			i.addCategory(Intent.CATEGORY_HOME);
			// closing all the activity
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// add new flag to start new activity
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			i.putExtra("objhalomama", hm);
			i.putExtra("imgbmppop", bmp);
			i.putExtra("imgbmppref", bmpPref);
			i.putExtra("retweet", retweetCountPop);
			i.putExtra("imgthumb", bmpThumbPop);
			Log.e("GAMBAR", "" + bmp);
			Log.e("NAMA", hm.getUserNameTwitter());
			startActivity(i);
			DoneUploadActivity.this.finish();
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
