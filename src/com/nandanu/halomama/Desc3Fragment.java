package com.nandanu.halomama;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.roboto.RobotoTextView;

public class Desc3Fragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);
	private static final String ARG_SECTION_NUMBER = "section_number";

	private Dialog auth_dialog;
	private WebView web;
	private ProgressDialog progress;
	Button btnLogin;

	// Shared Preferences
	private static SharedPreferences pref;
	private Twitter twitter;
	private RequestToken requestToken = null;
	private AccessToken accessToken;
	private String oauth_url, oauth_verifier;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public Desc3Fragment newInstance(int sectionNumber) {
		Desc3Fragment fragment = new Desc3Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Desc3Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_desc3, container,
				false);
		/**
		 * twitter init
		 */
		pref = getActivity().getPreferences(0);
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(pref.getString("CONSUMER_KEY",
				Constants.TWITTER_CONSUMER_KEY), pref.getString(
				"CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET));
		Toast.makeText(getActivity(), Constants.TWITTER_CONSUMER_KEY, Toast.LENGTH_SHORT).show();

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
//						tvIzinkan.setTextColor(Color.parseColor("#4a90e2"));
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
						"Sorry ! Network Error or Invalid Credentials",
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
//				edit.putString("ACCESS_TOKEN", Constants.TWITTER_ACCESS_TOKEN);
				 edit.putString("ACCESS_TOKEN_SECRET",
				 accessToken.getTokenSecret());
//				edit.putString("ACCESS_TOKEN_SECRET",
//						Constants.TWITTER_ACCESS_TOKEN_SECRET);
				User user = twitter.showUser(accessToken.getUserId());
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
