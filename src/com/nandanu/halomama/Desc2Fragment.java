package com.nandanu.halomama;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Desc2Fragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public Desc2Fragment newInstance(int sectionNumber) {
		Desc2Fragment fragment = new Desc2Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Desc2Fragment() {
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
