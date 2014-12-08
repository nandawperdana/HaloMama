package com.nandanu.hallomama;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nandanu.hallomama.controller.AlertDialogManager;
import com.nandanu.hallomama.controller.ConnectionDetector;
import com.nandanu.hallomama.model.Constants;
import com.nandanu.hallomama.roboto.RobotoTextView;
import com.viewpagerindicator.LinePageIndicator;

@SuppressLint("ValidFragment")
public class MainActivity extends ActionBarActivity {
	/**
	 * widgets
	 */
	public Context ctx = MainActivity.this;
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);
	private LinePageIndicator mIndicator;
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

	/**
	 * variables
	 */
	// Shared Preferences
	private static SharedPreferences pref;
	// Internet Connection detector
	private ConnectionDetector cd;
	// Alert Dialog Manager
	private AlertDialogManager alert = new AlertDialogManager();

	/**
	 * Twitter
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 11) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		cd = new ConnectionDetector(getApplicationContext());

		/**
		 * Check if Internet present
		 */
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager_desc);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);

	}

	protected void onResume() {
		super.onResume();
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
				return new FragmentDesc1();
			case 1:
				// fragment2 activity
				return new FragmentDesc2();
			case 2:
				// fragment3 activity
				return new FragmentDesc3();
			}

			return null;
		}

		@Override
		public int getCount() {
			// Show x total pages.
			return 3;
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
	public static class FragmentDesc1 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public FragmentDesc1 newInstance(int sectionNumber) {
			FragmentDesc1 fragment = new FragmentDesc1();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentDesc1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_desc1,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label1);
			textView.setText(Html.fromHtml(getString(R.string.fragment1_desc)));
			return rootView;
		}
	}

	/**
	 * A placeholder fragment containing second description.
	 */
	public static class FragmentDesc2 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public FragmentDesc2 newInstance(int sectionNumber) {
			FragmentDesc2 fragment = new FragmentDesc2();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentDesc2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_desc2,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label2);
			textView.setText(Html.fromHtml(getString(R.string.fragment2_desc)));
			return rootView;
		}
	}

	/**
	 * A placeholder fragment containing third description.
	 */
	public static class FragmentDesc3 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		private Dialog auth_dialog;
		private WebView web;
		private ProgressDialog progress;

		private Twitter twitter;
		private RequestToken requestToken = null;
		private AccessToken accessToken;
		static String oauth_url, oauth_verifier, profile_url;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public FragmentDesc3 newInstance(int sectionNumber) {
			FragmentDesc3 fragment = new FragmentDesc3();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentDesc3() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_desc3,
					container, false);
			/**
			 * twitter init
			 */
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(pref.getString("CONSUMER_KEY",
					Constants.TWITTER_CONSUMER_KEY), pref.getString(
					"CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET));
			Button btnLogin;
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label3);
			textView.setText(Html.fromHtml(getString(R.string.fragment3_desc)));
			btnLogin = (Button) rootView.findViewById(R.id.buttonLogin);
			btnLogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.permission_dialog);

					final RobotoTextView tvTitle = (RobotoTextView) dialog
							.findViewById(R.id.textViewPermissionTitle);
					final RobotoTextView tvMessage = (RobotoTextView) dialog
							.findViewById(R.id.textViewPermission);
					tvMessage.setText(Html.fromHtml(getString(
							R.string.desc_permission).toString()));
					final RobotoTextView tvIzinkan = (RobotoTextView) dialog
							.findViewById(R.id.textViewIzinkan);
					final RobotoTextView tvTolak = (RobotoTextView) dialog
							.findViewById(R.id.textViewTolak);

					tvIzinkan.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							tvIzinkan.setTextColor(Color.parseColor("#4a90e2"));
							v.startAnimation(buttonClick);
							/*
							 * login to twitter
							 */
							new TokenGet().execute();

						}
					});

					tvTolak.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							tvTolak.setTextColor(Color.parseColor("#4a90e2"));
							v.startAnimation(buttonClick);
							dialog.dismiss();
						}
					});

					dialog.show();
				}
			});
			return rootView;
		}

		public class TokenGet extends AsyncTask<String, String, String> {

			@Override
			protected String doInBackground(String... args) {

				try {
					requestToken = twitter.getOAuthRequestToken();
					oauth_url = requestToken.getAuthorizationURL();
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return oauth_url;
			}

			@Override
			protected void onPostExecute(String oauth_url) {
				if (oauth_url != null) {
					Log.e("URL", oauth_url);
					auth_dialog = new Dialog(getActivity());
					auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					auth_dialog.setContentView(R.layout.auth_dialog);
					web = (WebView) auth_dialog.findViewById(R.id.webv);
					web.getSettings().setJavaScriptEnabled(true);
					web.loadUrl(oauth_url);
					web.setWebViewClient(new WebViewClient() {
						boolean authComplete = false;

						@Override
						public void onPageStarted(WebView view, String url,
								Bitmap favicon) {
							super.onPageStarted(view, url, favicon);
						}

						@Override
						public void onPageFinished(WebView view, String url) {
							super.onPageFinished(view, url);
							if (url.contains("oauth_verifier")
									&& authComplete == false) {
								authComplete = true;
								Log.e("Url", url);
								Uri uri = Uri.parse(url);
								oauth_verifier = uri
										.getQueryParameter("oauth_verifier");

								auth_dialog.dismiss();
								new AccessTokenGet().execute();
							} else if (url.contains("denied")) {
								auth_dialog.dismiss();
								Toast.makeText(getActivity(),
										"Sorry !, Permission Denied",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
					auth_dialog.show();
					auth_dialog.setCancelable(true);

				} else {

					Toast.makeText(getActivity(),
							"Sorry !, Network Error or Invalid Credentials",
							Toast.LENGTH_SHORT).show();

				}
			}
		}

		private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = new ProgressDialog(getActivity());
				progress.setMessage("Fetching Data ...");
				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progress.setIndeterminate(true);
				progress.show();

			}

			@Override
			protected Boolean doInBackground(String... args) {

				try {

					accessToken = twitter.getOAuthAccessToken(requestToken,
							oauth_verifier);
					SharedPreferences.Editor edit = pref.edit();
					edit.putString("ACCESS_TOKEN", accessToken.getToken());
					edit.putString("ACCESS_TOKEN_SECRET",
							accessToken.getTokenSecret());
					User user = twitter.showUser(accessToken.getUserId());
					profile_url = user.getOriginalProfileImageURL();
					edit.putString("NAME", user.getName());

					edit.commit();

				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean response) {
				if (response) {
					progress.hide();
					Intent i = new Intent(getActivity(), RecordActivity.class);
					startActivity(i);
				}
			}

		}
	}

}
