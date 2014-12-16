package it.fhab.halomama;

import com.amazonaws.AmazonClientException;

import it.fhab.halomama.controller.AlertDialogManager;
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
import android.graphics.BitmapFactory;
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
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StreamActivity extends Activity {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * widgets
	 */
	private ImageButton btnProfile, btnPlayVid, btnShareMedia;
	private RobotoTextView tvUsername, tvRetweet, tvSeen, tvVideoLainnya,
			tvMood;
	private ImageView imageUser;
	private Dialog dialog;
	private ProgressDialog progress;
	private FrameLayout layMood, layVideo;

	/*
	 * vars
	 */
	private Bitmap bitmapPop, bitmapThumb;
	private byte[] bytePop, byteThumbPop;
	private SharedPreferences pref;
	private String usernamePop, usernamePref, deviceOSPop, deviceOsPref;
	private int retweetCountPop, seenCountPop, emotionPop;
	private AlertDialogManager alert = new AlertDialogManager();
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

		bytePop = i.getByteArrayExtra("imgbmppop");
		byteThumbPop = i.getByteArrayExtra("imgthumb");

		bitmapPop = BitmapFactory.decodeByteArray(bytePop, 0, bytePop.length);
		if (byteThumbPop != null)
			bitmapThumb = BitmapFactory.decodeByteArray(byteThumbPop, 0,
					byteThumbPop.length);

		// bitmapPop = (Bitmap) i.getParcelableExtra("imgbmppop");
		// bitmapPref = (Bitmap) i.getParcelableExtra("imgbmppref");
		// bitmapThumb = (Bitmap) i.getParcelableExtra("imgthumb");
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
		imageUser = (ImageView) findViewById(R.id.imageViewUserImagePopular);
		btnPlayVid = (ImageButton) findViewById(R.id.buttonPlayVideo);
		btnProfile = (ImageButton) findViewById(R.id.buttonUserImage);
		btnShareMedia = (ImageButton) findViewById(R.id.buttonShareMedia);

		tvRetweet.setText("" + retweetCountPop);
		tvSeen.setText("" + seenCountPop);
		tvUsername.setText("@" + usernamePop);
		tvMood.setText(getStringMood(emotionPop));

		Bitmap roundImgPop = roundImgBitmap(bitmapPop);
		if (bitmapThumb != null)
			layVideo.setBackgroundDrawable(new BitmapDrawable(bitmapThumb));

		if (roundImgPop != null) {
			imageUser.setBackgroundDrawable(new BitmapDrawable(roundImgPop));
		}

		layMood.setBackgroundColor(Color.parseColor(getColorMood(emotionPop)));

		btnShareMedia.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(buttonClick);
				Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				String shareMessage = "ikut charity #halomama yuk, ada video menarik tentang #hariibu "
						+ "http://halo-mama.com/@" + usernamePop;
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareMessage);
				startActivity(Intent.createChooser(shareIntent,
						"Bagikan ke Sosial Media"));
			}
		});

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
						dialog.dismiss();
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

						/*
						 * clear cookie for sign out from twitter
						 */
						CookieSyncManager.createInstance(StreamActivity.this);
						CookieManager cookieManager = CookieManager
								.getInstance();
						cookieManager.removeAllCookie();

						SharedPreferences.Editor edit = pref.edit();
						edit.clear();
						edit.remove(Constants.TWITTER_ACCESS_TOKEN);
						edit.remove(Constants.TWITTER_ACCESS_TOKEN_SECRET);
						edit.remove(Constants.TWITTER_CONSUMER_KEY);
						edit.remove(Constants.TWITTER_CONSUMER_SECRET);
						edit.remove(Constants.TAG_TWITTER_USERNAME);
						edit.remove(Constants.TAG_TWITTER_FULLNAME);
						edit.remove(Constants.TAG_TWITTER_IMG_URL);
						edit.remove(Constants.TAG_DEVICE_OS);
						edit.commit();

						i.addCategory(Intent.CATEGORY_HOME);
						// closing all the activity
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						// add new flag to start new activity
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
						dialog.dismiss();
						StreamActivity.this.finish();
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
			try {
				acm = new AmazonClientManager(StreamActivity.this);
				router = new DynamoDBRouter(acm);

				HaloMama hm = new HaloMama(usernamePref, deviceOsPref);

				// deleteHaloMama : called when user choose "hapus video"
				hm.setCreatedDate(RecordActivity.createdDateVideo);
				hm.prepareDeleteHaloMama();
				router.deleteHaloMama(hm);
				return true;
			} catch (AmazonClientException e) {
				// TODO: handle exception
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			if (result) {
				Intent i = new Intent(StreamActivity.this, DescActivity.class);
				/*
				 * clear cookie for sign out from twitter
				 */
				CookieSyncManager.createInstance(StreamActivity.this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();

				SharedPreferences.Editor edit = pref.edit();
				edit.clear();
				edit.remove(Constants.TWITTER_ACCESS_TOKEN);
				edit.remove(Constants.TWITTER_ACCESS_TOKEN_SECRET);
				edit.remove(Constants.TWITTER_CONSUMER_KEY);
				edit.remove(Constants.TWITTER_CONSUMER_SECRET);
				edit.remove(Constants.TAG_TWITTER_USERNAME);
				edit.remove(Constants.TAG_TWITTER_FULLNAME);
				edit.remove(Constants.TAG_TWITTER_IMG_URL);
				edit.remove(Constants.TAG_DEVICE_OS);
				edit.commit();

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				dialog.dismiss();
				StreamActivity.this.finish();
			} else {
				alert.showAlertDialog(StreamActivity.this,
						"Kesalahan koneksi server", "Koneksi server gagal",
						false);
			}
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
			try {
				acm = new AmazonClientManager(StreamActivity.this);
				router = new DynamoDBRouter(acm);
				router.incrementSeen(usernamePop, hm.getCreatedDate());
				return true;
			} catch (AmazonClientException e) {
				// TODO: handle exception
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			if (result) {
				String url = "http://halo-mama.com/@" + usernamePop;
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(url));
				tvSeen.setText("" + (seenCountPop + 1));
				startActivity(browserIntent);
			} else {
				alert.showAlertDialog(StreamActivity.this,
						"Kesalahan koneksi server", "Koneksi server gagal",
						false);
			}
		}
	}
}
