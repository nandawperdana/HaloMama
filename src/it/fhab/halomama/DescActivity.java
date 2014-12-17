package it.fhab.halomama;

import it.fhab.halomama.controller.LinePageIndicator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.animation.AlphaAnimation;

public class DescActivity extends FragmentActivity {
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

	public static boolean exists = false;
	public static boolean checked = false;
	public static final String createdDatePeople = ""
			+ System.currentTimeMillis();

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

		SharedPreferences settings = DescActivity.this.getSharedPreferences(
				"halomama", Context.MODE_PRIVATE);
		settings.edit().clear().commit();

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

			return null;
		}
	}

}
