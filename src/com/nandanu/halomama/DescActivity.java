package com.nandanu.halomama;

import it.fhab.aws.AmazonClientManager;
import it.fhab.aws.DynamoDBRouter;
import it.fhab.aws.dynamodb.tablerow.HaloMama;
import it.fhab.aws.dynamodb.tablerow.People;
import it.fhab.aws.dynamodb.tablerow.Question;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;

import com.nandanu.halomama.controller.AlertDialogManager;
import com.nandanu.halomama.controller.ConnectionDetector;
import com.nandanu.halomama.controller.LinePageIndicator;
import com.nandanu.halomama.model.Constants;

public class DescActivity extends FragmentActivity {

	/**
	 * widgets
	 */
	public Context ctx = DescActivity.this;
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
	private static String KEY_FIRST_RUN = "";
	// Shared Preferences
	private SharedPreferences pref;
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
			alert.showAlertDialog(DescActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
//		AmazonClientManager acm = new AmazonClientManager(this);
//		DynamoDBRouter router = new DynamoDBRouter(acm);
//
//		People p = new People(acm.getIdentityId(), "android version");
//		p.prepareSignUp("username", "fullname");
//		router.signUp(p);
//		
//		p.prepareSignIn("username", "fullname");
//		router.signIn(p);
//		
//		p.prepareDeactivate("username", "fullname");
//		router.deactive(p);
//		
//		router.queryQuestion();
//		
//		HaloMama hm = new HaloMama("username", "android OS version");
//		
//		hm.preparePostFhab(2);
//		router.postFhab(hm);
//		
//		router.incrementSeen("username", hm.getCreatedDate());
//		
//		hm.preparePostHaloMama(hm.getCreatedDate(), "avatarURL", "twetId", 1, "description", "mp4");
//		router.postHaloMama(hm);
//		
//		hm.prepareDeleteHaloMama();
//		router.deleteHaloMama(hm);
//		
//		router.getLastHaloMama("username");
//		router.getPopularHaloMama();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager_desc);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);

//		pref = getPreferences(0);
		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
		if (!pref.contains("KEY_FIRST_RUN")) {
			KEY_FIRST_RUN = "something";
			Log.d("First", "First run!");
		} else {
			Log.d("Second...", "Second run...!");
		}

		SharedPreferences.Editor edit = pref.edit();
		edit.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
		edit.putString("CONSUMER_KEY", Constants.TWITTER_CONSUMER_KEY);
		edit.putString("CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET);
		edit.commit();
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
				return new Desc1Fragment();
			case 1:
				// fragment2 activity
				return new Desc2Fragment();
			case 2:
				// fragment3 activity
				return new Desc3Fragment();
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
}
