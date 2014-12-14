package com.nandanu.halomama;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.nandanu.halomama.controller.AmazonClientManager;
import com.nandanu.halomama.controller.DynamoDBRouter;
import com.nandanu.halomama.controller.Util;
import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.model.HaloMama;
import com.nandanu.halomama.model.UploadModel;
import com.nandanu.halomama.roboto.RobotoTextView;

/**
 * A placeholder fragment containing a first description.
 */
@SuppressLint("NewApi")
public class FeelingFragment extends Fragment {
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);
	/*
	 * widgets
	 */
	private ImageButton btnBatalkan;
	private ProgressDialog progress;
	private RobotoTextView textViewDesc;
	private ImageButton btnUpload, btnRekamUlang;
	private ImageView ivSedih, ivBosan, ivMarah, ivWasWas, ivKaget, ivTakut,
			ivPercaya, ivSenang;

	/*
	 * vars
	 */
	private String tweetText, namaMama, mentionTeman, username, deviceOS,
			avatarUrl, tweetId;
	private SharedPreferences pref;
	private boolean feeling = false;
	private int feel;
	private twitter4j.Status tweet;

	/*
	 * transfer s3 vars
	 */
	private Uri uriPath;
	private TransferManager mManager;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	// instantiate interface for databaserouter
	DynamoDBRouter router = null;

	public FeelingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upload_feeling,
				container, false);
		/*
		 * get shared preferences
		 */
		pref = getActivity().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		username = pref.getString(Constants.TAG_TWITTER_USERNAME, "");
		deviceOS = pref.getString(Constants.TAG_DEVICE_OS, "");
		avatarUrl = pref.getString(Constants.TAG_AVATAR_URL, "");

		Toast.makeText(getActivity(), "" + deviceOS, Toast.LENGTH_LONG).show();

		Bundle bundle = this.getArguments();
		uriPath = bundle.getParcelable("VIDEO_URI");
		namaMama = bundle.getString("NAMA_MAMA", "");
		mentionTeman = bundle.getString("NAMA_TEMAN", "");

		mManager = new TransferManager(Util.getCredProvider(getActivity()));

		/*
		 * widgets init
		 */
		ivSedih = (ImageView) rootView.findViewById(R.id.imageViewSedih);
		ivBosan = (ImageView) rootView.findViewById(R.id.imageViewBosan);
		ivMarah = (ImageView) rootView.findViewById(R.id.imageViewMarah);
		ivWasWas = (ImageView) rootView.findViewById(R.id.imageViewWasWas);
		ivKaget = (ImageView) rootView.findViewById(R.id.imageViewKaget);
		ivTakut = (ImageView) rootView.findViewById(R.id.imageViewTakut);
		ivPercaya = (ImageView) rootView.findViewById(R.id.imageViewPercaya);
		ivSenang = (ImageView) rootView.findViewById(R.id.imageViewSenang);
		textViewDesc = (RobotoTextView) rootView
				.findViewById(R.id.textViewFeelingDesc);
		textViewDesc.setText(Html
				.fromHtml(getString(R.string.fragment_feeling_desc)));
		btnBatalkan = (ImageButton) rootView
				.findViewById(R.id.imageButtonBatalkan3);
		btnUpload = (ImageButton) rootView.findViewById(R.id.buttonUpload);
		if (!feeling)
			btnUpload.setVisibility(View.GONE);
		btnRekamUlang = (ImageButton) rootView.findViewById(R.id.buttonUlang);

		btnBatalkan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(buttonClick);
				Intent i = new Intent(getActivity(), DescActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			}
		});

		btnUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub
				/**
				 * upload video
				 */
				new UploadAndTweet().execute();
			}
		});

		btnRekamUlang.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(buttonClick);
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), RecordActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			}
		});

		ivSedih.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 0;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_selected_hdpi); // sel
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivBosan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 1;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_selected_hdpi); // sel
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivMarah.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 2;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_selected_hdpi); // sel
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivWasWas.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 3;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_selected_hdpi); // sel
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivKaget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 4;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_selected_hdpi); // sel
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivTakut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 5;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_selected_hdpi); // sel
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivPercaya.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 6;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_selected_hdpi); // sel
				ivSenang.setImageResource(R.drawable.emotion_button_senang_hdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivSenang.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 7;
				ivSedih.setImageResource(R.drawable.emotion_button_sedih_hdpi);
				ivBosan.setImageResource(R.drawable.emotion_button_bosan_hdpi);
				ivMarah.setImageResource(R.drawable.emotion_button_marah_hdpi);
				ivWasWas.setImageResource(R.drawable.emotion_button_waswas_hdpi);
				ivKaget.setImageResource(R.drawable.emotion_button_kaget_hdpi);
				ivTakut.setImageResource(R.drawable.emotion_button_takut_hdpi);
				ivPercaya
						.setImageResource(R.drawable.emotion_button_percaya_hdpi);
				ivSenang.setImageResource(R.drawable.emotion_button_senang_selected_hdpi); // sel
				btnUpload.setVisibility(View.VISIBLE);
			}
		});
		return rootView;
	}

	/**
	 * asynctask to post tweet and upload video to s3
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class UploadAndTweet extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Mengunggah video ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		protected String doInBackground(String... args) {
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

			try {
				/*
				 * tweet
				 */
				tweetText = "#HaloMama " + namaMama
						+ " selamat #hariibu! http://fhab.it/halomama/@"
						+ username + " cc:@" + mentionTeman;
				String text = tweetText;
				twitter4j.Status response = twitter.updateStatus(text);
				tweet = response;

				return response.toString();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String res) {
			if (res != null) {
				progress.dismiss();
				Toast.makeText(getActivity(), "Tweet Posted",
						Toast.LENGTH_SHORT).show();

				new UploadVideo().execute();

				Intent i = new Intent(getActivity(), DoneUploadActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			} else {
//				Toast.makeText(getActivity(), "res, " + tweet.toString(),
//						Toast.LENGTH_SHORT).show();
				progress.dismiss();
				Toast.makeText(getActivity(), "Error while tweeting !",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * async task to update DB
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class UploadVideo extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Mengunggah ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			/*
			 * upload to s3
			 */
			UploadModel model = new UploadModel(getActivity(), uriPath,
					mManager);
			model.upload();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			/*
			 * update db
			 */
			new CreateInitialData().execute();
			progress.dismiss();
		}
	}

	/**
	 * async task to update DB
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class CreateInitialData extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Meng-update ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			acm = new AmazonClientManager(getActivity());
			router = new DynamoDBRouter(acm);
			/*
			 * postfhab : create initial data after video is uploaded to s3
			 */
			HaloMama hm = new HaloMama(username, deviceOS);
			hm.preparePostFhab(feel);
			router.postFhab(hm);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			new UpdateVideoData().execute();
		}
	}

	/**
	 * async task to update DB
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class UpdateVideoData extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Meng-update ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			acm = new AmazonClientManager(getActivity());
			router = new DynamoDBRouter(acm);

			HaloMama hm = new HaloMama(username, deviceOS);

			/*
			 * postHaloMama : updating the video data, called after posting to
			 * twitter
			 */
			String twitId = String.valueOf(tweet.getId());
			hm.preparePostHaloMama(hm.getCreatedDate(), avatarUrl, twitId,
					feel, tweetText, "mp4");
			router.postHaloMama(hm);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progress.dismiss();
		}
	}
}
