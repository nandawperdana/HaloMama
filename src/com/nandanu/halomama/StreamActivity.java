package com.nandanu.halomama;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nandanu.halomama.controller.AmazonClientManager;
import com.nandanu.halomama.controller.DynamoDBRouter;
import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.model.HaloMama;
import com.nandanu.halomama.roboto.RobotoTextView;

public class StreamActivity extends Activity {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * widgets
	 */
	private ImageButton btnProfile, btnPlayVid;
	private RobotoTextView tvUsername, tvRetweet, tvSeen, tvVideoLainnya;
	private ImageView imageUser, imageShare;
	private Dialog dialog;
	private ProgressDialog progress;

	/*
	 * vars
	 */
	private Bitmap bitmap;
	private SharedPreferences pref;
	private String usernamePop, usernamePref, deviceOSPop, deviceOsPref,
			avatarUrlPop;
	private int retweetCountPop, seenCountPop, emotionPop;

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
		setContentView(R.layout.activity_stream);

		// get shared pref
		pref = getApplicationContext().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		usernamePref = pref.getString(Constants.TAG_TWITTER_USERNAME, "");
		deviceOsPref = pref.getString(Constants.TAG_DEVICE_OS, "");

		tvUsername = (RobotoTextView) findViewById(R.id.textViewUsername);
		tvRetweet = (RobotoTextView) findViewById(R.id.textViewRetweet);
		tvSeen = (RobotoTextView) findViewById(R.id.textViewSeen);
		tvVideoLainnya = (RobotoTextView) findViewById(R.id.textViewVideoLainnya);
		imageShare = (ImageView) findViewById(R.id.imageViewShare);
		imageUser = (ImageView) findViewById(R.id.imageViewUserImage);
		btnPlayVid = (ImageButton) findViewById(R.id.buttonPlayVideo);
		btnProfile = (ImageButton) findViewById(R.id.buttonUserImage);

		new PrepareStream().execute();

		tvVideoLainnya.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.fhab.it/halomama"));
				startActivity(browserIntent);
			}
		});

		tvUsername.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.twitter.com/" + usernamePop));
				startActivity(browserIntent);
			}
		});

		btnPlayVid.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(buttonClick);

			}
		});

		btnProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(buttonClick);
				dialog = new Dialog(StreamActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.video_dialog);

				RobotoTextView tvRekamUlang = (RobotoTextView) dialog
						.findViewById(R.id.textViewRekamUlang);
				RobotoTextView tvGantiAkun = (RobotoTextView) dialog
						.findViewById(R.id.textViewGantiAkun);
				RobotoTextView tvShareMedia = (RobotoTextView) dialog
						.findViewById(R.id.textViewShareMedia);
				RobotoTextView tvHapus = (RobotoTextView) dialog
						.findViewById(R.id.textViewHapusVideo);

				tvRekamUlang.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								RecordActivity.class);
						startActivity(i);

					}
				});

				tvGantiAkun.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								DescActivity.class);
						startActivity(i);

					}
				});

				tvShareMedia.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								RecordActivity.class);
						startActivity(i);
					}
				});

				tvHapus.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new DeleteVideo().execute();
					}
				});

				dialog.show();
			}
		});
	}

	/**
	 * async task to prepare stream screen activity
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class PrepareStream extends AsyncTask<String, String, Bitmap> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(StreamActivity.this);
			progress.setMessage("Loading ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			acm = new AmazonClientManager(StreamActivity.this);
			router = new DynamoDBRouter(acm);
			// TODO Auto-generated method stub
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

			// get popular
			HaloMama hm = router.getPopularHaloMama();
			String tweetId = hm.getTweetId();

			try {
				// twitter
				twitter4j.Status status = twitter.showStatus(Long
						.parseLong(tweetId));

				// get property
				usernamePop = hm.getUserNameTwitter();
				retweetCountPop = status.getRetweetedStatus().getRetweetCount();
				seenCountPop = hm.getSeen();
				emotionPop = hm.getEmotionId();
				avatarUrlPop = hm.getAvatarURL();

				// get bitmap
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						avatarUrlPop).getContent());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			tvRetweet.setText(retweetCountPop);
			tvSeen.setText(seenCountPop);
			tvUsername.setText("@" + usernamePop);

			Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Bitmap.Config.ARGB_8888);

			BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
					TileMode.CLAMP);
			Paint paint = new Paint();
			paint.setShader(shader);
			Canvas c = new Canvas(image_circle);
			c.drawCircle(result.getWidth() / 2, result.getHeight() / 2,
					result.getWidth() / 2, paint);
			imageUser.setImageBitmap(image_circle);
			btnProfile.setImageBitmap(image_circle);
			progress.dismiss();
		}
	}

	/**
	 * async task to delete video
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class DeleteVideo extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(StreamActivity.this);
			progress.setMessage("Menghapus video ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			acm = new AmazonClientManager(StreamActivity.this);
			router = new DynamoDBRouter(acm);
			// TODO Auto-generated method stub
			HaloMama hm = new HaloMama(usernamePref, deviceOsPref);
			// deleteHaloMama : called when user choose "hapus video"
			hm.prepareDeleteHaloMama();
			router.deleteHaloMama(hm);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
		}
	}
}
