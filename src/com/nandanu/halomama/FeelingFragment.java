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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.roboto.RobotoTextView;

/**
 * A placeholder fragment containing a first description.
 */
@SuppressLint("NewApi")
public class FeelingFragment extends Fragment {
	private SharedPreferences pref;
	private ImageButton btnBatalkan;
	private ProgressDialog progress;;
	private String tweetText, namaMama, mentionTeman;
	private RobotoTextView textViewDesc;

	public FeelingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Button btnUpload, btnRekamUlang;
		View rootView = inflater.inflate(R.layout.fragment_upload_feeling,
				container, false);
		pref = getActivity().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		Toast.makeText(getActivity(), pref.getString("NAME", ""),
				Toast.LENGTH_LONG).show();

		Bundle bundle = this.getArguments();
		namaMama = bundle.getString("NAMA_MAMA", "");
		mentionTeman = bundle.getString("NAMA_TEMAN", "");

		textViewDesc = (RobotoTextView) rootView
				.findViewById(R.id.textViewFeelingDesc);
		textViewDesc.setText(Html
				.fromHtml(getString(R.string.fragment_feeling_desc)));
		
		btnBatalkan = (ImageButton) rootView
				.findViewById(R.id.imageButtonBatalkan2);
		btnBatalkan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),
						DescActivity.class);

				i.addCategory(Intent.CATEGORY_HOME);
				// closing all the activity
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// add new flag to start new activity
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);
			}
		});

		btnUpload = (Button) rootView.findViewById(R.id.buttonUpload);
		btnRekamUlang = (Button) rootView.findViewById(R.id.buttonUlang);

		btnUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new PostTweet().execute();
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
		return rootView;
	}

	private class PostTweet extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Posting tweet ...");
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
			// builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
			// builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);

			// AccessToken accessToken = new AccessToken(
			// Constants.TWITTER_ACCESS_TOKEN,
			// Constants.TWITTER_ACCESS_TOKEN_SECRET);
			AccessToken accessToken = new AccessToken(pref.getString(
					"ACCESS_TOKEN", Constants.TWITTER_ACCESS_TOKEN),
					pref.getString("ACCESS_TOKEN_SECRET",
							Constants.TWITTER_ACCESS_TOKEN_SECRET));
			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);

			try {
				String nama = namaMama;
				String mention = mentionTeman;
				tweetText = "#HaloMama " + nama
						+ " selamat #hariibu! http://fhab.it/halomama/"
						+ pref.getString("USERNAME", "") + " cc:@" + mention;
				String text = tweetText;
				twitter4j.Status response = twitter.updateStatus(text);
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
}
