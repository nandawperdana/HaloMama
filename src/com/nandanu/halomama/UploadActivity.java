package com.nandanu.halomama;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nandanu.halomama.model.Constants;

public class UploadActivity extends FragmentActivity {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	// private LinePageIndicator mIndicator;

	SharedPreferences pref;
	Bitmap bitmap;
	ImageView prof_img, tweet, signout, post_tweet;
	EditText tweet_text;
	ProgressDialog progress;
	Dialog tDialog;
	String tweetText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager_upload);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// mIndicator = (LinePageIndicator) findViewById(R.id.indicator_upload);
		// mIndicator.setViewPager(mViewPager);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent i = new Intent(getApplicationContext(), RecordActivity.class);

		i.addCategory(Intent.CATEGORY_HOME);
		// closing all the activity
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// add new flag to start new activity
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				// fragment1 activity
				return new FragmentUploadNama();
			case 1:
				// fragment2 activity
				return new FragmentFeeling();
			}

			return null;
		}

		@Override
		public int getCount() {
			// Show x total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// Locale l = Locale.getDefault();
			// switch (position) {
			// case 0:
			// return getString(R.string.title_section1).toUpperCase(l);
			// case 1:
			// return getString(R.string.title_section2).toUpperCase(l);
			// case 2:
			// return getString(R.string.title_section3).toUpperCase(l);
			// }
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a first description.
	 */
	public static class FragmentUploadNama extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static FragmentUploadNama newInstance(int sectionNumber) {
			FragmentUploadNama fragment = new FragmentUploadNama();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentUploadNama() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_upload_mama,
					container, false);
			return rootView;
		}
	}

	/**
	 * A placeholder fragment containing a first description.
	 */
	public static class FragmentFeeling extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static FragmentFeeling newInstance(int sectionNumber) {
			FragmentFeeling fragment = new FragmentFeeling();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentFeeling() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Button btnUpload, btnRekamUlang;
			View rootView = inflater.inflate(R.layout.fragment_upload_feeling,
					container, false);
			btnUpload = (Button) rootView.findViewById(R.id.buttonUpload);
			btnRekamUlang = (Button) rootView.findViewById(R.id.buttonUlang);

			btnUpload.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(getActivity(),
							ShareTwitterActivity.class);

					i.addCategory(Intent.CATEGORY_HOME);
					// closing all the activity
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					// add new flag to start new activity
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(i);
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
	}

	private class Tweet implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			tDialog = new Dialog(getApplicationContext());
			post_tweet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new PostTweet().execute();
				}
			});

			tDialog.show();

		}
	}

	private class PostTweet extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getApplicationContext());
			progress.setMessage("Posting tweet ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			tweetText = "Abis ngepost video di halo mama";
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
				twitter4j.Status response = twitter.updateStatus(tweetText);
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
				Toast.makeText(getApplicationContext(), "Tweet Posted",
						Toast.LENGTH_SHORT).show();
				tDialog.dismiss();
			} else {
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "Error while tweeting !",
						Toast.LENGTH_SHORT).show();
				tDialog.dismiss();
			}

		}
	}
}
