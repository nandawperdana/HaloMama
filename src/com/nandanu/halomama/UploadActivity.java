package com.nandanu.halomama;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.nandanu.halomama.model.Constants;

public class UploadActivity extends FragmentActivity {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	// SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	// ViewPager mViewPager;

	// private LinePageIndicator mIndicator;

	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		// mSectionsPagerAdapter = new SectionsPagerAdapter(
		// getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		// mViewPager = (ViewPager) findViewById(R.id.pager_upload);
		// mViewPager.setAdapter(mSectionsPagerAdapter);

		// mIndicator = (LinePageIndicator) findViewById(R.id.indicator_upload);
		// mIndicator.setViewPager(mViewPager);
//		pref = getPreferences(0);
		pref = getSharedPreferences("halomama", Context.MODE_PRIVATE);
//		SharedPreferences.Editor edit = pref.edit();
//		edit.putString("CONSUMER_KEY", Constants.TWITTER_CONSUMER_KEY);
//		edit.putString("CONSUMER_SECRET", Constants.TWITTER_CONSUMER_SECRET);
//		edit.commit();

		Fragment mama = new MamaFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.pager_upload, mama);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(null);
		ft.commit();
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
	// public class SectionsPagerAdapter extends FragmentPagerAdapter {
	//
	// public SectionsPagerAdapter(FragmentManager fm) {
	// super(fm);
	// }
	//
	// @Override
	// public Fragment getItem(int position) {
	// // getItem is called to instantiate the fragment for the given page.
	// // Return a PlaceholderFragment (defined as a static inner class
	// // below).
	// switch (position) {
	// case 0:
	// // fragment1 activity
	// return new FragmentUploadNama();
	// case 1:
	// // fragment2 activity
	// return new FragmentFeeling();
	// }
	//
	// return null;
	// }
	//
	// @Override
	// public int getCount() {
	// // Show x total pages.
	// return 2;
	// }
	//
	// @Override
	// public CharSequence getPageTitle(int position) {
	// // Locale l = Locale.getDefault();
	// // switch (position) {
	// // case 0:
	// // return getString(R.string.title_section1).toUpperCase(l);
	// // case 1:
	// // return getString(R.string.title_section2).toUpperCase(l);
	// // case 2:
	// // return getString(R.string.title_section3).toUpperCase(l);
	// // }
	// return null;
	// }
	// }

}
