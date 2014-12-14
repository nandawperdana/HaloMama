package it.fhab.halomama;

import it.fhab.halomama.controller.AmazonClientManager;
import it.fhab.halomama.controller.DynamoDBRouter;
import it.fhab.halomama.model.Constants;
import it.fhab.halomama.model.HaloMama;
import it.fhab.halomama.roboto.RobotoTextView;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StreamActivity extends Activity {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * widgets
	 */
	private ImageButton btnProfile, btnPlayVid;
	private RobotoTextView tvUsername, tvRetweet, tvSeen, tvVideoLainnya,
			tvMood;
	private ImageView imageUser, imageShare;
	private Dialog dialog;
	private ProgressDialog progress;
	private FrameLayout layMood, layVideo;

	/*
	 * vars
	 */
	private Bitmap bitmapPop, bitmapPref, bitmapThumb;
	private SharedPreferences pref;
	private String usernamePop, usernamePref, deviceOSPop, deviceOsPref;
	private int retweetCountPop, seenCountPop, emotionPop;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	// instantiate interface for databaserouter
	DynamoDBRouter router = null;
	HaloMama hm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_stream);

		Intent i = getIntent();
		hm = (HaloMama) i.getSerializableExtra("objhalomama");
		bitmapPop = (Bitmap) i.getParcelableExtra("imgbmppop");
		bitmapPref = (Bitmap) i.getParcelableExtra("imgbmppref");
		bitmapThumb = (Bitmap) i.getParcelableExtra("imgthumb");
		retweetCountPop = i.getIntExtra("retweet", 0);

		// get shared pref
		pref = getApplicationContext().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		usernamePref = pref.getString(Constants.TAG_TWITTER_USERNAME, "");
		deviceOsPref = pref.getString(Constants.TAG_DEVICE_OS, "");

		usernamePop = hm.getUserNameTwitter();
		seenCountPop = hm.getSeen();
		emotionPop = hm.getEmotionId();

		layMood = (FrameLayout) findViewById(R.id.lay_mood);
		layVideo = (FrameLayout) findViewById(R.id.video_preview);
		tvMood = (RobotoTextView) findViewById(R.id.textViewMoodStream);
		tvUsername = (RobotoTextView) findViewById(R.id.textViewUsernamePopular);
		tvRetweet = (RobotoTextView) findViewById(R.id.textViewRetweet);
		tvSeen = (RobotoTextView) findViewById(R.id.textViewSeen);
		tvVideoLainnya = (RobotoTextView) findViewById(R.id.textViewVideoLainnya);
		imageShare = (ImageView) findViewById(R.id.imageViewShare);
		imageUser = (ImageView) findViewById(R.id.imageViewUserImagePopular);
		btnPlayVid = (ImageButton) findViewById(R.id.buttonPlayVideo);
		btnProfile = (ImageButton) findViewById(R.id.buttonUserImage);

		tvRetweet.setText("" + retweetCountPop);
		tvSeen.setText("" + seenCountPop);
		tvUsername.setText("@" + usernamePop);
		tvMood.setText(getStringMood(emotionPop));

		Bitmap roundImgPop = roundImgBitmap(bitmapPop);
		Bitmap roundImgPref = roundImgBitmap(bitmapPref);
		layVideo.setBackgroundDrawable(new BitmapDrawable(bitmapThumb));

		if (roundImgPop != null) {
			imageUser.setBackgroundDrawable(new BitmapDrawable(roundImgPop));
		}
		btnProfile.setImageBitmap(roundImgPref);

		layMood.setBackgroundColor(Color.parseColor(getColorMood(emotionPop)));

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
				new IncrementSeen().execute();
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
						i.addCategory(Intent.CATEGORY_HOME);
						// closing all the activity
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						// add new flag to start new activity
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
						StreamActivity.this.finish();

					}
				});

				tvGantiAkun.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.startAnimation(buttonClick);
						Intent i = new Intent(getApplicationContext(),
								DescActivity.class);
						pref.edit().clear();
						pref.edit().commit();

						i.addCategory(Intent.CATEGORY_HOME);
						// closing all the activity
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						// add new flag to start new activity
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
						StreamActivity.this.finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		StreamActivity.this.finish();
		System.exit(1);
	}

	/**
	 * get color mood
	 * 
	 * @param emotion
	 * @return color code
	 */
	public String getStringMood(int emotion) {
		String color = null;
		switch (emotion) {
		case 0:
			color = "sedih";
			break;
		case 1:
			color = "bosan";
			break;
		case 2:
			color = "marah";
			break;
		case 3:
			color = "was-was";
			break;
		case 4:
			color = "kaget";
			break;
		case 5:
			color = "takut";
			break;
		case 6:
			color = "percaya";
			break;
		case 7:
			color = "senang";
			break;
		}
		return color;
	}

	/**
	 * get color mood
	 * 
	 * @param emotion
	 * @return color code
	 */
	public String getColorMood(int emotion) {
		String color = null;
		switch (emotion) {
		case 0:
			color = "#69ccf0";
			break;
		case 1:
			color = "#ce80ae";
			break;
		case 2:
			color = "#ec68aa";
			break;
		case 3:
			color = "#fad08c";
			break;
		case 4:
			color = "#83c7b0";
			break;
		case 5:
			color = "#b9d37f";
			break;
		case 6:
			color = "#dbdc71";
			break;
		case 7:
			color = "#f6e85a";
			break;
		}
		return color;
	}

	/**
	 * rounded image
	 * 
	 * @param result
	 * @return
	 */
	public Bitmap roundImgBitmap(Bitmap result) {
		if (result == null) {
			return null;
		} else {
			Bitmap image_circle = Bitmap.createBitmap(result.getWidth(),
					result.getHeight(), Bitmap.Config.ARGB_8888);

			BitmapShader shader = new BitmapShader(result, TileMode.CLAMP,
					TileMode.CLAMP);
			Paint paint = new Paint();
			paint.setShader(shader);
			Canvas c = new Canvas(image_circle);
			c.drawCircle(result.getWidth() / 2, result.getHeight() / 2,
					result.getWidth() / 2, paint);
			return image_circle;
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

	/**
	 * async task to delete video
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class IncrementSeen extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(StreamActivity.this);
			progress.setMessage("Mohon tunggu ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			acm = new AmazonClientManager(StreamActivity.this);
			router = new DynamoDBRouter(acm);

			router.incrementSeen(usernamePop, hm.getCreatedDate());
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			String url = "http://halo-mama.com/@" + usernamePop;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url));
			startActivity(browserIntent);
		}
	}
}
