package com.nandanu.halomama;

import twitter4j.Status;
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
	private Status tweet;

	/*
	 * transfer s3 vars
	 */
	private Uri uriPath;
	private TransferManager mManager;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = new AmazonClientManager(getActivity());
	// instantiate interface for databaserouter
	DynamoDBRouter router = new DynamoDBRouter(acm);

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
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivBosan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 1;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivMarah.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 2;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivWasWas.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 3;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivKaget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 4;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivTakut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 5;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivPercaya.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 6;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				btnUpload.setVisibility(View.VISIBLE);
			}
		});

		ivSenang.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feeling = true;
				feel = 7;
				ivSedih.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivBosan.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivMarah.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivWasWas.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivKaget.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivTakut.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivPercaya
						.setBackgroundResource(R.drawable.emotion_button_white_mdpi);
				ivSenang.setBackgroundResource(R.drawable.emotion_button_white_selected_mdpi); // sel
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
				tweet = twitter.updateStatus(text);

				/*
				 * upload to s3
				 */
				UploadModel model = new UploadModel(getActivity(), uriPath,
						mManager);
				model.upload();

				return tweet.toString();
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
				/*
				 * update db
				 */
				new UpdateVideoData().execute();

				Intent i = new Intent(getActivity(), DoneUploadActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			} else {
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
			/*
			 * postfhab : create initial data after video is uploaded to s3
			 */
			HaloMama hm = new HaloMama(username, deviceOS);
			hm.preparePostFhab(feel);
			router.postFhab(hm);

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
