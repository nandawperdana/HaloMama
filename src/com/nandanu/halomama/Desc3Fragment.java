package com.nandanu.halomama;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.nandanu.halomama.controller.AmazonClientManager;
import com.nandanu.halomama.controller.DynamoDBRouter;
import com.nandanu.halomama.model.Constants;
import com.nandanu.halomama.model.People;
import com.nandanu.halomama.roboto.RobotoTextView;

public class Desc3Fragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	final static AlphaAnimation buttonClick = new AlphaAnimation(5F, 0.1F);

	/*
	 * widgets
	 */
	private Dialog auth_dialog, dialog;
	private WebView web;
	private ProgressDialog progress;
	private ImageButton btnLogin;
	private RobotoTextView tvPolicy, tvDesc3;

	/*
	 * vars
	 */
	private SharedPreferences pref;
	private String deviceOS = "Android ";

	/*
	 * twitter
	 */
	private Twitter twitter;
	private RequestToken requestToken = null;
	private AccessToken accessToken;
	private String oauth_url, oauth_verifier;

	/*
	 * dynamo DB
	 */
	// instantiate cognito client manager
	AmazonClientManager acm = null;
	// instantiate interface for databaserouter
	DynamoDBRouter router = null;

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
		pref = getActivity().getSharedPreferences("halomama",
				Context.MODE_PRIVATE);
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(pref.getString("CONSUMER_KEY",
				Constants.TWITTER_CONSUMER_KEY), pref.getString(
				"CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET));

		tvDesc3 = (RobotoTextView) rootView.findViewById(R.id.section_label3);
		tvDesc3.setText(Html.fromHtml(getString(R.string.fragment3_desc)));
		tvPolicy = (RobotoTextView) rootView
				.findViewById(R.id.section_labelPolicy);
		dialog = new Dialog(getActivity());
		btnLogin = (ImageButton) rootView.findViewById(R.id.buttonLogin);

		tvPolicy.setText(Html.fromHtml(getString(R.string.fragment3_policy)));
		tvPolicy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setAnimation(buttonClick);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.fhab.it/policy"));
				startActivity(browserIntent);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setAnimation(buttonClick);
				dialog.setCancelable(false);
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
						// tvIzinkan.setTextColor(Color.parseColor("#4a90e2"));
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

	/**
	 * login with twitter
	 * 
	 * @author Aslab-NWP
	 * 
	 */
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

	/**
	 * get profile
	 * 
	 * @author Aslab-NWP
	 * 
	 */
	private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Fetching Data ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.setCancelable(false);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {

			try {
				acm = new AmazonClientManager(getActivity());
				router = new DynamoDBRouter(acm);

				/*
				 * twitter
				 */
				accessToken = twitter.getOAuthAccessToken(requestToken,
						oauth_verifier);
				SharedPreferences.Editor edit = pref.edit();
				edit.putString(Constants.TAG_TWITTER_ACCESS_TOKEN,
						accessToken.getToken());
				edit.putString(Constants.TAG_TWITTER_ACCESS_TOKEN_SECRET,
						accessToken.getTokenSecret());
				User user = twitter.showUser(accessToken.getUserId());
				String username = user.getScreenName();
				String fullname = user.getName();
				String url = user.getProfileImageURL();
				String androidOS = Build.VERSION.RELEASE;

				/*
				 * sign up
				 */
				deviceOS += androidOS;
				People p = new People(acm.getIdentityId(), deviceOS);
				p.prepareSignUp(username, fullname);
				router.signUp(p);

				edit.putString(Constants.TAG_DEVICE_OS, deviceOS);
				edit.putString(Constants.TAG_TWITTER_IMG_URL, url);
				edit.putString(Constants.TAG_TWITTER_FULLNAME, fullname);
				edit.putString(Constants.TAG_TWITTER_USERNAME, username);

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
				progress.dismiss();
				dialog.dismiss();
				Intent i = new Intent(getActivity(), RecordActivity.class);
				// Intent i = new Intent(getActivity(), UploadActivity.class);
				startActivity(i);
			}
		}

	}
}
